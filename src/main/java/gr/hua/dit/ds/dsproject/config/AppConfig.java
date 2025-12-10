package gr.hua.dit.ds.dsproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Supports {bcrypt}, {noop}, {pbkdf2}, etc.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
