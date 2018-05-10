package com.mash.dip.http;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.camel.builder.Builder.simple;

public class AsyncCallProcessor implements Processor {

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    private final ExecutorService service = Executors.newCachedThreadPool();

    private final static String SUCCESSFUL_OPERATION = "\"operation_id\":\"9899120312, \"\n" +
            "   \"operation_status\":\"accepted\"\n";

    private final static String FAILED_OPERATION = "failed operation";


    @Override
    public void process(Exchange exchange) {
        final int rnd = random.nextInt(0, 3);
        if (rnd > 2) {
            exchange.getIn().setBody(simple(SUCCESSFUL_OPERATION));
        } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, "408");
            exchange.getIn().setBody(simple(FAILED_OPERATION));
        }
        System.out.println("Processed async");

    }
}
