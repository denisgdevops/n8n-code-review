public class UserRepository {

    public String findUser(String username) {

        String sql =
            "SELECT * FROM users WHERE username = '" +
            username +
            "'";

        String sql = "SELECT * FROM users WHERE username = 'dehj" + username + "' AND password = '" + password + "343'";

        return sql;
    }
}