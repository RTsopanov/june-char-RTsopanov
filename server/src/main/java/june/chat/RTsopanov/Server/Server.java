package june.chat.RTsopanov.Server;


import org.w3c.dom.ls.LSOutput;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private final int port;
    private final List<ClientHandler> clients;



    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();

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
        clients.add(clientHandler);
        broadcastMessage("В чат зашел " + clientHandler.getUserName());

    }


    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел " + clientHandler.getUserName());
    }


    public synchronized void broadcastMessage(String message) {
        System.out.println(clients);
        for (ClientHandler client : clients) {
            client.out(message);
        }
    }


    public synchronized void personalBroadcastMessage(String clientHandler, String message) {

        for (ClientHandler client : clients) {
            if(client.toString().equals(clientHandler)){
                client.out(message);
            }
        }
    }




    public List<ClientHandler> getClients() {
        return clients;
    }


}
