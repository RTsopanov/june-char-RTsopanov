package june.chat.RTsopanov.Server;


import java.sql.SQLException;

public class ServerApplication {
    public static void main(String[] args) throws SQLException {

        new Server(9998).start();

    }
}