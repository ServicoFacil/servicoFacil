package br.com.servicoFacil.model.DTO.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePrestadorResponse {

    private String id;
    private Boolean cnpjAtivo;
}
