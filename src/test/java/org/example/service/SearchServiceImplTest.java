package org.example.service;

import org.example.search.dto.SearchDetailDto;
import org.example.search.repo.SearchDetailRepo;
import org.example.service.Impl.SearchServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
@ContextConfiguration(classes = SearchServiceImplTest.class)
@Import( { SearchServiceImpl.class})
@EnableElasticsearchRepositories(basePackageClasses = { SearchDetailRepo.class })
public class SearchServiceImplTest {

    private static final ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer(DockerImageName.parse("elasticsearch:8.13.0")
                    .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
                    .withReuse(true);
    static {
        elasticsearchContainer.start();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        System.out.println("elastic host" + elasticsearchContainer.getHttpHostAddress());
        registry.add("spring.elasticsearch.rest.uris", elasticsearchContainer::getHttpHostAddress);
        registry.add("app.elastic-executor.pool-size", () -> 3);
        registry.add("app.elastic-executor.hard-delay", () -> 3000);
        registry.add("app.elastic-executor.hard-priority-limit", () -> 10);
        registry.add("app.elastic-executor.soft-priority-limit", () -> 1);
        registry.add("app.elastic-executor.active-threads", () -> 2);
        registry.add("app.elastic-executor.timeout", () -> 10000);
        registry.add("app.elastic-executor.log-timeout", () -> 1000);
        registry.add("app.search.currency-id", () ->"643");
    }

    private static final Random random = new Random(System.currentTimeMillis());

    @Autowired
    private SearchServiceImpl searchService;

    @Autowired
    private SearchDetailRepo searchDetailRepo;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ElasticsearchConverter elasticsearchConverter;

    @AfterEach
    void tearDown() {
        clean();
    }

    private void clean() {
        searchDetailRepo.deleteAll();
        elasticsearchOperations.indexOps(SearchDetailDto.class).refresh();
    }

    @Test
    void simple_query(){
        searchDetailRepo.save(SearchDetailDto.builder()
                .id(1)
                .build());
        SearchHits<SearchDetailDto> searchHits = searchService.getDetails(null, null, null, null);
        assertTrue(searchHits.getTotalHits() >= 1);
    }

    @Test
    void pages_query(){
        searchDetailRepo.save(SearchDetailDto.builder()
                .id(1)
                .build());
        searchDetailRepo.save(SearchDetailDto.builder()
                .id(2)
                .build());
        searchDetailRepo.save(SearchDetailDto.builder()
                .id(3)
                .build());
        searchDetailRepo.save(SearchDetailDto.builder()
                .id(4)
                .build());
        searchDetailRepo.save(SearchDetailDto.builder()
                .id(5)
                .build());
        SearchHits<SearchDetailDto> searchHits = searchService.getDetails(null, null, null, null);
        assertTrue(searchHits.getSearchHits().size() < searchHits.getTotalHits());
    }

    @Test
    void query_get_detail_by_name(){
        searchDetailRepo.save(SearchDetailDto.builder()
                .id(1)
                .name("name")
                .build());
        searchDetailRepo.save(SearchDetailDto.builder()
                .id(2)
                .name("name1")
                .build());
        SearchHits<SearchDetailDto> searchHits = searchService.getDetails("name", null, null, null);
        assertEquals(1, searchHits.getTotalHits());
        assertEquals("name", searchHits.getSearchHits().get(0).getContent().getName());
    }
}
