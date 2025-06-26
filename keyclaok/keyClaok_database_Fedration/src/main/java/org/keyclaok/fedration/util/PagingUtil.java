package org.keyclaok.fedration.util;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.keyclaok.fedration.persistence.RDBMS;

import java.util.regex.Pattern;

public class PagingUtil {

    @SuppressWarnings("RegExpRedundantEscape")
    private static final Pattern SINGLE_QUESTION_MARK_REGEX = Pattern.compile("(^|[^\\?])(\\?)([^\\?]|$)");


    public static class Pageable {
        private final int firstResult;
        private final int maxResults;

        public Pageable(int firstResult, int maxResults) {
            this.firstResult = firstResult;
            this.maxResults = maxResults;
        }
    }

    public static String formatScriptWithPageable(String query, Pageable pageable, RDBMS RDBMS) {

        final Dialect dialect = RDBMS.getDialect();

        String escapedSQL = escapeQuestionMarks(query);

        LimitHandler limitHandler = dialect.getLimitHandler();
        String processedSQL = limitHandler.processSql(escapedSQL, null); // No need for RowSelection

        // Apply pagination directly on query
        processedSQL += " LIMIT " + pageable.maxResults + " OFFSET " + pageable.firstResult;

        return unescapeQuestionMarks(processedSQL);
    }



    private static String unescapeQuestionMarks(String sql) {
        return sql.replaceAll("\\?\\?", "?");
    }

    private static String escapeQuestionMarks(String sql) {
        return sql.replaceAll("\\?", "??");
    }

}
