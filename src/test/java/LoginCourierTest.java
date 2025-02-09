import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import org.junit.*;

public class LoginCourierTest {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private Courier courier;
    private String id;
    private ScooterServiceClient client;

    @Before
    public void before(){
        client = new ScooterServiceClient(BASE_URI);
        courier = new Courier("jackson", "password", "peter");
        client.createCourier(courier);
        ValidatableResponse responseOriginal = client.login(Credentials.fromCourier(courier));
        id = client.getIdFromAnswerBody(responseOriginal);
    }
    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Базовый тест на эндпоинт авторизации курьера, проверяет код ответа сервера")
    public void AuthorizationCourierTest_ok(){
        //Шаг 1
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        //Шаг 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(200,code);
    }
    @Test
    @DisplayName("Возврат id при успешной авторизация")
    public void AuthorizationReturnsIdCourierTest_ok(){
        //Шаг 1
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        //Шаг 2
        String id = client.getIdFromAnswerBody(response);
        Assert.assertNotEquals(0,id);
    }
    @Test
    @DisplayName("Неуспешная авторизация курьера с неправильным логином")
    public void AuthorizationMistakeInLoginCourierTest_fail(){
        courier.setLogin("ULTRANOGGEBATOR9000");
        //Шаг 1
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        //Шаг 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(404,code);
        //Шаг 3
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Учетная запись не найдена");
    }

    @Test
    @DisplayName("Неуспешная авторизация курьера с неправильным паролем")
    public void AuthorizationMistakeInPasswordCourierTest_fail(){
        courier.setPassword("ULTRANOGGEBATOR9000");
        //Шаг 1
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        //Шаг 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(404,code);
        //Шаг 3
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Учетная запись не найдена");
    }

    @Test
    @DisplayName("Неуспешная авторизация курьера с отсуствующим логином")
    public void AuthorizationNoLoginCourierTest_fail(){
        courier.setLogin(null);
        //Шаг 1
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        //Шаг 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(400,code);
        //Шаг 3
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Недостаточно данных для входа");
    }

    @Test
    @DisplayName("Неуспешная авторизация курьера с отсуствующим паролем")
    @Issue("Ссылка на баг, что ответ сервера 504 вместо 400")
    public void AuthorizationNoPasswordCourierTest_fail(){
        courier.setPassword(null);
        //Шаг 1
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        //Шаг 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(400,code);
        //Шаг 3
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Недостаточно данных для входа");
    }

    @Test
    @DisplayName("Неуспешная авторизация несуществующего курьера")
    public void AuthorizationNotExistingCourierTest_fail(){
        //Шаг 1
        client.deleteCourier(id);
        //Шаг 2
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        //Шаг 3
        int code = client.getStatusCode(response);
        Assert.assertEquals(404,code);
        //Шаг 4
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Учетная запись не найдена");
    }

    @After
    public void after() {
        client.deleteCourier(id);
    }
}
