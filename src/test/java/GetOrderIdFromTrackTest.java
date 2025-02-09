import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.Track;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class GetOrderIdFromTrackTest {

    private ScooterServiceClient client;
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private Track track;

    @Before
    public void before(){
        client = new ScooterServiceClient(BASE_URI);
        Order order= new Order("Platypus","Teal","Cartoon","7","82341111111",1,"2025-11-11","comment",new ArrayList<>());
        ValidatableResponse responseCreate = client.createOrder(order);
        track = client.getTrackFromAnswerBody(responseCreate);

    }

    @Test
    @DisplayName("Получение заказа по его номеру")
    @Description("Базовый тест, трек не нулевой")
    public void GetOrderWithExistingTrack_ok(){
        ValidatableResponse response = client.getOrder(track);
        int code = client.getStatusCode(response);
        Assert.assertEquals(200,code);
        Order order = client.getOrderFromAnswerBody(response);
        Assert.assertNotNull(order);
    }

    @Test
    @DisplayName("Неполучение заказа без трек номера")
    public void GetOrderWithoutTrack_fail(){
        ValidatableResponse response = client.getOrder(new Track());
        int code = client.getStatusCode(response);
        Assert.assertEquals(400,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Недостаточно данных для поиска",message);
    }

    @Test
    @DisplayName("Неполучение заказа с несуществующим заказом")
    public void GetOrderWithNoExistingTrack_fail(){
        ValidatableResponse response = client.getOrder(new Track(77777777));
        int code = client.getStatusCode(response);
        Assert.assertEquals(404,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Заказ не найден",message);
    }

    @After
    public void after(){
        client.cancelOrder(track);
    }
}

