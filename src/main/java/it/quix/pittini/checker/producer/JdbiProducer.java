package it.quix.pittini.checker.producer;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;

@CommonsLog
@Component(value = "jdbiGeoProducer")
@Getter
@Setter
public class JdbiProducer {

    protected Jdbi jdbi;

    private DataSource geoDataSource;

    @Autowired
    public void JdbiGeoProducer(@Qualifier("pittini")  DataSource dataSource) {
        geoDataSource = dataSource;
        init();
    }

    public void init() {
        jdbi = Jdbi.create(geoDataSource);
        SqlLogger myLogger = new SqlLogger() {

            @Override
            public void logBeforeExecution(StatementContext context) {
                log.debug("Execute query: \n\n" + context.getRenderedSql());
                log.debug(context.getBinding());
            }

            @Override
            public void logAfterExecution(StatementContext context) {
                if (log.isDebugEnabled()) {
                    log.debug("Query time: " + context.getElapsedTime(ChronoUnit.MILLIS) + " ms.\n");
                }

            }

            @Override
            public void logException(StatementContext context, SQLException ex) {
                log.error("Error on execute query \n " + context.getRenderedSql() + "\n\n Error:", ex);

            }
        };
        jdbi.setSqlLogger(myLogger);
    }

}
