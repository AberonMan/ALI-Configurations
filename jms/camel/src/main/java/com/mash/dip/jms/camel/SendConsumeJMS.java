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
        System.out.println("Delete old folder ......");
        //noinspection ResultOfMethodCallIgnored
        Paths.get("output/jmsout").toFile().delete();

        //create camel content
        System.out.println("Create camel context...........");
        CamelContext context = new DefaultCamelContext();

        // resolve connection factory and add to route
        ConnectionFactoryContextResolver resolver = new ConnectionFactoryContextResolver();
        System.out.println("Resolve connection factory and add to route");
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
            String message = "Test Message: " + i + "\n\r";
            template.sendBody("test-jms:queue:com.dip.mash.camel.queue", message);
            System.out.print("Send jms message: " + message);
        }

        // just to be sure that message was written to file
        Thread.sleep(3000);
        context.stop();
    }
}
