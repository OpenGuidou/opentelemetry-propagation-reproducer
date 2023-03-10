package com.quarkus.reproducer;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class GreetingResourceTest {

    /**
     * Test not working with custom executor
     */
    @Test
    public void testHelloEndpointWithCustomExecutor() {
        String body = given()
          .when().get("/hello")
          .then()
             .statusCode(200)
            .extract().response().getBody().print();

        assertTrue(body.startsWith("Hello "));
        String[] traces = body.split(" ")[1].split("-");
        assertEquals(traces[0], traces[1]);
        assertEquals(traces[1], traces[2]);
    }

    /**
     * Test working with ManagedExecutor
     */
    @Test
    public void testHelloEndpointWithManagedExecutor() {
        String body = given()
            .when().get("/hola")
            .then()
            .statusCode(200)
            .extract().response().getBody().print();

        assertTrue(body.startsWith("Hello "));
        String[] traces = body.split(" ")[1].split("-");
        assertEquals(traces[0], traces[1]);
        assertEquals(traces[1], traces[2]);
    }

}