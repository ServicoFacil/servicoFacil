package br.com.servicoFacil.error;

import lombok.Getter;

public enum ServicoFacilError {
    SF9999("Erro interno"),
    SF0404("Erro ao buscar"),
    SF0002("Erro ao validar CNPJ"),
    SF0003("Erro ao salvar prestador"),
    SF0004("Erro ao vincular perfis"),
    SF0001("Prestador não encontrado"),
    SF0005("Erro ao buscar cliente"),
    SF006("Erro ao salvar cliente"),;

//TODO: Ajustar devidamente as constantes de error
    @Getter
    private String description;

    ServicoFacilError(String description) {
        this.description = description;
    }
}