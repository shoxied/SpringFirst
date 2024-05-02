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

    private Integer startId;
    private Integer detailId;
    private Integer productTypeId;

    @BeforeEach
    void setUp() {
        clean();
        startId = Math.abs(random.nextInt()) / 1000;
        detailId = startId;
        productTypeId = startId + 100;
    }

    @AfterEach
    void tearDown() {
        clean();
    }

    private void clean() {
        searchDetailRepo.deleteAll();
        elasticsearchOperations.indexOps(SearchDetailDto.class).refresh();
    }

    @Test
    void saved_attribute() {
        int id = detailId++;
        searchDetailRepo.save(SearchDetailDto.builder()
                        .id(id)
                        .brand("brand")
                        .oem("oem")
                        .name("name")
                        .attributes(List.of(SearchDetailValueDto.builder()
                                .id(startId)
                                .attributeId(startId)
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
        assertEquals(startId, valueDto.getId());
        assertEquals(startId, valueDto.getAttributeId());
        assertEquals("attrName", valueDto.getAttributeName());
        assertEquals("value", valueDto.getValue());

        searchDetailRepo.deleteById(id);
        assertFalse(searchDetailRepo.existsById(id));
    }
}