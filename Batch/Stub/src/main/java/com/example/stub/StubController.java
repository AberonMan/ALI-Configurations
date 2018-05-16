package com.example.stub;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StubController {

    @GetMapping("get/json")
    public String getJson() {
        return "{\n" +
                "\t\"string\": \"yes\",\n" +
                "\t\"boolean\": true,\n" +
                "\t\"number\": 1\n" +
                "}";
    }

    @GetMapping("get/csv")
    public String getCsv() {
        return "is,it,csv\n" +
                "yes,yes,yes";
    }
}
