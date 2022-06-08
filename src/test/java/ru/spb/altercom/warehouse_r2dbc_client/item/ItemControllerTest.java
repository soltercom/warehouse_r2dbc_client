package ru.spb.altercom.warehouse_r2dbc_client.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BindingResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.spb.altercom.warehouse_r2dbc_client.common.TableData;

import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemControllerTest {

    private static ItemController itemController;
    private static BindingResult invalidBindingResult;
    private static BindingResult validBindingResult;

    private static List<ItemForm> itemFormList;
    private static TableData tableData;
    private static ItemForm emptyForm;

    @BeforeAll
    static void setup() {
        itemFormList = LongStream.range(1, 11)
                .mapToObj(num -> new ItemForm(num, "Item " + num))
                .toList();
        tableData = new TableData(1, itemFormList.size(), itemFormList.size(), itemFormList);
        emptyForm = new ItemForm(null, "");

        var itemService = Mockito.mock(ItemService.class);
        when(itemService.getTableData(anyInt(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(Mono.just(tableData));
        when(itemService.getFormById(anyLong()))
            .thenReturn(Mono.just(itemFormList.get(0)));
        when(itemService.getNewForm())
            .thenReturn(Mono.just(emptyForm));
        when(itemService.save(any()))
                .thenReturn(Mono.just(itemFormList.get(0)));
        when(itemService.update(any()))
                .thenReturn(Mono.just(itemFormList.get(0)));

        invalidBindingResult = Mockito.mock(BindingResult.class);
        when(invalidBindingResult.hasErrors()).thenReturn(true);

        validBindingResult = Mockito.mock(BindingResult.class);
        when(validBindingResult.hasErrors()).thenReturn(false);

        itemController = new ItemController(itemService);
    }

    @Test
    @DisplayName("should return View(list)")
    void doListTest() {
        StepVerifier.create(itemController.doList())
            .expectNextMatches(r -> Objects.requireNonNull(r.view()).toString().contains("list"))
            .verifyComplete();
    }

    @Test
    @DisplayName("should return View(form) with empty Item")
    void doNewForm() {
        StepVerifier.create(itemController.doNewForm())
            .expectNextMatches(r -> {
                var model = r.modelAttributes();
                var itemForm = (ItemForm) model.get("itemForm");
                var result = itemForm != null
                        && itemForm.getId() == null
                        && itemForm.getName().isEmpty();
                result = result && "Item (new)".equals(model.get("formTitle"));
                return result;
            }).verifyComplete();
    }

    @Test
    @DisplayName("should return View(form) with Item")
    void doFormTest() {
        StepVerifier.create(itemController.doForm(1L))
            .expectNextMatches(r -> {
                var model = r.modelAttributes();
                var itemForm = (ItemForm) model.get("itemForm");
                var result = itemForm != null
                        && itemForm.getId().equals(1L)
                        && itemForm.getName().equals("Item 1");
                result = result && "Item (1)".equals(model.get("formTitle"));
                return result;
            }).verifyComplete();
    }

    @Test
    @DisplayName("should return back to View(form)")
    void processInvalidNewForm() {
        StepVerifier.create(itemController.processNewForm(emptyForm, invalidBindingResult))
            .expectNextMatches(r -> Objects.requireNonNull(r.view()).toString().contains("form"))
            .verifyComplete();
    }

    @Test
    @DisplayName("should redirect to View(list)")
    void processValidNewForm() {
        StepVerifier.create(itemController.processNewForm(emptyForm, validBindingResult))
                .expectNextMatches(r -> Objects.requireNonNull(r.view()).toString().contains("items"))
                .verifyComplete();
    }

    @Test
    @DisplayName("should return back to View(form)")
    void processInvalidForm() {
        StepVerifier.create(itemController.processForm(itemFormList.get(0), invalidBindingResult))
                .expectNextMatches(r -> Objects.requireNonNull(r.view()).toString().contains("form"))
                .verifyComplete();
    }

    @Test
    @DisplayName("should redirect to View(list)")
    void processValidForm() {
        StepVerifier.create(itemController.processForm(itemFormList.get(0), validBindingResult))
                .expectNextMatches(r -> Objects.requireNonNull(r.view()).toString().contains("items"))
                .verifyComplete();
    }

    @Test
    @DisplayName("should return Mono<TableData>")
    void getTableDataTest() {
        StepVerifier.create(itemController.getTableData(1, 10, 10, "", ""))
                .expectNextMatches(tbl -> tbl.data().size() == 10)
                .expectComplete()
                .verify();
    }

}
