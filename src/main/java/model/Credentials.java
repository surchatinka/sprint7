package model;

public class Credentials {

    @Override
    public String toString(){
        return String.format("{\"login\":\"%s\",\"password\":\"%s\" }",login,password);
    }
//    public String toString() {
//        return "Credentials{" +
//                "login='" + login + '\'' +
//                ", password='" + password + '\'' +
//                '}';
//    }


    private String login;
    private String password;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public static Credentials fromCourier(Courier courier) {
        return new Credentials(courier.getLogin(), courier.getPassword());
    }
}