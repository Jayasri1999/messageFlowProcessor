package com.example.messageFlowProcessor.adapter;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OutboundAdapter extends RouteBuilder{
	@Override
	public void configure() throws Exception{
		from("activemq:outbound.in")
		.process(exchange->{
			log.info("+++++++++inside outbound adapter+++++++++");
			outboundProcess(exchange);
        })
		.log("Moving to output queue: ${header.outputQueue}")
        .toD("activemq:queue:${header.outputQueue}");
	}
	
	public void outboundProcess(Exchange exchange) {
		String scenario = exchange.getIn().getHeader("scenario", String.class);
        String country = exchange.getIn().getHeader("country", String.class);
        Integer instance = exchange.getIn().getHeader("instance", Integer.class);
     // Store the message in the corresponding dynamic queue output based on headers
        String outputQueue = scenario + "." + country + "." + instance+".out";
        exchange.getIn().setHeader("outputQueue", outputQueue);
	}
}
