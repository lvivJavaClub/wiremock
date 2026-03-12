package ua.javaclub.lviv.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;


@WireMockTest // random HTTP port by default
class UsersApiClientDeclarativeTest {

    @Test
    void returns_user_json(WireMockRuntimeInfo wmRuntimeInfo) {
        // Static DSL is auto-configured in declarative mode
        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"name\":\"John\"}")));

        UsersApiClient client = new UsersApiClient(wmRuntimeInfo.getHttpBaseUrl());
        String body = client.getUserJson(1);

        assertThat(body, containsString("\"name\":\"John\""));
        verify(getRequestedFor(urlEqualTo("/users/1")));
    }

    @Test
    void returns_500_and_client_throws(WireMockRuntimeInfo wmRuntimeInfo) {
        stubFor(get(urlEqualTo("/users/2"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("boom")));

        UsersApiClient client = new UsersApiClient(wmRuntimeInfo.getHttpBaseUrl());

        try {
            client.getUserJson(2);
            throw new AssertionError("Expected exception, but none thrown");
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage(), containsString("Non-2xx"));
        }

        verify(1, getRequestedFor(urlEqualTo("/users/2")));
    }
}
