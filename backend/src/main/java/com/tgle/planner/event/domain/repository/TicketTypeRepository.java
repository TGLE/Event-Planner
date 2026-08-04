package com.tgle.planner.event.domain.repository;

import com.tgle.planner.event.domain.model.TicketType;

import java.util.List;

public interface TicketTypeRepository {
    TicketType save(TicketType ticketType);
    List<TicketType> saveAll(List<TicketType> ticketTypes);
}
