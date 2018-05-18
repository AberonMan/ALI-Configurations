package com.netcracker.camelbatch;

import org.apache.camel.builder.RouteBuilder;

public abstract class AbstractBatchRoute extends RouteBuilder {

    @Override
    public void configure() {
        configureRest();
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
