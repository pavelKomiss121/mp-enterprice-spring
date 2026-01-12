/* @MENTEE_POWER (C)2024 */
package ru.mentee.ecommerce.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mentee.ecommerce.entity.Order;
import ru.mentee.ecommerce.projection.OrderProjection;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Метод с потенциальной N+1 проблемой (для демонстрации)
    List<Order> findByOrderDateAfter(LocalDate date);

    // Оптимизированный метод с JOIN FETCH
    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.orderDate > :date")
    List<Order> findByOrderDateAfterWithCustomer(LocalDate date);

    // Оптимизированный метод с JOIN FETCH для customer и orderItems
    @Query(
            "SELECT o FROM Order o JOIN FETCH o.customer JOIN FETCH o.orderItems WHERE o.orderDate"
                    + " > :date")
    List<Order> findByOrderDateAfterWithCustomerAndItems(LocalDate date);

    // Оптимизированный метод с @EntityGraph
    @EntityGraph(attributePaths = {"customer", "orderItems"})
    @Query("SELECT o FROM Order o WHERE o.orderDate > :date")
    List<Order> findByOrderDateAfterWithEntityGraph(LocalDate date);

    // Метод с @EntityGraph для загрузки customer, orderItems и product в orderItems
    @EntityGraph(attributePaths = {"customer", "orderItems", "orderItems.product"})
    @Query("SELECT o FROM Order o WHERE o.orderDate > :date")
    List<Order> findByOrderDateAfterWithFullEntityGraph(LocalDate date);

    // Метод с проекцией для оптимизации
    @Query(
            "SELECT o.id AS id, c.name AS customerName, o.orderDate AS orderDate, "
                    + "COUNT(oi.id) AS itemsCount "
                    + "FROM Order o "
                    + "JOIN o.customer c "
                    + "LEFT JOIN o.orderItems oi "
                    + "WHERE o.orderDate > :date "
                    + "GROUP BY o.id, c.name, o.orderDate")
    List<OrderProjection> findOrderProjectionsByDateAfter(LocalDate date);
}
