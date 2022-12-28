package br.com.monitoria.repository;

import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.ResultadoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    List<Inscricao> findByUsuarioIdAndVagaEditalId(Long usuarioId, Long editalId);

    List<Inscricao> findByUsuarioIdAndVagaEditalIdAndOpcao(Long usuarioId, Long editalId, Integer opcao);

    List<Inscricao> findByVagaEditalIdOrderByVagaDisciplinaAscMediaDescCreDescNotaDisciplinaDesc(Long editalId);

    List<Inscricao> findByUsuarioIdAndVagaId(Long usuarioId, Long vagaId);

    List<Inscricao> findByVagaIdOrderByMediaDescNotaDisciplinaDescCreDescUsuarioDataNascimentoAsc(Long vagaId);

    List<Inscricao> findByVagaEditalIdAndResultado(Long EditalId, ResultadoEnum resultado);

    List<Inscricao> findAllByOrderById();

}
