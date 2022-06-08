package ru.spb.altercom.warehouse_r2dbc_client.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Objects;

class RootControllerTest {

    private final RootController rootController = new RootController();

    @Test
    @DisplayName("should redirect to items")
    void indexTest() {
        StepVerifier.create(rootController.index())
            .expectNextMatches(r -> Objects.requireNonNull(r.view()).toString().contains("items"))
            .verifyComplete();
    }

}
