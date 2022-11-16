package br.com.monitoria.schedule;

import br.com.monitoria.domain.*;
import br.com.monitoria.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProcessarVencedoresTest {

    @Autowired
    private ProcessarVencedores processarVencedores;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    private Usuario coordenador;

    @BeforeEach
    public void setUp() throws Exception {
        this.coordenador = usuarioRepository.findByLogin("coordenador_01@gmail.com").get();

        // alimentar base com edital e vagas
        Edital edital = new Edital("2022.2", LocalDate.now(), LocalDate.now().plusDays(14), coordenador);
        editalRepository.save(edital);
        List<Vaga> vagas = new ArrayList<>();
        vagas.add(new Vaga("Linguagens de Marcação", "1", 2, edital, coordenador));
        vagas.add(new Vaga("Linguagens de Script", "2", 2, edital, coordenador));
        vagas.add(new Vaga("Programação para Web I", "4", 1, edital, coordenador));
        vagas.add(new Vaga("Programação para dispositivos móveis", "5", 2, edital, coordenador));
        vagaRepository.saveAll(vagas);

        // alimentar base com alunos
        String senha = "$2a$10$F/f76piJUaBdGsDlJ9dHD.yyxOUSWMY/bYob3Kwqx9whgIJ3hP1pu"; // senha = "123456" com jwt secret do src/test/application.yml
        Perfil perfilAluno = perfilRepository.findByNome(PerfilEnum.ALUNO).get();

        List<Usuario> alunos = new ArrayList<>();
        alunos.add(new Usuario("aluno_01@gmail.com", "NomeTeste", senha, "20221370001", LocalDate.of(2000, 1, 1), perfilAluno));
        alunos.add(new Usuario("aluno_02@gmail.com", "NomeTeste", senha, "20221370002", LocalDate.of(2000, 3, 1), perfilAluno));
        alunos.add(new Usuario("aluno_03@gmail.com", "NomeTeste", senha, "20221370003", LocalDate.of(2000, 6, 1), perfilAluno));
        alunos.add(new Usuario("aluno_04@gmail.com", "NomeTeste", senha, "20221370004", LocalDate.of(2001, 1, 1), perfilAluno));
        alunos.add(new Usuario("aluno_05@gmail.com", "NomeTeste", senha, "20221370005", LocalDate.of(2001, 3, 1), perfilAluno));
        alunos.add(new Usuario("aluno_06@gmail.com", "NomeTeste", senha, "20221370006", LocalDate.of(2001, 6, 1), perfilAluno));
        alunos.add(new Usuario("aluno_07@gmail.com", "NomeTeste", senha, "20221370007", LocalDate.of(2002, 1, 1), perfilAluno));
        alunos.add(new Usuario("aluno_08@gmail.com", "NomeTeste", senha, "20221370008", LocalDate.of(2002, 3, 1), perfilAluno));
        alunos.add(new Usuario("aluno_09@gmail.com", "NomeTeste", senha, "20221370009", LocalDate.of(2002, 6, 1), perfilAluno));

        alunos.forEach(perfilAluno::addUsuario);
        usuarioRepository.saveAll(alunos);

        // alimentar base com inscricoes
        List<Inscricao> inscricoes = new ArrayList<>();
        inscricoes.add(new Inscricao(1, 90.0, 90.0, 90.0, vagas.get(0), alunos.get(0)));
        inscricoes.add(new Inscricao(2, 90.0, 90.0, 90.0, vagas.get(1), alunos.get(0)));

        inscricoes.add(new Inscricao(1, 81.0, 74.0, 85.0, vagas.get(0), alunos.get(1)));

        inscricoes.add(new Inscricao(1, 80.0, 80.0, 85.0, vagas.get(0), alunos.get(2)));

        inscricoes.add(new Inscricao(1, 75.0, 82.0, 80.0, vagas.get(1), alunos.get(3)));

        inscricoes.add(new Inscricao(1, 75.0, 80.0, 80.0, vagas.get(1), alunos.get(4)));

        inscricoes.add(new Inscricao(1, 80.0, 80.0, 80.0, vagas.get(2), alunos.get(5)));

        inscricoes.add(new Inscricao(1, 80.0, 80.0, 80.0, vagas.get(2), alunos.get(6)));

        inscricoes.add(new Inscricao(1, 75.0, 81.0, 80.0, vagas.get(1), alunos.get(7)));
        inscricoes.add(new Inscricao(2, 70.0, 81.0, 75.0, vagas.get(2), alunos.get(7)));

        inscricoes.add(new Inscricao(1, 80.0, 82.0, 85.0, vagas.get(3), alunos.get(8)));

        inscricaoRepository.saveAll(inscricoes);

    }

    /*
    * O diagrama para esse entender o cenario desse teste pode ser encontrado no repositorio
    * */
    @Test
    @Transactional // precisei adicionar para nao dar lazy initialization exception na hora de chamar o perfilAluno.addUsuario()
    void testandoCenariosDeProcessamento() {
        processarVencedores.processar();

        assertEquals(4, vagaRepository.count());
        assertEquals(11, inscricaoRepository.count());

        List<Inscricao> inscricoes = inscricaoRepository.findAllByOrderById();

        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(0).getResultado());
        assertEquals(ResultadoEnum.IMPEDIDO, inscricoes.get(1).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(2).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(3).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(4).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(5).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(6).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(7).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(8).getResultado());
        assertEquals(ResultadoEnum.IMPEDIDO, inscricoes.get(9).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(10).getResultado());

    }

}