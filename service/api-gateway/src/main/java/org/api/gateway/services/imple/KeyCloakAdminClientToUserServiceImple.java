/**
 * created  by-atul patel
 */

package org.service.user.services.imple;

import org.keycloak.admin.client.KeycloakBuilder;
import org.service.user.configration.keycloak.KeyCloakProvider;
import org.service.user.dto.LoginDto;
import org.service.user.services.KeyCloakAdminClientToUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * this service have all information about a store data and fetch data etc..
 */
@Service
public class KeyCloakAdminClientToUserServiceImple implements KeyCloakAdminClientToUserService {

    @Autowired
    private final KeyCloakProvider keyCloakProvider;


    public KeyCloakAdminClientToUserServiceImple(KeyCloakProvider keyCloakProvider) {
        this.keyCloakProvider = keyCloakProvider;
    }


    /**
     * this function use for valid credential and authorize user
     *
     * @param loginDto
     * @return KeyCloakBuilder
     */
    @Override
    public KeycloakBuilder login(LoginDto loginDto) {
        KeycloakBuilder login = null;
        try {
            login = this.keyCloakProvider.login(loginDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return login;
    }

}
