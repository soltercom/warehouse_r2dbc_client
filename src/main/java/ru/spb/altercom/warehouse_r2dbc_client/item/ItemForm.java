package ru.spb.altercom.warehouse_r2dbc_client.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemForm {

    private Long id;

    @NotEmpty
    private String name;

    public boolean isNew() {
        return id == null;
    }

}
