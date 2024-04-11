package br.com.servicoFacil.model.entity;

import br.com.servicoFacil.model.DTO.DadosServico;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Document(collection = "prestador")
@NoArgsConstructor
public class Prestador implements Serializable {
    @Id
    private String id;
    private String nome;
    private String email;
    private String cpf;
    private String senha;
    private String idCliente;
    private DadosServico dadosServico;
    private LocalDateTime criacao;
    private LocalDateTime modificacao;
    private String tokenConfirmacao;
    private LocalDateTime expiracaoToken;
    private boolean ativo;


}
