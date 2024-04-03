package br.com.servicoFacil.service;

import br.com.servicoFacil.client.ServicosProxy;
import br.com.servicoFacil.error.ServicoFacilError;
import br.com.servicoFacil.error.ServicoFacilException;
import br.com.servicoFacil.model.DTO.CnpjDto;
import br.com.servicoFacil.model.DTO.DadosServico;
import br.com.servicoFacil.model.DTO.request.PrestadorRequest;
import br.com.servicoFacil.model.DTO.response.CreatePrestadorResponse;
import br.com.servicoFacil.model.DTO.response.PrestadorResponse;
import br.com.servicoFacil.model.entity.Prestador;
import br.com.servicoFacil.model.enums.SituacaoCNPJ;
import br.com.servicoFacil.repository.ClienteRepository;
import br.com.servicoFacil.repository.PrestadorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class PrestadorService {

    @Autowired
    private ServicosProxy servicosProxy;

    @Autowired
    private PrestadorRepository repo;

    @Autowired
    private ClienteRepository clienteRepository;

    public CreatePrestadorResponse saveOrUpdatePrestador(PrestadorRequest request) throws ServicoFacilException {
        LocalDateTime dataAtual = LocalDateTime.now();
        boolean ativo = false;

        Optional<Prestador> prestadorOpt = repo.findByCpf(request.getCpf());
        Prestador prestador = prestadorOpt.orElseGet(() -> Prestador.builder().criacao(dataAtual).build());

        prestador.setNome(request.getNome());
        prestador.setCpf(request.getCpf());
        prestador.setEmail(request.getEmail());
        prestador.setModificacao(dataAtual);
        prestador.setDadosServico(request.getDadosServico());

        prestador.setIdCliente(request.getIsCliente() ? String.valueOf(vinculaCliente(request.getCpf())) : null);

        if (request.getDadosServico().getCnpj() != null) {
            ativo = validateCnpj(request.getDadosServico().getCnpj());
        }

        try {
            Prestador prestadorSave = repo.save(prestador);
            return CreatePrestadorResponse.builder()
                    .id(prestadorSave.getId())
                    .cnpjAtivo(ativo)
                    .build();
        } catch (Exception e) {
            throw new ServicoFacilException(e, ServicoFacilError.SF0003);
        }
    }

    public DadosServico updateDadosServico(String codigo, DadosServico dados) throws ServicoFacilException {
        Optional<Prestador> prestadorOptional = repo.findById(codigo);
        return prestadorOptional.map(prestador -> {
            DadosServico dadosServico = prestador.getDadosServico();
            dadosServico.setCategoria(dados.getCategoria());
            dadosServico.setDescExperiencia(dados.getDescExperiencia());
            dadosServico.setCnpj(dados.getCnpj());
            dadosServico.setOrcamento(dados.isOrcamento());
            dadosServico.setNomeAlternativo(dados.getNomeAlternativo());
            dadosServico.setFormaPagamento(dados.getFormaPagamento());
            dadosServico.setEndereco(dados.getEndereco());
            Prestador prestadorSave = repo.save(prestador);
            return prestadorSave.getDadosServico();
        }).orElseThrow(() -> new ServicoFacilException(ServicoFacilError.SF0001));
    }

    public PrestadorResponse buscaDadosPrestador(String cpf) throws ServicoFacilException {
        return repo.findByCpf(cpf)
                .map(prestador -> PrestadorResponse.builder()
                        .id(prestador.getId())
                        .cpf(prestador.getCpf())
                        .nome(prestador.getNome())
                        .email(prestador.getEmail())
                        .dadosServico(prestador.getDadosServico())
                        .idCliente(prestador.getIdCliente() != null ? prestador.getIdCliente() : null)
                        .cnpjAtivo(prestador.getDadosServico().getCnpjAtivo())
                        .build())
                .orElseThrow(() -> new ServicoFacilException(ServicoFacilError.SF0001));
    }

    private boolean validateCnpj(String cnpj) throws ServicoFacilException {
        try {
            log.info("Validando CNPJ {}", cnpj);
           CnpjDto cnpjDto = servicosProxy.getCnpj(cnpj).orElse(null);
           return cnpjDto != null && cnpjDto.getSituacao().equals(SituacaoCNPJ.ATIVO);
        } catch (Exception e) {
            throw new ServicoFacilException(e, ServicoFacilError.SF0002);
        }
    }

    private Optional<String> vinculaCliente(String cpf) throws ServicoFacilException {
      log.info("Vinculando cliente ao prestador {}", cpf);
      try{
        return clienteRepository.findIdByCpf(cpf);
      } catch (Exception e){
          log.error("Erro ao vincular cliente ao prestador", e);
          throw new ServicoFacilException(e, ServicoFacilError.SF0004);
      }
    }
}
