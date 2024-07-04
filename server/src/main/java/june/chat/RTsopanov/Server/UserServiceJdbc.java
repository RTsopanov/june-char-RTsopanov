package june.chat.RTsopanov.Server;

import java.util.ArrayList;
import java.util.List;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserServiceJdbc implements UserService, AutoCloseable {
    private Server server;
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
        List<User> allUsers = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(USERS_QUERY)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String password = resultSet.getString(2);
                    String email = resultSet.getString(3);
                    User user = new User(id, password, email);
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


    public synchronized boolean authenticateJabc(ClientHandler clientHandler, String login, String password) throws SQLException {
        connection = DriverManager.getConnection(URL, userName, passwordDB);
        try (Statement statement = connection.createStatement()) {
            try (ResultSet re = statement.executeQuery("SELECT * FROM users WHERE login = " + login + "and password = " + password)) {
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
            }
        }
        return true;
    }


    @Override
    public void close() throws Exception {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

