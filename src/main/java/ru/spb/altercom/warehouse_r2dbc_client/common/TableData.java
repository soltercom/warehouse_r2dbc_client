package ru.spb.altercom.warehouse_r2dbc_client.common;

import java.util.List;
import java.util.Objects;

public record TableData(int draw, long recordsTotal, long recordsFiltered, List<?> data) {
}
