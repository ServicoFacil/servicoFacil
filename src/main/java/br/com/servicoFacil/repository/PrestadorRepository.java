package br.com.servicoFacil.repository;

import br.com.servicoFacil.model.entity.Prestador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestadorRepository extends MongoRepository<Prestador, String>, PrestadorRepositoryCustom{
    Optional<Prestador> findByCpf(String cpf);

    @Query(value = "{ 'cpf' : ?0 }", fields = "{ 'id' : 1 }")
    Optional<String> findIdByCpfUsingQuery(String cpf);

    boolean existsByDadosServicoCnpj(String cnpj);

    Optional<Prestador> findByTokenConfirmacao(String token);
    @Override
    Page<Prestador> findByDynamicQuery(String nome, String formaPagamento, String cnpj, String categoria, int tempoExperiencia, Pageable pageable);

}
