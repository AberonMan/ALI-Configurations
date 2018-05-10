package com.mash.dip.http;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import static com.mash.dip.http.RestFlowConfiguration.ACCOUNT_NUMBER_PATH_VARIABLE;
import static org.apache.camel.builder.SimpleBuilder.simple;

public class CustomerRepositoryProcessor implements Processor {


    private static final String STUB_DATA = "" +
            "{" +
            "\"accountNumber\": \"%s\"," +
            "     \"firstName\": \"John\"," +
            "     \"lastName\": \"Smith\"," +
            "     \"age\": 25," +
            "     \"address\":" +
            "     {" +
            "         \"streetAddress\": \"21 2nd Street\"," +
            "         \"city\": \"New York\"," +
            "         \"state\": \"NY\"," +
            "         \"postalCode\": \"10021\"" +
            "     }," +
            "     \"phoneNumber\":" +
            "     [" +
            "         {" +
            "           \"type\": \"home\"," +
            "           \"number\": \"212 555-1234\"" +
            "         }," +
            "         {" +
            "           \"type\": \"fax\"," +
            "           \"number\": \"646 555-4567\"" +
            "         }" +
            "     ]" +
            " }";

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
