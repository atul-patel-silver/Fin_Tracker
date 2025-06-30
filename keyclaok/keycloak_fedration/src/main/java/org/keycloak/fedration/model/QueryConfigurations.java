package org.keycloak.fedration.model;


import org.keycloak.fedration.persistence.RDBMS;

public class QueryConfigurations {

    private String count;
    private String listAll;
    private String findById;
    private String removeById;
    private String findByUsername;
    private String findByEmail;
    private String findBySearchTerm;
    private String findPasswordHash;
    private String hashFunction;
    private RDBMS RDBMS;
    private boolean allowKeycloakDelete;
    private boolean allowDatabaseToOverwriteKeycloak;

    public QueryConfigurations(String count, String listAll, String findById, String removeById, String findByEmail, String findByUsername, String findBySearchTerm, String findPasswordHash, String hashFunction, RDBMS RDBMS, boolean allowKeycloakDelete, boolean allowDatabaseToOverwriteKeycloak) {
        this.count = count;
        this.listAll = listAll;
        this.findById = findById;
        this.removeById = removeById;
        this.findByUsername = findByUsername;
        this.findByEmail = findByEmail;
        this.findBySearchTerm = findBySearchTerm;
        this.findPasswordHash = findPasswordHash;
        this.hashFunction = hashFunction;
        this.RDBMS = RDBMS;
        this.allowKeycloakDelete = allowKeycloakDelete;
        this.allowDatabaseToOverwriteKeycloak = allowDatabaseToOverwriteKeycloak;
    }

    public RDBMS getRDBMS() {
        return RDBMS;
    }

    public String getCount() {
        return count;
    }

    public String getListAll() {
        return listAll;
    }

    public String getFindById() {
        return findById;
    }

    public String getRemoveById(){
        return removeById;
    }

    public String getFindByUsername() {
        return findByUsername;
    }


    public String getFindByEmail() {
        return findByEmail;
    }

    public String getFindBySearchTerm() {
        return findBySearchTerm;
    }

    public String getFindPasswordHash() {
        return findPasswordHash;
    }

    public String getHashFunction() {
        return hashFunction;
    }

    public boolean isBlowfish() {
        return hashFunction.toLowerCase().contains("blowfish");
    }

    public boolean getAllowKeycloakDelete() {
        return allowKeycloakDelete;
    }

    public boolean getAllowDatabaseToOverwriteKeycloak() {
        return allowDatabaseToOverwriteKeycloak;
    }
}
