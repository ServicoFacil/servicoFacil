
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("servicofacil/prestador/v1")
@Slf4j
public class PrestadorController {


    @Autowired
    private PrestadorService prestadorService;

    @PostMapping(path = "/inserir")
    public ResponseEntity save(@RequestBody @Valid PrestadorRequest prestador) throws ServicoFacilException {
        log.info("Recebendo uma novo prestador");
        CreatePrestadorResponse createPrestadorResponse = prestadorService.savePrestador(prestador);
        return ResponseEntity.ok(Objects.requireNonNullElse(createPrestadorResponse, HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/ativar-conta/{token}")
    public String ativarContaPrestador(@PathVariable String token) throws ServicoFacilException {
        prestadorService.ativarPrestador(token);
        return "Prestador Ativado com Sucesso!";
    }

    @PutMapping(path = "/dados-servicos")
    public ResponseEntity updateByCpf( @RequestBody @Valid DadosServico dados) throws ServicoFacilException {
        log.info("Atualizando dados do prestador");
        if (dados == null) {
            return ResponseEntity.badRequest().build();
        }
        DadosServico dadosServico = prestadorService.updateDadosServico(dados);
        return ResponseEntity.ok(Objects.requireNonNullElse(dadosServico, HttpStatus.CONFLICT));
    }

    @GetMapping(path = "/buscaDadosPrestador")
    public ResponseEntity<Page<PrestadorResponse>> buscaDadosPrestador(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "formaPagamento", required = false) String formaPagamento,
            @RequestParam(value = "cnpj", required = false) String cnpj,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "tempoExperiencia", required = false) Integer tempoExperiencia,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws ServicoFacilException {
        Pageable pageable = PageRequest.of(page, size);
        Page<PrestadorResponse> prestadores = prestadorService.buscaDadosPrestador(nome, formaPagamento, cnpj, categoria, tempoExperiencia, pageable);
        return ResponseEntity.ok(prestadores);
    }
}

