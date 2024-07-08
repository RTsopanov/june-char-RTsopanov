package june.chat.RTsopanov.Server;


import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class Server {
    private final int port;
    private Map<String, ClientHandler> map;
    private AuthenticationProvider authenticationProvider;
    private UserService userService;

    public Server(int port) throws SQLException {
        this.port = port;
        this.map = new HashMap<>();
        this.authenticationProvider = new InMemoryAuthenticationProvider(this);
        this.authenticationProvider = new UserServiceJdbc(this);
    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен");
            authenticationProvider.initialize();


            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Подключился новый клиент");
                new ClientHandler(this, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void subscribe(ClientHandler clientHandler) {
        map.put(clientHandler.toString(), clientHandler);
        broadcastMessage("В чат зашел " + clientHandler.getUserName());

    }


    public synchronized void unsubscribe(ClientHandler clientHandler) {
        map.remove(clientHandler.toString(), clientHandler);
        broadcastMessage("Из чата вышел " + clientHandler.getUserName());
    }


    public synchronized void broadcastMessage(String message) {
        for (Map.Entry<String, ClientHandler> entry : map.entrySet()) {
            entry.getValue().out(message);
        }
    }


    public synchronized void personalBroadcastMessage(String clientHandler, String message) {
        for (Map.Entry<String, ClientHandler> entry : map.entrySet()) {
            if (entry.getValue().getUserName().equals(clientHandler)) {
                entry.getValue().out(message);
            }
        }
    }


    public boolean isUserNameBusy(String userNmae){
        for(Map.Entry<String, ClientHandler> entry: map.entrySet()){
            if(entry.getValue().getUserName().equals(userNmae)){
                return true;
            }
        }
        return false;
    }






    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }
}
