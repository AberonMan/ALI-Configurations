package com.mash.dip.http;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;


/**
 * This class get customer by id and return stub value due to emulate db call
 */
public class CamelRestConfigurations extends RouteBuilder {

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
                .produces(TEXT_PLAIN)
                .route()
                .multicast()
                .to("seda:sendAccepted")
                .to("seda:asyncProcess");

        from("seda:sendAccepted").wireTap("seda:sendAccepted").setHeader(Exchange.HTTP_RESPONSE_CODE, simple("202"))
                .setBody(simple("Task accepted"));

        from("seda:asyncProcess").wireTap("seda:asyncProcess")
                .process(new AsyncCallProcessor());

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

