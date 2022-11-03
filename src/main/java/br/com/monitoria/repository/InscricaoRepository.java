package br.com.monitoria.repository;

import br.com.monitoria.domain.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    List<Inscricao> findByUsuarioIdAndVagaEditalId(Long usuarioId, Long editalId);

    List<Inscricao> findByUsuarioIdAndVagaId(Long usuarioId, Long vagaId);

    List<Inscricao> findByVagaIdAndOpcaoOrderByMediaDescNotaDisciplinaDescCreDescUsuarioDataNascimentoAsc(Long vagaId, Integer opcao);

    List<Inscricao> findAllByOrderById();

}
