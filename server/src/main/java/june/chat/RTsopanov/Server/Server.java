package june.chat.RTsopanov.Server;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private final int port;
    private final Map<String, ClientHandler> clients;





    public Server(int port) {
        this.port = port;
        this.clients = new HashMap<>();

    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен");


            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Подключился новый клиент");
                subscribe(new ClientHandler(this, socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("В чать зашел " + clientHandler.getUserName());
        clients.put(String.valueOf(clientHandler), clientHandler);
    }


    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел " + clientHandler.getUserName());
    }


    public synchronized void broadcastMessage(String message) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            entry.getValue().out(message);
        }
    }


    public synchronized void personalBroadcastMessage(ClientHandler personUserName, String message) {
//        clients.get(String.valueOf(personUserName)).out(message);


        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (entry.getValue().equals(personUserName)) {
                entry.getValue().out(message);
            }
        }
    }


    public Map<String, ClientHandler> getClients() {
        return clients;
    }


}
