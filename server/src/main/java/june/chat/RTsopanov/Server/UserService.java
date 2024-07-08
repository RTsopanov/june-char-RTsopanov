package june.chat.RTsopanov.Server;

import java.util.List;

public interface UserService extends AutoCloseable {
    List<User> getAll();
    boolean isAdmin(int userId);

}
