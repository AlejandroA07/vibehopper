package com.vibehopper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class VibeHopperApplication {
    public static void main(String[] args) { SpringApplication.run(VibeHopperApplication.class, args); }
}
