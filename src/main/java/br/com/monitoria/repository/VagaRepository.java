package br.com.monitoria.repository;

import br.com.monitoria.domain.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
}
