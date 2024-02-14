package it.quix.pittini.checker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobRunningDTO {
    private String name;
    private String context;
    private LocalDateTime DATE_START;
    private String STATE;


    @Override
    public String toString(){
        return "Name: "+name+
                "\nContext: "+context+
                "\nState: "+STATE+
                "\nStartDate: "+DATE_START+
                "\n \n";
    }
}