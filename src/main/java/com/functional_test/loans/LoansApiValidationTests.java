package com.functional_test.loans;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoansApiValidationTests {


    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080"; // Update with actual base URL
    }


    // Test Case 1: Valid ID Number, Age >= 18, Valid Name, and Bank

    @Test
    public void applyForLoanValidData() {
        String requestBody = "{\n" +
                "  \"idNumber\": \"9001015009087\", \n" + // Valid SA ID Number
                "  \"name\": \"John\", \n" +
                "  \"surname\": \"Doe\", \n" +
                "  \"bank\": \"Scrum Bank\", \n" +
                "  \"accountNumber\": \"1234567890\"\n" +  // Valid account number
                "}";

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/loans")
        .then()
            .statusCode(200)
            .body("valid", equalTo(true))
            .body("loanStatus", equalTo("APPROVED"))
            .body("message", equalTo("Loan application successful"))
            .body("age", greaterThanOrEqualTo(18));
    }

    // Test Case 2: Invalid Name with Special Characters
    @Test
    public void applyForLoanInvalidName() {
        String requestBody = "{\n" +
                "  \"idNumber\": \"9001015009087\", \n" + // Valid SA ID Number
                "  \"name\": \"John@\",\n" + // Invalid name with special character
                "  \"surname\": \"Doe1\",\n" + // Invalid surname with digit
                "  \"bank\": \"Iconic Bank\", \n" +
                "  \"accountNumber\": \"1234567890\"\n" +
                "}";

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/loans")
        .then()
            .statusCode(400)
            .body("valid", equalTo(false))
            .body("message", containsString("Invalid characters in name or surname"));
    }

    // Test Case 3: Molewa Bank Warning
    @Test
    public void applyForLoanWithMolewaBank() {
        String requestBody = "{\n" +
                "  \"idNumber\": \"9001015009087\", \n" + // Valid SA ID Number
                "  \"name\": \"John\", \n" +
                "  \"surname\": \"Doe\", \n" +
                "  \"bank\": \"Molewa Bank\", \n" +
                "  \"accountNumber\": \"1234567890\"\n" +  // Valid account number
                "}";

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/loans")
        .then()
            .statusCode(200)
            .body("valid", equalTo(true))
            .body("message", equalTo("refer to compliance"));
    }

    // Test Case 4: Invalid Bank Account Number (less than 10 digits)
    @Test
    public void applyForLoanInvalidAccountNumber() {
        String requestBody = "{\n" +
                "  \"idNumber\": \"9001015009087\", \n" + // Valid SA ID Number
                "  \"name\": \"John\", \n" +
                "  \"surname\": \"Doe\", \n" +
                "  \"bank\": \"Scrum Bank\", \n" +
                "  \"accountNumber\": \"12345\"\n" + // Invalid account number
                "}";

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/loans")
        .then()
            .statusCode(400)
            .body("valid", equalTo(false))
            .body("message", containsString("Invalid account number, must be 10 digits"));
    }

    // Test Case 5: Invalid ID Number (less than 13 digits)
    @Test
    public void applyForLoanInvalidID() {
        String requestBody = "{\n" +
                "  \"idNumber\": \"90010150\",\n" + // Invalid SA ID number (less than 13 digits)
                "  \"name\": \"John\",\n" +
                "  \"surname\": \"Doe\",\n" +
                "  \"bank\": \"Scrum Bank\",\n" +
                "  \"accountNumber\": \"1234567890\"\n" +
                "}";

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/loans")
        .then()
            .statusCode(400)
            .body("valid", equalTo(false))
            .body("message", containsString("Invalid ID number"));
    }
}
