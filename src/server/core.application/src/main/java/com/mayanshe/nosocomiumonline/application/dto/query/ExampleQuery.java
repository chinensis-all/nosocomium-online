package com.mayanshe.nosocomiumonline.application.dto.query;

import com.mayanshe.nosocomiumonline.shared.cqrs.Query;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExampleQuery implements Query {
    private Long id;
}
