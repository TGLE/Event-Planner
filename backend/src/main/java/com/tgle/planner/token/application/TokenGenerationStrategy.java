package com.tgle.planner.token.application;

import com.tgle.planner.token.domain.TokenGenerationType;

public interface TokenGenerationStrategy {
    TokenGenerationType getGenerationType();
    String generate();
}
