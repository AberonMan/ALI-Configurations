package com.mash.dip.jms.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.ExecutorServiceManager;

import java.io.File;
import java.nio.file.Paths;

public class SendConsumeJMS {

    public static void  main(String[] args) throws Exception {

        //delete old fike
        //noinspection ResultOfMethodCallIgnored
        Paths.get("output/jmsout").toFile().delete();

        //create camel content
        CamelContext context = new DefaultCamelContext();

        // resolve connection factory and add to route
        ConnectionFactoryContextResolver resolver = new ConnectionFactoryContextResolver();
        context.addComponent("test-jms",
                JmsComponent.jmsComponentAutoAcknowledge(resolver.resolveConnectionFactory()));
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("test-jms:queue:com.dip.mash.camel.queue").to("file://output?fileName=jmsout&fileExist=append");
            }
        });

        // start context
        context.start();

        // send messages to queue
        ProducerTemplate template = context.createProducerTemplate();
        for (int i = 0; i < 10; i++) {
            template.sendBody("test-jms:queue:com.dip.mash.camel.queue", "Test Message: " + i + "\n\r");
        }

        // just to be sure that message was written to file
        Thread.sleep(3000);
        context.stop();
    }
}
