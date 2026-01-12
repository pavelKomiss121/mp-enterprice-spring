/* @MENTEE_POWER (C)2024 */
package ru.mentee.ecommerce.projection;

import java.time.LocalDate;

/**
 * Проекция для API, возвращающая только необходимые данные
 * Используется для оптимизации запросов и уменьшения объема передаваемых данных
 */
public interface OrderProjection {
    Long getId();

    String getCustomerName();

    LocalDate getOrderDate();

    Integer getItemsCount();
}
