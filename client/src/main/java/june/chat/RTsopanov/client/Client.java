package june.chat.RTsopanov.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private DataInputStream IN;
    private DataOutputStream OUT;
    private Socket socket;


    public Client() throws IOException {
        Scanner scanner = new Scanner(System.in);


        socket = new Socket("localhost", 9998);
        this.IN = new DataInputStream(socket.getInputStream());
        this.OUT = new DataOutputStream(socket.getOutputStream());


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String result = in();
                        if (result.equals("/exitok")) {
                            break;
                        }
                        if(result.startsWith("/authok ")){
                            System.out.println("Удалось войти в чат под пользователем " + result.split(" ")[1]);
                            continue;
                        }
                        if(result.startsWith("/regok ")){
                            System.out.println("Удалось успешно проти регистрацию и войти в чат под именем  " + result.split(" ")[1]);
                            continue;
                        }

                        System.out.println(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t1.start();


        while (true) {
            String message = scanner.nextLine();
            out(message);

            if (message.equals("/exit")) {
                break;
            }
        }
    }



    public String in() throws IOException {
        return IN.readUTF();
    }



    public void out(String str) throws IOException {
        OUT.writeUTF(str);
        OUT.flush();
    }



    public void disconnect() throws IOException {
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
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
