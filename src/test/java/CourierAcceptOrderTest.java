import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import model.Order;
import model.Track;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.apache.http.HttpStatus.*;

public class CourierAcceptOrderTest {

    private ScooterServiceClient client;
    private String courierId;
    private String orderId;
    private Track track;

    @Before
    public void before(){
        Faker faker = new Faker(new Locale("ru"));
        client = new ScooterServiceClient();
        Courier courier = new Courier(faker.letterify("????")+faker.number().digits(2),
                faker.number().digits(5),
                faker.name().fullName());
        client.createCourier(courier);
        ValidatableResponse loginResponse = client.login(Credentials.fromCourier(courier));
        courierId = client.getIdFromAnswerBody(loginResponse);
        Order order = new Order(faker.name().firstName(),
                faker.name().lastName(),
                faker.address().cityName(),
                faker.number().digits(2),
                faker.phoneNumber().phoneNumber(),
                faker.number().numberBetween(1,7),
                faker.date().future(30, TimeUnit.DAYS).toString(),
                faker.letterify("??????text??????"),
                new ArrayList<>());
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
        Assert.assertEquals(SC_OK,code);
        boolean ok = client.getOkFromAnswerBody(response);
        Assert.assertTrue(ok);
    }
    @Test
    @DisplayName("Непринятие заказа курьером, без id курьера")
    public void NoCourierIdAcceptOrderTest_fail(){
        ValidatableResponse response = client.acceptOrder("",orderId);
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_BAD_REQUEST,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Недостаточно данных для поиска",message);
    }
    @Test
    @DisplayName("Непринятие заказа курьером, с несуществующим курьером")
    public void WrongIdCourierAcceptOrderTest_fail(){
        client.deleteCourier(courierId);
        ValidatableResponse response = client.acceptOrder(courierId,orderId);
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_NOT_FOUND,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Курьера с таким id не существует",message);
    }
    @Test
    @DisplayName("Непринятие заказа курьером, без id заказа")
    @Issue("Ссылка на Баг репорт о неправильном ответе сервера")
    public void NoOrderIdAcceptOrderTest_fail(){
        ValidatableResponse response = client.acceptOrder(courierId,"");
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_BAD_REQUEST,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Недостаточно данных для поиска",message);
    }
    @Test
    @DisplayName("Непринятие заказа курьером, с неправильным номером заказа")
    public void WrongOrderNameAcceptOrderTest_fail(){
        Faker faker = new Faker();
        ValidatableResponse response = client.acceptOrder(courierId,faker.number().digits(7));
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_NOT_FOUND,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Заказа с таким id не существует",message);
    }

    @After
    public void after(){
        client.deleteCourier(courierId);
        client.cancelOrder(track);
    }
}

