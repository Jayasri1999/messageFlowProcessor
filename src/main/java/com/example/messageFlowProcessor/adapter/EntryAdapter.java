package com.example.messageFlowProcessor.adapter;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EntryAdapter extends RouteBuilder{
	@Override
	public void configure() throws Exception{
		from("activemq:entry.in")
        .process(exchange -> {
            String entryProcessMethod = exchange.getIn().getHeader("entryProcess", String.class);
            invokeMethod(entryProcessMethod, exchange);
        })
		.choice()
        .when(header("nextHop").isEqualTo("transform"))
            .to("activemq:transform.in")
        .when(header("nextHop").isEqualTo("exit"))
            .to("activemq:exit.in")
		.when(header("nextHop").isEqualTo("outbound"))
			.to("activemq:outboud.in");

	}
	
	public void entryProcess1(Exchange exchange) {
		//Determine next hop
		if (exchange.getIn().getHeader("transformProcess") != null) {
            exchange.getIn().setHeader("nextHop", "transform");
        } else if (exchange.getIn().getHeader("exitProcess") != null) {
            exchange.getIn().setHeader("nextHop", "exit");
        } else {
        	exchange.getIn().setHeader("nextHop", "outBound");
		}
	}
	public void invokeMethod(String methodName, Object... params) {
	    try {
	        // Get method with exact name and parameter types
	        Class<?>[] paramTypes = new Class<?>[]{
	            Exchange.class
	        };	        
	        Method method = this.getClass().getMethod(methodName, paramTypes);
	        log.info("++++++++++invoke method++++++++++++++"+method);
	        method.invoke(this, params);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
