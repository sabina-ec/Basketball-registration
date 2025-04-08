package steps;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserRegistrationStepdefs {

    private WebDriver driver;
    private WebDriverWait wait;

    // Genererar en fejkad e-postadress med tidsstämpel
    private String generateFakeEmail() {
        long timestamp = System.currentTimeMillis();
        return "sabina" + timestamp + "@example.com";
    }

    // Stänger webbläsaren efter varje scenario
    @After
    public void tearDown() {
        delay(2);
        if (driver != null) {
            driver.quit();
        }
    }
    // Öppnar registreringssidan i vald webbläsare (Chrome eller Firefox)
    @Given("I am on the registration page using browser {string}")
    public void iAmOnTheRegistrationPageUsingBrowser(String browser) {
        if (browser.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        } else {
            driver = new ChromeDriver();
        }
        driver.get("https://membership.basketballengland.co.uk/NewSupporterAccount/");
    }
    // Fyller i alla fält med giltiga värden
    @When("I fill all inputs with valid data")
    public void iFillAllInputsWithValidData() {
        String fakeEmail = generateFakeEmail();
        driver.findElement(By.id("dp")).sendKeys("01/06/2000");
        driver.findElement(By.id("member_firstname")).sendKeys("Sabina");
        driver.findElement(By.id("member_lastname")).sendKeys("Civgin");
        driver.findElement(By.id("member_emailaddress")).sendKeys(fakeEmail);
        driver.findElement(By.id("member_confirmemailaddress")).sendKeys(fakeEmail);
        driver.findElement(By.id("signupunlicenced_password")).sendKeys("Test1234!");
        driver.findElement(By.id("signupunlicenced_confirmpassword")).sendKeys("Test1234!");
    }
    // Accepterar villkor genom att klicka i checkboxar
    @And("I accept the terms and conditions")
    public void iAcceptTheTermsAndConditions() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); //Wait until the label for 'Terms and Conditions' is clickable and click it
        waitForCheckboxAndClick("sign_up_25");
        waitForCheckboxAndClick("sign_up_26");
        waitForCheckboxAndClick("fanmembersignup_agreetocodeofethicsandconduct");
    }
    // Klickar på knappen "Join" för att skicka formuläret
    @And("I submit the form")
    public void iSubmitTheForm() {
        driver.findElement(By.cssSelector("input[name='join']")).click();
        delay(1);
    }
    // Bekräftar att registreringen lyckades
    @Then("Account should be created successfully")
    public void accountShouldBeCreatedSuccessfully() {
        boolean success = driver.getPageSource().contains("THANK YOU FOR CREATING AN ACCOUNT WITH BASKETBALL ENGLAND");
        assertTrue(success, "Account creation failed");
    }

    // Fyller i data utan efternamn
    @When("I fill in valid data without last name")
    public void iFillInValidDataWithoutLastName() {
        driver.findElement(By.id("dp")).sendKeys("01/01/2000");
        driver.findElement(By.id("member_firstname")).sendKeys("Sabina");
        //WE WRITE WITHOUT LAST NAME
        driver.findElement(By.id("member_emailaddress")).sendKeys("sabina.test@example.com");
        driver.findElement(By.id("member_confirmemailaddress")).sendKeys("sabina.test@example.com");
        driver.findElement(By.id("signupunlicenced_password")).sendKeys("Test1234!");
        driver.findElement(By.id("signupunlicenced_confirmpassword")).sendKeys("Test1234!");
    }
    // Verifierar felmeddelande för saknat efternamn
    @Then("An error message for missing last name should be displayed")
    public void anErrorMessageForMissingLastNameShouldBeDisplayed() {
        //Check if the error message is displayed
        WebElement error = driver.findElement(By.cssSelector("span[data-valmsg-for='Surname']"));
        assertTrue(error.isDisplayed(), "Expected error message to be displayed for missing last name.");
        // Additional check to ensure the message text is correct
        String expectedMessage = "Last Name is required";
        String actualMessage = error.getText().trim();
        assertEquals(expectedMessage, actualMessage, "Error message text did not match.");
    }

    // Fyller i data med olika lösenord
    @When("I fill in valid data with mismatched passwords")
    public void iFillInValidDataWithMismatchedPasswords() {
        driver.findElement(By.id("dp")).sendKeys("01/01/2000");
        driver.findElement(By.id("member_firstname")).sendKeys("Sabina");
        driver.findElement(By.id("member_lastname")).sendKeys("Civgin");
        driver.findElement(By.id("member_emailaddress")).sendKeys("sabina.test@example.com");
        driver.findElement(By.id("member_confirmemailaddress")).sendKeys("sabina.test@example.com");
        driver.findElement(By.id("signupunlicenced_password")).sendKeys("Test1234!");
        driver.findElement(By.id("signupunlicenced_confirmpassword")).sendKeys("WrongPass");

    }
    // Verifierar felmeddelande vid olika lösenord
    @Then("An error message for mismatched passwords should be displayed")
    public void anErrorMessageForMismatchedPasswordsShouldBeDisplayed() {

        WebElement error = driver.findElement(By.cssSelector("span[for='signupunlicenced_confirmpassword']"));
        String errorMessage = error.getText();
        Assertions.assertEquals("Password did not match", errorMessage, "Expected error message not shown for mismatched passwords.");
    }
    // Checkboxar markeras inte medvetet
    @And("I do not accept the terms and conditions")
    public void iDoNotAcceptTheTermsAndConditions() {

    }
    // Verifierar felmeddelande om villkor inte accepterats
    @Then("An error message for terms and conditions should be displayed")
    public void anErrorMessageForTermsAndConditionsShouldBeDisplayed() {
        if (wait == null) {
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        }
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@data-valmsg-for='TermsAccept']")
        ));
        assertTrue(error.isDisplayed(), "Expected error message for not accepting Terms and Conditions.");
    }

    // Fyller i formulärfält med värden från Scenario Outline
    @When("I fill in the registration form with details: {string}, {string}, {string}, {string}, {string}, {string}")
    public void iFillInTheRegistrationFormWithDetails(String firstname, String lastname, String email, String confirmEmail, String password, String confirmPassword) {

        driver.findElement(By.id("dp")).sendKeys("01/01/2000");
        if (!firstname.isEmpty())
            driver.findElement(By.id("member_firstname")).sendKeys(firstname);

        if (!lastname.isEmpty())
            driver.findElement(By.id("member_lastname")).sendKeys(lastname);

        driver.findElement(By.id("member_emailaddress")).sendKeys(email);
        driver.findElement(By.id("member_confirmemailaddress")).sendKeys(confirmEmail);
        driver.findElement(By.id("signupunlicenced_password")).sendKeys(password);
        driver.findElement(By.id("signupunlicenced_confirmpassword")).sendKeys(confirmPassword);
    }
    // Kontrollerar att förväntat felmeddelande visas på sidan
    @Then("{string} should be shown")
    public void shouldBeShown(String expectedMessage) {
        boolean found = driver.getPageSource().contains(expectedMessage);
        assertTrue(found, "Expected message not found: " + expectedMessage);
    }

    // Paus i sekunder (används för att se steg tydligare under test)
    private void delay(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // Väntar tills checkboxens label är klickbar, klickar och verifierar att den markerats
    private void waitForCheckboxAndClick(String labelForId) {
        By labelLocator = By.cssSelector("label[for='" + labelForId + "']");
        wait.until(ExpectedConditions.presenceOfElementLocated(labelLocator));
        wait.until(ExpectedConditions.elementToBeClickable(labelLocator));

        // Klicka på labeln (som representerar checkboxen)
        WebElement label = driver.findElement(labelLocator);
        delay(1); // Kort paus för att undvika att klicket missas
        label.click();

        // Kontrollera att checkboxen blev markerad
        WebElement checkbox = driver.findElement(By.id(labelForId));
        if (!checkbox.isSelected()) {
            delay(1); // Vänta lite till innan ett nytt klick
            label.click(); // Försök klicka igen om det behövs
        }

    }


}









