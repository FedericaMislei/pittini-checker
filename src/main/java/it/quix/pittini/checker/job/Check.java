package it.quix.pittini.checker.job;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import it.quix.pittini.checker.dao.SqlServerDAO;
import it.quix.pittini.checker.dto.*;
import it.quix.pittini.checker.model.Elaborazione;
import jakarta.inject.Inject;
import jakarta.transaction.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Slf4j
public class Check {
    @Inject
    SqlServerDAO sqlServerDAO;
    @Inject
    Mailer mailer;
    @ConfigProperty(name = "config.notification.pittini.emails")
    String email;

    @ConfigProperty(name = "custom.mail.1.type")
    String tipo1;

    @ConfigProperty(name = "custom.mail.2.type")
    String tipo2;
    @ConfigProperty(name = "custom.mail.3.type")
    String tipo3;
    @ConfigProperty(name = "custom.mail.4.type")
    String tipo4;

    @ConfigProperty(name = "custom.mail.5.type")
    String tipo5;
    @ConfigProperty(name = "custom.mail.6.type")
    String tipo6;
    @ConfigProperty(name = "custom.mail.7.type")
    String tipo7;
    List<ControlloDTO> lista= new ArrayList<>();
    Boolean errore=false;
    @Inject
    Config globals;



    //DA SPEDIRE PIù VOLTE AL GIORNO: ORARIO DA CONCORDARE
    @Scheduled(cron = "0 0/3 * ? * * *", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void start() throws Exception {
        send("federica.mislei@quix.it",tipo1);
        send("federica.mislei@quix.it",tipo2);
        send("federica.mislei@quix.it",tipo6);
        send("federica.mislei@quix.it",tipo7);
        for(ControlloDTO d:lista){
            if(d.getErrore()){
                errore=true;
                break;
            }
        }
        log.debug("errore: "+errore);
        inviaEmail(email,lista,errore.toString(),"federica.mislei@quix.it");
        lista.clear();
    }

    //DA SPEDIRE 1 VOLTA AL GIORNO: ORARIO DA CONCORDARE
    //@Scheduled(cron = "0 0/3 * ? * * *", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void start1() throws Exception {

        send("federica.mislei@quix.it",tipo3);
        send("federica.mislei@quix.it",tipo4);
        send("federica.mislei@quix.it",tipo5);
        for(ControlloDTO d:lista){
            if(d.getErrore()){
                errore=true;
                break;
            }
        }
        log.debug("errore: "+errore);
        inviaEmail(email,lista,errore.toString(),"federica.mislei@quix.it");
        lista.clear();
    }



    private void send(String email, String tipocontrollo) throws Exception {
        if(tipocontrollo.equals("Elaborazioni in corso")){
            List<Elaborazione> vecchie=new ArrayList<>();
            List<Elaborazione> listaElaborazioni=sqlServerDAO.getList();
            LocalDateTime ora=LocalDateTime.now().minusMinutes(30);
            for(Elaborazione e: listaElaborazioni){
                if(e.getUltimoAggiornamento().isBefore(ora)){
                    vecchie.add(e);
                }
            }
            ControlloDTO controlloDTO=new ControlloDTO();
            if(vecchie.size()<=0){
                controlloDTO.setErrore(false);
            }else{
                controlloDTO.setErrore(true);
            }
            controlloDTO.setControllo1(tipo1);
            controlloDTO.setValue1(String.valueOf(vecchie.size()));
            controlloDTO.setValue2(String.valueOf(ora));
            controlloDTO.setIstruzioni1("Elimina le seguenti elaborazioni");
            for(Elaborazione e: vecchie){
                controlloDTO.setIstruzioni1(controlloDTO.getIstruzioni1()+ e.getNomeJob());
            }
            lista.add(controlloDTO);
        }else if (tipocontrollo.equals("Qery")){
            for(String code: globals.rest().keySet()){
                if(globals.rest().get(code).type().equals("app")) {
                    ControlloDTO c = elab("REST", code, globals.rest().get(code));
                    lista.add(c);
                }
            }
            //inviaEmail(email,lista,c.getErrore().toString(),"haoran.chen@quix.it");
        }else if(tipocontrollo.equals("Import fonti indici")){
            for(String code: globals.rest().keySet()){
                if(globals.rest().get(code).type().equals("job")) {
                    ControlloDTO c = elabJob("REST", code, globals.rest().get(code));
                    lista.add(c);
                }
            }

        } else if (tipocontrollo.equals("Import sentinel")){
            for(String code: globals.rest().keySet()){
                if(globals.rest().get(code).type().equals("job")) {
                    ControlloDTO c = elabJob("REST", code, globals.rest().get(code));
                    lista.add(c);
                }
            }
        } else if (tipocontrollo.equals("Import dati produzione")) {
            for(String code: globals.rest().keySet()){
                if(globals.rest().get(code).type().equals("job")) {
                    ControlloDTO c = elabJob("REST", code, globals.rest().get(code));
                    lista.add(c);
                }
            }
            
        }else if (tipocontrollo.equals("Elastic qdoc2")){
            for(String code: globals.rest().keySet()){
                if(globals.rest().get(code).type().equals("job")) {
                    ControlloDTO c = elab("REST", code, globals.rest().get(code));
                    lista.add(c);
                }
            }

        } else if (tipocontrollo.equals("Elastic Liferay")) {
            for(String code: globals.rest().keySet()){
                if(globals.rest().get(code).type().equals("job")) {
                    ControlloDTO c = elabJob("REST", code, globals.rest().get(code));
                    lista.add(c);
                }
            }
            
        }


    }



    private ControlloDTO elab(String type, String code, CheckConfig config) throws Exception {
        Map<String, CheckResult> results = new HashMap<>();
        ControlloDTO c=new ControlloDTO();
        if (results.get(code) == null) {
            results.put(code, new CheckResult());
        }
        CheckResult r = results.get(code);
        try {
            log.info("Begin check " + code);
            // TODO Check is time

            // TODO
            try {
                RestConfig rc = (RestConfig) config;
                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(5)
                        .setConnectTimeout(5)
                        .build();
                CloseableHttpClient client = HttpClients.custom()
                        .setDefaultRequestConfig(requestConfig)
                        .build();

                Executor executor = Executor.newInstance();
                Response e = executor.execute(Request.Get(rc.url()));
                int status = e.returnResponse().getStatusLine().getStatusCode();
                if (status > 399) {
                    r.ko();
                    c.setErrore(true);
                    c.setValue1(String.valueOf(status));
                } else {
                    r.ok();
                    c.setErrore(false);
                    c.setValue1(String.valueOf(status));
                }
            } catch (Exception e) {
                // TODO Log
                log.warn("Error on call url", e);
                r.ko();
                c.setErrore(true);
                c.setValue1(e.getMessage());
            }
            log.info("End check " + code);
        } catch (Exception e) {
            log.error("Error on exceute check " + code);
            r.error();
            c.setErrore(true);
        }
        if(c.getErrore()) {
            c.setIstruzioni1("Riavvia il servizio" +config.name() );
        }else{
            c.setIstruzioni1("");
        }
        c.setControllo1("Applicazione "+ config.name());
        return c;
    }

    private void inviaEmail(String dest, List<ControlloDTO> list, String email, String cc) {
        log.info("inizio invio email");
        StringWriter wtitle = new StringWriter();
        StringWriter wbody = new StringWriter();
        try {
            Configuration cfg = new Configuration();
            // Where do we load the templates from:
            cfg.setClassForTemplateLoading(Check.class, "templates");
            // Some other recommended settings:
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLocale(Locale.ITALY);
            Map<String, Object> vars = new HashMap<>();
            vars.put("email", email);
            vars.put("list", list);
            // title
            InputStream in = this.getClass().getResourceAsStream("/templates/title.ftl");
            String templateStrTitle = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            Template template = new Template("title", new StringReader(templateStrTitle), cfg);
            template.process(vars, wtitle);
            wtitle.close();
            String object = wtitle.toString();
            log.debug("oggetto:"+object);
            // title
            InputStream in2 = this.getClass().getResourceAsStream("/templates/body.ftl");
            String templateStrBody = new String(in2.readAllBytes(), StandardCharsets.UTF_8);
            template = new Template("body", new StringReader(templateStrBody), cfg);
            template.process(vars, wbody);
            wbody.close();
            String text = wbody.toString();
            // TODO
            log.debug("Send email to = " + dest);
            log.debug("Object = " + object);
            log.debug("Text = " + text);
            if (dest.toLowerCase().contains("support")) {
                return;
            }
            sendEmail(object, text, dest, cc); // TODO destinatario
            log.info("fine invio email");
            object=null;
        } catch (Exception e) {
            log.error("Error on send email", e);
        } finally {
            try {
                wtitle.close();
            } catch (IOException e) {
            }
            try {
                wbody.close();
            } catch (IOException e) {
            }
        }
    }

    private void sendEmail(String subject, String body, String emailTo, String cc) throws Exception {
        log.debug("sendEmail");
        try {
            List<String> list=List.of(emailTo.split(";"));
            Set<String> emailToo = new HashSet<String>(list);

            Mail mail = new  Mail();
            //copia conoscenza
            mail.addCc(cc);
            mail.addTo(toArray(emailToo));
            mail.setSubject(subject);
            mail.setHtml(body);
            //mail.addCc("luca.marcone@quix.it");
            mailer.send(mail);

            //Mail mail = Mail.withHtml(, subject, body);
            //copia conoscenza
            //mail.addCc(cc);
            //mail.addCc("luca.marcone@quix.it");
            //mailer.send(mail);
            //  }
        } catch (Exception e) {
            log.error("Errore durante l'invio della mail", e);
            throw e;
        }
    }

    private ControlloDTO elabJob(String rest, String code, RestConfig restConfig) throws SystemException {
        ControlloDTO c=new ControlloDTO();
        LocalDateTime oggi= LocalDate.now().atStartOfDay();
        LocalDateTime fineOggi=LocalDate.now().atTime(23,59,59,999);
        if(code.equals("downloadIndiciEEX")){
            Boolean er=sqlServerDAO.getError(code.substring(14,3),oggi,fineOggi);
            if(er){
                c.setControllo1(restConfig.name()+" ultima esecuzione andata in errore");
                String erroreEsecuzione=sqlServerDAO.getErrore(code.substring(14,3),oggi,fineOggi);
                c.setValue1(erroreEsecuzione);
                c.setValue2("");
                c.setErrore(true);
                c.setIstruzioni1("Controllare"+ restConfig.name());
            }
        }
        if(code.equals("downloadIndiciGME")){
            Boolean er=sqlServerDAO.getError(code.substring(14,3),oggi,fineOggi);
            if(er){
                c.setControllo1(restConfig.name()+" ultima esecuzione andata in errore");
                String erroreEsecuzione=sqlServerDAO.getErrore(code.substring(14,3),oggi,fineOggi);
                c.setValue1(erroreEsecuzione);
                c.setValue2("");
                c.setErrore(true);
                c.setIstruzioni1("Controllare"+ restConfig.name());
            }
        }
        if(code.equals("downloadIndiciICIS-API")){
            Boolean er=sqlServerDAO.getError(code.substring(14,8).replace("-"," "),oggi,fineOggi);
            if(er){
                c.setControllo1(restConfig.name()+" ultima esecuzione andata in errore");
                String erroreEsecuzione=sqlServerDAO.getErrore(code.substring(14,3),oggi,fineOggi);
                c.setValue1(erroreEsecuzione);
                c.setValue2("");
                c.setErrore(true);
                c.setIstruzioni1("Controllare"+ restConfig.name());
            }
        }
        if(code.equals("importSentinel")){
            Boolean er=sqlServerDAO.getError(code.substring(6,code.length()),oggi,fineOggi);
            if(er){
                c.setControllo1(restConfig.name()+" ultima esecuzione andata in errore");
                String erroreEsecuzione=sqlServerDAO.getErrore(code.substring(6,code.length()),oggi,fineOggi);
                c.setValue1(erroreEsecuzione);
                c.setValue2("");
                c.setErrore(true);
                c.setIstruzioni1("Controllare"+ restConfig.name());
            }
        }
        if(code.equals("importProduzione")){
            Boolean er=sqlServerDAO.getError(code.substring(6,code.length()),oggi,fineOggi);
            if(er){
                c.setControllo1(restConfig.name()+" ultima esecuzione andata in errore");
                String erroreEsecuzione=sqlServerDAO.getErrore(code.substring(6,code.length()),oggi,fineOggi);
                c.setValue1(erroreEsecuzione);
                c.setValue2("");
                c.setErrore(true);
                c.setIstruzioni1("Controllare"+ restConfig.name());
            }
        }
        return c;
    }
    private String [] toArray(Collection<String> to) {
        String [] arr = new String[to.size()];
        int i = 0;
        for(String t: to) {
            arr[i] = StringUtils.trim(t);
            i++;
        }
        return arr;
    }
}
