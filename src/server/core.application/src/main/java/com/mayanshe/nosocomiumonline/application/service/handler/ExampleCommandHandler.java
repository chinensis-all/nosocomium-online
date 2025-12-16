package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.command.ExampleCommand;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExampleCommandHandler implements CommandHandler<ExampleCommand, String> {
    @Override
    public String handle(ExampleCommand command) {
        log.info("Handling command: {}", command);
        return "Processed " + command.getName();
    }
}
