package june.chat.RTsopanov.Server;

public interface AuthenticationProvider {
    boolean authenticate(ClientHandler clientHandler, String login, String password);
    boolean registration(ClientHandler clientHandler, String login, String password, String name, String role);
     boolean kickUserName(ClientHandler clientHandler, String username, String name);

    void initialize();
}
