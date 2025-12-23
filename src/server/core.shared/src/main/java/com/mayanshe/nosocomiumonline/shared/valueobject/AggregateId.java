package com.mayanshe.nosocomiumonline.shared.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregateId {
    private Long id;

    private boolean newed;

    public static AggregateId of(Long id) {
        return new AggregateId(id, false);
    }
}
