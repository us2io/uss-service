package io.us2.svc.seed.services;

import akka.actor.ActorSystem;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Singleton
public class DB {
    private final ExecutionContextExecutor databaseContext;
    private Sql2o sql;

    @Inject
    public DB(Sql2o sql, ActorSystem actorSystem) {
        this.sql = sql;
        this.databaseContext = actorSystem.dispatchers().lookup("contexts.database");
    }

    public <A> CompletionStage<A> query(Function<Connection, A> block) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection con = sql.open()) {
                return block.apply(con);
            }
        }, databaseContext);
    }

    public <A> CompletionStage<A> withTransaction(Function<Connection, A> block) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection con = sql.beginTransaction()) {
                A ret = block.apply(con);
                con.commit();
                return ret;
            }
        }, databaseContext);
    }

    public CompletionStage<Boolean> isValid(int timeout) {
        return query(con -> {
            try {
                return con.getJdbcConnection().isValid(timeout);
            } catch (SQLException e) {
                return false;
            }
        });
    }
}
