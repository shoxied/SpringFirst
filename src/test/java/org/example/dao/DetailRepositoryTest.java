package org.example.dao;

import jakarta.persistence.EntityManager;
import org.example.entity.Detail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes= DetailRepositoryTest.class)
@DataJpaTest
@EntityScan(basePackageClasses = {Detail.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
})
class DetailRepositoryTest {

    @Autowired
    private DetailRepository detailRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void save_new_detail(){
        Detail detail = detailRepository.save(Detail.builder().brand("brand").build());
    }
}