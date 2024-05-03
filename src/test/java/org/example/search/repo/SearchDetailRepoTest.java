package org.example.search.repo;

import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
@ContextConfiguration(classes = SearchDetailRepoTest.class)
@EnableElasticsearchRepositories(basePackageClasses = { SearchDetailRepo.class })
class SearchDetailRepoTest {

    private static final ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer(DockerImageName.parse("elasticsearch:8.13.0")
                    .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
                    .withReuse(true);
    static {
        elasticsearchContainer.start();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.rest.uris", elasticsearchContainer::getHttpHostAddress);
    }

    private static final Random random = new Random(System.currentTimeMillis());

    @Autowired
    private SearchDetailRepo searchDetailRepo;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @AfterEach
    void tearDown() {
        clean();
    }

    private void clean() {
        searchDetailRepo.deleteAll();
        elasticsearchOperations.indexOps(SearchDetailDto.class).refresh();
    }

    @Test
    void save_detail_with_attributeValue() {
        int id = 1;
        searchDetailRepo.save(SearchDetailDto.builder()
                        .id(id)
                        .brand("brand")
                        .oem("oem")
                        .name("name")
                        .attributes(List.of(SearchDetailValueDto.builder()
                                .id(2)
                                .attributeId(2)
                                .attributeName("attrName")
                                .value("value")
                                .build()))
                        .build());
        SearchDetailDto actual = searchDetailRepo.findById(id).orElseThrow();

        assertEquals(id, actual.getId());
        assertEquals("brand", actual.getBrand());
        assertEquals("oem", actual.getOem());
        assertEquals("name", actual.getName());
        SearchDetailValueDto valueDto = actual.getAttributes().iterator().next();
        assertEquals(2, valueDto.getId());
        assertEquals(2, valueDto.getAttributeId());
        assertEquals("attrName", valueDto.getAttributeName());
        assertEquals("value", valueDto.getValue());

        searchDetailRepo.deleteById(id);
        assertFalse(searchDetailRepo.existsById(id));
    }
}