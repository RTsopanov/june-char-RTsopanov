package june.chat.RTsopanov.Server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserServiceJdbc implements UserService, AutoCloseable, AuthenticationProvider {
    private static final String URL = "jdbc:postgresql://localhost:5433/june-chat";
    private static final String passwordDB = "!Hund111";
    private static final String userName = "postgres";

    private static final String USERS_QUERY = "SELECT * FROM users";
    private static final String USER_ROLES_QUERY = """
            select r.id, r.role from role r
            join user_to_role ur ON r.id = ur.role_id
            WHERE user_id = ?
            ORDER BY id
            """;

    private static final String IS_ADMIN_QUERY = """
            select count(1) from role r
            join user_to_role ur ON r.id = ur.role_id
            WHERE user_id = ? and r.role = 'ADMIN'
            """;

    private Connection connection;
    private Server server;
    List<User> allUsers = new ArrayList<>();

    public UserServiceJdbc(Server server) throws SQLException {
        this.server = server;
        connection = DriverManager.getConnection(URL, userName, passwordDB);
    }

    public UserServiceJdbc() throws SQLException {
        connection = DriverManager.getConnection(URL, userName, passwordDB);
    }


    public UserServiceJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<User> getAll() {

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(USERS_QUERY)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String login = resultSet.getString(2);
                    String password = resultSet.getString(3);
                    User user = new User(id, login, password);
                    allUsers.add(user);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement ps = connection.prepareStatement(USER_ROLES_QUERY)) {
            for (User user : allUsers) {
                ps.setInt(1, user.getId());
                List<Role> roles = new ArrayList<>();
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString(2);
                        Role role = new Role(id, name);
                        roles.add(role);
                    }
                    user.setRoles(roles);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allUsers;
    }

    @Override
    public boolean isAdmin(int userId) {
        int flag = 0;
        try (PreparedStatement ps = connection.prepareStatement(IS_ADMIN_QUERY)) {
            ps.setInt(1, userId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    flag = resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag == 1;
    }



    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
        try {
            connection = DriverManager.getConnection(URL, userName, passwordDB);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (Statement statement = connection.createStatement()) {
            try (ResultSet re = statement.executeQuery("SELECT * FROM users WHERE login = " + "'" + login + "'" + " and password = " + "'" + password + "'")) {
                while (re.next()) {
                    String log = re.getString("login");
                    String pass = re.getString("password");
                    String username = re.getString("username");
                    if (log != null && pass != null) {
                        clientHandler.setUserName(username);
                        server.subscribe(clientHandler);
                        clientHandler.out("/authok " + username);
                        return true;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String name, String role) {
        if (isLoginAmlreadyExist(login, password)) {
            try {connection = DriverManager.getConnection(URL, userName, passwordDB);
                Statement statement = connection.createStatement();
                 statement.executeUpdate("Insert into users (login, password, username) values ('" + login + "', " + "'" + password + "', " + "'" + name + "');");


        allUsers.add(new User(password, name ));
        clientHandler.setUserName(name);
        server.subscribe(clientHandler);
        clientHandler.out("/regok " + name);
                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
            clientHandler.out("Указанный логин/пароль уже занят");
        return false;
    }


    @Override
    public boolean kickUserName(ClientHandler clientHandler, String username, String name) {
        return false;
    }

    @Override
    public void initialize() {
        System.out.println("Сервер аутентификации запущен: In-Memory режим");
    }

    @Override
    public void close() throws Exception {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isLoginAmlreadyExist(String login, String password) {
        getAll();
        for (User us : allUsers) {
            if (us.getLogin().equals(login)) {
                return false;
            }
            if (us.getPassword().equals(password)) {
                return false;
            }
        }
        return true;
    }





}

