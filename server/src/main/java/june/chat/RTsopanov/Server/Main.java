package june.chat.RTsopanov.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {


    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/test2";

    private static final String USERS_QUERY = "SELECT * FROM users";
    private static final String USER_ROLES_QUERY = """
            select r.id, r.name from roles r
            join users_to_roles ur ON r.id = ur.role_id
            WHERE user_id = ?
            ORDER BY id
            """;


    public static void main(String[] args) throws SQLException {

        try(UserService userService = new UserServiceJdbc()) {
            List<User> all = userService.getAll();
            System.out.println(all);
            all.forEach(it -> System.out.println(userService.isAdmin(it.getId())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





}
