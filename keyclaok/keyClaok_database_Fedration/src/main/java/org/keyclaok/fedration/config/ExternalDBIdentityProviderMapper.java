package org.keyclaok.fedration.config;

import lombok.extern.jbosslog.JBossLog;
import org.keyclaok.fedration.persistence.UserRepository;
import org.keycloak.broker.oidc.OIDCIdentityProviderFactory;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityProviderMapper;
import org.keycloak.models.*;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;
@JBossLog
public class ExternalDBIdentityProviderMapper extends AbstractIdentityProviderMapper
        implements IdentityProviderMapper {

    public static final String PROVIDER_ID = "external-db-idp-mapper";

    private final UserRepository userRepository;

    public ExternalDBIdentityProviderMapper() {
        this.userRepository = UserRepository.getInstance();
    }


    @Override
    public String[] getCompatibleProviders() {
        return new String[] { OIDCIdentityProviderFactory.PROVIDER_ID }; // e.g., Google
    }

    @Override
    public String getDisplayCategory() {
        return "Federation";
    }

    @Override
    public String getDisplayType() {
        return "External DB Federation Mapper";
    }

    @Override
    public String getHelpText() {
        return "Maps IdP login to user from external DB (federated). Prevents local user creation.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
         return new ArrayList<>();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void preprocessFederatedIdentity(KeycloakSession session,
                                            RealmModel realm,
                                            IdentityProviderMapperModel mapperModel,
                                            BrokeredIdentityContext context) {
        System.out.println(">>> Inside preprocessFederatedIdentity() <<<");
        log.debug(">>> Entered preprocessFederatedIdentity <<<");
        String email = context.getEmail();
        String username = context.getUsername();
         String firstName = context.getFirstName();
        String lastName = context.getLastName();

        if (email == null && username == null) {
            System.out.println("Email and username both null in IdP context. Skipping.");
            return;
        }

        UserProvider userProvider = session.users();
        UserModel existingUser = userProvider.getUserByEmail(realm, email);

        if (existingUser != null) {
//            context= (BrokeredIdentityContext) existingUser;
            context.getContextData().put("BROKERED_USER", existingUser);

//            context.setUsername(existingUser); // user already exists
            System.out.println("Found user in federated store: " + email);
            return;
        }

        // Insert user into external DB manually if not found
        boolean added = this.userRepository.addToExternalDB(email, username,firstName,lastName);
        if (added) {
            UserModel federatedUser = userProvider.getUserByEmail(realm, email);
            if (federatedUser != null) {
                context.getContextData().put("BROKERED_USER", federatedUser);
                System.out.println("User added and fetched from federated DB: " + email);
            } else {
                System.out.println("User added to DB but not found by Keycloak yet: " + email);
            }
        } else {
            System.err.println("Failed to add user to external DB: " + email);
        }
    }
}
