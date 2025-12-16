package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.query.ExampleQuery;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExampleQueryHandler implements QueryHandler<ExampleQuery, String> {
    @Override
    public String handle(ExampleQuery query) {
        log.info("Handling query: {}", query);
        return "Result for ID " + query.getId();
    }
}
