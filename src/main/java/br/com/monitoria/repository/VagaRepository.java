package br.com.monitoria.repository;

import br.com.monitoria.domain.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    List<Vaga> findByEditalId(Long idEdital);

}
