package br.com.monitoria.service;

import br.com.monitoria.domain.Usuario;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SnsService {

    private AmazonSNS snsClient;
    private Topic topicoCadastroUsuario;

    public SnsService(AmazonSNS snsClient, @Qualifier("cadastro-usuario") Topic topicoCadastroUsuario) {
        this.snsClient = snsClient;
        this.topicoCadastroUsuario = topicoCadastroUsuario;
    }

    public void enviarEmail(Usuario usuario) {
        PublishResult publishResult = snsClient.publish(
                topicoCadastroUsuario.getTopicArn(),
                "fala brow");
    }

}
