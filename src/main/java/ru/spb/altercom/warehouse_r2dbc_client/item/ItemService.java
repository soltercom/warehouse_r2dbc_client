package ru.spb.altercom.warehouse_r2dbc_client.item;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.spb.altercom.warehouse_r2dbc_client.common.TableData;

@Service
public class ItemService {

    private final WebClient webClient;

    public ItemService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<TableData> getTableData(int draw, int start, int length, String search, String dir) {
        var uri = String.format("/items/table?draw=%d&start=%d&length=%d&search[value]=%s&order[0][dir]=%s",
                draw, start, length, search, dir);
        return webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(TableData.class);
    }

    public Mono<ItemForm> getNewForm() {
        return webClient.get()
                .uri("/items/new")
                .retrieve()
                .bodyToMono(ItemForm.class);
    }

    public Mono<ItemForm> getFormById(Long id) {
        return webClient.get()
                .uri("/items/" + id)
                .retrieve()
                .bodyToMono(ItemForm.class);
    }

    public Mono<ItemForm> save(ItemForm itemForm) {
        return webClient.post()
                .uri("/items/new")
                .bodyValue(itemForm)
                .retrieve()
                .bodyToMono(ItemForm.class);
    }

    public Mono<ItemForm> update(ItemForm itemForm) {
        return webClient.post()
                .uri("/items/" + itemForm.getId())
                .bodyValue(itemForm)
                .retrieve()
                .bodyToMono(ItemForm.class);
    }

}
