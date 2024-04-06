package br.com.servicoFacil.client;

import br.com.servicoFacil.model.DTO.CnpjDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;


@FeignClient(name = "servicos", url = "${spring.servicos.address}")
public interface ServicosProxy {
    @GetMapping(path = "${spring.servicos.url}", produces = "application/json")
    Optional<CnpjDto> getCnpj(@PathVariable("cnpj") String cnpj);
}


