public class UserRepository {

    public String findUser(String username) {

        String sql =
            "SELECT * FROM users WHERE username = '" +
            username +
            "'";

        return sql;
    }
}