package com.mayanshe.nosocomiumonline.application.service.bus;

import com.mayanshe.nosocomiumonline.application.dto.command.ExampleCommand;
import com.mayanshe.nosocomiumonline.application.dto.query.ExampleQuery;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandBus;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusUsageExampleService {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public void demonstrateUsage() {
        // Dispatch command
        ExampleCommand cmd = ExampleCommand.builder().name("Demo").build();
        String result = commandBus.dispatch(cmd);
        log.info("Command result: {}", result);

        // Ask query
        ExampleQuery query = ExampleQuery.builder().id(123L).build();
        String queryResult = queryBus.ask(query);
        log.info("Query result: {}", queryResult);
    }
}
