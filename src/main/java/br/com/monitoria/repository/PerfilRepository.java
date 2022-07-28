package br.com.monitoria.repository;

import br.com.monitoria.domain.Perfil;
import br.com.monitoria.domain.PerfilEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNome(PerfilEnum nome);

}
