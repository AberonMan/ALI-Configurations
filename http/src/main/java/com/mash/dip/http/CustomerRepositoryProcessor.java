package com.mash.dip.http;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import static com.mash.dip.http.CamelRestConfigurations.ACCOUNT_NUMBER_PATH_VARIABLE;
import static org.apache.camel.builder.SimpleBuilder.simple;

public class CustomerRepositoryProcessor implements Processor {

    private static final String STUB_DATA = "\n" +
            "\n" +
            "{\n" +
            "\"accountNumber\": \"%s\",\n" +
            "     \"firstName\": \"John\",\n" +
            "     \"lastName\": \"Smith\",\n" +
            "     \"age\": 25,\n" +
            "     \"address\":\n" +
            "     {\n" +
            "         \"streetAddress\": \"21 2nd Street\",\n" +
            "         \"city\": \"New York\",\n" +
            "         \"state\": \"NY\",\n" +
            "         \"postalCode\": \"10021\"\n" +
            "     },\n" +
            "     \"phoneNumber\":\n" +
            "     [\n" +
            "         {\n" +
            "           \"type\": \"home\",\n" +
            "           \"number\": \"212 555-1234\"\n" +
            "         },\n" +
            "         {\n" +
            "           \"type\": \"fax\",\n" +
            "           \"number\": \"646 555-4567\"\n" +
            "         }\n" +
            "     ]\n" +
            " }\n";

    private static final String errorData = "Empty account number";


    @Override
    public void process(Exchange exchange) throws Exception {
        final String accountNumber = exchange.getIn().getHeader(ACCOUNT_NUMBER_PATH_VARIABLE).toString();
        if (StringUtils.isEmpty(accountNumber)) {
            exchange.getIn().setBody(simple(errorData));
        } else {
            exchange.getIn().setBody(String.format(STUB_DATA, accountNumber));
        }
    }
}
