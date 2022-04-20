package br.com.monitoria.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SQSService {

    @Value("${aws.sqs.queue.url}")
    private String QUEUE_URL;

    @Value("${aws.sqs.accessKey}")
    private String accessKey;

    @Value("${aws.sqs.secretKey}")
    private String secretKey;

    public void enviarEmailDeInscricao(String disciplina) {

        AmazonSQS sqs = criarClientBuilderSQS();

        String mensagem = "Olá Aluno, sua inscrição para a disciplina " + disciplina + " foi efetuada com sucesso!";

        SendMessageRequest sendMessageFifoQueue = new SendMessageRequest()
                .withQueueUrl(QUEUE_URL)
                .withMessageBody(mensagem)
                .withMessageGroupId("grupo_email_inscricao");

        sqs.sendMessage(sendMessageFifoQueue);

    }

    private AmazonSQS criarClientBuilderSQS() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.SA_EAST_1)
                .build();
    }

}
