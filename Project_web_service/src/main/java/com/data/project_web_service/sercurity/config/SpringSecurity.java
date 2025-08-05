package com.data.project_web_service.sercurity.config;

import com.data.project_web_service.sercurity.jwt.JWTAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SpringSecurity {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SpringSecurity(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JWTAuthenticationEntryPoint();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return daoProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, JWTAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public cho phép truy cập đăng ký, đăng nhập
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/verify-otp").permitAll()

                        // Profile và đổi mật khẩu, logout
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/profile").hasAnyRole("ADMIN", "CUSTOMER", "SALES")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/auth/profile").hasAnyRole("ADMIN", "CUSTOMER", "SALES")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/auth/change-password").hasAnyRole("ADMIN", "CUSTOMER", "SALES")
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").hasAnyRole("ADMIN", "CUSTOMER", "SALES")

                        // Quản lý user:
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/*/status").hasRole("ADMIN")

                        // Danh mục
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/categories/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasAnyRole("ADMIN", "SALES")

                        // Sản phẩm
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")

                        // giỏ hàng
                        .requestMatchers(HttpMethod.GET, "/api/v1/cart-items").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart-items").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cart-items/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart-items/**").hasRole("CUSTOMER")

                        // đơn hàng
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyRole("ADMIN", "SALES", "CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/*/status").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/*").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/*").hasRole("ADMIN")

                        // hóa đơn
                        .requestMatchers(HttpMethod.GET, "/api/v1/invoices/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.POST, "/api/v1/invoices").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/invoices/*/status").hasAnyRole("ADMIN", "SALES")

                        // hóa đơn theo đơn hàng
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/*/invoice").hasAnyRole("ADMIN", "SALES")

                        // Thanh toán
                        .requestMatchers(HttpMethod.POST, "/api/v1/payments").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/*").hasAnyRole("ADMIN", "SALES", "CUSTOMER")

                        // báo cáo thống kê
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports/sales-summary").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports/top-products").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports/revenue").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports/inventory").hasAnyRole("ADMIN", "SALES")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
