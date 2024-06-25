package june.chat.RTsopanov.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;

public class ClientHandler {
    private Server server;
    private Socket clientSocket;
    private String userName;
    private String personUserName;
    private String message;


    private final DataInputStream IN;
    private final DataOutputStream OUT;


    public ClientHandler(Server server, Socket socket) throws IOException {

        this.clientSocket = socket;
        this.server = server;
        this.IN = new DataInputStream(socket.getInputStream());
        this.OUT = new DataOutputStream(socket.getOutputStream());


        out("Введите свой ник.");
        String name = in();
        this.userName = name;


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String result = in();
                        if (result.startsWith("/")) {
                            if (result.equals("/exit")) {
                                out("/exitok");
                                break;
                            } else if (result.startsWith("/w ")) {

                                String[] str = result.split(" ");
                                personUserName = str[1];
                                message = str[2];

                                server.personalBroadcastMessage(personUserName, message);
                            }
                            continue;
                        }
                        server.broadcastMessage(userName + ": " + result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            }
        });
        t1.start();

    }


    public String in() throws IOException {
        return IN.readUTF();
    }


    public void out(String str) {
        try {
            OUT.writeUTF(str);
            OUT.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (IN != null) {
                IN.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (OUT != null) {
                OUT.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getUserName() {
        return userName;
    }

}
