package com.restaurante.bot.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        String rawServiceAccountJson = System.getenv("FIREBASE_SERVICE_ACCOUNT");

        if (rawServiceAccountJson == null || rawServiceAccountJson.isBlank()) {
            log.warn("Environment variable FIREBASE_SERVICE_ACCOUNT is not set. Firebase will not be initialized.");
            return;
        }

        log.info(
                "Firebase env detected. length={}, startsWithBrace={}, endsWithBrace={}, wrappedSingleQuotes={}, wrappedDoubleQuotes={}",
                rawServiceAccountJson.length(),
                rawServiceAccountJson.trim().startsWith("{"),
                rawServiceAccountJson.trim().endsWith("}"),
                isWrappedWith(rawServiceAccountJson, '\''),
                isWrappedWith(rawServiceAccountJson, '"'));

        String serviceAccountJson = normalizeEnvJson(rawServiceAccountJson);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(serviceAccountJson);

            String projectId = getText(root, "project_id");
            String clientEmail = getText(root, "client_email");
            String privateKey = getText(root, "private_key");

            log.info(
                    "Firebase JSON parsed. projectId={}, clientEmailDomain={}, privateKeyPresent={}, escapedNewlinesInKey={}, realNewlinesInKey={}",
                    projectId,
                    extractEmailDomain(clientEmail),
                    !privateKey.isBlank(),
                    countOccurrences(privateKey, "\\n"),
                    countOccurrences(privateKey, "\n"));

            if (privateKey.isBlank()) {
                log.error("FIREBASE_SERVICE_ACCOUNT JSON has empty private_key. Firebase will not be initialized.");
                return;
            }

            // Normalize private key for cases where newline escapes arrive double-escaped from env sources.
            String normalizedPrivateKey = privateKey.replace("\\n", "\n").trim();
            if (!normalizedPrivateKey.contains("BEGIN PRIVATE KEY")) {
                log.warn("Firebase private_key does not contain expected PKCS#8 header.");
            }

            if (root instanceof ObjectNode objectNode) {
                objectNode.put("private_key", normalizedPrivateKey);
                serviceAccountJson = objectNode.toString();
            }

            InputStream serviceAccount = new java.io.ByteArrayInputStream(
                    serviceAccountJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://TU_PROYECTO.firebaseio.com") // Reemplaza con tu URL real
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully. projectId={}", projectId);
            } else {
                log.info("Firebase already initialized. apps={}", FirebaseApp.getApps().size());
            }
        } catch (Exception e) {
            log.error(
                    "Failed to initialize Firebase. envLength={}, envPreview='{}', cause='{}'",
                    rawServiceAccountJson.length(),
                    safePreview(rawServiceAccountJson),
                    e.getMessage(),
                    e);
        }
    }

    private static String normalizeEnvJson(String rawJson) {
        String normalized = rawJson.trim();
        if (isWrappedWith(normalized, '\'')) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        if (isWrappedWith(normalized, '"')) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        return normalized.trim();
    }

    private static boolean isWrappedWith(String value, char wrapper) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.length() >= 2
                && trimmed.charAt(0) == wrapper
                && trimmed.charAt(trimmed.length() - 1) == wrapper;
    }

    private static String getText(JsonNode root, String field) {
        JsonNode node = root.get(field);
        return node == null || node.isNull() ? "" : node.asText("");
    }

    private static int countOccurrences(String value, String token) {
        if (value == null || value.isEmpty() || token == null || token.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;
        while ((index = value.indexOf(token, index)) != -1) {
            count++;
            index += token.length();
        }
        return count;
    }

    private static String extractEmailDomain(String email) {
        if (email == null || !email.contains("@")) {
            return "unknown";
        }
        return email.substring(email.indexOf('@') + 1);
    }

    private static String safePreview(String value) {
        if (value == null) {
            return "null";
        }
        String compact = value.replace("\n", "\\n");
        int max = 120;
        if (compact.length() <= max) {
            return compact;
        }
        return compact.substring(0, max) + "...";
    }

}
