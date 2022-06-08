package ru.spb.altercom.warehouse_r2dbc_client.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.spb.altercom.warehouse_r2dbc_client.common.TableData;

import java.util.List;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

class ItemServiceTest {

    private static List<ItemForm> itemFormList;
    private static TableData tableData;
    private static ItemForm emptyForm;
    private static ItemForm newForm;
    private static ItemForm updatedForm;

    @BeforeAll
    private static void setUp() {
        itemFormList = LongStream.range(1, 11)
                .mapToObj(num -> new ItemForm(num, "Item " + num))
                .toList();
        tableData = new TableData(1, itemFormList.size(), itemFormList.size(), itemFormList);
        emptyForm = new ItemForm(null, "");
        newForm = new ItemForm(null, itemFormList.get(0).getName());
        updatedForm = new ItemForm(itemFormList.get(0).getId(), itemFormList.get(0).getName() + " (updated)");
    }

    @Test
    @DisplayName("should return Mono<TableData>")
    void getTableDataTest() {
        var webClient = getWebClient(tableData);
        var itemService = new ItemService(webClient);

        var result = itemService.getTableData(anyInt(), anyInt(), anyInt(), anyString(), anyString());

        StepVerifier.create(result)
                .expectNextMatches(tbl -> tbl.data().size() == 10)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("should return empty ItemForm")
    void getNewFormTest() {
        var webClient = getWebClient(emptyForm);
        var itemService = new ItemService(webClient);

        var result = itemService.getNewForm();

        StepVerifier.create(result)
                .expectNextMatches(itemForm -> itemForm.getId() == null && itemForm.getName().isEmpty())
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("should return ItemForm by it id")
    void getFormById() {
        var webClient = getWebClient(itemFormList.get(0));
        var itemService = new ItemService(webClient);

        var result = itemService.getFormById(itemFormList.get(0).getId());

        StepVerifier.create(result)
                .expectNextMatches(itemForm ->
                        itemForm.getId().equals(itemFormList.get(0).getId())
                        && itemForm.getName().equals(itemFormList.get(0).getName()))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("should save new ItemForm and return ItemForm with new id")
    void saveTest() {
        var webClient = getWebClient(itemFormList.get(0));
        var itemService = new ItemService(webClient);

        var result = itemService.save(newForm);

        StepVerifier.create(result)
                .expectNextMatches(itemForm ->
                        itemForm.getId().equals(itemFormList.get(0).getId())
                                && itemForm.getName().equals(newForm.getName()))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("should update new ItemForm and return updated ItemForm")
    void updateTest() {
        var webClient = getWebClient(updatedForm);
        var itemService = new ItemService(webClient);

        var result = itemService.update(updatedForm);

        StepVerifier.create(result)
                .expectNextMatches(itemForm ->
                        itemForm.getId().equals(updatedForm.getId())
                                && itemForm.getName().equals(updatedForm.getName()))
                .expectComplete()
                .verify();
    }

    private WebClient getWebClient(Object obj) {
        return WebClient.builder()
                .exchangeFunction(getExchangeFunction(obj)).build();
    }

    private ExchangeFunction getExchangeFunction(Object obj) {
        var ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        String temp = "";
        try {
            temp = ow.writeValueAsString(obj);
        } catch (JsonProcessingException ignored) {}
        var json = temp;


        return req -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header("content-type", "application/json")
                .body(json).build());
    }

}
