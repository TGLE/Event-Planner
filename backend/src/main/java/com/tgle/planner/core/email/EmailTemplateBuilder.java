package com.tgle.planner.core.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailTemplateBuilder {

    private final SpringTemplateEngine springTemplateEngine;

    public RenderedEmail buildEmail(EmailTemplate template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String body = springTemplateEngine.process(template.getPath(), context);
        String subject = template.getSubject();
        return new RenderedEmail(subject, body);
    }
}
