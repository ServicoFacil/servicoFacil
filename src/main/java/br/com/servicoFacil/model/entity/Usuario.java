package br.com.servicoFacil.model.entity;

import br.com.servicoFacil.model.enums.TipoUsuarioEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Document(collection = "usuario")
@NoArgsConstructor
public class Usuario implements Serializable {

    @Id
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime criacao;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime modificacao;
    private TipoUsuarioEnum tipoUsuario;
}
