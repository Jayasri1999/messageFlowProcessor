package com.example.messageFlowProcessor.adapter;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.messageFlowProcessor.cache.ProcessFlowCache;
import com.example.messageFlowProcessor.entity.ProcessFlow;
import com.example.messageFlowProcessor.repository.ProcessFlowRepository;


@Component
public class InboundAdapter extends RouteBuilder{
	@Autowired
	ProcessFlowRepository processFlowRepository;
	@Autowired
	ProcessFlowCache processFlowCache;
	@Override
	public void configure()  throws Exception{
	    configureInboundRoute("sc1.US.1.in");
	    configureInboundRoute("sc2.MX.2.in");
	}
	public void configureInboundRoute(String inputQueue){
		from("activemq:"+inputQueue)
		.process(exchange->{
			log.info("+++Before Inbound Process+++");
			inboundProcess(exchange);
			log.info("+++After Inbound Process+++");
		})
		.choice()
        .when(header("nextHop").isEqualTo("entry"))
            .to("activemq:entry.in")
        .when(header("nextHop").isEqualTo("transform"))
            .to("activemq:transform.in")
        .when(header("nextHop").isEqualTo("exit"))
            .to("activemq:exit.in")
		.when(header("nextHop").isEqualTo("outbound"))
			.to("activemq:outboud.in");
		
	}
	

	
	public void inboundProcess(Exchange exchange) {
		//Process data from queue name
		String inputQueue= exchange.getIn().getHeader("JMSDestination",String.class);
		String[] fields= inputQueue.split("\\.");
		String scenario= fields[0].substring(8);
		String country= fields[1];
		String instance= fields[2];
		log.info("scenario: "+scenario+" country: "+country+" instance: "+instance);
		//fetch from cache or db
		ProcessFlow processFlow= processFlowCache.getProcessFlow(scenario, country, instance);
		//set header
        exchange.getIn().setHeader("scenario", scenario);
        exchange.getIn().setHeader("country", country);
        exchange.getIn().setHeader("instance", instance);
        exchange.getIn().setHeader("entryProcess", processFlow.getEntry_process());
        exchange.getIn().setHeader("transformProcess", processFlow.getTransform_process());
        exchange.getIn().setHeader("exitProcess", processFlow.getExit_process());
        exchange.getIn().setHeader("xsltContent", processFlow.getXslt_content());
        // Determine next hop
        if (processFlow.getEntry_process() != null) {
            exchange.getIn().setHeader("nextHop", "entry");
        } else if (processFlow.getTransform_process() != null) {
            exchange.getIn().setHeader("nextHop", "transform");
        } else if (processFlow.getExit_process() != null) {
            exchange.getIn().setHeader("nextHop", "exit");
        } else {
        	exchange.getIn().setHeader("nextHop", "outbound");
        }
	}
	
	public ProcessFlow fetchProcessFlow(String scenario, String country, String instance) {
		ProcessFlow processFlow = processFlowRepository.findByKeyCountryInstance(scenario, country, instance)
				.orElseThrow(() -> new IllegalArgumentException("ProcessFlow not found"));
		return processFlow;
	}

}
