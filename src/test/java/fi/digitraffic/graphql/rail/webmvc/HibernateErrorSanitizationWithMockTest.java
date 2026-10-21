package fi.digitraffic.graphql.rail.webmvc;

import graphql.execution.AbortExecutionException;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class HibernateErrorSanitizationWithMockTest extends BaseWebMVCTest {

    @MockitoBean
    private JpqlWhereBuilder jpqlWhereBuilder;

    @Test
    public void mockedHibernateExceptionShouldBeSanitizedForUser() throws Exception {
        when(jpqlWhereBuilder.build(anyString(), anyMap()))
                .thenThrow(new AbortExecutionException(new HibernateException("Mocked Hibernate failure: could not resolve path")));

        final ResultActions result = queryAndExpectError("""
                {
                  passengerInformationMessages(where: {
                    trainNumber: {
                      equals: 1
                    }
                  }) {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.errors").isNotEmpty())
              .andExpect(jsonPath("$.errors[*].message").value(everyItem(not(containsString("Exception")))))
              .andExpect(jsonPath("$.errors[*].message").value(everyItem(not(containsString("Hibernate")))))
              .andExpect(jsonPath("$.errors[*].message").value(everyItem(not(containsString("org.hibernate")))));
    }
}

