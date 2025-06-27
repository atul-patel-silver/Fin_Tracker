package org.keyclaok.fedration.persistence;

import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.NotImplementedException;

import org.keyclaok.fedration.exception.DBUserStorageException;
import org.keyclaok.fedration.model.QueryConfigurations;
import org.keyclaok.fedration.util.PBKDF2SHA256HashingUtil;
import org.keyclaok.fedration.util.PagingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

@JBossLog
public class UserRepository {
    
    
    private  DataSourceProvider  dataSourceProvider;
    private QueryConfigurations queryConfigurations;


    public UserRepository(DataSourceProvider dataSourceProvider, QueryConfigurations queryConfigurations) {
        this.dataSourceProvider  = dataSourceProvider;
        this.queryConfigurations = queryConfigurations;
    }
    
    
    private <T> T doQuery(String query, PagingUtil.Pageable pageable, Function<ResultSet, T> resultTransformer, Object... params) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        Optional<DataSource> dataSourceOpt = dataSourceProvider.getDataSource();
        if (dataSourceOpt.isPresent()) {
            DataSource dataSource = dataSourceOpt.get();
            try (Connection c = dataSource.getConnection()) {
                if (pageable != null) {
                    query = PagingUtil.formatScriptWithPageable(query, pageable, queryConfigurations.getRDBMS());
                }
                log.infov("Query: {0} params: {1} ", query, Arrays.toString(params));
                try (PreparedStatement statement = c.prepareStatement(query)) {
                    if (params != null) {
                        for (int i = 1; i <= params.length; i++) {
                            statement.setObject(i, params[i - 1]);
                        }
                    }
                    try (ResultSet rs = statement.executeQuery()) {
                        return resultTransformer.apply(rs);
                    }
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        } else {
            log.error("No DataSource available!");
            throw new IllegalStateException("No DataSource found.");
        }
    }
    
    private List<Map<String, String>> readMap(ResultSet rs) {
        try {
            List<Map<String, String>> data         = new ArrayList<>();
            Map<String, String> columnMapping = getColumnMapping(); // Column-to-Attribute mapping

            Set<String> columnsFound = new HashSet<>();

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String columnLabel = rs.getMetaData().getColumnLabel(i);
                columnsFound.add(columnLabel);
            }
            while (rs.next()) {
                Map<String, String> result = new HashMap<>();
                for (String col : columnsFound) {
                    String mappedKey = columnMapping.getOrDefault(col, col); // Apply mapping
                    result.put(mappedKey, rs.getString(col));
                }
                data.add(result);
            }
            return data;
        } catch (Exception e) {
            throw new DBUserStorageException(e.getMessage(), e);
        }
    }

    private Map<String, String> getColumnMapping() {
        Map<String, String> columnMapping = new HashMap<>();

        // Database Column Name -> Keycloak Attribute Name
        columnMapping.put("id", "id");
        columnMapping.put("user_name", "username");
        columnMapping.put("email_id", "email");
        columnMapping.put("first_name", "firstName");
        columnMapping.put("last_name", "lastName");

        return columnMapping;
    }
    
    
    private Integer readInt(ResultSet rs) {
        try {
            return rs.next() ? rs.getInt(1) : null;
        } catch (Exception e) {
            throw new DBUserStorageException(e.getMessage(), e);
        }
    }
    
    private Boolean readBoolean(ResultSet rs) {
        try {
            return rs.next() ? rs.getBoolean(1) : null;
        } catch (Exception e) {
            throw new DBUserStorageException(e.getMessage(), e);
        }
    }
    
    private String readString(ResultSet rs) {
        try {
            return rs.next() ? rs.getString(1) : null;
        } catch (Exception e) {
            throw new DBUserStorageException(e.getMessage(), e);
        }
    }
    
    public List<Map<String, String>> getAllUsers() {
        return doQuery(queryConfigurations.getListAll(), null, this::readMap);
    }
    
    public int getUsersCount(String search) {
        if (search == null || search.isEmpty()) {
            return Optional.ofNullable(doQuery(queryConfigurations.getCount(), null, this::readInt)).orElse(0);
        } else {
            String query = String.format("select count(*) from (%s) count", queryConfigurations.getFindBySearchTerm());
            return Optional.ofNullable(doQuery(query, null, this::readInt, search)).orElse(0);
        }
    }


    public Map<String, String> findUserById(String id) {
        try {
            BigInteger userId = new BigInteger(id);  // Convert String to Long
            return Optional.ofNullable(doQuery(queryConfigurations.getFindById(), null, this::readMap, userId))
                    .orElse(Collections.emptyList())
                    .stream().findFirst().orElse(null);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: " + id, e);
            return null;
        }
    }

    public Optional<Map<String, String>> findUserByUsername(String username) {
        String query = queryConfigurations.getFindByUsername();
        if (query == null) {
            log.error("Query for findUserByUsername is null! Check configuration.");
            throw new IllegalStateException("Query for finding user by username is not configured.");
        }
        return Optional.ofNullable(doQuery(query, null, this::readMap, username))
                .orElse(Collections.emptyList())
                .stream().findFirst();
    }


    public Optional<Map<String, String>> findUserByEmail(String email) {
        return Optional.ofNullable(doQuery(queryConfigurations.getFindByEmail(), null, this::readMap, email))
                .orElse(Collections.emptyList())
                .stream().findFirst();
    }
    
    public List<Map<String, String>> findUsers(String search, PagingUtil.Pageable pageable) {
        if (search == null || search.isEmpty()) {
            pageable = null;
            return doQuery(queryConfigurations.getListAll(), pageable, this::readMap);
        }
        String searchValue = "%" + search.toUpperCase() + "%";
        return doQuery(queryConfigurations.getFindBySearchTerm(), pageable, this::readMap, searchValue, searchValue);
    }

    public boolean validateCredentials(String username, String password) {
        log.infov("Validating credentials for username: {0}", username);

        // Retrieve the stored hash
        String hash = Optional.ofNullable(doQuery(queryConfigurations.getFindPasswordHash(), null, this::readString, username)).orElse("");
        log.infov("Retrieved password hash for user {0}: {1}", username, hash);

        if (hash.isEmpty()) {
            log.warn("No password hash found for user: " + username);
            return false;
        }

        if (queryConfigurations.isBlowfish()) {

            /*boolean result = BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
            log.infov("BCrypt validation result for {0}: {1}", username, result);*/
            System.out.println("BCrypt validation result for " + password + " " + hash);
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            boolean isMatch = passwordEncoder.matches(password, hash);
//            boolean isMatch = BCrypt.checkpw(password, hash);
            log.infov("BCrypt validation result for {0}: {1}", username, isMatch);

            return isMatch;
        } else {
            String hashFunction = queryConfigurations.getHashFunction();
            log.infov("Using hash function {0} for validation", hashFunction);

            if (hashFunction.equals("PBKDF2-SHA256")) {
                if (hash.isEmpty() || !hash.contains("$")) {
                    log.warn("Invalid hash format for user: " + username);
                    return false;
                }

                String[] components = hash.split("\\$");
                if (components.length < 4) {
                    log.warn("Hash format incorrect for PBKDF2-SHA256, user: " + username);
                    return false;
                }

                log.infov("PBKDF2 components: Iterations={0}, Salt={1}, StoredHash={2}", components[1], components[2], components[3]);

                boolean result = new PBKDF2SHA256HashingUtil(password, components[2], Integer.parseInt(components[1])).validatePassword(components[3]);
                log.infov("PBKDF2 validation result for {0}: {1}", username, result);
                return result;
            }

            MessageDigest digest = DigestUtils.getDigest(hashFunction);
            byte[] pwdBytes = StringUtils.getBytesUtf8(password);
            boolean result = Objects.equals(Hex.encodeHexString(digest.digest(pwdBytes)), hash);

            log.infov("Digest validation result for {0}: {1}", username, result);
            return result;
        }
    }


    public boolean updateCredentials(String username, String password) {
        throw new NotImplementedException("Password update not supported");
    }
    
    public boolean removeUser(String id) {
//        return queryConfigurations.getAllowKeycloakDelete();
        try {
            BigInteger userId = new BigInteger(id);  // Convert String to Long
            return Optional.ofNullable(doQuery(queryConfigurations.getRemoveById(), null, this::readMap, userId))
                    .orElse(Collections.emptyList())
                    .stream().findFirst().isPresent();
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: " + id, e);
            return false;
        }
    }

    public List<String> findRolesByUsername(String username) {
        String query = "SELECT r.code FROM user_roles ur Join tbl_users u ON u.id = ur.user_id JOIN tbl_role r on r.id = ur.role_id WHERE u.is_active=true and u.is_deleted =false \n" +
                "and r.is_active= true and r.is_deleted = false and u.user_name = ?";
        return doQuery(query, null, rs -> {
            List<String> roles = new ArrayList<>();
            try {
                while (rs.next()) {
                    roles.add(rs.getString("code"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return roles;
        }, username);
    }
    public boolean addToExternalDB(String email, String username, String firstName, String lastName) {
        log.debug(">>> Entered External db <<<===========================================================================");
        try {
            String insertUserSQL = "INSERT INTO tbl_users (created_at, created_by, update_by, update_at, email_id, first_name, is_active, is_deleted, last_name, middle_name, mobile_number, password, role, user_name, login_type, sub) " +
                    "VALUES (?, now(), 'keycloak', 'keycloak', now(), ?, ?, true, false, ?, '', '', '', '', '', ?, 'THIRD_PARTY', '')";

            String insertRoleSQL = "INSERT INTO user_roles (user_id, role_id) " +
                    "SELECT u.id, r.id FROM tbl_users u, tbl_role r " +
                    "WHERE u.email_id = ? AND r.code = ?";



            doQuery(insertUserSQL, null, rs -> null,
                    email,          // email_id
                    firstName,      // first_name
                    lastName,       // last_name
                    username        // user_name
            );

            doQuery(insertRoleSQL, null, rs -> null, email, "ROLE_USER");

            System.out.println("✅ User inserted into local DB: " + email);
            return true;
        } catch (Exception e) {
            System.err.println("❌ DB Insert failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static UserRepository getInstance() {
        try {
            // STEP 1: Manually configure SQL queries
            QueryConfigurations queryConfig = new QueryConfigurations(
                    "SELECT COUNT(*) FROM tbl_users WHERE is_deleted = false", // count
                    "SELECT * FROM tbl_users WHERE is_deleted = false", // listAll
                    "SELECT * FROM tbl_users WHERE id = ?", // findById
                    "DELETE FROM tbl_users WHERE id = ?", // removeById
                    "SELECT * FROM tbl_users WHERE email = ?", // findByEmail
                    "SELECT * FROM tbl_users WHERE user_name = ?", // findByUsername
                    "SELECT * FROM tbl_users WHERE UPPER(user_name) LIKE ? OR UPPER(email) LIKE ?", // findBySearchTerm
                    "SELECT password FROM tbl_users WHERE user_name = ?", // findPasswordHash
                    "bcrypt", // hashFunction: choose "bcrypt" or "PBKDF2-SHA256"
                    RDBMS.POSTGRESQL, // your enum type, e.g. MYSQL, POSTGRESQL, etc.
                    true, // allowKeycloakDelete
                    true  // allowDatabaseToOverwriteKeycloak
            );

            // STEP 2: Create DataSourceProvider (default no-arg constructor)
            DataSourceProvider dataSourceProvider = new DataSourceProvider();

            return new UserRepository(dataSourceProvider, queryConfig);

        } catch (Exception e) {
            System.err.println("Failed to create UserRepository instance: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Could not initialize UserRepository", e);
        }
    }
}
