package org.example.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.DetailRepository;
import org.example.entity.AttributeValue;
import org.example.entity.Detail;
import org.example.search.dto.SearchDetailDto;
import org.example.search.dto.SearchDetailValueDto;
import org.example.search.repo.SearchDetailRepo;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitListener implements InitializingBean {

    private static final int ID = Math.abs(new Random().nextInt());

    private static final String QUEUE = "my-first-queue";
    public static final String EXCHANGE = "my-exchange";
    public static final String ROUTING_KEY = "key";

    private final SearchDetailRepo searchDetailRepo;
    private final DetailRepository detailRepository;

    @org.springframework.amqp.rabbit.annotation.RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY,
            value = @Queue(value = QUEUE, durable = "false", exclusive = "true"),
            exchange = @Exchange(value = EXCHANGE, durable = "false", type = "topic")))
    public void receive(int id) {

        log.info("received detail {}", id);

        List<SearchDetailValueDto> attributes = new ArrayList<>();

        Optional<Detail> detail = detailRepository.findById(id);
        if (detail.get().getAttributeValues() != null){
            for (AttributeValue attribute: detail.get().getAttributeValues()){
                attributes.add(SearchDetailValueDto.builder()
                        .id(attribute.getId())
                        .attributeId(attribute.getValue().getAttribute().getId())
                        .attributeName(attribute.getValue().getAttribute().getName())
                        .valueId(attribute.getValue().getId())
                        .value(attribute.getValue().getValue())
                        .build());

            }
        }

        searchDetailRepo.save(SearchDetailDto.builder()
                .id(id)
                .brand(detail.get().getBrand())
                .oem(detail.get().getOem())
                .name(detail.get().getName())
                .attributes(attributes)
                .build());

        log.info("saved detail {} to elastic", id);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("ID - {}", ID);
    }
}
