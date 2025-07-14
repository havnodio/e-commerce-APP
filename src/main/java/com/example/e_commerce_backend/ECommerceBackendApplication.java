package com.example.e_commerce_backend;

import com.example.e_commerce_backend.user.User;
import com.example.e_commerce_backend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@SpringBootApplication
public class ECommerceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin") == null) {
				User user = new User();
				user.setUsername("admin");
				user.setPassword(passwordEncoder.encode("admin123"));
				user.setRoles(Set.of("ADMIN"));
				userRepository.save(user);
				System.out.println("✅ Default admin user created: admin/admin123");
			} else {
				System.out.println("ℹ️ Admin user already exists, skipping creation.");
			}
		};
	}
}
