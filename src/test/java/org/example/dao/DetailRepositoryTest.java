package org.example.dao;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes= DetailRepositoryTest.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@EntityScan(basePackageClasses = {Detail.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
})
class DetailRepositoryTest {

    @Autowired
    private ValueRepository valueRepository;
    @Autowired
    private DetailRepository detailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void save_new_detail(){
        Value value = valueRepository.findById(1).orElseThrow();

        Detail detail = detailRepository.save(Detail.builder().brand("brand").oem("oem").name("name")
                        .attributeValues(List.of(AttributeValue.builder().value(value).build())).build());

        entityManager.clear();
        detail = detailRepository.findById(detail.getId()).orElseThrow();

        assertEquals("oem", detail.getOem());
        assertEquals("1", detail.getAttributeValues().get(0).getValue().getValue());
    }

    @Test
    void find_by_brand(){
        Detail detail = Detail.builder()
                .brand("brand")
                .oem("oem")
                .name("name")
                .build();
        detailRepository.save(detail);

        List<Detail> details = detailRepository.findByBrand("brand");
        assertEquals("brand", details.get(0).getBrand());
        assertEquals("oem", details.get(0).getOem());
        assertEquals(1, details.get(0).getId());
    }
}