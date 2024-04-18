package br.com.servicoFacil.security;

import br.com.servicoFacil.model.entity.Usuario;
import br.com.servicoFacil.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    public static final String PRESTADOR = "hasRole('PRESTADOR')";

    public static final String CLIENTE = "hasRole('CLIENTE')";

    public static final String PRESTADOR_CLIENTE = "hasAnyRole('PRESTADOR, CLIENTE')";

    public static final String PERMIT_ALL = "permitAll()";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter jwtFilter() {
        return new JwtAuthFilter(jwtService, usuarioService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(req ->{
            req.requestMatchers("/servicofacil/prestador/v1/inserir").permitAll();
            req.requestMatchers("/servicofacil/prestador/v1/ativar-conta/{token}").permitAll();
            req.requestMatchers("/servicofacil/cliente/v1/inserir").permitAll();
            req.requestMatchers("/servicofacil/usuario/v1/autenticar").permitAll();
            req.anyRequest().authenticated();
        }).addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

}
