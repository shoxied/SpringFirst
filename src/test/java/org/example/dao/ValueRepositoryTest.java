package org.example.dao;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.entity.Attribute;
import org.example.entity.Value;
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

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
class ValueRepositoryTest {

    @Autowired
    private ValueRepository valueRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void findAttributeByNameTest(){
        Attribute attribute = attributeRepository.findByName("Объем");
        List<Value> values = valueRepository.findByAttribute(attribute);

        assertEquals(attribute.getId(), values.get(0).getAttribute().getId());
        entityManager.clear();
        assertEquals("2", values.get(1).getValue());
    }
}