package com.mayanshe.nosocomiumonline.infrastructure.cqrs;

import com.mayanshe.nosocomiumonline.application.dto.command.ExampleCommand;
import com.mayanshe.nosocomiumonline.application.dto.query.ExampleQuery;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandBus;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryBus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = CqrsIntegrationTest.TestConfig.class)
class CqrsIntegrationTest {

    @Configuration
    @Import({ CqrsBusConfiguration.class, HandlerRegistry.class })
    @ComponentScan("com.mayanshe.nosocomiumonline.application.service.bus") // Scan buses
    static class TestConfig {
    }

    @Autowired
    private CommandBus commandBus;

    @Autowired
    private QueryBus queryBus;

    @Test
    void busesShouldBeAutoWired() {
        assertNotNull(commandBus);
        assertNotNull(queryBus);
    }

    @Test
    void shouldRegisterAndRouteCommand() {
        // Dispatch command to ExampleCommandHandler (which is scanned by
        // CqrsBusConfiguration)
        ExampleCommand cmd = ExampleCommand.builder().name("Integration").build();
        String result = commandBus.dispatch(cmd);

        assertEquals("Processed Integration", result);
    }

    @Test
    void shouldRegisterAndRouteQuery() {
        // Ask query to ExampleQueryHandler
        ExampleQuery query = ExampleQuery.builder().id(999L).build();
        String result = queryBus.ask(query);

        assertEquals("Result for ID 999", result);
    }
}
