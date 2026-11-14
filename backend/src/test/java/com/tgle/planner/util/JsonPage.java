package com.tgle.planner.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class JsonPage<T> extends PageImpl<T> {
    @JsonCreator
    public JsonPage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("page") PageMetadata page
    ) {
        super(content, PageRequest.of(page.number(), page.size() == 0 ? 1 : page.size()), page.totalElements());
    }

    public record PageMetadata(int size, int number, long totalElements, int totalPages) {}
}
