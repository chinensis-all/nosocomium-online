package com.mayanshe.nosocomiumonline.application.service.bus;

import com.mayanshe.nosocomiumonline.application.dto.command.ExampleCommand;
import com.mayanshe.nosocomiumonline.application.service.handler.ExampleCommandHandler;
import com.mayanshe.nosocomiumonline.shared.cqrs.exceptions.HandlerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultCommandBusTest {

    private DefaultCommandBus commandBus;
    private ExampleCommandHandler exampleHandler;

    @BeforeEach
    void setUp() {
        commandBus = new DefaultCommandBus();
        exampleHandler = new ExampleCommandHandler();
        commandBus.register(ExampleCommand.class, exampleHandler);
    }

    @Test
    void dispatch_shouldRouteToRegisteredHandler() {
        ExampleCommand cmd = ExampleCommand.builder().name("Test").build();
        String result = commandBus.dispatch(cmd);
        assertEquals("Processed Test", result);
    }

    @Test
    void dispatch_shouldThrowException_whenHandlerNotFound() {
        ExampleCommand unregisteredCmd = ExampleCommand.builder().name("Unregistered").build();
        // Create a new bus to ensure no handlers
        DefaultCommandBus emptyBus = new DefaultCommandBus();

        assertThrows(HandlerNotFoundException.class, () -> emptyBus.dispatch(unregisteredCmd));
    }
}
