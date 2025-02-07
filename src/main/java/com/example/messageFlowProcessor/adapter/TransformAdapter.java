package com.example.messageFlowProcessor.adapter;

import java.lang.reflect.Method;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TransformAdapter extends RouteBuilder{
	@Override
	public void configure() throws Exception{
		from("activemq:transform.in")
        .process(exchange -> {
            String entryProcessMethod = exchange.getIn().getHeader("transformProcess", String.class);
            invokeMethod(entryProcessMethod, exchange);
        })
		.choice()
        .when(header("nextHop").isEqualTo("exit"))
            .to("activemq:exit.in")
		.when(header("nextHop").isEqualTo("outbound"))
			.to("activemq:outboud.in");
	}
	public void transformProcess1(Exchange exchange) {
		// transform xml to json using XSLT
        String xsltContent = exchange.getIn().getHeader("xsltContent", String.class);
        String body = exchange.getIn().getBody(String.class);
        try {
        String transformedBody = xmlToJsonTransformXSLT(body, xsltContent);
        exchange.getIn().setBody(transformedBody);
        } catch (TransformerException e) {
            log.error("Error during XSLT transformation", e);
            throw new RuntimeException("Transformation failed", e);
        }
		//Determine next hop
		if (exchange.getIn().getHeader("exitProcess") != null) {
            exchange.getIn().setHeader("nextHop", "exit");
        } else {
        	exchange.getIn().setHeader("nextHop", "outBound");
		}
	}
	
	public String xmlToJsonTransformXSLT(String body, String xsltContent) throws TransformerException  {
		TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(
            new StreamSource(new StringReader(xsltContent))
        );
        StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(body)), new StreamResult(writer));

        return writer.toString();
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
