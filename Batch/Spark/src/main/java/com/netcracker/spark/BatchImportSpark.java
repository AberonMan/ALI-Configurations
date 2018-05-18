package com.netcracker.spark;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BatchImportSpark {

    private static final List<String> ENDPOINTS = Arrays.asList(
            "http://localhost:8081/get/json"
    );

    public static void main(String[] args) throws IOException {
        SparkSession spark = SparkSession.builder().appName("Batch Import").getOrCreate();

        spark
                .read()
                .json(spark.createDataset(requestDataFromExternalResources(), Encoders.STRING()))
                //Any function for converting JSON to CSV format
                .map(
                        (MapFunction<Row, String>) value -> "str,bool,num\n" +
                                value.get(0) + "," +
                                value.get(1) + "," +
                                value.get(2),
                        Encoders.STRING())
                .write()
                .format("jdbc")
                .option("url", "jdbc:oracle:thin:@db058.netcracker.com:1523/DBG115")
                .option("driver", "oracle.jdbc.OracleDriver")
                .option("user", "U214_D46_6810")
                .option("password", "U214_D46_6810")
                .option("dbtable", "TEST_SPARK")
                .mode(SaveMode.Append)
                .save();

        spark.stop();
    }

    private static List<String> requestDataFromExternalResources() throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        List<String> result = new ArrayList<>();

        for (String uri : BatchImportSpark.ENDPOINTS) {
            CloseableHttpResponse response = client.execute(new HttpGet(uri));
            result.add(extractBody(response.getEntity().getContent()));
        }

        return result;
    }

    private static String extractBody(InputStream input) {
        Scanner scanner = new Scanner(input);
        StringBuilder result = new StringBuilder();

        while (scanner.hasNext()) {
            result.append(scanner.nextLine());
        }

        return result.toString();
    }
}
