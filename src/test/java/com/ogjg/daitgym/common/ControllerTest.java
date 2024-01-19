package com.ogjg.daitgym.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogjg.daitgym.alarm.service.FCMInitializer;
import com.ogjg.daitgym.exercise.service.ExerciseHelper;
import com.ogjg.daitgym.exercise.service.ExerciseService;
import com.ogjg.daitgym.feed.service.FeedExerciseJournalService;
import com.ogjg.daitgym.feed.service.FeedJournalHelper;
import com.ogjg.daitgym.feed.service.UserFeedExerciseJournalService;
import com.ogjg.daitgym.follow.service.FollowService;
import com.ogjg.daitgym.journal.service.ExerciseJournalHelper;
import com.ogjg.daitgym.journal.service.ExerciseJournalService;
import com.ogjg.daitgym.user.controller.LoginController;
import com.ogjg.daitgym.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {

    @RegisterExtension
    final RestDocumentationExtension restDocumentation = new RestDocumentationExtension();

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected ExerciseJournalHelper exerciseJournalHelper;

    @MockBean
    protected ExerciseJournalService exerciseJournalService;

    @MockBean
    protected FollowService followService;

    @MockBean
    protected FeedExerciseJournalService feedExerciseJournalService;

    @MockBean
    protected FeedJournalHelper feedJournalHelper;

    @MockBean
    protected UserFeedExerciseJournalService userFeedExerciseJournalService;

    @MockBean
    protected ExerciseHelper exerciseHelper;

    @MockBean
    protected ExerciseService exerciseService;

    @MockBean
    protected FCMInitializer fcmInitializer;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected LoginController loginController;
}
