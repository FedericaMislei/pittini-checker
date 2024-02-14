package it.quix.pittini.checker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobSlowDTO {
    private String name;
    private LocalDateTime dateStart;
    private int duration;
    @Override
    public String toString(){
        return "Name: "+name+
                "\nDate Start: "+dateStart+
                "\nduration: "+duration+"ms"+
                "\n  \n";
    }
}
