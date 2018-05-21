package com.mash.dip.jms.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class SendConsumeJMS {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();


        ConnectionFactoryContextResolver resolver = new ConnectionFactoryContextResolver();
        context.addComponent("test-jms",
                JmsComponent.jmsComponentAutoAcknowledge(resolver.resolveConnectionFactory()));
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("test-jms:queue:com.netcracker.mash.queue").to("file://result/");
            }
        });
        context.setLogExhaustedMessageBody(true);
        context.start();

        ProducerTemplate template = context.createProducerTemplate();
        for (int i = 0; i < 4; i++) {
            template.sendBody("test-jms:queue:", "Test Message: " + i);
        }


        Thread.sleep(4000);
        context.stop();

    }
}
