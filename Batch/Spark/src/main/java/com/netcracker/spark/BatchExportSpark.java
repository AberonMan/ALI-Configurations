package com.netcracker.spark;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;

public class BatchExportSpark {

    private static final String STRING_FIELD_NAME = "str";
    private static final String BOOLEAN_FIELD_NAME = "bool";
    private static final String NUMERIC_FIELD_NAME = "num";

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().appName("Batch Import").getOrCreate();

        spark
                .read()
                .csv(
                        spark.read().jdbc(
                                "jdbc:oracle:thin:@db058.netcracker.com:1523/DBG115",
                                "(SELECT * FROM TEST_SPARK WHERE ROWNUM < 5) SPARK_TEST",
                                getOracleConnectionProperties()
                        ).as(Encoders.STRING())
                )
                .map((MapFunction<Row, String>) value ->
                                new ObjectMapper()
                                        .createObjectNode()
                                        .put(STRING_FIELD_NAME, value.getString(0))
                                        .put(BOOLEAN_FIELD_NAME, value.getString(1))
                                        .put(NUMERIC_FIELD_NAME, value.getString(2))
                                        .toString(),
                        Encoders.STRING()
                )
                .write()
                .mode(SaveMode.Append)
                .text("../spark-test-export");
    }

    private static Properties getOracleConnectionProperties() {
        Properties result = new Properties();
        result.put("user", "U214_D46_6810");
        result.put("password", "U214_D46_6810");

        return result;
    }
}
