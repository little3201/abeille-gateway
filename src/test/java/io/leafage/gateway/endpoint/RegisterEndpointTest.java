package io.leafage.gateway.endpoint;

import io.leafage.gateway.api.HypervisorApi;
import io.leafage.gateway.bo.UserBO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = RegisterEndpoint.class)
class RegisterEndpointTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private HypervisorApi hypervisorApi;

    @WithMockUser
    @Test
    void register() {
        UserBO userBO = new UserBO();
        userBO.setUsername("test");
        Mockito.when(hypervisorApi.createUser("test", "li@163.com", "test"))
                .thenReturn(Mono.just(userBO));
        webTestClient.mutateWith(csrf()).post().uri("/register").exchange().expectStatus().isOk();
    }
}