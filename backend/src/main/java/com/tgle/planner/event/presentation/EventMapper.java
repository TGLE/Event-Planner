package com.tgle.planner.event.presentation;

import com.tgle.planner.event.application.CapacityMapper;
import com.tgle.planner.event.application.dto.command.EventCreateCommand;
import com.tgle.planner.event.application.dto.command.EventImageUploadCommand;
import com.tgle.planner.event.application.dto.command.EventUpdateCommand;
import com.tgle.planner.event.application.dto.result.EventResult;
import com.tgle.planner.event.domain.data.EventCreateData;
import com.tgle.planner.event.domain.data.EventUpdateData;
import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.domain.model.EventDates;
import com.tgle.planner.event.infrastructure.persistence.projections.EventSummary;
import com.tgle.planner.event.infrastructure.persistence.projections.PublicEventSummary;
import com.tgle.planner.event.presentation.dto.request.*;
import com.tgle.planner.event.presentation.dto.response.*;
import com.tgle.planner.event.presentation.search.EventSearchCriteria;
import com.tgle.planner.event.presentation.search.PublicEventSearchCriteria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CapacityMapper.class)
public interface EventMapper {
    EventCreateCommand toCommand(EventCreateRequest request, Long userId);
    EventCreateData toData(EventCreateCommand command);

    EventUpdateCommand toCommand(EventUpdateRequest request, Long publicId, Long userId);
    EventUpdateData toData(EventUpdateCommand command);

    EventImageUploadCommand toCommand(EventImageUploadRequest request, Long publicId, Long userId);

    EventSearchCriteria toCriteria(EventSearchRequest request);

    @Mapping(source = "imagePath", target = "imageUrl")
    EventSummaryResponse toResponse(EventSummary summary);

    PublicEventSearchCriteria toCriteria(PublicEventSearchRequest request);

    @Mapping(source = "imagePath", target = "imageUrl")
    @Mapping(source = "dates.publishedAt", target = "publishedAt")
    PublicEventSummaryResponse toResponse(PublicEventSummary summary);

    @Mapping(source = "imagePath", target = "imageUrl")
    EventResponse toResponse(Event event);

    @Mapping(source = "imagePath", target = "imageUrl")
    PublicEventResponse toPublicResponse(Event event);

    @Mapping(source = "imagePath", target = "imageUrl")
    EventResponse toResponse(EventResult result);
    EventResult toResult(Event event);
}
