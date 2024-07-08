package june.chat.RTsopanov.Server;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticationProvider {

    private class User {
        private String login;
        private String password;
        private String username;
        private String role;


        public User(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }


        public User(String login, String password, String username, String role) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.role = role;
        }
    }


    private Server server;
    private List<User> users;

    public InMemoryAuthenticationProvider(Server server) {
        this.server = server;
        this.users = new ArrayList<>();
        this.users.add(new User("login1", "pass1", "user1", "user"));
        this.users.add(new User("login2", "pass2", "user2", "user"));
        this.users.add(new User("login3", "pass3", "user3", "user"));
    }


    private String getUserNameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.username;
            }
        }
        return null;
    }


    private boolean isLoginAmlreadyExist(String login) {
        for (User user : users) {
            if (user.login.equals(login)) {
                return true;
            }
        }
        return false;
    }


    private boolean isUserNameAmlreadyExist(String password) {
        for (User user : users) {
            if (user.password.equals(password)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public synchronized boolean authenticate(ClientHandler clientHandler, String login, String password) {
//        String authtUserName = getUserNameByLoginAndPassword(login, password);
//
//        if (authtUserName == null) {
//            clientHandler.out("Некорректный логин/пароль");
//            return false;
//        }
//
//        if (server.isUserNameBusy(authtUserName)) {
//            clientHandler.out("Данный пользователь уже авторизован");
//            return false;
//        }
//
//        clientHandler.setUserName(authtUserName);
//        server.subscribe(clientHandler);
//        clientHandler.out("/authok " + authtUserName);
        return false;
//        return true;
    }


    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String name, String role) {
//        if (login.trim().length() < 3 || password.trim().length() < 6 || name.trim().length() < 1) {
//            clientHandler.out("Логин 3+ символа, Пароль 6+ символов, Имя 1+ символ.");
//            return false;
//        }
//
//        if (isLoginAmlreadyExist(login)) {
//            clientHandler.out("Указанный логин уже занят");
//            return false;
//        }
//
//        if (isUserNameAmlreadyExist(name)) {
//            clientHandler.out("Указанный имя уже занято");
//            return false;
//        }
//
//        if (!(role.equals("user") || (role.equals("admin")))) {
//            clientHandler.out("Не верно указанна роль 'user / admin'");
//            return false;
//        }
//
//        users.add(new User(login, password, name, role));
//        clientHandler.setUserName(name);
//        server.subscribe(clientHandler);
//        clientHandler.out("/regok " + name);
        return true;
    }


    @Override
    public boolean kickUserName(ClientHandler clientHandler, String username, String name) {
        for (User user : users) {
            if (username.equals(user.username) && !user.role.equals("admin")) {
                clientHandler.out("Для вас недоступна команда '/kick'");
                return false;
            }
            if (username.equals(user.username) && user.role.equals("admin")) {

                for (User us : users) {
                    if (us.username.equals(name)) {
                        users.remove(us);
                        clientHandler.out("Пользователь ник: " + name + " удален успешно.");
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void initialize() {
        System.out.println(
//                "Сервер аутентификации запущен: In-Memory режим"
        );
    }
}
