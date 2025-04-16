import client.ScooterServiceClient;
import io.qameta.allure.Description;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.apache.http.HttpStatus.*;

public class GetOrderListTest {

    private ScooterServiceClient client;
    private String idCourier;
    private Track track;

    @Before
    public void before(){
        Faker faker = new Faker(new Locale("ru"));
        client = new ScooterServiceClient();
        Courier courier = new Courier(faker.letterify("????")+faker.number().digits(2),
                faker.number().digits(5),
                faker.name().fullName());
        client.createCourier(courier);
        ValidatableResponse responseLogin = client.login(Credentials.fromCourier(courier));
        idCourier = client.getIdFromAnswerBody(responseLogin);
        Order order1 = new Order(faker.name().firstName(),
                faker.name().lastName(),
                faker.address().cityName(),
                faker.number().digits(2),
                faker.phoneNumber().phoneNumber(),
                faker.number().numberBetween(1,7),
                faker.date().future(30, TimeUnit.DAYS).toString(),
                faker.letterify("??????text??????"),
                new ArrayList<>());
        ValidatableResponse responseTrack = client.createOrder(order1);
        track = client.getTrackFromAnswerBody(responseTrack);
        ValidatableResponse responseId = client.getOrder(track);
        String orderId = client.getOrderFromAnswerBody(responseId).getId();
        client.acceptOrder(idCourier,orderId);
    }
    @Test
    @DisplayName("Список заказов не пустой")
    @Description("После создания курьера, заказов и принятии заказа курьером в работу.")
    public void checkResponseContainsOrdersListTest_ok(){
        ValidatableResponse response = client.getOrders(idCourier);
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_OK,code);
        List<Order> list = client.getOrderListFromAnswerBody(response);
        Assert.assertNotNull(list);
    }

    @After
    public void after(){
        client.deleteCourier(idCourier);
        client.cancelOrder(track);
    }
}
