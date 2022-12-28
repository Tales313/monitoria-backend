package br.com.monitoria.schedule;

import br.com.monitoria.domain.*;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.InscricaoRepository;
import br.com.monitoria.repository.VagaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Component
@EnableScheduling
public class ProcessarVencedores {

    private EditalRepository editalRepository;
    private VagaRepository vagaRepository;
    private InscricaoRepository inscricaoRepository;

    private Logger log = LoggerFactory.getLogger(ProcessarVencedores.class);


    public ProcessarVencedores(EditalRepository editalRepository,
                               VagaRepository vagaRepository,
                               InscricaoRepository inscricaoRepository)
    {
        this.editalRepository = editalRepository;
        this.vagaRepository = vagaRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    /*
    * Esse método está como public apenas para ser chamado nos testes
    * Ele não deve ser chamado no código normal
    * */
    @Scheduled(cron = "0 0 3 * * ?") //Vai rodar todos os dias as 03:00
    public void processar() {

        log.info("### Inicio do processamento ###");

        Optional<Edital> editalOptional = editalRepository.findTopByOrderByIdDesc();
        Edital edital = editalOptional.orElse(null);

        if(edital != null) {

            List<Vaga> vagas = vagaRepository.findByEditalId(edital.getId());
            if(vagas.isEmpty()) {
                log.warn("### Não foi cadastrada nenhuma vaga para o edital " + edital.getSemestre() + " ###");
                return;
            }

            definirClassificados(vagas);
            verificarImpedidos(edital);
            log.info("### Fim do processamento ###");

        } else {
            log.warn("### Processamento cancelado, não existe nenhum edital na base ###");
        }

    }

    private void definirClassificados(List<Vaga> vagas) {

        List<Inscricao> inscricoes;
        for(Vaga vaga : vagas) {

            // Pegando da base as inscricoes pelo id da vaga
            // ordenando pela maior media, nota, cre e aluno mais velho
            inscricoes = inscricaoRepository.
                    findByVagaIdOrderByMediaDescNotaDisciplinaDescCreDescUsuarioDataNascimentoAsc(vaga.getId());

            if(!inscricoes.isEmpty()) {

                Iterator<Inscricao> inscricoesIterable = inscricoes.stream().iterator();
                Inscricao inscricao;
                int vagasPreenchidas = 0;

                while (!vaga.isPreenchida()) {

                    if (inscricoesIterable.hasNext())
                        inscricao = inscricoesIterable.next();
                    else
                        break; // é necessario parar o while caso a qtd de vagas seja maior que a qtd de inscricoes

                    vagasPreenchidas++;
                    inscricao.setResultado(ResultadoEnum.CLASSIFICADO);
                    if(vagasPreenchidas == vaga.getQuantidade())
                        vaga.setPreenchida(true);
                }

                // Setando o resultado de nao classificado nos alunos restantes que nao venceram
                inscricoes.forEach(i -> {
                    if (i.getResultado().equals(ResultadoEnum.AGUARDANDO))
                        i.setResultado(ResultadoEnum.NAO_CLASSIFICADO);
                });

                inscricaoRepository.saveAll(inscricoes);

            }

        }

    }

    private void verificarImpedidos(Edital edital) {

        List<Inscricao> classificados = inscricaoRepository.findByVagaEditalIdAndResultado(edital.getId(), ResultadoEnum.CLASSIFICADO);

        for(Inscricao classificado: classificados) {

            Vaga vaga = classificado.getVaga();
            List<Inscricao> inscricoesDaVaga = inscricaoRepository.findByVagaIdOrderByMediaDescNotaDisciplinaDescCreDescUsuarioDataNascimentoAsc(vaga.getId());

            for(Inscricao inscricao : inscricoesDaVaga) {
                // setar impedidos
                if(inscricao.isSegundaOpcao() && alunoJaClassificadoNesseEditalEmPrimeiraOpcao(inscricao)) {
                    inscricao.setResultado(ResultadoEnum.IMPEDIDO);
                    inscricaoRepository.save(inscricao);
                }
            }

            if(classificado.isSegundaOpcao() && alunoJaClassificadoNesseEditalEmPrimeiraOpcao(classificado)) {

                classificado.setResultado(ResultadoEnum.IMPEDIDO);
                inscricaoRepository.save(classificado);

                for(Inscricao inscricao : inscricoesDaVaga) {
                    // iterar ate achar um nao classificado
                    if (inscricao.getResultado() == ResultadoEnum.NAO_CLASSIFICADO) {
                        inscricao.setResultado(ResultadoEnum.CLASSIFICADO);
                        inscricaoRepository.save(inscricao);
                        break;
                    }
                }
            }

        }

    }

    private boolean alunoJaClassificadoNesseEditalEmPrimeiraOpcao(Inscricao inscricao) {
        Usuario aluno = inscricao.getUsuario();
        Edital edital = inscricao.getVaga().getEdital();

        List<Inscricao> inscricoesDesseAlunoNesseEdital = inscricaoRepository.
                findByUsuarioIdAndVagaEditalIdAndOpcao(aluno.getId(), edital.getId(), 1);

        return inscricoesDesseAlunoNesseEdital.stream()
                .anyMatch(i -> i.getResultado().equals(ResultadoEnum.CLASSIFICADO));
    }

}
