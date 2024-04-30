package br.com.servicoFacil.model.DTO.request;

import br.com.servicoFacil.model.DTO.DadosServico;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;



@Data
@Builder
public class PrestadorRequest {

    @NotNull(message = "Campo nome não pode ser nulo")
    @NotBlank(message = "Campo nome não pode ser vazio")
    private String nome;
    @Email(message = "Email inválido, por favor verifique")
    @NotNull(message = "Campo email não pode ser nulo/vazio")
    private String email;
    private String senha;
    @CPF(message = "CPF inválido, por favor verifique")
    @NotNull(message = "Campo cpf não pode ser nulo/vazio")
    private String cpf;
    private Boolean isCliente;
    private DadosServico dadosServico;
}
