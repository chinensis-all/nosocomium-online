package com.mayanshe.nosocomiumonline.application.dto.command;

import com.mayanshe.nosocomiumonline.shared.cqrs.Command;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExampleCommand implements Command {
    private String name;
}
