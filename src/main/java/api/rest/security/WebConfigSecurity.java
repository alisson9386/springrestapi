package api.rest.security;

import api.rest.services.ImplementacaoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/*Mapeia Url, endereços, autoriza ou bloqueia acessos a URL*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private ImplementacaoUserDetailsService implementacaoUserDetailsService;

    /*Configura solicitações de acesso por HTTP*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*Ativando a proteção contra usuario que não está validado por token*/
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                /*Ativar permissão para acesso a página inicial do sistema. Ex: sistema.com.br/ */
                .disable().authorizeHttpRequests().antMatchers("/").permitAll()
                        .antMatchers("/index").permitAll()

                /*URL de logout. Redireciona ao deslogar*/
                .anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")

                /*Mapeia URL de Logout e invalida o usuario*/
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

                /*Filtra requisições de login para autenticação*/
                .and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)

                /*Filtra demais requisições para verificar a presença do Token JWT no Header*/
                .addFilterBefore(new JWTApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*Service que consulta o usuario no banco de dados*/
        auth.userDetailsService(implementacaoUserDetailsService)
                /*Padrão de codificação*/
                .passwordEncoder(new BCryptPasswordEncoder());
    }
}
