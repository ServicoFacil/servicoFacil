package br.com.servicoFacil.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "endereco")
@NoArgsConstructor
public class Endereco {

    public String cep;
    public String estado;
    public String logradouro;
    public String bairro;
    public String numero;
    public String referencia;

}
