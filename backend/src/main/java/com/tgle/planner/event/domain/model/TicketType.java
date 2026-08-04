package com.tgle.planner.event.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TicketType {
    @EqualsAndHashCode.Include
    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final String description;
    private final Integer capacity;
    private final Long eventId;

    public static TicketType create(
            String name,
            BigDecimal price,
            String description,
            Integer capacity,
            Long eventId
    ) {
        return TicketType.builder()
                .name(name)
                .price(price)
                .description(description)
                .capacity(capacity)
                .eventId(eventId)
                .build();
    }
}
