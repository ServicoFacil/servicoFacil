package br.com.servicoFacil.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ServicoFacilError {
    SF9999("Erro interno", HttpStatus.NOT_FOUND),
    SF0001("Prestador não encontrado", HttpStatus.NOT_FOUND),
    SF0002("Erro ao validar CNPJ", HttpStatus.BAD_REQUEST),
    SF0003("Erro ao salvar prestador", HttpStatus.BAD_REQUEST),
    SF0004("Erro ao vincular perfis", HttpStatus.BAD_REQUEST),
    SF0005("Erro ao buscar cliente", HttpStatus.BAD_REQUEST),
    SF006("Erro ao salvar cliente", HttpStatus.BAD_REQUEST),
    SF007("Token não é válido", HttpStatus.UNAUTHORIZED),
    SF008("CNPJ já existente na base de dados", HttpStatus.IM_USED),
    SF009("Senha inválida", HttpStatus.CONFLICT),
    SF010("Nenhum prestador encontrado com esses critérios", HttpStatus.NOT_FOUND),

    SF011("Token expiradado", HttpStatus.UNAUTHORIZED);

    @Getter
    private String description;

    ServicoFacilError(String description, HttpStatus notFound) {
        this.description = description;
    }
}