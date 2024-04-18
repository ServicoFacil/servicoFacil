package br.com.servicoFacil.repository;

import br.com.servicoFacil.model.entity.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends MongoRepository<Cliente, String> {
    Optional<Cliente> findByCpf(String cpf);

    @Query(value = "{ 'cpf' : ?0 }", fields = "{ 'id' : 1 }")
    Optional<String> findIdByCpf(String cpf);

    @Query(value = "{'_id': ?0}", fields = "{'_id': 1}")
    Optional<String> saveAndReturnId(Cliente cliente);

    boolean existsByEmail(String email);
}
