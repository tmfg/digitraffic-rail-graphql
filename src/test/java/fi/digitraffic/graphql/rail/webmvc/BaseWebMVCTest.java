package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.GraphqlApplication;
import fi.digitraffic.graphql.rail.repositories.StationRepository;
import fi.digitraffic.graphql.rail.repositories.TrainCategoryRepository;
import fi.digitraffic.graphql.rail.repositories.TrainLocationRepository;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.repositories.TrainTypeRepository;

@SpringBootTest(classes = GraphqlApplication.class)
@AutoConfigureMockMvc
//@Transactional // Unfortunately java-graphql runs multi-threaded no matter what, so we can't use this
public abstract class BaseWebMVCTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected TrainRepository trainRepository;

    @Autowired
    protected StationRepository stationRepository;

    @Autowired
    private TrainLocationRepository trainLocationRepository;

    @Autowired
    private TrainTypeRepository trainTypeRepository;

    @Autowired
    private TrainCategoryRepository trainCategoryRepository;

    @BeforeEach()
    private void setup() {
        trainRepository.deleteAll();
        stationRepository.deleteAll();
        trainLocationRepository.deleteAll();
        trainTypeRepository.deleteAll();
        trainCategoryRepository.deleteAll();
    }

    public ResultActions queryAndExpectError(final String query) throws Exception {
        final String safeQuery = query.strip()
            .replace("\n", "")
            .replace("\"", "\\\"");

        final MvcResult first = this.mockMvc.perform(
                post("/graphql")
                    .content("{\"query\":\"" + safeQuery + "\"}")
                    .header("Content-Type", "application/json")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
            .andReturn();
        return this.mockMvc.perform(asyncDispatch(first))
            .andExpect(content().contentType("application/json"))
            .andExpect(status().isOk());
    }

    public ResultActions query(final String query) throws Exception {
        return queryAndExpectError(query).andExpect(jsonPath("$.errors.length()").doesNotExist());
    }
}
