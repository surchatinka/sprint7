import client.ScooterServiceClient;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.Track;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import java.util.List;

@RunWith(Parameterized.class)
public class MakeOrderTest {

    private final Order order;
    private ScooterServiceClient client;
    private Track track;

    public MakeOrderTest(List<String> colors){
         order = new Order("Name","Surname","Address","3","+23445678901",3,"2025-12-12"," ", colors);
    }

    @Before
    public void before(){
        client = new ScooterServiceClient();
    }

    @Test
    @DisplayName("Создание заказа")
    public void makeOrderTest_ok(){
        AllureLifecycle lifecycle = Allure.getLifecycle();
        lifecycle.updateTestCase(testResult -> testResult.setName("Создание заказа, самоката с цветом "+order.getColor().toString()));
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
