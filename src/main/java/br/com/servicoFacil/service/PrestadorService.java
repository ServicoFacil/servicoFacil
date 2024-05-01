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
import br.com.servicoFacil.model.enums.TipoUsuarioEnum;
import br.com.servicoFacil.repository.ClienteRepository;
import br.com.servicoFacil.repository.PrestadorRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PrestadorService {

    public static final int MINUTO_EXPIRACAO_TOKEN = 15;
    public static final String EMPYT = "";
    @Autowired
    private ServicosProxy servicosProxy;
    @Autowired
    private PrestadorRepository repo;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public CreatePrestadorResponse savePrestador(PrestadorRequest prestadorRequest) throws ServicoFacilException {
        Prestador prestador = buildPrestadorFromRequest(prestadorRequest);

        boolean ativo = false;
        boolean contemDadosServicos = prestador.getDadosServico() != null && prestadorRequest.getDadosServico().getCnpj() != null;

        if (contemDadosServicos) {
            CnpjDto cnpj = validateAndSendEmail(prestadorRequest.getDadosServico().getCnpj(), prestador.getTokenConfirmacao());
            ativo = cnpj.getSituacao().equals(SituacaoCNPJ.ATIVA.name());
            prestador.getDadosServico().setCnpjAtivo(ativo);
            prestador.getDadosServico().setEmailVincCnpj(cnpj.getEmail());
        } else sendEmail(prestador.getEmail(), prestador.getTokenConfirmacao());

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

    private Prestador buildPrestadorFromRequest(PrestadorRequest prestadorRequest) throws ServicoFacilException {
        String senhaCriptografada = passwordEncoder.encode(prestadorRequest.getSenha());
        Optional<Prestador> prestadorOpt = repo.findByCpf(prestadorRequest.getCpf());
        Prestador prestador = prestadorOpt.orElseGet(() -> Prestador.builder().criacao(LocalDateTime.now()).build());

        prestador.setNome(prestadorRequest.getNome());
        prestador.setCpf(prestadorRequest.getCpf());
        prestador.setEmail(prestadorRequest.getEmail());
        prestador.setSenha(senhaCriptografada);
        prestador.setModificacao(LocalDateTime.now());
        prestador.setDadosServico(prestadorRequest.getDadosServico());
        prestador.setTipoUsuario(TipoUsuarioEnum.valueOf(TipoUsuarioEnum.PRESTADOR.name()));
        prestador.setTokenConfirmacao(UUID.randomUUID().toString());
        prestador.setExpiracaoToken(LocalDateTime.now().plusMinutes(MINUTO_EXPIRACAO_TOKEN));
        prestador.setIdCliente(prestadorRequest.getIsCliente() ? String.valueOf(vinculaCliente(prestadorRequest.getCpf())) : null);

        return prestador;
    }

    private CnpjDto validateAndSendEmail(String cnpj, String tokenConfirmacao) throws ServicoFacilException {
        validaCnpjExistente(cnpj);
        CnpjDto cnpjDto = validateCnpj(cnpj);
        envioEmailPrestador(cnpjDto.getEmail(), tokenConfirmacao);
        return cnpjDto;
    }

    private void sendEmail(String email, String tokenConfirmacao) throws ServicoFacilException {
        envioEmailPrestador(email, tokenConfirmacao);
    }

    public void ativarPrestador(String token) throws ServicoFacilException {
        Prestador prestador = repo.findByTokenConfirmacao(token)
                .orElseThrow(() -> new ServicoFacilException("Token não encontrado!", ServicoFacilError.SF007));
        if(prestador.getExpiracaoToken().isBefore(LocalDateTime.now())){
            throw new ServicoFacilException("Token Expirado!", ServicoFacilError.SF011);
        }
        try {
            prestador.setAtivo(true);
            repo.save(prestador);
        } catch (Exception e){
            throw new ServicoFacilException(e, ServicoFacilError.SF0003);
        }
    }
    public DadosServico updateDadosServico(DadosServico dados) throws ServicoFacilException {
        Optional<Prestador> prestadorOptional = repo.findById(usuarioService.usuarioAutenticado().getId());
        return prestadorOptional.map(prestador -> {
            DadosServico dadosServico = prestador.getDadosServico();
            dadosServico.setCategoria(dados.getCategoria());
            dadosServico.setDescExperiencia(dados.getDescExperiencia());
            dadosServico.setCnpj(dados.getCnpj());
            dadosServico.setCnpjAtivo(dados.getCnpjAtivo());
            dadosServico.setTmpExperiencia(dados.getTmpExperiencia());
            dadosServico.setOrcamento(dados.isOrcamento());
            dadosServico.setNomeAlternativo(dados.getNomeAlternativo());
            dadosServico.setFormaPagamento(dados.getFormaPagamento());
            dadosServico.setEndereco(dados.getEndereco());
            Prestador prestadorSave = repo.save(prestador);
            return prestadorSave.getDadosServico();
        }).orElseThrow(() -> new ServicoFacilException(ServicoFacilError.SF0001));
    }

    public Page<PrestadorResponse> buscaDadosPrestador(String nome, String formaPagamento, String cnpj, String categoria, Integer tempoExperiencia, Pageable pageable) throws ServicoFacilException {
        Page<Prestador> prestadores = repo.findByDynamicQuery(nome, formaPagamento, cnpj, categoria, tempoExperiencia, pageable);
        return prestadores.map(prestador -> PrestadorResponse.builder()
                .id(prestador.getId())
                .cpf(prestador.getCpf())
                .nome(prestador.getNome())
                .email(prestador.getEmail())
                .dadosServico(prestador.getDadosServico())
                .idCliente(Optional.ofNullable(prestador.getIdCliente()).orElse(null))
                .cnpjAtivo(Optional.ofNullable(prestador.getDadosServico()).map(DadosServico::getCnpjAtivo).orElse(null))
                .build());
    }

    private CnpjDto validateCnpj(String cnpj) throws ServicoFacilException {
        try {
            log.info("Validando CNPJ {}", cnpj);
            cnpj = cnpj.replaceAll("\\p{Punct}", EMPYT);
            CnpjDto cnpjDto = servicosProxy.getCnpj(cnpj).orElse(null);
            if (cnpjDto != null && cnpjDto.getSituacao().equals(SituacaoCNPJ.ATIVA.name())) {
                return cnpjDto;
            } else {
                throw new ServicoFacilException("CNPJ não encontra-se ativado!", ServicoFacilError.SF0002);
            }
        } catch (FeignException.FeignClientException ex) {
            if (ex.status() == 404) {
                throw new ServicoFacilException("CNPJ não encontrado!", ServicoFacilError.SF0002);
            } else {
                throw new ServicoFacilException(ex, ServicoFacilError.SF0002);
            }
        }
    }

    private void envioEmailPrestador(String email, String token) throws ServicoFacilException {
        log.info("Endereço de e-mail para envio de ativação:  {}", email);
        log.info("Token de confirmação gerado: {}", token);
        try{
            emailService.envioDeEmailComprovacaoPrestador(email, token);
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

    private void validaCnpjExistente(String cnpj) throws ServicoFacilException {
        if (repo.existsByDadosServicoCnpj(cnpj)){
            throw new ServicoFacilException("CNPJ existente na base de dados!", ServicoFacilError.SF008);
        }
    }
}
