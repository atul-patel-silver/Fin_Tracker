package org.keycloak.fedration.persistence;


import org.hibernate.dialect.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum RDBMS {
    POSTGRESQL("PostgreSQL 10+", org.postgresql.Driver.class.getName(), "SELECT 1", new PostgreSQLDialect()),
    MYSQL("MySQL 5.7+", "com.mysql.cj.jdbc.Driver", "SELECT 1", new MySQLDialect()),
    ORACLE("Oracle 12+", "oracle.jdbc.OracleDriver", "SELECT 1 FROM DUAL", new OracleDialect()),
    IBMDB2("IBM DB2", "com.ibm.db2.jcc.DB2Driver", "SELECT 1 FROM SYSIBM.SYSDUMMY1", new DB2Dialect()),
    SQL_SERVER("MS SQL Server 2012+ (jtds)", "net.sourceforge.jtds.jdbc.Driver", "SELECT 1", new SQLServerDialect());

    private final String  desc;
    private final String  driver;
    private final String  testString;
    private final Dialect dialect;

    RDBMS(String desc, String driver, String testString, Dialect dialect) {
        this.desc = desc;
        this.driver = driver;
        this.testString = testString;
        this.dialect = dialect;
    }

    public static RDBMS getByDescription(String desc) {
        for (RDBMS value : values()) {
            if (value.desc.equals(desc)) {
                return value;
            }
        }
        return null;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public static List<String> getAllDescriptions() {
        return Arrays.stream(values()).map(RDBMS::getDesc).collect(Collectors.toList());
    }

    public String getDesc() {
        return desc;
    }

    public String getDriver() {
        return driver;
    }

    public String getTestString() {
        return testString;
    }
}
