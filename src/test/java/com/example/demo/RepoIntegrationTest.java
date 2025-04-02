package com.example.demo;

import com.example.demo.repos.dto.BranchDto;
import com.example.demo.repos.dto.ErrorStatusDto;
import com.example.demo.repos.dto.RepoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.yml") // no need for separate config for this case
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepoIntegrationTest {

    @Value("${github.api}")
    private String repoApi;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockRestServiceServer mockServer;

    // Test data begin

    private String branch1 = """
            {"name":"test1", "commit":{"sha": "12345"}}
            """;
    private String branch2 = """
            {"name":"test2", "commit":{"sha": "12345"}}
            """;

    private BranchDto branchDto1 = new BranchDto("test1", "12345");
    private BranchDto branchDto2 = new BranchDto("test2", "12345");

    private String repoNoFork = """
            {"name": "repo1", "owner":{"login":"user1"}, "fork": false}
            """;
    private String repoFork = """
            {"name": "repo2", "owner":{"login":"user1"}, "fork": true}
            """;
    private String repoNoForkNoBranches = """
            {"name": "repo3", "owner":{"login":"userNoBranch"}, "fork": false}
            """;
    private String repoDeleted = """
            {"name": "repoDel", "owner":{"login":"userDelRepo"}, "fork": false}
            """;

    private RepoDto repoDtoNoFork = new RepoDto("repo1", "user1", Arrays.asList(branchDto1, branchDto2));
    private RepoDto repoDtoNoForkNoBranches = new RepoDto("repo3", "userNoBranch", Collections.emptyList());

    private String userDefault = "user1", userNoRepo = "userNoRepo", userOnlyFork = "userFork",
            userNoBranches = "userNoBranch", userNonExist = "userNoExist", userDeletedRepo = "userDelRepo";

    private ErrorStatusDto err1 = new ErrorStatusDto(404, MessageFormat.format("User {0} was not found", userNonExist));
    private ErrorStatusDto err2 = new ErrorStatusDto(404, MessageFormat.format("Repository {0}/{1} was not found", userDeletedRepo, "repoDel"));

    // Test data end

    @BeforeAll
    public void init() {
        this.mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @AfterEach
    public void tearDown() {
        mockServer.reset();
    }

    @Test
    public void testUserHasManyRepos() throws Exception {
        mockServer.expect(requestTo(MessageFormat.format("{0}users/{1}/repos", repoApi, userDefault)))
                .andRespond(withSuccess(MessageFormat.format("[{0},{1},{2}]", repoFork, repoNoFork, repoNoFork), MediaType.APPLICATION_JSON));
        mockServer.expect(ExpectedCount.times(2),
                        requestTo(MessageFormat.format("{0}repos/{1}/{2}/branches", repoApi, userDefault, "repo1")))
                        .andRespond(withSuccess(MessageFormat.format("[{0},{1}]", branch1, branch2), MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/" + userDefault))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(repoDtoNoFork, repoDtoNoFork))));

        mockServer.verify();
    }

    @Test
    public void testUserHasOnlyForks() throws Exception {
        mockServer.expect(requestTo(MessageFormat.format("{0}users/{1}/repos", repoApi, userOnlyFork)))
                .andRespond(withSuccess(MessageFormat.format("[{0},{1},{2}]", repoFork, repoFork, repoFork), MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/" + userOnlyFork))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));

        mockServer.verify();
    }

    @Test
    public void testHasNoRepos() throws Exception {
        mockServer.expect(requestTo(MessageFormat.format("{0}users/{1}/repos", repoApi, userNoRepo)))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/" + userNoRepo))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));

        mockServer.verify();
    }

    @Test
    public void testHasReposButNoBranches() throws Exception {
        mockServer.expect(requestTo(MessageFormat.format("{0}users/{1}/repos", repoApi, userNoBranches)))
                .andRespond(withSuccess(MessageFormat.format("[{0}]", repoNoForkNoBranches), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(MessageFormat.format("{0}repos/{1}/{2}/branches", repoApi, userNoBranches, "repo3")))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/" + userNoBranches))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(repoDtoNoForkNoBranches))));

        mockServer.verify();
    }

    @Test
    public void testUserNotFound() throws Exception {
        mockServer.expect(requestTo(MessageFormat.format("{0}users/{1}/repos", repoApi, userNonExist)))
                .andRespond(withResourceNotFound());

        mockMvc.perform(get("/" + userNonExist))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(err1)));

        mockServer.verify();
    }

    @Test
    public void testRepoDeleted() throws Exception {
        mockServer.expect(requestTo(MessageFormat.format("{0}users/{1}/repos", repoApi, userDeletedRepo)))
                .andRespond(withSuccess(MessageFormat.format("[{0}]", repoDeleted), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(MessageFormat.format("{0}repos/{1}/{2}/branches", repoApi, userDeletedRepo, "repoDel")))
                .andRespond(withResourceNotFound());

        mockMvc.perform(get("/" + userDeletedRepo))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(err2)));

        mockServer.verify();
    }
}
