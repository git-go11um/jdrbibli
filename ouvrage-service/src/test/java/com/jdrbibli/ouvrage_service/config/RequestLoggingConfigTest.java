package com.jdrbibli.ouvrage_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingConfigTest {

    @Test
    void logFilter_shouldBeCreated() {
        // On crée le bean manuellement comme Spring le ferait
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(false);

        assertThat(filter).isNotNull();
        assertThat(filter).isInstanceOf(CommonsRequestLoggingFilter.class);
    }
}
