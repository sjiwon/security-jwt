package com.sjiwon.securityjwt.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.securityjwt.common.config.DatabaseCleanerEachCallbackExtension;
import com.sjiwon.securityjwt.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
public abstract class SecurityIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(final WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected String toBody(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected ResultMatcher[] getResultMatchersViaErrorCode(final ErrorCode eror) {
        return new ResultMatcher[]{
                jsonPath("$.status").exists(),
                jsonPath("$.status").value(eror.getStatus().value()),
                jsonPath("$.errorCode").exists(),
                jsonPath("$.errorCode").value(eror.getErrorCode()),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(eror.getMessage())
        };
    }

    protected ResultMatcher[] getResultMatchersViaErrorCode(final ErrorCode eror, final String message) {
        return new ResultMatcher[]{
                jsonPath("$.status").exists(),
                jsonPath("$.status").value(eror.getStatus().value()),
                jsonPath("$.errorCode").exists(),
                jsonPath("$.errorCode").value(eror.getErrorCode()),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(message)
        };
    }
}
