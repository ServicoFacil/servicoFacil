package br.com.servicoFacil.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Document(collection = "cliente")
@NoArgsConstructor
public class Cliente implements Serializable {

    @Id
    private String id;
    private String nome;
    private String email;
    private String cpf;
    private String senha;
    private String idPrestador;
    private Endereco endereco;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime criacao;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime modificacao;
}
