import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import net.datafaker.Faker;
import org.junit.*;

import java.util.Locale;

import static org.apache.http.HttpStatus.*;

public class CreateCourierTest {
    private Courier courier;
    private int statusCode;
    private ScooterServiceClient client;

    @Before
    public void before(){
        client = new ScooterServiceClient();
        Faker faker = new Faker(new Locale("ru"));
        courier = new Courier(faker.letterify("????")+faker.number().digits(2),
                faker.number().digits(5),
                faker.name().fullName());
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Базовая проверка на работоспособность")
    public void createCourierTest_ok() {
        ValidatableResponse response = client.createCourier(courier);
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(SC_CREATED, statusCode);
        boolean ok = client.getOkFromAnswerBody(response);
        Assert.assertTrue(ok);
  }
    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Issue("Ссылкаа на БР про баг в тексте ответа")
    public void createTwoSameCourierTest_fail() {
        client.createCourier(courier);
        ValidatableResponse response = client.createCourier(courier);
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(SC_CONFLICT, statusCode);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Этот логин уже используется");
    }
    @Test
    @DisplayName("Создание курьера без login")
    public void createNoNameCourierTest_fail() {
        courier.setLogin(null);
        ValidatableResponse response = client.createCourier(courier);
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(SC_BAD_REQUEST, statusCode);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Недостаточно данных для создания учетной записи");
    }
    @Test
    @DisplayName("Создание курьера без password")
    public void createNoPasswordCourierTest_fail() {
        courier.setPassword(null);
        ValidatableResponse response = client.createCourier(courier);
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(SC_BAD_REQUEST, statusCode);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Недостаточно данных для создания учетной записи");
    }
    @Test
    @DisplayName("Создание курьера без firstName")
    public void createNoFirstNameCourierTest_ok() {
        courier.setFirstName(null);
        ValidatableResponse response = client.createCourier(courier);
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(SC_CREATED, statusCode);
        boolean ok = client.getOkFromAnswerBody(response);
        Assert.assertTrue(ok);
    }

    @After
    public void after() {
        if (statusCode!=400) {
            ValidatableResponse loginResponse = client.login(Credentials.fromCourier(courier));
            String id = loginResponse.extract().jsonPath().getString("id");
            client.deleteCourier(id);
        }
    }
}
