// SecurityConfig.java

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PHP-এর password_hash() (BCrypt) এর সাথে জাভার এনকোডার
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // users.json থেকে ইউজার ডেটা লোড করার জন্য
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) throws Exception {
        // users.json ফাইলটি পড়া
        ObjectMapper mapper = new ObjectMapper();
        File userFile = new File("users.json");
        Map<String, String> userData = mapper.readValue(userFile, Map.class);
        
        // মেমরিতে ইউজার তৈরি করা
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername(userData.get("username"))
                .password(userData.get("password_hash")) // হ্যাশ করা পাসওয়ার্ড
                .roles("ADMIN")
                .build());
        return manager;
    }

    // কোন পেজ সুরক্ষিত থাকবে আর কোনটি নয়, তা ঠিক করা
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // এই পেজগুলো সবাই দেখতে পাবে
                .requestMatchers("/", "/index.html", "/main.js", "/welcome.php", "/login.php").permitAll()
                // এই পেজগুলো শুধুমাত্র লগইন করা ইউজাররা দেখতে পাবে
                .requestMatchers("/data.php", "/uploads.php", "/change_password.php", "/update_password.php", "/uploads/**").authenticated() 
            )
            // লগইন ফর্ম কনফিগার করা
            .formLogin(form -> form
                .loginPage("/login.php") // আপনার লগইন পেজের URL
                .loginProcessingUrl("/check_login.php") // ফর্মটি এই URL-এ সাবমিট হবে
                .defaultSuccessUrl("/data.php", true) // লগইন সফল হলে এখানে যাবে
                .failureUrl("/login.php?error=1") // লগইন ব্যর্থ হলে
                .permitAll()
            )
            // লগআউট কনফিগার করা
            .logout(logout -> logout
                .logoutUrl("/logout.php") // এই URL-এ লগআউট হবে
                .logoutSuccessUrl("/login.php") // লগআউট সফল হলে এখানে যাবে
                .permitAll()
            )
            // CSRF সুরক্ষা বন্ধ করা (API-এর জন্য সহজ)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
