package br.com.servicoFacil.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CnpjDto {

    private String situacao;

    private String email;
}
