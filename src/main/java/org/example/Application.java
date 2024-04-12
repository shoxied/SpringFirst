package org.example;

import org.example.aspect.DetailRestAspect;
import org.example.config.JaegerConfiguration;
import org.example.controller.DetailController;
import org.example.csv.Impl.DetailCsvReaderImpl;
import org.example.dao.DetailRepository;
import org.example.entity.Detail;
import org.example.search.repo.SearchDetailRepo;
import org.example.service.Impl.DetailRestServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackageClasses = {
        DetailController.class,
        DetailRestServiceImpl.class,
        DetailCsvReaderImpl.class,
        JaegerConfiguration.class,
        DetailRestAspect.class
})
@EntityScan(basePackageClasses = {Detail.class})
@EnableJpaRepositories(basePackageClasses = {DetailRepository.class})
@EnableElasticsearchRepositories(basePackageClasses = {SearchDetailRepo.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
