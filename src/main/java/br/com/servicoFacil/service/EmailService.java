package br.com.servicoFacil.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public void envioDeEmailComprovacaoPrestador(String emailPrestador, String token) throws MessagingException {
        //TODO: Trata exceção, logar o email que está sendo enviado, para manter rastreabilidade
        MimeMessage email = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(email, true);
        String corpoEmail = "<div style=\"background-color: #f4f4f4; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); max-width: 500px; margin: 0 auto; text-align: center;\">"
                + "<img src=\"CID:imagem\" alt=\"Imagem\" style=\"max-width: 100%;\">"
                + "<p>Por favor, clique no botão abaixo para ativar sua conta no serviço fácil:</p>"
                + "<a href=\"http://localhost:8080/servicofacil/prestador/v1/ativar-conta/" + token
                + "\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin-top: 20px; border-radius: 5px; border: none; cursor: pointer;\">Ativar Conta</a>"
                + "</div>";

        helper.setFrom("Serviço Fácil"+ "<"+remetente+">");
        helper.setSubject("Solicitação de acesso - Serviço Fácil!");
        helper.setText(corpoEmail, true);
        helper.addTo(emailPrestador);
        FileSystemResource img = new FileSystemResource(new File("src/main/resources/arquivos-imagens/serviçoFácil.png"));
        helper.addInline("imagem", img);

        javaMailSender.send(email);
    }

}
