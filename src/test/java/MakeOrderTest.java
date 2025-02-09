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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

@RunWith(Parameterized.class)
public class MakeOrderTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private final Order order;
    private ScooterServiceClient client;
    private Track track;

    public MakeOrderTest(List<String> colors){
         order = new Order("Name","Surname","Address","3","+23445678901",3,"2025-12-12"," ", colors);
    }

    @Before
    public void before(){
        client = new ScooterServiceClient(BASE_URI);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Базовый тест, если цвет самоката - %1")
    public void makeOrderTest_ok(){
        //Шаг 1
        ValidatableResponse response = client.createOrder(order);
        //Шаг 2
        int code = client.getStatusCode(response);
        Assert.assertEquals(201,code);
        //Шаг 3
        track = client.getTrackFromAnswerBody(response);

    }
    @Test
    @DisplayName("В теле ответа при создании заказа возвращается трек номер, если цвет самоката - %1")
    public void makeOrderTestReturnsTrackNumber_ok(){
        //Шаг 1
        ValidatableResponse response = client.createOrder(order);
        //Шаг 2
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
