package br.com.servicoFacil.repository;

import br.com.servicoFacil.model.entity.Cliente;
import br.com.servicoFacil.model.entity.Prestador;
import br.com.servicoFacil.model.entity.PrestadorTemporario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PrestadorTemporarioRepository extends MongoRepository<PrestadorTemporario, String> {
    Optional<PrestadorTemporario> findByCpf(String cpf);

    Optional<PrestadorTemporario> findByTokenConfirmacao(String token);

    boolean existsByDadosServicoCnpj(String cnpj);

}
