
package br.com.servicoFacil.controller;

import br.com.servicoFacil.error.ServicoFacilException;

import br.com.servicoFacil.model.DTO.DadosServico;
import br.com.servicoFacil.model.DTO.request.PrestadorRequest;
import br.com.servicoFacil.model.DTO.response.CreatePrestadorResponse;
import br.com.servicoFacil.model.DTO.response.PrestadorResponse;
import br.com.servicoFacil.service.PrestadorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("servicofacil/prestador/v1")
@Slf4j
public class PrestadorController {
    @Autowired
    private PrestadorService PrestadorService;

    @PostMapping(path = "/inserir")
    public ResponseEntity save(@RequestBody @Valid PrestadorRequest prestador) throws ServicoFacilException {
        log.info("Recebendo uma novo prestador");
        CreatePrestadorResponse createPrestadorResponse = PrestadorService.saveOrUpdatePrestador(prestador);
        return ResponseEntity.ok(Objects.requireNonNullElse(createPrestadorResponse, HttpStatus.BAD_REQUEST));
    }

    @PutMapping(path = "/dados-servicos/{codigoPres}")
    public ResponseEntity updateByCpf(@PathVariable String cpf, @RequestBody @Valid DadosServico dados) throws ServicoFacilException {
        log.info("Atualizando dados do prestador");
        if (dados == null) {
            return ResponseEntity.badRequest().build();
        }
        DadosServico dadosServico = PrestadorService.updateDadosServico(cpf, dados);
        return ResponseEntity.ok(Objects.requireNonNullElse(dadosServico, HttpStatus.CONFLICT));
    }

    @GetMapping(path = "/buscaDadosPrestador/{cpf}")
    public ResponseEntity buscaDadosPrestador(@PathVariable String cpf) throws ServicoFacilException {
        log.info("Buscando dados do prestador");
        PrestadorResponse prestador = PrestadorService.buscaDadosPrestador(cpf);
        return ResponseEntity.ok(Objects.requireNonNullElse(prestador, HttpStatus.NOT_FOUND));
    }
}

