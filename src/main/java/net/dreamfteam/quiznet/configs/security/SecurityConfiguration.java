package net.dreamfteam.quiznet.configs.security;


import net.dreamfteam.quiznet.configs.constants.Constants;
import net.dreamfteam.quiznet.configs.token.JwtAuthenticationEntryPoint;
import net.dreamfteam.quiznet.configs.token.JwtConfigurer;
import net.dreamfteam.quiznet.configs.token.JwtTokenProvider;
import net.dreamfteam.quiznet.service.impl.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsUtils;

/**
 * Main security configuration
 *
 * @author Yevhen Khominich
 * @version 1.0
 */


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    final private JwtAuthenticationEntryPoint unauthorizedHandler;
    final private JwtUserDetailsService jwtUserDetailsService;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;
    final private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfiguration(JwtAuthenticationEntryPoint unauthorizedHandler, JwtUserDetailsService jwtUserDetailsService,
                                 BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(jwtUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public void configure(WebSecurity webSecurity) {
        webSecurity
                .ignoring()
                .antMatchers(
                        Constants.SECUR_SIGN_UP_URLS,
                        Constants.SECUR_ANONYM,
                        Constants.SECUR_LOG_IN_URLS,
                        Constants.SECUR_ACTIVATION_URLS,
                        Constants.SECUR_RECOVER_URLS,
                        Constants.SECUR_QUIZ_TOTAL_SIZE_URLS,
                        Constants.SECUR_QUIZ_LIST_URLS,
                        Constants.SECUR_QUIZ_CATEG_LIST_URLS,
                        Constants.SECURE_ANNOUNCEMENT_LIST_URLS,
                        Constants.SECURE_ANNOUNCEMENT_URLS,
                        Constants.SECURE_ANNOUNCEMENT_SIZE,
                        Constants.SECUR_QUIZ_TAGS_URLS,
                        Constants.SECUR_SHORT_QUIZ_LIST_URLS,
                        Constants.SECUR_NOTIFICATION_SSE_URLS,
                        Constants.SECUR_GAME_SSE_URLS,
                        Constants.SECUR_FILTER_QUIZ_LIST_URLS,
                        //for Swagger
                        Constants.SECUR_DOCS_URLS,
                        Constants.SECUR_CONFIG_UI_URLS,
                        Constants.SECUR_SWAGGER_RESOURCES_URLS,
                        Constants.SECUR_CONFIG_SECURITY_URLS,
                        Constants.SECUR_SWAGGER_UI_URLS,
                        Constants.SECUR_WEBJARS_URLS

                )
                .antMatchers(HttpMethod.GET, Constants.SECUR_QUIZ_URLS, Constants.SECUR_QUIZ_QUESTION_LIST_URLS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()// off httpBasic
                .csrf().disable()     // off csrf
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)// add Exception Handler
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // without session
                .and()
                .authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()//other URLS only authenticated( with token)
                .and()
                .anonymous()
                .and()
                .cors()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));


    }

}
