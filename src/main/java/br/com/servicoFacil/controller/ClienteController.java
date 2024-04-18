package br.com.servicoFacil.controller;

import br.com.servicoFacil.error.ServicoFacilException;
import br.com.servicoFacil.model.DTO.request.ClientRequest;
import br.com.servicoFacil.model.DTO.response.ClienteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.servicoFacil.service.ClienteService;

import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("servicofacil/cliente/v1")
@Slf4j
public class ClienteController {

    //TODO: Definir permissões para cada endpoint - Verificar papeis na classe se SecurityConfig

    @Autowired
    private ClienteService clienteService;

    @PostMapping(path = "/inserir")
    public ResponseEntity<String> save(@RequestBody ClientRequest user) throws ServicoFacilException {
        log.info("Recebendo uma novo cliente");
         Optional<String> codigoCli = clienteService.saveOrUpdateCliente(user);
        return ResponseEntity.ok(Objects.requireNonNullElse(codigoCli, HttpStatus.CONFLICT).toString());
    }
    @GetMapping(path = "/buscarDadosClientes/{cpf}")
    public ResponseEntity busca(@PathVariable("codigo") String cpf) throws ServicoFacilException {
        log.info("Buscando cliente");
        if(cpf == null){
            return ResponseEntity.ok(HttpStatus.CONFLICT);
        }
        ClienteResponse cliente = clienteService.buscaCliente(cpf);
        return ResponseEntity.ok(Objects.requireNonNullElse(cliente, HttpStatus.CONFLICT));
    }


}
