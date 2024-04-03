package br.com.servicoFacil.repository;

import br.com.servicoFacil.model.entity.Prestador;

import java.util.List;

public interface PrestadorRepositoryCustom {
     List<Prestador> findByCriteria(String value);
}
