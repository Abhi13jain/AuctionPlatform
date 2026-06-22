package com.example.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * What this file/class does in simple English:
 * This is the starting point of our entire application. When we run the program, Java looks for this specific file to know where to begin. It's like the ignition switch for the car.
 *
 * Why it exists in this project:
 * Every Spring Boot application needs exactly one class with the @SpringBootApplication annotation and a main() method to launch the server and start wiring all the pieces together.
 *
 * What would break if it was removed:
 * The application simply wouldn't start. There would be no way to run our code.
 */
@SpringBootApplication
@EnableScheduling
public class AuctionPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionPlatformApplication.class, args);
    }
}
