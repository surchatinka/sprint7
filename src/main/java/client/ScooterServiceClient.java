package client;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import model.Order;
import model.Track;

import java.util.List;

public class ScooterServiceClient {

    private final String baseURI;

    public ScooterServiceClient(String baseURI) {
        this.baseURI = baseURI;
    }

    @Step("Клиент – создание курьера")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .log()
                .all();
    }
    @Step("Клиент – логин курьера")
    public ValidatableResponse login(Credentials credentials) {
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .body(credentials.toString())
                .post("/api/v1/courier/login")
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
                    .baseUri(baseURI)
                    .header("Content-Type", "application/json")
                    .delete(String.format("/api/v1/courier/%s",id)) //:
                    .then()
                    .log()
                    .all();
    }
    @Step("Клиент - создание заказа")
    public ValidatableResponse createOrder(Order order){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type","application/json")
                .body(order)
                .post("/api/v1/orders")
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
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .body(track)
                .put("/api/v1/orders/cancel")
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
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .queryParam("courierId",courierId)
                .get("/api/v1/orders")
                .then()
                .log()
                .all();

    }
    @Step("Клиент - резирвирование заказа курьером")
    public ValidatableResponse acceptOrder(String courierId,String orderId){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type","application/json")
                .queryParam("courierId",courierId)
                .put(String.format("/api/v1/orders/accept/%s",orderId))
                .then()
                .log()
                .all();
    }
    @Step("Клиент - получение id заказа")
    public ValidatableResponse getOrder(Track track){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                //.header("Content-Type","application/json")
                .queryParam("t",track.getTrack())
                .get("/api/v1/orders/track")
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
    @JsonIgnoreProperties(ignoreUnknown=true)
    @Step("Клиент - проверка тела ответа")
    public Order getOrderFromAnswerBody(ValidatableResponse response){
        return response.extract().jsonPath().getObject("order", Order.class);
    }
    @Step("Клиент - проверка тела ответа")
    public String getIdFromAnswerBody(ValidatableResponse response){
        return response.extract().jsonPath().get("id").toString();
    }
}

