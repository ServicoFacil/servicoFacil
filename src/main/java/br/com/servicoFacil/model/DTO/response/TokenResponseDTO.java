package br.com.servicoFacil.model.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenResponseDTO {

    private String email;
    private String token;
}
