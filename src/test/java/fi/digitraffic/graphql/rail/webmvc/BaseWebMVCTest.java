package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.GraphqlApplication;
import fi.digitraffic.graphql.rail.factory.FactoryService;

@SpringBootTest(classes = GraphqlApplication.class)
@AutoConfigureMockMvc
//@Transactional // Unfortunately java-graphql runs multi-threaded no matter what, so we can't use this
public abstract class BaseWebMVCTest {
    @Autowired
    protected FactoryService factoryService;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach()
    protected void setup() {
        factoryService.deleteAll();
    }

    public ResultActions queryAndExpectError(final String query) throws Exception {
        final String safeQuery = query.strip()
                .replace("\n", "")
                .replace("\"", "\\\"");

        final ResultActions firstResultActions = this.mockMvc.perform(
                        post("/graphql")
                                .content("{\"query\":\"" + safeQuery + "\"}")
                                .header("Content-Type", "application/json")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")));
        final MvcResult first = firstResultActions.andReturn();
        // Some are async request and some are not.
        if (!first.getRequest().isAsyncStarted()) {
            firstResultActions.andExpect(content().contentType("application/json"));
            return firstResultActions;
        }

        return this.mockMvc.perform(asyncDispatch(first))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk());
    }

    public ResultActions query(final String query) throws Exception {
        return queryAndExpectError(query).andExpect(jsonPath("$.errors").doesNotExist());
    }
}
