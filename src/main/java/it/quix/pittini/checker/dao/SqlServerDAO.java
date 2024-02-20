package it.quix.pittini.checker.dao;


import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import it.quix.pittini.checker.model.Elaborazione;
import it.quix.pittini.checker.producer.JdbiProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.SystemException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;

import java.time.LocalDateTime;
import java.util.List;


@ApplicationScoped
@Data
@Slf4j
public class SqlServerDAO {

    @Inject
    JdbiProducer jdbiProducer;

    @DataSource("pittini")
    @Inject
    protected AgroalDataSource sqlserver;

    public List<Elaborazione> getList() {
        log.debug("dentro getList");
     String query="SELECT js.queueId,js.dataInserimento, js.dataInizio, js.utenteInserimento, CONCAT(js.entita, ' (', js.nome, ')') as nomeJob,"+
                  " a.descrizione as descrizione, js.filePath, js.codiceProgressivo, js.periodoId, js.priorita, js.stato, p.meseDescrizione + ' ' + p.annoDescrizione as per,js.dataUltimoAggiornamento as ultimoAggiornamento  " +
                  " FROM JOBS_QUEUE js " +
                  "left join albero a ON a.alberoId = js.alberoId " +
                  "left join periodo p ON p.periodoId = js.periodoId " +
                  "where 1 = 1 " +
                  "AND js.stato='RUNNING'" +
                  "order by CASE WHEN js.stato = 'RUNNING' THEN 1 WHEN js.stato = 'STARTED' THEN 2 ELSE 3 END, js.priorita DESC, js.dataInserimento, js.nome, a.descrizione, p.periodoId DESC ";
        Jdbi jdbi=jdbiProducer.getJdbi(sqlserver);
        return jdbi.withHandle(handle -> {
            Query q=handle.createQuery(query);
            return q.mapToBean(Elaborazione.class).list();
        });
    }

    public Boolean getError(String code, LocalDateTime oggi,LocalDateTime fineOggi) {
        String query="select case when js.stato = 'ERROR' THEN 1 ELSE 0 END " +
                "from JOBS_STORICO js " +
                "inner join JOBS_SCHEDULAZIONE js2 on js2.entita =js.entita and js2.entitaChiave =js.entitaChiave " +
                "where js.stato ='ERROR' and js2.nome like :code AND js.dataInserimento  between :dataOggi and :FineOggi";
        Jdbi jdbi=jdbiProducer.getJdbi(sqlserver);
        return jdbi.withHandle(handle -> {
            Query q=handle.createQuery(query).bind("code",code).bind("dataOggi",oggi).bind("FineOggi",fineOggi);
            return q.mapTo(Boolean.class).one();
        });
    }

    public String getErrore(String code, LocalDateTime oggi,LocalDateTime fineOggi) {
        String query="select js.errore " +
                "from JOBS_STORICO js " +
                "inner join JOBS_SCHEDULAZIONE js2 on js2.entita =js.entita and js2.entitaChiave =js.entitaChiave " +
                "where js.stato ='ERROR' and js2.nome like :code AND js.dataInserimento between :dataOggi and :dataFineOggi";
        Jdbi jdbi=jdbiProducer.getJdbi(sqlserver);
        return jdbi.withHandle(handle -> {
            Query q=handle.createQuery(query).bind("code",code).bind("dataOggi",oggi).bind("dataFineOggi",fineOggi);
            return q.mapTo(String.class).one();
        });
    }

    public LocalDateTime getUltimaEsecuzione() {
        String query="select ultimaEsecuzione " +
                "from JOBS_SCHEDULAZIONE  " +
                "where nome like '%produzione%'";
        Jdbi jdbi=jdbiProducer.getJdbi(sqlserver);
        return jdbi.withHandle(handle -> {
            Query q=handle.createQuery(query);
            return q.mapTo(LocalDateTime.class).one();
        });
    }
}
