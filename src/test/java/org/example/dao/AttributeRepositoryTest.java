package org.example.dao;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.entity.Attribute;
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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes= AttributeRepositoryTest.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@EntityScan(basePackageClasses = {Attribute.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
})
class AttributeRepositoryTest {

    @Autowired
    private AttributeRepository attributeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void findAttributeByNameTest(){
        Attribute attribute = attributeRepository.findByName("Объем");
        entityManager.clear();
        assertEquals("Объем", attribute.getName());
        assertEquals(1, attribute.getId());
    }
}