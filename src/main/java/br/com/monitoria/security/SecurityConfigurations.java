package br.com.monitoria.security;

import br.com.monitoria.repository.UsuarioRepository;
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
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth").permitAll()
                .antMatchers(HttpMethod.POST, "/usuarios").permitAll()
                .anyRequest().authenticated() // qualquer outra url deve estar autenticado para acessar
                .and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // nao criar sessao
                .and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class);
    }

    // Configs de recursos estaticos(css, js, imgs..)
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");
    }

}
