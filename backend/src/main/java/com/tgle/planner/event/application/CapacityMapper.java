package com.tgle.planner.event.application;

import com.tgle.planner.event.domain.model.Capacity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CapacityMapper {

    default Capacity toCapacity(Integer capacity) {
        return capacity == null ? Capacity.unlimited() : Capacity.of(capacity);
    }

    default Integer toInteger(Capacity capacity) {
        if (capacity == null) {
            return null;
        }
        return capacity.isUnlimited() ? null : capacity.value();
    }

    default JsonNullable<Capacity> toCapacity(JsonNullable<Integer> capacity) {
        if (capacity == null || capacity.isUndefined()) {
            return JsonNullable.undefined();
        }
        return JsonNullable.of(toCapacity(capacity.orElse(null)));
    }

    default JsonNullable<Integer> toInteger(JsonNullable<Capacity> capacity) {
        if (capacity == null || capacity.isUndefined()) {
            return JsonNullable.undefined();
        }
        return JsonNullable.of(toInteger(capacity.orElse(null)));
    }
}
