package com.ogjg.daitgym.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogjg.daitgym.alarm.service.FCMInitializer;
import com.ogjg.daitgym.config.security.details.OAuth2JwtUserDetails;
import com.ogjg.daitgym.config.security.jwt.dto.JwtUserClaimsDto;
import com.ogjg.daitgym.domain.Role;
import com.ogjg.daitgym.feed.service.FeedExerciseJournalService;
import com.ogjg.daitgym.feed.service.FeedJournalHelper;
import com.ogjg.daitgym.feed.service.UserFeedExerciseJournalService;
import com.ogjg.daitgym.journal.service.ExerciseJournalHelper;
import com.ogjg.daitgym.journal.service.ExerciseJournalService;
import com.ogjg.daitgym.user.controller.LoginController;
import com.ogjg.daitgym.user.service.AuthService;
import com.ogjg.daitgym.user.service.UserHelper;
import com.ogjg.daitgym.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ControllerTest {

    @RegisterExtension
    final RestDocumentationExtension restDocumentation = new RestDocumentationExtension();

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        // JwtUserDetails 객체 생성
        OAuth2JwtUserDetails userDetails = new OAuth2JwtUserDetails(
                JwtUserClaimsDto.builder()
                        .email("ogjg@mail.com")
                        .nickname("ogjg")
                        .role(Role.ADMIN)
                        .build()
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);
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
    protected FeedExerciseJournalService feedExerciseJournalService;

    @MockBean
    protected FeedJournalHelper feedJournalHelper;

    @MockBean
    protected UserFeedExerciseJournalService userFeedExerciseJournalService;

    @MockBean
    protected UserHelper userHelper;

    @MockBean
    protected UserService userService;

    @MockBean
    protected FCMInitializer fcmInitializer;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected LoginController loginController;
}
