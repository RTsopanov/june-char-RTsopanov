package june.chat.RTsopanov.Server;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String password;
    private String login;

    private List<Role> roles = new ArrayList<>();

    public User(int id, String login, String password) {
        this.id = id;
        this.password = password;
        this.login = login;
    }


    public User(String login, String password) {
        this.password = password;
        this.login = login;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }




    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", login='" + login + '\'' +
                ", roles=" + roles +
                '}';
    }
}
