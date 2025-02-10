import client.ScooterServiceClient;
import io.qameta.allure.Description;
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
import java.util.List;
import static org.apache.http.HttpStatus.*;

public class GetOrderListTest {

    private ScooterServiceClient client;
    private String idCourier;
    private Track track;

    @Before
    public void before(){
        client = new ScooterServiceClient();
        Courier courier = new Courier("j4k4l","courier","COURIER");
        client.createCourier(courier);
        ValidatableResponse responseLogin = client.login(Credentials.fromCourier(courier));
        idCourier = client.getIdFromAnswerBody(responseLogin);
        Order order1= new Order("Some","Person","Hawaii","5","82345678901",1,"2025-10-10","buratto",new ArrayList<>());
        ValidatableResponse responseTrack = client.createOrder(order1);
        track = client.getTrackFromAnswerBody(responseTrack);
        ValidatableResponse responseId = client.getOrder(track);
        String orderId = client.getOrderFromAnswerBody(responseId).getId();
        client.acceptOrder(idCourier,orderId);
    }
    @Test
    @DisplayName("Список заказов не пустой")
    @Description("После создания курьера, заказов и принятии заказа курьером в работу.")
    public void checkResponseContainsOrdersList_ok(){
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
