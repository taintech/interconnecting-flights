package com.taintech.interconnflights;

import com.taintech.ryanair.client.RoutesServiceClient;
import com.taintech.ryanair.conf.RoutesServiceClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
@SpringBootApplication
@Import(RoutesServiceClientConfiguration.class)
@RestController
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

    @GetMapping("/")
    public String home() {
        return routesServiceClient.loadRoutes().toString();
    }
}
