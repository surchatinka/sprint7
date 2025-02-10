import client.ScooterServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.Track;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GetOrderIdFromTrackTest {

    private ScooterServiceClient client;
    private Track track;

    @Before
    public void before(){
        client = new ScooterServiceClient();
        Faker faker = new Faker(new Locale("ru"));
        Order order = new Order(faker.name().firstName(),
                faker.name().lastName(),
                faker.address().cityName(),
                faker.number().digits(2),
                faker.phoneNumber().phoneNumber(),
                faker.number().numberBetween(1,7),
                faker.date().future(30, TimeUnit.DAYS).toString(),
                faker.letterify("??????text??????"),
                new ArrayList<>());
        ValidatableResponse responseCreate = client.createOrder(order);
        track = client.getTrackFromAnswerBody(responseCreate);
    }

    @Test
    @DisplayName("Получение заказа по его номеру")
    @Description("Базовый тест, трек не нулевой")
    public void GetOrderWithExistingTrackTest_ok(){
        ValidatableResponse response = client.getOrder(track);
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_OK,code);
        Order order = client.getOrderFromAnswerBody(response);
        Assert.assertNotNull(order);
    }
    @Test
    @DisplayName("Неполучение заказа без трек номера")
    public void GetOrderWithoutTrackTest_fail(){
        ValidatableResponse response = client.getOrder(new Track());
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_BAD_REQUEST,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Недостаточно данных для поиска",message);
    }
    @Test
    @DisplayName("Неполучение заказа с несуществующим заказом")
    public void GetOrderWithNoExistingTrackTest_fail(){
        Faker faker = new Faker(new Locale("ru"));
        ValidatableResponse response = client.getOrder(new Track((int)faker.number().randomNumber()));
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_NOT_FOUND,code);
        String message = client.getMessageFromAnswerBody(response);
        Assert.assertEquals("Заказ не найден",message);
    }

    @After
    public void after(){
        client.cancelOrder(track);
    }
}

