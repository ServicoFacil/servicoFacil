package br.com.servicoFacil.service;

import br.com.servicoFacil.error.ServicoFacilError;
import br.com.servicoFacil.error.ServicoFacilException;
import br.com.servicoFacil.model.DTO.request.ClientRequest;
import br.com.servicoFacil.model.DTO.response.ClienteResponse;
import br.com.servicoFacil.model.entity.Cliente;
import br.com.servicoFacil.repository.ClienteRepository;
import br.com.servicoFacil.repository.PrestadorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepo;
    @Autowired
    private PrestadorRepository prestadorRepository;

    public Optional<String> saveOrUpdateCliente(ClientRequest clientRequest) throws ServicoFacilException {
        Optional<Cliente> clienteExistente = clienteRepo.findByCpf(clientRequest.getCpf());
        LocalDateTime dataAtual = LocalDateTime.now();
        Cliente cliente = clienteExistente.orElseGet(() -> Cliente.builder().criacao(dataAtual).build());

        cliente.setNome(clientRequest.getNome());
        cliente.setCpf(clientRequest.getCpf());
        cliente.setEmail(clientRequest.getEmail());
        cliente.setModificacao(dataAtual);
        cliente.setIdPrestador(clientRequest.getIsPrestador() ? String.valueOf(vinculaPrestador(clientRequest.getCpf())) : null);
        cliente.setEndereco(clientRequest.getEndereco());
        try {
            return clienteRepo.saveAndReturnId(cliente);
        } catch (Exception e) {
            throw new ServicoFacilException(e, ServicoFacilError.SF006);
        }
    }

    public ClienteResponse buscaCliente(String cpf) throws ServicoFacilException {
        log.info("Buscando cliente {}", cpf);
        return clienteRepo.findByCpf(cpf)
                .map(cliente -> ClienteResponse.builder()
                        .id(cliente.getId())
                        .cpf(cliente.getCpf())
                        .nome(cliente.getNome())
                        .email(cliente.getEmail())
                        .idPrestador(cliente.getIdPrestador())
                        .build())
                .orElseThrow(() -> new ServicoFacilException(ServicoFacilError.SF0005));
    }

    private Optional<String> vinculaPrestador(String cpf) throws ServicoFacilException {
        log.info("Vinculando cliente ao prestador {}", cpf);
        try {
            return prestadorRepository.findIdByCpfUsingQuery(cpf);
        } catch (Exception e) {
            log.error("Erro ao vincular cliente ao prestador", e);
            throw new ServicoFacilException(e, ServicoFacilError.SF0004);
        }
    }
}
