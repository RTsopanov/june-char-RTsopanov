package june.chat.RTsopanov.Server;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;



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
        broadcastMessage("В чать зашел " + clientHandler.getUserName());
        clients.add(clientHandler);
    }





    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел " + clientHandler.getUserName());
    }


    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.out(message);
        }
    }



    public synchronized void personalBroadcastMessage(String message) {
        for (ClientHandler client : clients) {
           if(client.getUserName().equals("Tom")){
               client.out(message);
           }
        }
    }





    public List<ClientHandler> getClients() {
        return clients;
    }
}
