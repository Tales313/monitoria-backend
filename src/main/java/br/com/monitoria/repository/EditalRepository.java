package br.com.monitoria.repository;

import br.com.monitoria.domain.Edital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EditalRepository extends JpaRepository<Edital, Long> {

    Optional<Edital> findBySemestre(String semestre);

    Optional<Edital> findTopByOrderByIdDesc();

}
