import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import org.junit.*;

public class CreateCourierTest {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private Courier courier;
    private int statusCode;
    private ScooterServiceClient client;

    @Before
    public void before(){
        client = new ScooterServiceClient(BASE_URI);
        courier = new Courier("jackson", "password", "peter");
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Базовая проверка на работоспособность")
    public void createCourier_ok() {
        //Шаг 1
        ValidatableResponse response = client.createCourier(courier);
        //Шаг 2
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(201, statusCode);
        //Шаг 3
        boolean ok = client.getOkFromAnswerBody(response);
        Assert.assertTrue(ok);
  }
    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Issue("Ссылкаа на БР про баг в тексте ответа")
    public void createTwoSameCourier_fail() {
        //Шаг 1
        client.createCourier(courier);
        //Шаг 2
        ValidatableResponse response = client.createCourier(courier);
        //Шаг 3
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(409, statusCode);
        //Шаг 4
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Этот логин уже используется");
    }
    @Test
    @DisplayName("Создание курьера без login")
    public void createNoNameCourier_fail() {
        //Шаг 1
        courier.setLogin(null);
        ValidatableResponse response = client.createCourier(courier);
        //Шаг 2
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(400, statusCode);
        //Шаг 3
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Недостаточно данных для создания учетной записи");
    }
    @Test
    @DisplayName("Создание курьера без password")
    public void createNoPasswordCourier_fail() {
        //Шаг 1
        courier.setPassword(null);
        ValidatableResponse response = client.createCourier(courier);
        //Шаг 2
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(400, statusCode);
        //Шаг 3
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Недостаточно данных для создания учетной записи");
    }
    @Test
    @DisplayName("Создание курьера без firstName")
    public void createNoFirstNameCourier_ok() {
        //Шаг 1
        courier.setFirstName(null);
        ValidatableResponse response = client.createCourier(courier);
        //Шаг 2
        statusCode = client.getStatusCode(response);
        Assert.assertEquals(201, statusCode);
        //Шаг 3
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
