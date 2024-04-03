package br.com.servicoFacil.model.DTO.response;

import br.com.servicoFacil.model.entity.Endereco;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponse {

    public String id;
    public String nome;
    public String email;
    public String cpf;
    public String idPrestador;
    public Endereco endereco;

}
