package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Credentials {

    @Override
    public String toString(){
        return String.format("{\"login\":\"%s\",\"password\":\"%s\" }",login,password);
    }

    private String login;
    private String password;

    public static Credentials fromCourier(Courier courier) {
        return new Credentials(courier.getLogin(), courier.getPassword());
    }
}