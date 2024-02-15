package it.quix.pittini.checker.dao;


import it.quix.pittini.checker.model.Elaborazione;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.SystemException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.jdbc.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static it.quix.framework.core.dao.AbstractJDBCDAO.*;
import static it.quix.framework.core.dao.ConnectionLocator.getConnection;
import static org.apache.pdfbox.pdfwriter.COSStandardOutputStream.EOL;
import static org.flywaydb.core.internal.jdbc.JdbcUtils.closeConnection;
import static org.flywaydb.core.internal.jdbc.JdbcUtils.closeStatement;

@ApplicationScoped
@Data
@Slf4j
public class SqlServerDAO {


    public List<Elaborazione> getList() throws SystemException {
        List<Elaborazione> list = new ArrayList<Elaborazione>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            // Compose the select query
            StringBuilder query = new StringBuilder();
            query.append("SELECT js.dataInserimento, js.dataInizio, js.utenteInserimento, js.dataUltimoAggiornamento,\" +\n" +
                    "                \"CONCAT(js.entita, ' (', js.nome, ')') as nomeJob, a.descrizione, js.filePath, js.codiceProgressivo, js.periodoId, js.priorita, js.stato, p.meseDescrizione + ' ' + p.annoDescrizione as per \" +\n" +
                    "                \"FROM JOBS_QUEUE js \" +\n" +
                    "                \"left join albero a ON a.alberoId = js.alberoId \" +\n" +
                    "                \"left join periodo p ON p.periodoId = js.periodoId \" +\n" +
                    "                \"where 1 = 1 \" +\n" +
                    "                \"AND js.stato='RUNNING' \" +\n" +
                    "                \"order by CASE WHEN js.stato = 'RUNNING' THEN 1 WHEN js.stato = 'STARTED' THEN 2 ELSE 3 END, js.priorita DESC, js.dataInserimento, js.nome, a.descrizione, p.periodoId DESC ").append(EOL);



            // Get connection
            connection = getConnection();
            // Prepare the statement
            statement = connection.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // Set the parameters

            // Execute the query
            long startTime = System.currentTimeMillis();
            rs = statement.executeQuery();
            long endTime = System.currentTimeMillis();
            long queryRunningTime = endTime - startTime;

            while (rs.next()) {
                Elaborazione elaborazione = buildModelFromResultSet(rs);
                list.add(elaborazione);

            }
            return list;
        } catch (SQLException ex) {
            String msg = "Unexpeted error on find elaborazioni  on database.";
            if (log.isErrorEnabled()) {
                log.error(msg, ex);
            }
            throw new SystemException();
        } finally {
            JdbcUtils.closeResultSet(rs);
            closeStatement(statement);
            closeConnection(connection);
        }

    }

    public Elaborazione buildModelFromResultSet(ResultSet rs) throws SQLException {
        Elaborazione elab = new Elaborazione();

        elab.setPer(getParameterString(rs, "per"));
        elab.setCodiceProgressivo(getParameterString(rs, "codiceProgressivo"));
        elab.setNomeJob(getParameterString(rs, "nomeJob"));
        elab.setDescrizione(getParameterString(rs, "descrizione"));
        elab.setFilePath(getParameterString(rs, "filePath"));
        elab.setPeriodoId(getParameterLong(rs, "periodoInizioId"));
        elab.setPriorita(getParameterInteger(rs, "priorita"));
        elab.setStato(getParameterString(rs, "stato"));
        elab.setQueueId(getParameterString(rs, "queueId"));
        elab.setUtenteInserimento(getParameterString(rs, "utenteInserimento"));
        elab.setDataInizio(LocalDateTime.parse(getParameterString(rs, "dataInizio")));
        elab.setDataInserimento(LocalDateTime.parse(getParameterString(rs, "dataInserimento")));
        elab.setUltimoAggiornamento(LocalDateTime.parse(getParameterString(rs, "ultimoInserimento")));

        return elab;
    }


}
