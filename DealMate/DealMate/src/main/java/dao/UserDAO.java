package dao;

import java.util.List;
import model.User;

public class UserDAO {

    public boolean register(User user) {
        List<User> users = DatabaseConnection.loadUsers();
        users.add(user);
        DatabaseConnection.saveUsers(users);
        return true;
    }

    public User login(String email, String password) {
        List<User> users = DatabaseConnection.loadUsers();
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }
}
