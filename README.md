// Please see the below Functional Test using Karate

Feature: Validate REST API for Loans Api

  Scenario: Test valid ID number and client age
    Given url 'http://localhost:8080/loans'
    And param idNumber = '9001011234082'
    When method GET
    Then status 200
    And match response.isValidId == true
    And match response.age >= 18

  Scenario: Validate name and surname (no special characters or digits)
    Given url 'http://localhost:8080/loans'
    And param name = 'John'
    And param surname = 'Doe'
    When method GET
    Then status 200
    And match response.hasSpecialCharacters == false
    And match response.hasDigits == false

  Scenario: Test valid bank and account number
    Given url 'http://localhost:8080/client/check'
    And param bank = 'Molewa Bank'
    And param accountNumber = '1234567890'
    When method GET
    Then status 200
    And match response.bank == 'Molewa Bank'
    And match response.warningMessage == 'refer to compliance'
    And match response.isAccountNumberValid == true



//   E2E Testing 


Feature('Login functionality');

Scenario('Successful login', ({ I }) => {
  I.amOnPage('https://etalente.co.za');
  I.fillField('username', 'valid_user');
  I.fillField('password', 'valid_password');
  I.click('Login');
  I.see('Welcome'); // Verify login success
});

Scenario('Failed login', ({ I }) => {
  I.amOnPage('https://etalente.co.za');
  I.fillField('username', 'invalid_user');
  I.fillField('password', 'wrong_password');
  I.click('Login');
  I.see('Invalid credentials'); // Verify failure message
});

// import { Selector } from 'testcafe';

fixture `Login Page Tests`
    .page `https://etalente.co.za`;

test('Successful login', async t => {
    await t
        .typeText('#username', 'valid_user')
        .typeText('#password', 'valid_password')
        .click('#loginButton')
        .expect(Selector('h1').innerText).eql('Welcome'); // Verify login success
});

test('Failed login', async t => {
    await t
        .typeText('#username', 'invalid_user')
        .typeText('#password', 'wrong_password')
        .click('#loginButton')
        .expect(Selector('.error-message').innerText).eql('Invalid credentials'); // Verify failure message
});



// Performance testing

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class LoadTestSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080") // replace with actual URL
    .acceptHeader("application/json")

  val scn = scenario("Loan API Load Test")
    .exec(
      http("Get Loan Info")
        .get("/loan/check")
        .queryParam("idNumber", "9001011234082")
        .check(status.is(200))
    )

  setUp(
    scn.inject(
      atOnceUsers(10), // Start with 10 users
      rampUsers(100).during(10.seconds) // Ramp up to 100 users in 10 seconds
    )
  ).protocols(httpProtocol)
}
