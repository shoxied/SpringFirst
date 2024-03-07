package org.example.myrest;

import org.example.dao.DetailRepository;
import org.example.entity.Detail;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import({MyRest.class})
@EntityScan(basePackageClasses = {Detail.class})
@EnableJpaRepositories(basePackageClasses = {DetailRepository.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
