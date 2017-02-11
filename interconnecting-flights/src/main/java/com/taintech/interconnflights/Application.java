package com.taintech.interconnflights;

import com.taintech.ryanair.client.RoutesServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
@SpringBootApplication
@Controller
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private RoutesServiceClient routesServiceClient;

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    @Autowired
    public Application(RoutesServiceClient routesServiceClient) {
        this.routesServiceClient = routesServiceClient;
    }

    @Bean
    public CommandLineRunner run() throws Exception {
        return args -> log.info(routesServiceClient.loadRoutes().toString());
    }
}
