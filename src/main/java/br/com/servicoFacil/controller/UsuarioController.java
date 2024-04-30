package br.com.servicoFacil.controller;

import br.com.servicoFacil.error.ServicoFacilError;
import br.com.servicoFacil.error.ServicoFacilException;
import br.com.servicoFacil.model.DTO.request.CredenciaisRequestDTO;
import br.com.servicoFacil.model.DTO.response.TokenResponseDTO;
import br.com.servicoFacil.model.entity.Usuario;
import br.com.servicoFacil.model.enums.TipoUsuarioEnum;
import br.com.servicoFacil.security.JwtService;
import br.com.servicoFacil.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("servicofacil/usuario/v1")
public class UsuarioController {


    private final UsuarioService usuarioService;

    private final JwtService jwtService;

    @PostMapping("/autenticar")
    public TokenResponseDTO autenticar(@RequestBody @Valid CredenciaisRequestDTO dto) throws ServicoFacilException {
        try {
            TipoUsuarioEnum tipoUsuario = usuarioService.retornaTipoUsuario(dto.getEmail());
            Usuario usuario = Usuario.builder().email(dto.getEmail()).senha(dto.getSenha()).tipoUsuario(tipoUsuario).build();
            UserDetails usuarioAutenticado = usuarioService.autenticar(usuario);
            String token = jwtService.gerarToken(usuario);
            return new TokenResponseDTO(usuarioAutenticado.getUsername(), token);

        } catch (Exception e) {
            throw new ServicoFacilException(e, ServicoFacilError.SF009);
        }
    }
}
