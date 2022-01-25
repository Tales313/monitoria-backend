package br.com.monitoria.repository;

import br.com.monitoria.domain.Edital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditalRepository extends JpaRepository<Edital, Long> {
}
