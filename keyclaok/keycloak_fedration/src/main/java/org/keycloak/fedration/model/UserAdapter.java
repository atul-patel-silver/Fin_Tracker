package org.keycloak.fedration.model;

import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JBossLog
public class UserAdapter extends AbstractUserAdapterFederatedStorage {

    private final String keycloakId;
    private       String username;
    private  List<String> rolesFromDb;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, Map<String, String> data, boolean allowDatabaseToOverwriteKeycloak,List<String> rolesFromDb) {
        super(session, realm, model);
        this.keycloakId = StorageId.keycloakId(model, data.get("id"));
        this.username = data.get("username");
        this.rolesFromDb = rolesFromDb;
        try {
          Map<String, List<String>> attributes = this.getAttributes();
          for (Entry<String, String> e : data.entrySet()) {
              Set<String>  newValues = new HashSet<>();
              if (!allowDatabaseToOverwriteKeycloak) {
                List<String> attribute = attributes.get(e.getKey());
                if (attribute != null) {
                    newValues.addAll(attribute);
                }
              }
              newValues.add(StringUtils.trimToNull(e.getValue()));
              this.setAttribute(e.getKey(), newValues.stream().filter(Objects::nonNull).collect(Collectors.toList()));
          }
        } catch(Exception e) {
          log.errorv(e, "UserAdapter constructor, username={0}", this.username);
        }
    }


    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Stream<RoleModel> getRoleMappingsStream() {
        return rolesFromDb.stream()
                .map(roleName -> {
                    RoleModel role = realm.getRole(roleName);
                    if (role == null) {
                        role = realm.addRole(roleName);  // Dynamically create if not exist
                    }
                    return role;
                });
    }

    @Override
    public boolean hasRole(RoleModel role) {
        return getRoleMappingsStream()
                .anyMatch(r -> r.getName().equals(role.getName()));
    }
}
