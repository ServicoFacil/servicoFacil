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
import br.com.servicoFacil.model.entity.PrestadorTemporario;
import br.com.servicoFacil.model.enums.SituacaoCNPJ;
import br.com.servicoFacil.repository.ClienteRepository;
import br.com.servicoFacil.repository.PrestadorRepository;
import br.com.servicoFacil.repository.PrestadorTemporarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PrestadorService {

    public static final int MINUTO_EXPIRACAO_TOKEN = 15;
    @Autowired
    private ServicosProxy servicosProxy;

    @Autowired
    private PrestadorRepository repo;

    @Autowired
    private PrestadorTemporarioRepository prestadorTemporarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmailService emailService;

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

    public CreatePrestadorResponse savePrestadorTemporario(PrestadorRequest prestadorRequest) throws ServicoFacilException {
        boolean ativo = false;
        Optional<PrestadorTemporario> prestadorOpt = prestadorTemporarioRepository.findByCpf(prestadorRequest.getCpf());
        PrestadorTemporario prestadorTemporario = prestadorOpt.orElseGet(() -> PrestadorTemporario.builder().criacao(LocalDateTime.now()).build());

        prestadorTemporario.setNome(prestadorRequest.getNome());
        prestadorTemporario.setCpf(prestadorRequest.getCpf());
        prestadorTemporario.setEmail(prestadorRequest.getEmail());
        prestadorTemporario.setModificacao(LocalDateTime.now());
        prestadorTemporario.setDadosServico(prestadorRequest.getDadosServico());
        prestadorTemporario.setTokenConfirmacao(UUID.randomUUID().toString());
        prestadorTemporario.setExpiracaoToken(LocalDateTime.now().plusMinutes(MINUTO_EXPIRACAO_TOKEN));
        prestadorTemporario.setIdCliente(prestadorRequest.getIsCliente() ? String.valueOf(vinculaCliente(prestadorRequest.getCpf())) : null);

        if (prestadorRequest.getDadosServico().getCnpj() != null) {
            if(prestadorTemporarioRepository.existsByDadosServicoCnpj(prestadorRequest.getDadosServico().getCnpj())){
                throw new ServicoFacilException("CNPJ existente na base de dados!", HttpStatus.CONFLICT.value());
            }
            ativo = validateCnpj(prestadorRequest.getDadosServico().getCnpj());
            envioEmailPrestadorTemporario(prestadorRequest.getDadosServico().getCnpj(), prestadorTemporario.getTokenConfirmacao());
        }

        try {
            PrestadorTemporario prestadorSave = prestadorTemporarioRepository.save(prestadorTemporario);
            return CreatePrestadorResponse.builder()
                    .id(prestadorSave.getId())
                    .cnpjAtivo(ativo)
                    .build();
        } catch (Exception e) {
            throw new ServicoFacilException(e, ServicoFacilError.SF0003);
        }

    }

    public void ativarPrestador(String token) throws ServicoFacilException {
        PrestadorTemporario prestadorTemporario = prestadorTemporarioRepository.findByTokenConfirmacao(token)
                .orElseThrow(() -> new ServicoFacilException("E-mail Inválido!", 404));

        if(prestadorTemporario.getExpiracaoToken().isBefore(LocalDateTime.now())){
            throw new ServicoFacilException("Token Expirado!");
        }
        try {
            Prestador prestador = new Prestador();
            prestador.setNome(prestadorTemporario.getNome());
            prestador.setCpf(prestadorTemporario.getCpf());
            prestador.setEmail(prestadorTemporario.getEmail());
            prestador.setModificacao(LocalDateTime.now());
            prestador.setDadosServico(prestadorTemporario.getDadosServico());
            prestador.setIdCliente(prestadorTemporario.getIdCliente());
            prestador.setDadosServico(prestadorTemporario.getDadosServico());
            prestador.setAtivo(true);
            repo.save(prestador);
            prestadorTemporarioRepository.delete(prestadorTemporario);
        } catch (Exception e){
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
           return cnpjDto != null && cnpjDto.getSituacao().equals(SituacaoCNPJ.ATIVA.name());
        } catch (Exception e) {
            throw new ServicoFacilException(e, ServicoFacilError.SF0002);
        }
    }

    private void envioEmailPrestadorTemporario(String cnpj, String token) throws ServicoFacilException {
        try{
            CnpjDto cnpjDto = servicosProxy.getCnpj(cnpj).orElse(null);
            emailService.envioDeEmailComprovacaoPrestador(cnpjDto.getEmail(), token);
        }catch (Exception e) {
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
