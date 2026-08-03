package com.tgle.planner.token.application;

import com.tgle.planner.token.domain.TokenGenerationType;
import com.tgle.planner.token.domain.TokenType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class TokenGenerationFactory {

    private final Map<TokenGenerationType, TokenGenerationStrategy> generators;

    public TokenGenerationFactory(List<TokenGenerationStrategy> generators) {
        Map<TokenGenerationType, TokenGenerationStrategy> map = new EnumMap<>(TokenGenerationType.class);
        generators.forEach(generator -> map.put(generator.getGenerationType(), generator));
        this.generators = map;
    }

    public String generateToken(TokenType type) {
        TokenGenerationStrategy generator = generators.get(type.getTokenGenerationType());

        if (generator == null) {
            throw new IllegalArgumentException("No generator for type: " + type.getTokenGenerationType());
        }

        return generator.generate();
    }
}
