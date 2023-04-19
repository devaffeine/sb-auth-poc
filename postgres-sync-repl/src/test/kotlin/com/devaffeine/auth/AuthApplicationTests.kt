package com.devaffeine.auth

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class AuthApplicationTests {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun contextLoads() {
        Assertions.assertNotNull(passwordEncoder)
    }
}
