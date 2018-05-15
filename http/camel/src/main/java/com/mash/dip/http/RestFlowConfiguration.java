package com.mash.dip.http;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;


/**
 * This class get customer by id and return stub value due to emulate db call
 */
public class RestFlowConfiguration extends RouteBuilder {

    private final CustomerRepositoryProcessor repository = new CustomerRepositoryProcessor();


    public final static String ACCOUNT_NUMBER_PATH_VARIABLE = "accountNumber";

    @Override
    public void configure() throws Exception {
        getCustomerByName();
        acceptTaskSendResponseAndProcess();

    }

    private void acceptTaskSendResponseAndProcess() {
        rest("/services")
                .description("Test task http trigger")
                .bindingMode(RestBindingMode.json)
                .post("/process")
                .description("Task description endpoint")
                .produces(APPLICATION_JSON)
                .route()
                .process(exchange -> {
                    exchange.getOut().setHeaders(exchange.getIn().getHeaders());
                    exchange.getOut().setHeader("operation_id", UUID.randomUUID());
                    exchange.getOut().setBody(exchange.getIn().getBody());
                })
                .to("direct:sendAccepted")
                .wireTap("direct:no.return")
                .end();


        // send accept status code and return process id
        from("direct:sendAccepted")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("202"))
                .setBody(simple("{\"operation_id\":\"${in.headers.operation_id}\",\"operation_status\":\"ACCEPTED\"}"));

        //emulate operation exectuin and send post request with operation result
        from("direct:no.return")
                .process(exchange -> exchange.getOut().setBody(simple("{\"operation_id\":\"${in.headers.operation_id}\"," +
                        " \"operation_status\": \"SUCCESS\"}")))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant(TEXT_PLAIN))
                .to("http://localhost:8080/camel/responcehook");

        // emulate system which is listening operation execution callback
        rest()
                .post("/responcehook")
                .route()
                .process(exchange -> System.out.println(exchange.getIn().getBody()));

    }

    private void getCustomerByName() {
        rest("/services/account/")
                .description("Test customer account")

                .bindingMode(RestBindingMode.json)
                .get()
                .description("A simple get customer endpoint")

                .param()
                .name(ACCOUNT_NUMBER_PATH_VARIABLE)
                .type(RestParamType.query)
                .description("Customer account number")
                .endParam()

                .produces(APPLICATION_JSON)
                /*
                    embed a camel route in rest dsl
                 */
                .route()
                /*
                   process request to repository and set output
                 */
                .process(repository);
    }
}


