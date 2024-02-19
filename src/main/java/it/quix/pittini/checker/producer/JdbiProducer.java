package it.quix.pittini.checker.producer;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
@Slf4j
public class JdbiProducer {


    public Jdbi getJdbi(DataSource dataSource) {

        Jdbi jdbi = Jdbi.create(dataSource)
                .installPlugin(new SqlObjectPlugin());

        configureJdbi(jdbi);

        return jdbi;
    }
    private void configureJdbi(Jdbi jdbi) {
        jdbi.setSqlLogger(new SqlLogger() {

            @Override
            public void logBeforeExecution(StatementContext context) {
                if (log.isDebugEnabled()) {
                    log.debug("Execute query: " + context.getRenderedSql());
                    log.debug(context.getBinding().toString());
                }
            }

            @Override
            public void logAfterExecution(StatementContext context) {
                if (log.isDebugEnabled()) {
                    log.debug("Query time: " + context.getElapsedTime(ChronoUnit.MILLIS) + " ms.");
                }
            }

            @Override
            public void logException(StatementContext context, SQLException ex) {
                log.error("Error on execute query " + context.getRenderedSql() + " Error:", ex);
            }
        });
    }

}
