package it.quix.pittini.checker.dao;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import it.quix.pittini.checker.producer.JdbiProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;

@ApplicationScoped
@Data
@Slf4j
public class SqlServerDAO {
    @Inject
    JdbiProducer jdbiProducer;

    @DataSource("pittini")
    @Inject
    protected AgroalDataSource mysql;
    public int countMySql(){
        Jdbi jdbi=jdbiProducer.getJdbi(mysql);
        String select="SELECT js.dataInserimento, js.dataInizio, js.utenteInserimento, js.queueId, js.dataUltimoAggiornamento,\n" +
                "CONCAT(js.entita, ' (', js.nome, ')') as nomeJob, a.descrizione, js.filePath, js.codiceProgressivo, js.periodoId, js.priorita, js.stato, p.meseDescrizione + ' ' + p.annoDescrizione as per\n" +
                "FROM JOBS_QUEUE js\n" +
                "left join albero a ON a.alberoId = js.alberoId\n" +
                "left join periodo p ON p.periodoId = js.periodoId\n" +
                "where 1 = 1\n" +
                "<#if filtroEntita?exists>AND js.entita like :%filtroEntita% OR js.nome like :%filtroEntita%</#if>\n" +
                "<#if filtroCodiceProgressivo?exists>AND js.codiceProgressivo like :%filtroCodiceProgressivo%</#if>\n" +
                "order by CASE WHEN js.stato = 'RUNNING' THEN 1 WHEN js.stato = 'STARTED' THEN 2 ELSE 3 END, js.priorita DESC, js.dataInserimento, js.nome, a.descrizione, p.periodoId DESC " ;
        String s= jdbi.withHandle(handle -> {
            Query query = handle.createQuery(select);
            return query.mapTo(String.class).findOne().get();
        });
        log.info(s);
        return Integer.parseInt(s);
    }
}
