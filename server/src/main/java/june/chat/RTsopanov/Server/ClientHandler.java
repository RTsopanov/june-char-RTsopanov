package june.chat.RTsopanov.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ClientHandler {

    private Server server;
    private Socket clientSocket;
    private String userName;
    private String personUserName;
    private String message;
    private final DataInputStream IN;
    private final DataOutputStream OUT;




    public ClientHandler(Server server, Socket socket ) throws IOException {
        this.clientSocket = socket;
        this.server = server;
        this.IN = new DataInputStream(socket.getInputStream());
        this.OUT = new DataOutputStream(socket.getOutputStream());


        new Thread(() -> {

            try {
                while (true) {
                    String result = in();

                    if (result.equals("/exit")) {
                        out("/exitok");
                        return;
                    }


                    if (result.startsWith("/auth ")) {
                        String[] str = result.split(" ");
                        if (str.length != 3) {
                            out("Неверный формат данных /auth");
                            continue;
                        }

                            if (server.getAuthenticationProvider().authenticate(this, str[1], str[2])) {
                                break;
                            }

                        continue;
                    }

                    if (result.startsWith("/register ")) {
                        String[] str = result.split(" ");
                        if (str.length < 4 || str.length > 5) {
                            out("Неверный формат комфнды /register");
                            continue;
                        }

                        if (str.length == 4) {
                            String[] str1 = {str[1], str[2], str[3], "user"};
                            if (server.getAuthenticationProvider().registration(this, str1[0], str1[1], str1[2], str1[3]))
                                break;
                        }

                        if (str.length == 5) {
                            if (server.getAuthenticationProvider().registration(this, str[1], str[2], str[3], str[4])) {
                                break;
                            }
                        }
                        continue;
                    }
                    out("Перед работой с чатом необходимо выполнить аутентификацию '/auth login passord' или региистрацию '/register login password userName'");
                }


                while (true) {
                    String result = in();
                    if (result.startsWith("/")) {
                        if (result.equals("/exit")) {
                            out("/exitok");
                            break;
                        } else if (result.startsWith("/w ")) {
                            String[] str = result.split(" ");

                            if (str.length != 3) {
                                out("Некорректный формат данных '/w username'");
                            }

                            personUserName = str[1];
                            message = str[2];
                            server.personalBroadcastMessage(personUserName, message);
                        }

                        if (result.startsWith("/kick ")) {
                            String[] str = result.split(" ");
                            if (str.length != 2) {
                                out("Некорректный формат данных '/kick username'");
                            }
                            server.getAuthenticationProvider().kickUserName(this, userName, str[1]);
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
        })
                .start();
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

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
