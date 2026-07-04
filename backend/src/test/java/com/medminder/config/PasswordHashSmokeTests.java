package com.medminder.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordHashSmokeTests {

    @Test
    void seededDemoHashMatchesPassword() {
        String seededHash = "$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG";

        assertThat(new BCryptPasswordEncoder().matches("password", seededHash)).isTrue();
    }
}
