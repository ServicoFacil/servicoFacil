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
@NoArgsConstructor
public class Prestador extends Usuario implements Serializable {

    private String cnpj;
    private String idCliente;
    private DadosServico dadosServico;
    private String tokenConfirmacao;
    private LocalDateTime expiracaoToken;
    private boolean ativo;


}
