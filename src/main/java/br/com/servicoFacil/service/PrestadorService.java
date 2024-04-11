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


    public CreatePrestadorResponse savePrestador(PrestadorRequest prestadorRequest) throws ServicoFacilException {
        boolean ativo = false;
        Optional<Prestador> prestadorOpt = repo.findByCpf(prestadorRequest.getCpf());
        Prestador prestador = prestadorOpt.orElseGet(() -> Prestador.builder().criacao(LocalDateTime.now()).build());

        prestador.setNome(prestadorRequest.getNome());
        prestador.setCpf(prestadorRequest.getCpf());
        prestador.setEmail(prestadorRequest.getEmail());
        prestador.setModificacao(LocalDateTime.now());
        prestador.setDadosServico(prestadorRequest.getDadosServico());
        prestador.setTokenConfirmacao(UUID.randomUUID().toString());
        prestador.setExpiracaoToken(LocalDateTime.now().plusMinutes(MINUTO_EXPIRACAO_TOKEN));
        prestador.setIdCliente(prestadorRequest.getIsCliente() ? String.valueOf(vinculaCliente(prestadorRequest.getCpf())) : null);

        if (prestadorRequest.getDadosServico().getCnpj() != null) {
            validaCnpjExistente(prestadorRequest.getDadosServico().getCnpj());
            CnpjDto cnpj = validateCnpj(prestadorRequest.getDadosServico().getCnpj());
            envioEmailPrestadorTemporario(cnpj.getEmail(), prestador.getTokenConfirmacao());
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
    //TODO: Criar um mapper para converter PrestadorTemporario em Prestador
    }

    public void ativarPrestador(String token) throws ServicoFacilException {
        Prestador prestador = repo.findByTokenConfirmacao(token)
                .orElseThrow(() -> new ServicoFacilException("Token não encontrado!", ServicoFacilError.SF0404));
        if(prestador.getExpiracaoToken().isBefore(LocalDateTime.now())){
            throw new ServicoFacilException("Token Expirado!");
        }
        try {
            Prestador.builder().ativo(true).build();//TODO: Pq não usar o builder?
            repo.save(prestador);
        } catch (Exception e){
            throw new ServicoFacilException(e, ServicoFacilError.SF0003); //TODO: Ajustar erro, já que o método está nesse caso deletando o registro
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

    private CnpjDto validateCnpj(String cnpj) throws ServicoFacilException {
        try {
            log.info("Validando CNPJ {}", cnpj);
           CnpjDto cnpjDto = servicosProxy.getCnpj(cnpj).orElse(null);
           boolean cnpjAtivo = cnpjDto != null && cnpjDto.getSituacao().equals(SituacaoCNPJ.ATIVA.name());
           if(cnpjAtivo){
              return cnpjDto;
           } else{
               throw new ServicoFacilException("CNPJ não encontra-se ativado!", ServicoFacilError.SF0002);
           }
        } catch (Exception e) {
            throw new ServicoFacilException(e, ServicoFacilError.SF0002);
        }
    }

    private void envioEmailPrestadorTemporario(String email, String token) throws ServicoFacilException {
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
}
