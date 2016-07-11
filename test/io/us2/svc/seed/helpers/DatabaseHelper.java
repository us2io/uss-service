package io.us2.svc.seed.helpers;

import io.us2.svc.seed.services.QueryStore;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class DatabaseHelper {
    public void executeStatement(QueryStore store, String queryName, Sql2o sql) {
        String query = store.getQuery(queryName);

        try (Connection con = sql.open()) {
            con.createQuery(query).executeUpdate();
        }
    }

    public void executeStatements(List<String> statements, Sql2o sql) {
        try (Connection con = sql.open()) {
            statements.forEach(query -> {
                con.createQuery(query).executeUpdate();
            });
        }
    }

}
