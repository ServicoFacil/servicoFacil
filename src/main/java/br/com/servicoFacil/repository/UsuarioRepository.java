package br.com.servicoFacil.repository;

import br.com.servicoFacil.model.entity.Usuario;
import br.com.servicoFacil.model.enums.TipoUsuarioEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

}
