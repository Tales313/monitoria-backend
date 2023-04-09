package br.com.monitoria.service;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.exception.InscricaoException;
import br.com.monitoria.repository.InscricaoRepository;
import br.com.monitoria.web.request.InscricaoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InscricaoService {

    private Logger log = LoggerFactory.getLogger(InscricaoService.class);

    private InscricaoRepository inscricaoRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository) {
        this.inscricaoRepository = inscricaoRepository;
    }

    /*
    * Um aluno pode ter no maximo 2 inscrições em seu nome. E elas devem ser cadastradas em ordem
    * de opcao. A opcao 1 primeiro e depois a opcao 2.
    *
    * Um aluno não pode se inscrever duas vezes na mesma vaga.
    *
    * Esse método considera que as opcoes sempre serão 1 ou 2, nunca outro valor. Isso é validado
    * na classe InscricaoRequest, pela anotação @Range
    *
    * A inscricao deve ser feita no perido entre inicio e fim das inscricoes do edital.
    * As datas de inicio e fim sao includentes.
    * */
    public void validarInscricaoDoAluno(Usuario usuario, Vaga vaga, InscricaoRequest inscricaoRequest) {

        log.info("Validando inscrição");

        Edital edital = vaga.getEdital();
        LocalDate hoje = LocalDate.now();

        if(edital.getInicioInscricoes().isAfter(hoje))
            throw new InscricaoException("inscricao.edital.ainda.fechado");

        if(edital.getFimInscricoes().isBefore(hoje))
            throw new InscricaoException("inscricao.edital.ja.fechado");

        List<Inscricao> inscricoesDoAlunoNessaVaga = inscricaoRepository.
                findByUsuarioIdAndVagaId(usuario.getId(), vaga.getId());

        if(! inscricoesDoAlunoNessaVaga.isEmpty())
            throw new InscricaoException("inscricao.aluno.ja.inscrito");

        List<Inscricao> inscricoesDoAlunoNesseEdital = inscricaoRepository.
                findByUsuarioIdAndVagaEditalId(usuario.getId(), edital.getId());

        if(! inscricoesDoAlunoNesseEdital.isEmpty()) {

            if(inscricoesDoAlunoNesseEdital.size() == 1 && inscricaoRequest.getOpcao() == 1)
                throw new InscricaoException("inscricao.segunda.opcao");
            else if (inscricoesDoAlunoNesseEdital.size() == 2)
                throw new InscricaoException("inscricao.duas.opcoes");

        } else {
            if(inscricaoRequest.getOpcao() == 2)
                throw new InscricaoException("inscricao.primeira.opcao");
        }

        log.info("Inscrição validada com sucesso");
    }

}
