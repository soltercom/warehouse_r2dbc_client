package ru.spb.altercom.warehouse_r2dbc_client.item;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.spb.altercom.warehouse_r2dbc_client.common.TableData;

import javax.validation.Valid;
import java.util.Objects;

@Controller
@RequestMapping("/items")
@CrossOrigin(origins="*")
public class ItemController {

    private static final Rendering VIEW_LIST = Rendering.view("/item/list").build();
    private static final Rendering REDIRECT_TO_VIEW_LIST = Rendering.redirectTo("/items").build();
    private static final String FORM = "/item/form";

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Mono<Rendering> doList() {
        return Mono.just(VIEW_LIST);
    }

    @GetMapping("/new")
    public Mono<Rendering> doNewForm() {
        return prepareModel(itemService.getNewForm());
    }

    @PostMapping("/new")
    public Mono<Rendering> processNewForm(@Valid @ModelAttribute("itemForm") ItemForm itemForm,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.just(prepareModel(itemForm));
        } else {
            return itemService.save(itemForm)
                    .map(f -> REDIRECT_TO_VIEW_LIST);
        }
    }

    @GetMapping("/{id}")
    public Mono<Rendering> doForm(@PathVariable("id") Long id) {
        return prepareModel(itemService.getFormById(id));
    }

    @PostMapping("/{id}")
    public Mono<Rendering> processForm(@Valid @ModelAttribute("itemForm") ItemForm itemForm,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.just(prepareModel(itemForm));
        } else {
            return itemService.update(itemForm)
                    .map(f -> REDIRECT_TO_VIEW_LIST);
        }
    }

    @GetMapping("/table")
    @ResponseBody
    public Mono<TableData> getTableData(@RequestParam int draw,
                                              @RequestParam int start,
                                              @RequestParam int length,
                                              @RequestParam(name = "search[value]", defaultValue = "") String search,
                                              @RequestParam(name = "order[0][dir]") String dir) {
        return itemService.getTableData(draw, start, length, search, dir);
    }

    private Mono<Rendering> prepareModel(Mono<ItemForm> itemForm) {
        return itemForm.map(this::prepareModel);
    }

    private Rendering prepareModel(ItemForm  itemForm) {
        return Rendering.view(FORM)
                .modelAttribute("itemForm", itemForm)
                .modelAttribute("formTitle", getFormTitle(itemForm))
                .build();
    }

    private String getFormTitle(ItemForm itemForm) {
        Objects.requireNonNull(itemForm);
        return itemForm.isNew() ? "Item (new)" : "Item (" + itemForm.getId() + ")";
    }

}
