package it.quix.pittini.checker.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ResponseDTO {

    private String code;

    private String description;


    public ResponseDTO() {
        super();
    }

    public ResponseDTO(String code, String description) {
        this();

        this.setCode(code);
        this.setDescription(description);
    }



}
