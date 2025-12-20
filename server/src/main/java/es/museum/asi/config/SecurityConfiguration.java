package es.museum.asi.config;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import es.museum.asi.model.enums.UserAuthority;
import es.museum.asi.security.JWTConfigurer;
import es.museum.asi.security.MyAccessDeniedHandler;
import es.museum.asi.security.MyUnauthorizedEntryPoint;
import es.museum.asi.security.MyUserDetailsService;
import es.museum.asi.security.TokenProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {

  @Autowired
  private Properties properties;

  @Autowired
  private MyUnauthorizedEntryPoint myUnauthorizedEntryPoint;

  @Autowired
  private MyAccessDeniedHandler myAccessDeniedHandler;

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private MyUserDetailsService myUserDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .csrf((csrf) -> csrf.disable())
      .exceptionHandling((exceptionHandling) -> exceptionHandling
        .authenticationEntryPoint(myUnauthorizedEntryPoint)
        .accessDeniedHandler(myAccessDeniedHandler)
      )
      .headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
      .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests((authorize) -> authorize
        // OPCIONES Y PÚBLICO
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // AUTENTICACÓN (HU1-HU3)
        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

        // USUARIOS (HU4-HU8) - SOLO ADMIN
        .requestMatchers("/api/users/**").hasAuthority(UserAuthority.ADMIN.toString())

        // SALAS (HU37-HU40) - SOLO ADMIN
        .requestMatchers("/api/salas/**").hasAuthority(UserAuthority.ADMIN.toString())

        // EXPOS (HU9 - HU19)
        .requestMatchers(HttpMethod.GET, "/api/exposiciones/publico/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/exposiciones/admin").hasAuthority(UserAuthority.ADMIN.toString())
        .requestMatchers(HttpMethod.GET, "/api/exposiciones/gestor").hasAnyAuthority(UserAuthority.ADMIN.toString(), UserAuthority.GESTOR.toString())
        .requestMatchers("/api/exposiciones/**").hasAnyAuthority(UserAuthority.ADMIN.toString(), UserAuthority.GESTOR.toString())

        // EDICIONES (HU20 - HU26)
        .requestMatchers(HttpMethod.GET, "/api/ediciones/*/publico").permitAll()
        .requestMatchers("/api/ediciones/**").hasAnyAuthority(UserAuthority.ADMIN.toString(), UserAuthority.GESTOR.toString())

        // SESIONES HU31-HU36
        .requestMatchers(HttpMethod.GET, "/api/sesiones/*/publico").permitAll()
        .requestMatchers("/api/sesiones/**").hasAnyAuthority(UserAuthority.ADMIN.toString(), UserAuthority.GESTOR.toString())

        // PIEZAS EXPUESTAS HU27-HU30
        .requestMatchers("/api/piezas-expuestas/**").hasAnyAuthority(UserAuthority.ADMIN.toString(), UserAuthority.GESTOR.toString())

        // OBRAS HU43-HU47
        .requestMatchers(HttpMethod.GET, "/api/obras/**").permitAll() // Público puede ver catálogo
        .requestMatchers("/api/obras/**").hasAnyAuthority(UserAuthority.ADMIN.toString(), UserAuthority.GESTOR.toString())

        // THE MET HU48-HU49
        .requestMatchers("/api/met/**").hasAnyAuthority(UserAuthority.ADMIN.toString(), UserAuthority.GESTOR.toString())

        // TIPOS DE ENTRADA HU50
        .requestMatchers(HttpMethod.GET, "/api/tipos-entrada/**").permitAll()

        // RESERVAS HU51-HU54, HU57-HU58
        .requestMatchers("/api/mis-reservas/**").hasAuthority(UserAuthority.VISITANTE.toString())
        .requestMatchers(HttpMethod.POST, "/api/reservas").hasAuthority(UserAuthority.VISITANTE.toString())
        .requestMatchers("/api/reservas/**").hasAnyAuthority(UserAuthority.ADMIN.toString(), UserAuthority.GESTOR.toString())

        // ENTRADAS HU55,56
        .requestMatchers("/api/mis-entradas/**").hasAuthority(UserAuthority.VISITANTE.toString())

        // por defecto...
        .requestMatchers("/**").authenticated())
      .with(securityConfigurerAdapter(), Customizer.withDefaults());
    // @formatter:on
    return http.build();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins(properties.getClientHost());
      }
    };
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Autowired
  public void configureAuth(AuthenticationManagerBuilder auth) {
    try {
      auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder);
    } catch (Exception e) {
      throw new BeanInitializationException("SecurityConfiguration.configureAuth failed", e);
    }
  }

  private JWTConfigurer securityConfigurerAdapter() {
    return new JWTConfigurer(tokenProvider);
  }
}
