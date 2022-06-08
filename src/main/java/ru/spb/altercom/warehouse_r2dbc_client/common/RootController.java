package ru.spb.altercom.warehouse_r2dbc_client.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping
public class RootController {

    private static final Rendering REDIRECT_TO_VIEW_LIST = Rendering.redirectTo("/items").build();

    @GetMapping
    public Mono<Rendering> index() {
        return Mono.just(REDIRECT_TO_VIEW_LIST);
    }

}
