import client.ScooterServiceClient;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)
public class MakeOrderTest {

    private final Order order;
    private ScooterServiceClient client;
    private Track track;

    public MakeOrderTest(List<String> colors){
        Faker faker = new Faker(new Locale("ru"));
        order = new Order(faker.name().firstName(),
                faker.name().lastName(),
                faker.address().cityName(),
                faker.number().digits(2),
                faker.phoneNumber().phoneNumber(),
                faker.number().numberBetween(1,7),
                faker.date().future(30, TimeUnit.DAYS).toString(),
                faker.letterify("??????text??????"),
                new ArrayList<>());
}

    @Before
    public void before(){
        client = new ScooterServiceClient();
    }

    @Test
    @DisplayName("Создание заказа")
    public void makeOrderTest_ok(){
        ValidatableResponse response = client.createOrder(order);
        int code = client.getStatusCode(response);
        Assert.assertEquals(SC_CREATED,code);
        track = client.getTrackFromAnswerBody(response);
        Assert.assertNotNull(track);
    }

    @Parameterized.Parameters
    public static Object[][] getParameters(){
        List<String> checkOne  = new ArrayList<>();
        checkOne.add("BLACK");
        List<String> checkTwo = new ArrayList<>();
        checkTwo.add("BLACK");
        checkTwo.add("GREY");
        List<String> checkEmpty = new ArrayList<>();

        return new Object[][]{
                {checkOne},
                {checkTwo},
                {checkEmpty}
        };
    }

    @After
    public void after(){
        client.cancelOrder(track);
    }
}
