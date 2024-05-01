package br.com.servicoFacil.repository;

import br.com.servicoFacil.model.entity.Prestador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PrestadorRepositoryCustom {
     Page<Prestador> findByDynamicQuery(String nome, String formaPagamento, String cnpj, String categoria, Integer tempoExperiencia, Pageable pageable);
}
