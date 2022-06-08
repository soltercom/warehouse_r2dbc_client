package ru.spb.altercom.warehouse_r2dbc_client.common;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.spb.altercom.warehouse_r2dbc_client.item.ItemForm;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {

    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setup() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
    }

    @Test
    @DisplayName("should not validate ItemForm with empty name")
    void itemEmptyNameTest() {
        var itemFormEmptyName = new ItemForm(1L, "");

        var resultEmptyName = validator.validate(itemFormEmptyName);

        assertThat(resultEmptyName).hasSize(1);
        var violation = resultEmptyName.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("name");
    }

    @Test
    @DisplayName("should validate ItemForm with correct name")
    void itemCorrectNameTest() {
        var itemNameCorrectName = new ItemForm(null, "Name");

        var resultCorrectName = validator.validate(itemNameCorrectName);

        assertThat(resultCorrectName).isEmpty();
    }
}
