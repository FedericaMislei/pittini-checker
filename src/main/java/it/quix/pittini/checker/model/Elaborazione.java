package it.quix.pittini.checker.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Elaborazione {
    private LocalDateTime dataInserimento;
    private LocalDateTime dataInizio;
    private String utenteInserimento;
    private Long periodoId;
    private String nomeJob;
    private String descrizione;
    private String filePath;
    private String codiceProgressivo;
    private String per;
    private int priorita;
    private String stato;
    private String queueId;
    private LocalDateTime ultimoAggiornamento;
}
