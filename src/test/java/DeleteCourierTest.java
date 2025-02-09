import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeleteCourierTest {

    private ScooterServiceClient client;
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private String id;
    //private Courier courier;

    @Before
    public void before(){
        client = new ScooterServiceClient(BASE_URI);
        Courier courier = new Courier("vasya90210","vasya","pupkin");
        client.createCourier(courier);
        ValidatableResponse response = client.login(Credentials.fromCourier(courier));
        id = client.getIdFromAnswerBody(response);
    }

    @Test
    @DisplayName("Удаление существующего курьера")
    @Description("Базовый тест, на удаление существующего курьера")
    public void deleteCourierTest_ok(){
        //Step 1
        ValidatableResponse response = client.deleteCourier(id);
        //Step 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(200,code);
        //Step 3
        boolean ok = client.getOkFromAnswerBody(response);
        Assert.assertTrue(ok);
    }

    @Test
    @DisplayName("Отправка запроса на удаление без id")
    @Issue("Ссылка на багрепорт о неверном коде ошибки")
    public void deleteNoIdCourierTest_fail(){
        //Step 1
        ValidatableResponse response = client.deleteCourier("");
        //Step 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(400,code);
        //Step 3
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Недостаточно данных для удаления курьера",message);

        client.deleteCourier(id);
    }

    @Test
    @DisplayName("Отправка запроса на удаление без id")
    @Issue("Ссылка на баг репорт о неверном теле ответа")
    public void deleteNotExistingCourierTest_fail(){
        client.deleteCourier(id);
        //Step 1
        ValidatableResponse response = client.deleteCourier("");
        //Step 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(404,code);
        //Step 3
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Курьера с таким id нет",message);
    }
}
