package com.user.mock;

import com.user.model.UserModel;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.util.Support.generateRandomPassword;

public class WiremockManager {

    public static void mockAPi() {
        UserModel userModel = new UserModel();

        userModel.setId(1L);
        userModel.setUserName("Andre Diegues");
        userModel.setInitialPassword(generateRandomPassword(6));
        userModel.setActiveUser(true);

        WireMockServer wireMockServer = new WireMockServer();

        wireMockServer.start();

        configureFor("localhost", 8080);
        stubFor(get(urlEqualTo("/users")).willReturn(aResponse().withBody(userModel.toJSON()).
                withHeader("Content-Type", "application/json")));
        stubFor(get(urlEqualTo("/users/1")).willReturn(aResponse()
                .withBody(userModel.toJSON()).withHeader("Content-Type", "application/json")));
        stubFor(post(urlEqualTo("/users")).withRequestBody(equalToJson(userModel.toJSON()))
                .willReturn(aResponse().withStatus(201)));

    }

}
