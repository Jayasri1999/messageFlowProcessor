package com.example.messageFlowProcessor.adapter;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExitAdapter extends RouteBuilder{
	@Override
	public void configure() throws Exception{
		from("activemq:exit.in")
        .process(exchange -> {
            String entryProcessMethod = exchange.getIn().getHeader("exitProcess", String.class);
            invokeMethod(entryProcessMethod, exchange);
        })
		.choice()
		.when(header("nextHop").isEqualTo("outbound"))
			.to("activemq:outbound.in");
	
	}
	public void exitProcess1(Exchange exchange) {
		//Determine next hop
        exchange.getIn().setHeader("nextHop", "outbound");

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
