package br.com.servicoFacil.model.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredenciaisRequestDTO {

    @NotNull(message = "Campo email não pode ser nulo/vazio")
    @Email(message = "Email inválido, por favor verifique")
    private String email;

    private String senha;
}
