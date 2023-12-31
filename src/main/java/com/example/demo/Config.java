package com.example.demo;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Config {

	
 
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
         http.csrf(csrf -> csrf.disable())

                .httpBasic(Customizer.withDefaults());
		return http.build();
	}
	


    @Bean
    public UserDetailsService userDetailsService() {
        return new JPAUserDetailsService();
    }

	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
}


 