package com.itsol.mockup.config;

import com.itsol.mockup.filter.AuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author anhvd_itsol
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(super.authenticationManagerBean());
        return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf() //Adds CSRF support.
                .disable() //Disabling CSRF support
                .exceptionHandling() //Allows configuring exception handling.
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement() //Allows configuring of Session Management.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Allows specifying the SessionCreationPolicy
                //STATELESS - Spring Security will never create an HttpSession and it will never use it to obtain the SecurityContext
                .and()
                .authorizeRequests() //Request configuration
                .antMatchers("/auth").permitAll() //Permit all from this path
//                .antMatchers("/api").hasAuthority("MEMBER") //Permit all from this path
                .antMatchers("/**").permitAll()
                .antMatchers("/api/updateActiveUser").permitAll()
                .anyRequest().authenticated(); //From any request check the authentication

        //Filter for JWT based authentication
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
}
