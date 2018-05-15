/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netcracker.camelbatchimport;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * A Camel route that calls the REST service using a timer
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class RestRoute extends RouteBuilder {

    private static final List<String> ENDPOINTS = Arrays.asList(
            "http4://randomuser.me/api/0.6/?results={count}&format=json&bridgeEndpoint=true",
            "http4://randomuser.me/api/0.6/?results={count}&format=SQL&bridgeEndpoint=true",
            "http4://randomuser.me/api/0.6/?results={count}&format=CSV&bridgeEndpoint=true"
    );

    @Override
    public void configure() {
        configureRest();

        rest().get("/invoke").to("direct:invoke");

        RouteDefinition fromTimer = from("direct:invoke")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"));

        for (String uri : ENDPOINTS) {
            fromTimer
                    .setHeader("count", simple("${random(1,3)}"))
                    .to(uri)
                    .to("direct:response");
        }

        from("direct:response")
                .setBody(simple("insert into test(data) values('${body}')"))
                .to("jdbc:dataSource");
    }

    private void configureRest() {
        restConfiguration()
                .contextPath("/camel").apiContextPath("/api-doc")
                .apiProperty("api.title", "Camel REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiContextRouteId("doc-api");
    }
}
