package com.firstSpring.firstSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication()
public class FirstSpringApplication {
    private static final Logger LOG = Logger.getLogger(FirstSpringApplication.class.getName());

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry
                    -> System.setProperty(entry.getKey(), entry.getValue())
            );
            LOG.log(Level.INFO, "Variables are loaded correctly: {values}", dotenv.entries().toString());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error loading .env: {error}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(FirstSpringApplication.class, args);
    }

}
