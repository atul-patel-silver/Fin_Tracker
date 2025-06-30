package org.keycloak.fedration.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class JdbcUtil {
    public static boolean insertExternalUser(DataSource dataSource, String email, String username, String firstName, String lastName,String alis) {
        System.out.println(">>> Entered External DB Utility <<<");

        String insertUserSQL = """
            INSERT INTO tbl_users (created_at, created_by, email_id, first_name, is_active, is_deleted, last_name, user_name, login_type,password,mobile_number,middle_name) 
            VALUES (now(), 'keycloak', ?, ?, true, false, ?, ?,?,' ',' ',' ')
        """;

        String insertRoleSQL = """
            INSERT INTO user_roles (user_id, role_id)
            SELECT u.id, r.id FROM tbl_users u, tbl_role r
            WHERE u.email_id = ? AND r.code = ?
        """;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement userStmt = connection.prepareStatement(insertUserSQL);
                 PreparedStatement roleStmt = connection.prepareStatement(insertRoleSQL)) {

                userStmt.setString(1, email);
                userStmt.setString(2, firstName);
                userStmt.setString(3, lastName);
                userStmt.setString(4, username);
                userStmt.setString(5, alis);
                userStmt.executeUpdate();

                roleStmt.setString(1, email);
                roleStmt.setString(2, "ROLE_USER");
                roleStmt.executeUpdate();

                connection.commit();
                System.out.println("✅ User inserted into external DB: " + email);
                return true;

            } catch (Exception e) {
                connection.rollback();
                System.err.println("❌ Insert failed, transaction rolled back.");
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ DB Connection failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
