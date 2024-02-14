package it.quix.pittini.checker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobFinishedDTO {
    private String name;
    private String context;
    private LocalDateTime DATE_START;
    private int duration;
    private String state;
    @Override
    public String toString(){
        return "Name: "+name+
                "\nContext: "+context+
                "\nstartDate: "+DATE_START+
                "\nduration: "+duration+"ms"+
                "\nSTATE: "+state+
                "\n  \n";
    }

}