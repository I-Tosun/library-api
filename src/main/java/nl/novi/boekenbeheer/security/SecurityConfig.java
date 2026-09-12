package nl.novi.boekenbeheer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Configureert Spring Security met OAuth2/JWT-authenticatie via Keycloak.
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Definieert de beveiligingsregels per endpoint en HTTP-methode.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF uitgeschakeld — API gebruikt stateless JWT-tokens
                .csrf(csrf -> csrf.disable())
                // Geen sessies — elke request wordt via JWT geauthenticeerd
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger endpoints zijn publiek toegankelijk
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Auteurs: lezen voor beide rollen, schrijven alleen voor BEHEERDER
                        .requestMatchers(HttpMethod.GET, "/api/authors/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.POST, "/api/authors/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.PUT, "/api/authors/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/authors/**").hasRole("BEHEERDER")

                        // Boeken: lezen voor beide rollen, schrijven alleen voor BEHEERDER
                        .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("BEHEERDER")

                        // Exemplaren: lezen voor beide rollen, schrijven alleen voor BEHEERDER
                        .requestMatchers(HttpMethod.GET, "/api/book-copies/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.POST, "/api/book-copies/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/book-copies/**").hasRole("BEHEERDER")

                        // Klanten en bibliotheekpassen: alleen BEHEERDER
                        .requestMatchers("/api/customers/**").hasRole("BEHEERDER")
                        .requestMatchers("/api/library-cards/**").hasRole("BEHEERDER")

                        // Leningen: lezen en aanmaken voor beide rollen, retourneren/verwijderen alleen BEHEERDER
                        .requestMatchers(HttpMethod.GET, "/api/loans/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.POST, "/api/loans/**").hasAnyRole("BEHEERDER", "KLANT")
                        .requestMatchers(HttpMethod.PUT, "/api/loans/**").hasRole("BEHEERDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/loans/**").hasRole("BEHEERDER")

                        // Alle overige endpoints vereisen authenticatie
                        .anyRequest().authenticated()
                )

                // JWT-tokens worden gevalideerd via Keycloak issuer-uri
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }
    // Configureert de JWT-converter met een eigen rol-extractor
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(this::extractRoles);
        return jwtConverter;
    }
    // Leest rollen uit het Keycloak JWT-token, Keycloak plaatst rollen in realm acces roles, voeg rol prefix toe, zodat springsecurity rollen herkent
    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return List.of();
        }
        Object rolesObject = realmAccess.get("roles");
        if (!(rolesObject instanceof List<?> roles)) {
            return List.of();
        }
        return roles.stream()
                .map(Object::toString)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}