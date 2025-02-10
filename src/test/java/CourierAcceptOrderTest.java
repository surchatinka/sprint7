import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import model.Order;
import model.Track;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class CourierAcceptOrderTest {

    private ScooterServiceClient client;
    private String courierId;
    private String orderId;
    private Track track;

    @Before
    public void before(){
        client = new ScooterServiceClient();
        Courier courier = new Courier("Usein", "usya", "Bolt");
        client.createCourier(courier);
        ValidatableResponse loginResponse = client.login(Credentials.fromCourier(courier));
        courierId = client.getIdFromAnswerBody(loginResponse);
        Order order = new Order("Namezz", "Surnamezz", "Addrezz", "33", "+23400000001", 5, "2025-12-12", " ", new ArrayList<>());
        ValidatableResponse createOrderResponse = client.createOrder(order);
        track = client.getTrackFromAnswerBody(createOrderResponse);
        ValidatableResponse getIdResponse = client.getOrder(track);
        Order orderWithId = client.getOrderFromAnswerBody(getIdResponse);
        orderId = orderWithId.getId();
    }

    @Test
    @DisplayName("Принятие заказа курьером")
    @Description("Базовый сценарий, курьер есть, заказ есть")
    public void CourierAcceptOrderTest_ok(){
        ValidatableResponse response = client.acceptOrder(courierId,orderId);
        int code = client.getStatusCode(response);
        Assert.assertEquals(200,code);
        boolean ok = client.getOkFromAnswerBody(response);
        Assert.assertTrue(ok);
    }
    @Test
    @DisplayName("Непринятие заказа курьером, без id курьера")
    public void NoCourierIdAcceptOrderTest_fail(){
        ValidatableResponse response = client.acceptOrder("",orderId);
        int code = client.getStatusCode(response);
        Assert.assertEquals(400,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Недостаточно данных для поиска",message);
    }
    @Test
    @DisplayName("Непринятие заказа курьером, с несуществующим курьером")
    public void WrongIdCourierAcceptOrderTest_fail(){
        client.deleteCourier(courierId);
        ValidatableResponse response = client.acceptOrder(courierId,orderId);
        int code = client.getStatusCode(response);
        Assert.assertEquals(404,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Курьера с таким id не существует",message);
    }
    @Test
    @DisplayName("Непринятие заказа курьером, без id заказа")
    @Issue("Ссылка на Баг репорт о неправильном ответе сервера")
    public void NoOrderIdAcceptOrderTest_fail(){
        ValidatableResponse response = client.acceptOrder(courierId,"");
        int code = client.getStatusCode(response);
        Assert.assertEquals(400,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Недостаточно данных для поиска",message);
    }
    @Test
    @DisplayName("Непринятие заказа курьером, с неправильным номером заказа")
    public void WrongOrderNameAcceptOrderTest_fail(){
        ValidatableResponse response = client.acceptOrder(courierId,orderId.substring(4));
        int code = client.getStatusCode(response);
        Assert.assertEquals(404,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Заказа с таким id не существует",message);
    }

    @After
    public void after(){
        client.deleteCourier(courierId);
        client.cancelOrder(track);
    }
}

