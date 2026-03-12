package ua.javaclub.lviv.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

class UsersApiClientProgrammaticTest {

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().dynamicHttpsPort())
            .configureStaticDsl(true)
            .failOnUnmatchedRequests(true)
            .build();

    private UsersApiClient client;

    @BeforeEach
    void setUp() {
        client = new UsersApiClient(wm.getRuntimeInfo().getHttpBaseUrl());
    }

    @Test
    void instance_dsl_and_static_dsl_both_work() {
        // Static DSL (because configureStaticDsl(true))
        stubFor(get(urlEqualTo("/static"))
                .willReturn(ok("static-ok")));

        // Instance DSL
        wm.stubFor(get(urlEqualTo("/instance"))
                .willReturn(ok("instance-ok")));

        Assertions.assertEquals("static-ok", client.getUserJsonPathRaw("/static"));
        Assertions.assertEquals("instance-ok", client.getUserJsonPathRaw("/instance"));

        verify(getRequestedFor(urlEqualTo("/static")));
        wm.verify(getRequestedFor(urlEqualTo("/instance")));
    }

}
