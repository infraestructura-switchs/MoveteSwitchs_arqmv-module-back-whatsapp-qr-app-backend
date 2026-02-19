package com.restaurante.bot.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        String serviceAccountJson = System.getenv("FIREBASE_SERVICE_ACCOUNT");

        if (serviceAccountJson == null || serviceAccountJson.isEmpty()) {
            throw new IllegalStateException("Environment variable FIREBASE_SERVICE_ACCOUNT is not set");
        }

        InputStream serviceAccount = new java.io.ByteArrayInputStream(
                serviceAccountJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://TU_PROYECTO.firebaseio.com") // Reemplaza con tu URL real
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

}
