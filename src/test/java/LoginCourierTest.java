import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import org.junit.*;
import static org.apache.http.HttpStatus.*;

public class LoginCourierTest {
    private Courier courier;
    private String id;
    private ScooterServiceClient client;

    @Before
    public void before(){
        client = new ScooterServiceClient();
        courier = new Courier("jackson", "password", "peter");
        client.createCourier(courier);
        ValidatableResponse responseOriginal = client.login(Credentials.fromCourier(courier));
        id = client.getIdFromAnswerBody(responseOriginal);
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Базовый тест на эндпоинт авторизации курьера")
    public void AuthorizationCourierTest_ok(){
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_OK,code);
        String id = client.getIdFromAnswerBody(response);
        Assert.assertNotEquals("0",id);
    }
    @Test
    @DisplayName("Неуспешная авторизация курьера с неправильным логином")
    public void AuthorizationMistakeInLoginCourierTest_fail(){
        courier.setLogin("ULTRANOGGEBATOR9000");
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_NOT_FOUND,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Учетная запись не найдена");
    }
    @Test
    @DisplayName("Неуспешная авторизация курьера с неправильным паролем")
    public void AuthorizationMistakeInPasswordCourierTest_fail(){
        courier.setPassword("ULTRANOGGEBATOR9000");
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_NOT_FOUND,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Учетная запись не найдена");
    }
    @Test
    @DisplayName("Неуспешная авторизация курьера с отсуствующим логином")
    public void AuthorizationNoLoginCourierTest_fail(){
        courier.setLogin(null);
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_BAD_REQUEST,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Недостаточно данных для входа");
    }
    @Test
    @DisplayName("Неуспешная авторизация курьера с отсуствующим паролем")
    @Issue("Ссылка на баг, что ответ сервера 504 вместо 400")
    public void AuthorizationNoPasswordCourierTest_fail(){
        courier.setPassword(null);
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_BAD_REQUEST,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Недостаточно данных для входа");
    }
    @Test
    @DisplayName("Неуспешная авторизация несуществующего курьера")
    public void AuthorizationNotExistingCourierTest_fail(){
        client.deleteCourier(id);
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_NOT_FOUND,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals(message,"Учетная запись не найдена");
    }

    @After
    public void after() {
        client.deleteCourier(id);
    }
}
