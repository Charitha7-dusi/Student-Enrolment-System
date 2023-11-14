package ses.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class InMemorySecurity {

	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

		UserDetails user = User.withUsername("charitha").password(passwordEncoder.encode("charitha"))
				.roles("ADMIN", "USER").build();

		UserDetails admin = User.withUsername("user").password(passwordEncoder.encode("user")).roles("USER").build();

		return new InMemoryUserDetailsManager(user, admin);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return encoder;
	}
}