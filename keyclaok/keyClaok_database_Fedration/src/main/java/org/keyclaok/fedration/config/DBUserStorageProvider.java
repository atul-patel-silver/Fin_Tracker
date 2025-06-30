package org.keyclaok.fedration.config;

import lombok.extern.jbosslog.JBossLog;
import org.keyclaok.fedration.model.QueryConfigurations;
import org.keyclaok.fedration.model.UserAdapter;
import org.keyclaok.fedration.persistence.DataSourceProvider;
import org.keyclaok.fedration.persistence.UserRepository;
import org.keyclaok.fedration.util.PagingUtil;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.role.RoleStorageProviderModel;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JBossLog
public class DBUserStorageProvider extends RoleStorageProviderModel implements UserStorageProvider,
        UserLookupProvider, UserQueryProvider, CredentialInputUpdater, CredentialInputValidator, UserRegistrationProvider {
    
    private final KeycloakSession session;
    private final ComponentModel  model;
    private final UserRepository repository;
    private final boolean allowDatabaseToOverwriteKeycloak;
    List<String> roles = new ArrayList<>();
    DBUserStorageProvider(KeycloakSession session, ComponentModel model, DataSourceProvider dataSourceProvider, QueryConfigurations queryConfigurations) {
        this.session    = session;
        this.model      = model;
        this.repository = new UserRepository(dataSourceProvider, queryConfigurations);
        this.allowDatabaseToOverwriteKeycloak = queryConfigurations.getAllowDatabaseToOverwriteKeycloak();
    }
    
    
    private List<UserModel> toUserModel(RealmModel realm, List<Map<String, String>> users) {
        return users.stream()
                    .map(m -> new UserAdapter(session, realm, model, m, allowDatabaseToOverwriteKeycloak,roles)).collect(Collectors.toList());
    }
    
    
    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }
    
    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }
    
    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        
        log.infov("isValid user credential: userId={0}", user.getId());

//        if (!supportsCredentialType(input.getType()) || !(input instanceof PasswordCredentialModel)) {
//            log.infov("hello: userId={0}", user.getId());
//            return false;
//        }
        
        UserCredentialModel cred = (UserCredentialModel) input;

        UserModel dbUser = user;
        // If the cache just got loaded in the last 500 millisec (i.e. probably part of the actual flow), there is no point in reloading the user.)
        if (allowDatabaseToOverwriteKeycloak && user instanceof CachedUserModel && (System.currentTimeMillis() - ((CachedUserModel) user).getCacheTimestamp()) > 500) {
            log.infov("hiii: userId={0}", user.getId());
          dbUser = this.getUserById(realm, user.getId());

          if (dbUser == null) {
            ((CachedUserModel) user).invalidate();
            return false;
          }

          // For now, we'll just invalidate the cache if username or email has changed. Eventually we could check all (or a parametered list of) attributes fetched from the DB.
          if (!java.util.Objects.equals(user.getUsername(), dbUser.getUsername()) || !java.util.Objects.equals(user.getEmail(), dbUser.getEmail())) {
            ((CachedUserModel) user).invalidate();
          }
        }
        log.infov("isValid user credential: userId={0}", user.getId());
        return repository.validateCredentials(dbUser.getUsername(), cred.getChallengeResponse());
    }
    
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        
        log.infov("updating credential: realm={0} user={1}", realm.getId(), user.getUsername());
        
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        
        UserCredentialModel cred = (UserCredentialModel) input;
        return repository.updateCredentials(user.getUsername(), cred.getChallengeResponse());
    }
    
    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realmModel, UserModel userModel) {
        return Stream.empty();
    }

    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }
    
    @Override
    public void preRemove(RealmModel realm) {
        
        log.infov("pre-remove realm");
    }
    
    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        
        log.infov("pre-remove group");
    }
    
    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        
        log.infov("pre-remove role");
    }
    
    @Override
    public void close() {
        log.debugv("closing");
    }
    
    @Override
    public int getUsersCount(RealmModel realm) {
        return repository.getUsersCount(null);
    }
    
    @Override
    public int getUsersCount(RealmModel realm, Set<String> groupIds) {
        return repository.getUsersCount(null);
    }
    
    @Override
    public int getUsersCount(RealmModel realm, String search) {
        return repository.getUsersCount(search);
    }
    
    @Override
    public int getUsersCount(RealmModel realm, String search, Set<String> groupIds) {
        return repository.getUsersCount(search);
    }
    
    @Override
    public int getUsersCount(RealmModel realm, Map<String, String> params) {
        return repository.getUsersCount(null);
    }
    
    @Override
    public int getUsersCount(RealmModel realm, Map<String, String> params, Set<String> groupIds) {
        return repository.getUsersCount(null);
    }
    
    @Override
    public int getUsersCount(RealmModel realm, boolean includeServiceAccount) {
        return repository.getUsersCount(null);
    }
    

    public List<UserModel> getUsers(RealmModel realm) {
        log.infov("list users: realm={0}", realm.getId());
        return internalSearchForUser(null, realm, null);
    }

    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        
        log.infov("list users: realm={0} firstResult={1} maxResults={2}", realm.getId(), firstResult, maxResults);
        return internalSearchForUser(null, realm, new PagingUtil.Pageable(0, maxResults));
    }
    
    private List<UserModel> internalSearchForUser(String search, RealmModel realm, PagingUtil.Pageable pageable) {
        if (search != null && (search.equalsIgnoreCase("false") || search.equalsIgnoreCase("true"))) {
            search = null; // Ignore invalid boolean values
        }
        return toUserModel(realm, repository.findUsers(search, pageable));
    }
    
    
    @Override
    public UserModel addUser(RealmModel realm, String username) {
        // from documentation: "If your provider has a configuration switch to turn off adding a user, returning null from this method will skip the provider and call the next one."
        return null;
    }
    
    
    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        boolean userRemoved = repository.removeUser(user.getUsername());
        
        if (userRemoved) {
            log.infov("deleted keycloak user: realm={0} userId={1} username={2}", realm.getId(), user.getId(), user.getUsername());
        }
        
        return userRemoved;
    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String s) {
        log.infov("lookup user by id: realm={0} userId={1}", realmModel.getId(), s);

        String externalId = StorageId.externalId(s);
        Map<String, String> user = repository.findUserById(externalId);

        if (user == null) {
            log.debugv("findUserById returned null, skipping creation of UserAdapter, expect login error");
            return null;
        } else {
            List<String> roles2 = repository.findRolesByUsername(user.get("username"));
            return new UserAdapter(session, realmModel, model, user, allowDatabaseToOverwriteKeycloak,roles2);
        }
    }


    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String username) {
        Optional<Map<String, String>> userOpt = repository.findUserByUsername(username);
        if (userOpt.isPresent()) {
            Map<String, String> user = userOpt.get();
            List<String> roles = repository.findRolesByUsername(username); // ✅ Your custom method
            return new UserAdapter(session, realmModel, model, user, allowDatabaseToOverwriteKeycloak, roles);
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String s) {
        log.infov("lookup user by username: realm={0} email={1}", realmModel.getId(), s);

        return repository.findUserByEmail(s)
                .map(u -> new UserAdapter(session, realmModel, model, u, allowDatabaseToOverwriteKeycloak,roles))
                .orElse(null);

    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realmModel, Map<String, String> map, Integer integer, Integer integer1) {
        if(integer == null){
            integer = 0;
        }
        log.infov("search for users with params: realm={0} firstResult={1} maxResults={2} params={3}", realmModel.getId(), integer, integer1, map);
        String searchQuery = map.values().stream().findFirst().orElse(null);
        List<UserModel> users = internalSearchForUser(searchQuery, realmModel, new PagingUtil.Pageable(integer, integer1));

        return users.stream(); // Convert List to Stream
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realmModel, GroupModel groupModel, Integer integer, Integer integer1) {
        log.infov("search for group members: realm={0} groupId={1} firstResult={2} maxResults={3}", groupModel.getId(), groupModel.getId(), integer, integer1);
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realmModel, String s, String s1) {
        log.infov("search for group members: realm={0} attrName={1} attrValue={2}", realmModel.getId(), s, s1);
        return Stream.empty();
    }


}
