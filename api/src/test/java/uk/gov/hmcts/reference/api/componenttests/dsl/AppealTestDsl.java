package uk.gov.hmcts.reference.api.componenttests.dsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.auth.checker.UserRequestAuthorizer;
import uk.gov.hmcts.reference.api.appeals.Appeal.AppealType;
import uk.gov.hmcts.reference.api.appeals.AppealDto;
import uk.gov.hmcts.reference.api.componenttests.backdoors.UserResolverBackdoor;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppealTestDsl {
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final MockMvc mvc;
    private final UserResolverBackdoor userRequestAuthorizer;
    private final ObjectMapper objectMapper;
    private ResultActions resultActions;

    public AppealTestDsl(MockMvc mvc, UserResolverBackdoor userRequestAuthorizer, ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.userRequestAuthorizer = userRequestAuthorizer;
        this.objectMapper = objectMapper;
    }

    public AppealGivenDsl given() {
        return new AppealGivenDsl();
    }

    public class AppealGivenDsl {
        public AppealGivenDsl userId(String userId) {
            String token = "Bearer user-" + userId;
            userRequestAuthorizer.registerToken(token, userId);
            httpHeaders.add(UserRequestAuthorizer.AUTHORISATION, token);
            return this;
        }

        public AppealWhenDsl when() {
            return new AppealWhenDsl();
        }
    }

    public class AppealWhenDsl {

        @SneakyThrows
        public AppealWhenDsl createAppeal(String userId, AppealType appealType, String description, AtomicReference<AppealDto> createdHolder) {
            createAppeal(userId, appealType, description);
            createdHolder.set(bodyAs(AppealDto.class));
            return this;
        }

        @SneakyThrows
        public AppealWhenDsl createAppeal(String userId, AppealType appealType, String description) {
            resultActions = mvc.perform(MockMvcRequestBuilders
                    .post("/users/{userId}/appeals?type={type}&description={description}", userId, appealType, description)
                    .headers(httpHeaders)
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON));
            return this;
        }

        public AppealWhenDsl retrieveAppeal(String userId, Integer appealId) {
            return retrieveAppeal(userId, appealId.toString());
        }

        @SneakyThrows
        public AppealWhenDsl retrieveAppeal(String userId, String appealId) {
            resultActions = mvc.perform(MockMvcRequestBuilders.get("/users/{userId}/appeals/{appealId}", userId, appealId)
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .headers(httpHeaders));
            return this;
        }

        public AppealWhenDsl and() {
            return this;
        }

        public AppealThenDsl then() {
            return new AppealThenDsl();
        }
    }

    public class AppealThenDsl {
        @SneakyThrows
        public AppealThenDsl forbidden() {
            resultActions.andExpect(status().isForbidden());
            return this;
        }

        @SneakyThrows
        public AppealThenDsl created(Consumer<AppealDto> consumer) {
            resultActions.andExpect(status().isOk());
            consumer.accept(bodyAs(AppealDto.class));
            return this;
        }

        @SneakyThrows
        public AppealThenDsl retrieved(Consumer<AppealDto> consumer) {
            resultActions.andExpect(status().isOk());
            consumer.accept(bodyAs(AppealDto.class));
            return this;
        }

    }

    private AppealDto bodyAs(Class<AppealDto> valueType) throws java.io.IOException {
        return objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsByteArray(), valueType);
    }

}
