package ftn.sbnz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ftn.sbnz.security.CustomUserDetailsService;
import ftn.sbnz.security.TokenUtils;
import ftn.sbnz.security.auth.RestAuthenticationEntryPoint;
import ftn.sbnz.security.auth.TokenAuthenticationFilter;
import ftn.sbnz.service.KieSessionService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JobAssistantSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		// TODO: frontend
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				CorsRegistration cr = registry.addMapping("/**");
				cr.allowedMethods("*");
			}
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private CustomUserDetailsService jwtUserDetailsService;

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	@Autowired
	private KieSessionService kieSession;

	// Registrujemo authentication manager koji ce da uradi autentifikaciju
	// korisnika za nas
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// Definisemo uputstvo za authentication managera koji servis da koristi da
	// izvuce podatke o korisniku koji zeli da se autentifikuje,
	// kao i kroz koji enkoder da provuce lozinku koju je dobio od klijenta u
	// zahtevu da bi adekvatan hash koji dobije kao rezultat bcrypt algoritma
	// uporedio sa onim koji se nalazi u bazi (posto se u bazi ne cuva plain
	// lozinka)
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}

	// Injektujemo implementaciju iz TokenUtils klase kako bismo mogli da koristimo
	// njene metode za rad sa JWT u TokenAuthenticationFilteru
	@Autowired
	private TokenUtils tokenUtils;

	// Definisemo prava pristupa odredjenim URL-ovima
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		 * http.authorizeRequests().anyRequest().authenticated() .and() .x509()
		 * .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
		 * .userDetailsService(userDetailsService());
		 */

		http
				// komunikacija izmedju klijenta i servera je stateless posto je u pitanju REST
				// aplikacija
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

				// sve neautentifikovane zahteve obradi uniformno i posalji 401 gresku
				.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()

				// svim korisnicima dopusti da pristupe putanjama /auth/**, (/h2-console/** ako
				// se koristi H2 baza) i /api/foo
				.authorizeRequests().antMatchers("/auth/**").permitAll()

				// za svaki drugi zahtev korisnik mora biti autentifikovan
				.anyRequest().authenticated().and()
				// za development svrhe ukljuci konfiguraciju za CORS iz WebConfig klase
				.cors().and()

				// umetni custom filter TokenAuthenticationFilter kako bi se vrsila provera JWT
				// tokena umesto cistih korisnickog imena i lozinke (koje radi
				// BasicAuthenticationFilter)
				.addFilterBefore(new TokenAuthenticationFilter(tokenUtils, jwtUserDetailsService, kieSession),
						BasicAuthenticationFilter.class);
		// zbog jednostavnosti primera
		http.csrf().disable();

	}

	// Generalna bezbednost aplikacije
	@Override
	public void configure(WebSecurity web) throws Exception {
		// TokenAuthenticationFilter ce ignorisati sve ispod navedene putanje
		web.ignoring().antMatchers(HttpMethod.POST, "/auth/login", "/auth/register");
		web.ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "/favicon.ico", "/**/*.html",
				"/**/*.css", "/**/*.js", "/api/test");
	}
}
