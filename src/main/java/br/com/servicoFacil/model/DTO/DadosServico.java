package br.com.servicoFacil.model.DTO;

import br.com.servicoFacil.model.entity.Endereco;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

import java.util.List;

@Builder
@Data
@Setter
public class DadosServico {

    private List<String> categoria;
    private String descExperiencia;
    private int tmpExperiencia;
    @CNPJ(message = "CNPJ inválido, por favor verifique")
    private String cnpj;
    private Boolean cnpjAtivo;
    private String telefone;
    private String emailCoorporativo;
    private String nomeAlternativo;
    private Endereco endereco;
    private boolean orcamento;
    private String formaPagamento;

}
