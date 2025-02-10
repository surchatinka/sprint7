package client;

import static io.restassured.RestAssured.given;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import model.Order;
import model.Track;
import java.util.List;

public class ScooterServiceClient {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    public static final String CREATE_COURIER_ENDPOINT = "/api/v1/courier";
    public static final String LOGIN_COURIER_ENDPOINT = "/api/v1/courier/login";
    public static final String DELETE_COURIER_ENDPOINT = "/api/v1/courier/%s";
    public static final String CANCEL_ORDER_ENDPOINT = "/api/v1/orders/cancel";
    public static final String GET_LIST_OF_ORDERS_ENDPOINT = "/api/v1/orders";
    public static final String ACCEPT_ORDER_ENDPOINT = "/api/v1/orders/accept/%s";
    public static final String GET_ORDER_ENDPOINT = "/api/v1/orders/track";

    public ScooterServiceClient() {}

    @Step("Клиент – создание курьера")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(CREATE_COURIER_ENDPOINT)
                .body(courier)
                .post()
                .then()
                .log()
                .all();
    }
    @Step("Клиент – логин курьера")
    public ValidatableResponse login(Credentials credentials) {
        return given()
                .log()
                .all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(LOGIN_COURIER_ENDPOINT)
                .body(credentials.toString())
                .post()
                .then()
                .log()
                .all();
    }
    @Step("Клиент – удаление курьера")
    public ValidatableResponse deleteCourier(String id){
            return given()
                    .filter(new AllureRestAssured())
                    .log()
                    .all()
                    .baseUri(BASE_URI)
                    .contentType(ContentType.JSON)
                    .basePath(String.format(DELETE_COURIER_ENDPOINT,id))
                    .delete()
                    .then()
                    .log()
                    .all();
    }
    @Step("Клиент - создание заказа")
    public ValidatableResponse createOrder(Order order){
        return given()
                .log()
                .all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath("/api/v1/orders")
                .body(order)
                .post()
                .then()
                .log()
                .all();
    }
    @Step("Клиент - отмена заказа")
    public void cancelOrder(Track track){
        given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(CANCEL_ORDER_ENDPOINT)
                .body(track)
                .put()
                .then()
                .log()
                .all();
    }
    @Step("Клиент - получение заказа")
    public ValidatableResponse getOrders(String courierId) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(GET_LIST_OF_ORDERS_ENDPOINT)
                .queryParam("courierId",courierId)
                .get()
                .then()
                .log()
                .all();
    }
    @Step("Клиент - резирвирование заказа курьером")
    public ValidatableResponse acceptOrder(String courierId,String orderId){
        return given()
                .log()
                .all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(String.format(ACCEPT_ORDER_ENDPOINT,orderId))
                .queryParam("courierId",courierId)
                .put()
                .then()
                .log()
                .all();
    }
    @Step("Клиент - получение id заказа")
    public ValidatableResponse getOrder(Track track){
        return given()
                .log()
                .all()
                .baseUri(BASE_URI)
                .basePath(GET_ORDER_ENDPOINT)
                .queryParam("t",track.getTrack())
                .get()
                .then()
                .log()
                .all();
    }
    @Step("Клиент - проверка кода ответа сервера")
    public int getStatusCode(ValidatableResponse response){
        return response.extract().statusCode();
    }
    @Step("Клиент - проверка тела ответа")
    public Track getTrackFromAnswerBody(ValidatableResponse response){
        return new Track(response.extract().jsonPath().getInt("track"));
    }
    @Step("Клиент - проверка тела ответа")
    public boolean getOkFromAnswerBody(ValidatableResponse response){
        return response.extract().jsonPath().getBoolean("ok");
    }
    @Step("Клиент - проверка тела ответа")
    public String getMessageFromAnswerBody(ValidatableResponse response){
        return response.extract().jsonPath().getString("message");
    }
    @Step("Клиент - проверка тела ответа")
    public List<Order> getOrderListFromAnswerBody(ValidatableResponse response){
        return response.extract().jsonPath().getList("orders");
    }
    @Step("Клиент - проверка тела ответа")
    public Order getOrderFromAnswerBody(ValidatableResponse response){
        return response.extract().jsonPath().getObject("order", Order.class);
    }
    @Step("Клиент - проверка тела ответа")
    public String getIdFromAnswerBody(ValidatableResponse response){
        return response.extract().jsonPath().get("id").toString();
    }
}

