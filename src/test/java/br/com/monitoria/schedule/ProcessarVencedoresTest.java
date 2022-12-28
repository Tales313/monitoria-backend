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

    public void setUpCenarioCriado() {
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
    * O diagrama para entender o cenario desse teste pode ser encontrado no repositorio
    * */
    @Test
    @Transactional // precisei adicionar para nao dar lazy initialization exception na hora de chamar o perfilAluno.addUsuario()
    void testandoCenarioCriado() {
        setUpCenarioCriado();

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

    public void setUpCenarioReal() {
        this.coordenador = usuarioRepository.findByLogin("coordenador_01@gmail.com").get();

        // alimentar base com edital e vagas
        Edital edital = new Edital("2022.2", LocalDate.now(), LocalDate.now().plusDays(14), coordenador);
        editalRepository.save(edital);
        List<Vaga> vagas = new ArrayList<>();
        vagas.add(new Vaga("ALGORITMOS E PROGRAMAÇÃO ESTRUTURADA", "1", 2, edital, coordenador));
        vagas.add(new Vaga("BANCO DE DADOS I", "2", 2, edital, coordenador));
        vagas.add(new Vaga("LEGISLAÇÃO SOCIAL", "4", 1, edital, coordenador));
        vagas.add(new Vaga("LINGUAGENS DE MARCAÇÃO", "1", 2, edital, coordenador));
        vagas.add(new Vaga("LINGUAGENS DE SCRIPT", "2", 2, edital, coordenador));
        vagas.add(new Vaga("PERSISTÊNCIA DE OBJETOS", "4", 1, edital, coordenador));
        vagas.add(new Vaga("PROGRAMAÇAO ORIENTADA A OBJETOS", "3", 1, edital, coordenador));
        vagas.add(new Vaga("SISTEMAS OPERACIONAIS", "2", 2, edital, coordenador));
        vagaRepository.saveAll(vagas);

        // alimentar base com alunos
        String senha = "$2a$10$F/f76piJUaBdGsDlJ9dHD.yyxOUSWMY/bYob3Kwqx9whgIJ3hP1pu"; // senha = "123456" com jwt secret do src/test/application.yml
        Perfil perfilAluno = perfilRepository.findByNome(PerfilEnum.ALUNO).get();

        List<Usuario> alunos = new ArrayList<>();
        alunos.add(new Usuario("paulo_elias01@gmail.com", "paulo_elias01", senha, "20221370001", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("roosevelt_henrique02@gmail.com", "roosevelt_henrique02", senha, "20221370002", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("adriana_albuquerque03@gmail.com", "adriana_albuquerque03", senha, "20221370003", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("pedro_da_costa04@gmail.com", "pedro_da_costa04", senha, "20221370004", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("alecsander_camilo05@gmail.com", "alecsander_camilo05", senha, "20221370005", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("danilo_marques06@gmail.com", "danilo_marques06", senha, "20221370006", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("amarildo_pereira07@gmail.com", "amarildo_pereira07", senha, "20221370007", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("jefferson_marcelo08@gmail.com", "jefferson_marcelo08", senha, "20221370008", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("jose_raimundo09@gmail.com", "jose_raimundo09", senha, "20221370009", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("lucian_hendyo10@gmail.com", "lucian_hendyo10", senha, "20221370010", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("marianna_alves11@gmail.com", "marianna_alves11", senha, "20221370011", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("christopher_silva12@gmail.com", "christopher_silva12", senha, "20221370012", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("andreia_de_lima13@gmail.com", "andreia_de_lima13", senha, "20221370013", LocalDate.of(1998, 10, 11), perfilAluno));
        alunos.add(new Usuario("josilete_rodrigues14@gmail.com", "josilete_rodrigues14", senha, "20221370014", LocalDate.of(1998, 10, 11), perfilAluno));

        alunos.forEach(perfilAluno::addUsuario);
        usuarioRepository.saveAll(alunos);

        // alimentar base com inscricoes
        List<Inscricao> inscricoes = new ArrayList<>();

        // ALGORITMOS E PROGRAMAÇÃO ESTRUTURADA
        inscricoes.add(new Inscricao(1, 100.0, 98.00, 99.40, vagas.get(0), alunos.get(0)));
        inscricoes.add(new Inscricao(1, 100.0, 91.92, 97.58, vagas.get(0), alunos.get(1)));
        inscricoes.add(new Inscricao(1, 98.00, 91.17, 95.95, vagas.get(0), alunos.get(2)));
        inscricoes.add(new Inscricao(1, 98.00, 90.64, 95.79, vagas.get(0), alunos.get(3)));
        inscricoes.add(new Inscricao(1, 98.00, 83.91, 93.77, vagas.get(0), alunos.get(4)));
        inscricoes.add(new Inscricao(1, 91.00, 84.51, 89.05, vagas.get(0), alunos.get(5)));
        inscricoes.add(new Inscricao(2, 76.00, 43.67, 66.30, vagas.get(0), alunos.get(6)));

        // BANCO DE DADOS I
        inscricoes.add(new Inscricao(1, 89.00, 82.91, 87.17, vagas.get(1), alunos.get(7)));
        inscricoes.add(new Inscricao(2, 87.00, 73.21, 82.86, vagas.get(1), alunos.get(8)));

        // LEGISLAÇÃO SOCIAL
        inscricoes.add(new Inscricao(1, 100.0, 74.82, 92.45, vagas.get(2), alunos.get(9)));
        inscricoes.add(new Inscricao(1, 90.00, 55.91, 79.77, vagas.get(2), alunos.get(10)));

        // LINGUAGENS DE MARCAÇÃO
        inscricoes.add(new Inscricao(2, 100.0, 98.00, 99.40, vagas.get(3), alunos.get(0)));
        inscricoes.add(new Inscricao(2, 98.00, 90.64, 95.79, vagas.get(3), alunos.get(3)));
        inscricoes.add(new Inscricao(1, 91.00, 89.34, 90.50, vagas.get(3), alunos.get(11)));
        inscricoes.add(new Inscricao(2, 87.00, 91.92, 88.48, vagas.get(3), alunos.get(1)));
        inscricoes.add(new Inscricao(2, 88.00, 74.82, 84.05, vagas.get(3), alunos.get(9)));
        inscricoes.add(new Inscricao(1, 100.0, 43.67, 83.10, vagas.get(3), alunos.get(6)));

        // LINGUAGENS DE SCRIPT
        inscricoes.add(new Inscricao(2, 100.0, 84.51, 93.95, vagas.get(4), alunos.get(5)));
        inscricoes.add(new Inscricao(2, 94.00, 82.91, 90.67, vagas.get(4), alunos.get(7)));

        // PERSISTÊNCIA DE OBJETOS
        inscricoes.add(new Inscricao(1, 100.0, 79.16, 93.75, vagas.get(5), alunos.get(12)));

        // PROGRAMAÇAO ORIENTADA A OBJETOS
        inscricoes.add(new Inscricao(1, 100.0, 73.21, 91.96, vagas.get(6), alunos.get(8)));
        inscricoes.add(new Inscricao(2, 92.00, 79.16, 88.15, vagas.get(6), alunos.get(12)));

        // SISTEMAS OPERACIONAIS
        inscricoes.add(new Inscricao(2, 95.00, 83.91, 91.67, vagas.get(7), alunos.get(4)));
        inscricoes.add(new Inscricao(1, 70.00, 45.62, 62.69, vagas.get(7), alunos.get(13)));

        inscricaoRepository.saveAll(inscricoes);
    }

    /*
     * O setUp desse teste foi copiado do cenario real que aconteceu no edital 2020.2 do curso.
     * */
    @Test
    @Transactional
    void testandoCenarioReal_Edital2020_2() {
        setUpCenarioReal();

        //TODO
        processarVencedores.processar();

        assertEquals(8, vagaRepository.count());
        assertEquals(24, inscricaoRepository.count());

        List<Inscricao> inscricoes = inscricaoRepository.findAllByOrderById();

        // ALGORITMOS E PROGRAMAÇÃO ESTRUTURADA
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(0).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(1).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(2).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(3).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(4).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(5).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(6).getResultado());

        // BANCO DE DADOS I
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(7).getResultado());
        assertEquals(ResultadoEnum.IMPEDIDO, inscricoes.get(8).getResultado());

        // LEGISLAÇÃO SOCIAL
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(9).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(10).getResultado());

        // LINGUAGENS DE MARCAÇÃO
        assertEquals(ResultadoEnum.IMPEDIDO, inscricoes.get(11).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(12).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(13).getResultado());
        assertEquals(ResultadoEnum.IMPEDIDO, inscricoes.get(14).getResultado());
        assertEquals(ResultadoEnum.IMPEDIDO, inscricoes.get(15).getResultado());
        assertEquals(ResultadoEnum.NAO_CLASSIFICADO, inscricoes.get(16).getResultado());

        // LINGUAGENS DE SCRIPT
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(17).getResultado());
        assertEquals(ResultadoEnum.IMPEDIDO, inscricoes.get(18).getResultado());

        // PERSISTÊNCIA DE OBJETOS
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(19).getResultado());

        // PROGRAMAÇAO ORIENTADA A OBJETOS
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(20).getResultado());
        assertEquals(ResultadoEnum.IMPEDIDO, inscricoes.get(21).getResultado());

        // SISTEMAS OPERACIONAIS
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(22).getResultado());
        assertEquals(ResultadoEnum.CLASSIFICADO, inscricoes.get(23).getResultado());
    }

}