package org.service.user.configration.keycloak;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
//import lombok.Getter;
import lombok.Getter;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.service.user.dto.LoginDto;
import org.service.user.dto.TokenDto;
import org.service.user.model.Role;
import org.service.user.model.UserModel;
import org.service.user.repository.RoleRepository;
import org.service.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@Getter
public class KeyCloakProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${keycloak.auth-server-url}")
    public String serverURL;
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.resource}")
    public String clientID;
    @Value("${grant-type}")
    public String grantType;
    @Value("${keycloak.credentials.secret}")
    public String clientSecret;
    @Value("${name}")
    public  String name;
    @Value("${password}")
    public String password;
    @Value("${keycloak.redirect-uri}")
    private String redirectUri;

    public KeyCloakProvider() {
    }

    private static  Keycloak keycloak = null;



    public Keycloak getInstance() {


        if (keycloak == null) {

            return KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverURL)
                    .clientId(clientID)
                    .clientSecret(clientSecret)
                    .grantType(grantType).password(password).username(name)
                    .build();
        }
        return keycloak;
    }


    public KeycloakBuilder login(LoginDto loginDto) {
        return KeycloakBuilder.builder() //
                .realm(realm) //
                .serverUrl(serverURL)//
                .clientId(clientID) //
                .clientSecret(clientSecret) //
                .username(loginDto.getUserName()) //
                .password(loginDto.getPassword());
    }

    /*public JsonNode refreshToken(String refreshToken) throws UnirestException {
        String url = serverURL + "/realms/" + realm + "/protocol/openid-connect/token";
        return Unirest.post(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", clientID)
                .field("client_secret", clientSecret)
                .field("refresh_token", refreshToken)
                .field("grant_type", "refresh_token")
                .asJson().getBody();
    }*/

    public TokenDto exchangeCode(String code) throws Exception {
        var tokenUrl = serverURL + "/realms/" + realm + "/protocol/openid-connect/token";

        var params = List.of(
                new BasicNameValuePair("client_id", clientID),
                new BasicNameValuePair("client_secret", clientSecret),
                new BasicNameValuePair("grant_type", "authorization_code"),
                new BasicNameValuePair("code", code),
                new BasicNameValuePair("redirect_uri", redirectUri)
        );

        try (var client = HttpClients.createDefault()) {
            var post = new HttpPost(tokenUrl);
            post.setEntity(new UrlEncodedFormEntity(params));
            var response = client.execute(post);
            var body = EntityUtils.toString(response.getEntity());
            return new ObjectMapper().readValue( body, TokenDto.class);
        }
    }

    public UserModel mapUser(String accessToken) throws Exception {
        var userInfoUrl = serverURL + "/realms/" + realm + "/protocol/openid-connect/userinfo";

        HttpGet get = new HttpGet(userInfoUrl);
        get.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpResponse response = client.execute(get);
            String json = EntityUtils.toString(response.getEntity());
            JsonNode node = new ObjectMapper().readTree(json);

            String sub = node.get("sub").asText();
            String email = node.get("email").asText();
            String firstName = node.has("given_name") ? node.get("given_name").asText() : email;
            String lastName = node.has("family_name") ? node.get("family_name").asText() : email;

             Optional<UserModel> userModelOptional = this.userRepository.findByEmailIdAndIsActiveTrueAndIsDeletedFalse(email);
            return userModelOptional.orElseGet(UserModel::new);


        }
    }
}
