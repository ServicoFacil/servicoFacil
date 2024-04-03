package br.com.servicoFacil.model.DTO.response;

import br.com.servicoFacil.model.DTO.DadosServico;
import br.com.servicoFacil.model.entity.Endereco;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrestadorResponse {

    public String id;
    public String nome;
    public String email;
    public String cpf;
    public String idCliente;
    public Endereco endereco;
    public Boolean cnpjAtivo;
    public DadosServico dadosServico;

}
