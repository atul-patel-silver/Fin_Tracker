package org.keycloak.fedration.config;

import lombok.extern.jbosslog.JBossLog;

import org.keycloak.broker.oidc.OIDCIdentityProviderFactory;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityProviderMapper;
import org.keycloak.fedration.persistence.DataSourceProvider;
import org.keycloak.fedration.util.JdbcUtil;
import org.keycloak.models.*;
import org.keycloak.provider.ProviderConfigProperty;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JBossLog
public class ExternalDBIdentityProviderMapper extends AbstractIdentityProviderMapper
        implements IdentityProviderMapper {
    public static final String PROVIDER_ID = "external-db-idp-mapper";


    static {
        log.info("ExternalDBIdentityProviderMapper initialized.");
        System.out.println("ExternalDBIdentityProviderMapper initialized======================================================.");
    }


    @Override
    public String[] getCompatibleProviders() {
        return new String[] {"oidc", "google", "facebook"}; // e.g., Google
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
            context.getContextData().put("EXISTING_USER_INFO", existingUser);
            System.out.println("Found existing user in federated store: " + email);
            return;
        }

        final DataSource instance = DataSourceProvider.getInstance();
        final boolean added = JdbcUtil.insertExternalUser(instance, email, username, firstName, lastName,context.getIdpConfig().getAlias());

        // Insert user into external DB manually if not found
        if (added) {
            UserModel federatedUser = userProvider.getUserByEmail(realm, email);
            if (federatedUser != null) {
                context.getContextData().put("EXISTING_USER_INFO", federatedUser);

                // ✅ Link IdP to user
                FederatedIdentityModel identity = new FederatedIdentityModel(
                        context.getIdpConfig().getAlias(), // IdP alias like "google"
                        context.getId(),                   // user ID in IdP
                        context.getUsername()              // username from IdP
                );
                UserModel alreadyLinkedUser = session.users().getUserByFederatedIdentity(realm, identity);
                if (alreadyLinkedUser != null && !alreadyLinkedUser.getId().equals(federatedUser.getId())) {
                    log.warn("Federated identity already linked to another user. Removing old link.");
                    session.users().removeFederatedIdentity(realm, alreadyLinkedUser, context.getIdpConfig().getAlias());
                }

                session.users().addFederatedIdentity(realm, federatedUser, identity);

                UserModel linkedUser = session.users().getUserByFederatedIdentity(realm, identity);
                if (linkedUser != null && !linkedUser.getEmail().equalsIgnoreCase(email)) {
                    log.error("❌ Federated identity already linked to a different user.");
                    return;
                }
//                context.getContextData().put("BROKERED_USER_ID", federatedUser.getId());
                System.out.println("User inserted into external DB and picked up by Keycloak: " + email);
            } else {
                System.out.println("User added to DB but not found by Keycloak yet: " + email);
            }
        } else {
            System.err.println("Failed to add user to external DB: " + email);
        }
    }
}
