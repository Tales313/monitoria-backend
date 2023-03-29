package br.com.monitoria.security;

import br.com.monitoria.domain.PerfilEnum;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.util.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        // Esse método é o que será chamado la no AutenticacaoController, estando anotado
        // com @Bean, vai existir apenas uma instância, então essa será chamada
        // caso tire o @Bean, vai dar um erro de injecao la no controller
        return super.authenticationManager();
    }

    // Configs de autenticacao
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
    }

    // Configs de autorizacao
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // nao criar sessao
            .and()
            .csrf().disable()
            .authorizeRequests()

            .antMatchers(HttpMethod.POST, Paths.AUTH).permitAll()
            .antMatchers(HttpMethod.POST, Paths.USUARIOS).permitAll()
            .antMatchers(HttpMethod.POST, Paths.EDITAIS).hasAnyAuthority(PerfilEnum.ADMIN.toString(), PerfilEnum.COORDENADOR.toString())
            .antMatchers(HttpMethod.POST, Paths.VAGAS).hasAnyAuthority(PerfilEnum.ADMIN.toString(), PerfilEnum.COORDENADOR.toString())
            .antMatchers(HttpMethod.GET, Paths.INSCRICOES + Paths.PROXIMA_OPCAO).hasAnyAuthority(PerfilEnum.ALUNO.toString())
            .antMatchers(HttpMethod.GET, Paths.INSCRICOES + Paths.RESULTADOS).permitAll()
            .antMatchers(HttpMethod.GET, Paths.SWAGGER).permitAll()
            .antMatchers(HttpMethod.GET, Paths.SWAGGER_RESRC).permitAll()
            .antMatchers(HttpMethod.GET, Paths.V3_DOCS).permitAll()

            .anyRequest().authenticated() // qualquer outra url deve estar autenticado para acessar
            .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
            .and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**.html", Paths.V3_DOCS, "/webjars/**", "/configuration/**", Paths.V3_DOCS);
    }

    // esse handler será chamado quando o spring subir uma excecao de access denied
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
