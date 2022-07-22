package br.com.monitoria.service;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.exception.InscricaoException;
import br.com.monitoria.repository.InscricaoRepository;
import br.com.monitoria.web.request.InscricaoRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InscricaoService {

    private InscricaoRepository inscricaoRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository) {
        this.inscricaoRepository = inscricaoRepository;
    }

    /*
    * Um aluno pode ter no maximo 2 inscrições em seu nome. E elas devem ser cadastradas em ordem
    * de opcao. A opcao 1 primeiro e depois a opcao 2.
    *
    * Esse método considera que as opcoes sempre serão 1 ou 2, nunca outro valor. Isso é validado
    * na classe InscricaoRequest, pela anotação @Range
    *
    * A inscricao deve ser feita no perido entre inicio e fim das inscricoes do edital.
    * As datas de inicio e fim sao includentes.
    * */
    public void validarInscricaoDoAluno(Usuario usuario, Vaga vaga, InscricaoRequest inscricaoRequest) {

        Edital edital = vaga.getEdital();
        LocalDate hoje = LocalDate.now();

        if(edital.getInicioInscricoes().isAfter(hoje))
            throw new InscricaoException("O edital " + edital.getSemestre() + " ainda não está aberto");

        if(edital.getFimInscricoes().isBefore(hoje))
            throw new InscricaoException("O edital " + edital.getSemestre() + " já está fechado");

        List<Inscricao> inscricoesDoAluno = inscricaoRepository.
                findByUsuarioIdAndVagaEditalId(usuario.getId(), edital.getId());

        if(!inscricoesDoAluno.isEmpty()) {

            if(inscricoesDoAluno.size() == 1 && inscricaoRequest.getOpcao() == 1)
                throw new InscricaoException("Sua segunda inscrição deve ser a opção 2");
            else if (inscricoesDoAluno.size() == 2)
                throw new InscricaoException("Você não pode ter mais que duas inscrições");

        } else {
            if(inscricaoRequest.getOpcao() == 2)
                throw new InscricaoException("Sua primeira inscrição deve ser a opção 1");
        }
    }

}
