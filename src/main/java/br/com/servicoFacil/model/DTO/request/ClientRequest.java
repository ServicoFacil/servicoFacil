package br.com.servicoFacil.model.DTO.request;



import br.com.servicoFacil.model.entity.Endereco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;


@Data
@Builder
public class ClientRequest {

    @NotNull(message = "Campo nome não pode ser nulo/vazio")
    public String nome;
    @NotNull(message = "Campo CPF não pode ser nulo/vazio")
    @CPF(message = "CPF inválido, por favor verifique")
    public String cpf;
    @NotNull(message = "Campo email não pode ser nulo/vazio")
    @Email(message = "Email inválido, por favor verifique")
    public String email;
    public String senha;
    public Boolean isPrestador;
    public Endereco endereco;


}
