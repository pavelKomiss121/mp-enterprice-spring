/* @MENTEE_POWER (C)2024 */
package ru.mentee.ecommerce.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.ecommerce.dto.OrderDetailDto;
import ru.mentee.ecommerce.dto.OrderDto;
import ru.mentee.ecommerce.dto.OrderItemDto;
import ru.mentee.ecommerce.entity.Order;
import ru.mentee.ecommerce.projection.OrderProjection;
import ru.mentee.ecommerce.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    /**
     * Метод с N+1 проблемой:
     * 1 запрос для получения заказов
     * N запросов для получения customer для каждого заказа
     * N запросов для получения orderItems для каждого заказа
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getRecentOrders() {
        LocalDate date = LocalDate.now().minusDays(30);
        List<Order> orders = orderRepository.findByOrderDateAfter(date);
        return orders.stream()
                .map(
                        order -> {
                            OrderDto dto = new OrderDto();
                            dto.setId(order.getId());
                            dto.setCustomerName(
                                    order.getCustomer()
                                            .getName()); // N+1: обращение к lazy customer
                            dto.setOrderDate(order.getOrderDate());
                            dto.setItemsCount(
                                    order.getOrderItems()
                                            .size()); // N+1: обращение к lazy orderItems
                            return dto;
                        })
                .collect(Collectors.toList());
    }

    /**
     * Оптимизированный метод с JOIN FETCH для customer
     * Устраняет N+1 для customer, но может остаться для orderItems
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getRecentOrdersOptimized() {
        LocalDate date = LocalDate.now().minusDays(30);
        List<Order> orders = orderRepository.findByOrderDateAfterWithCustomer(date);
        return orders.stream()
                .map(
                        order -> {
                            OrderDto dto = new OrderDto();
                            dto.setId(order.getId());
                            dto.setCustomerName(
                                    order.getCustomer().getName()); // Загружено через JOIN FETCH
                            dto.setOrderDate(order.getOrderDate());
                            dto.setItemsCount(
                                    order.getOrderItems()
                                            .size()); // Может быть N+1 если не загружено
                            return dto;
                        })
                .collect(Collectors.toList());
    }

    /**
     * Полностью оптимизированный метод с JOIN FETCH для customer и orderItems
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getRecentOrdersFullyOptimized() {
        LocalDate date = LocalDate.now().minusDays(30);
        List<Order> orders = orderRepository.findByOrderDateAfterWithCustomerAndItems(date);
        return orders.stream()
                .map(
                        order -> {
                            OrderDto dto = new OrderDto();
                            dto.setId(order.getId());
                            dto.setCustomerName(
                                    order.getCustomer().getName()); // Загружено через JOIN FETCH
                            dto.setOrderDate(order.getOrderDate());
                            dto.setItemsCount(
                                    order.getOrderItems().size()); // Загружено через JOIN FETCH
                            return dto;
                        })
                .collect(Collectors.toList());
    }

    /**
     * Метод с N+1 проблемой для получения детальной информации о заказах
     */
    @Transactional(readOnly = true)
    public List<OrderDetailDto> getOrderDetails() {
        LocalDate date = LocalDate.now().minusDays(30);
        List<Order> orders = orderRepository.findByOrderDateAfter(date);
        return orders.stream()
                .map(
                        order -> {
                            OrderDetailDto dto = new OrderDetailDto();
                            dto.setId(order.getId());
                            dto.setCustomerName(order.getCustomer().getName()); // N+1
                            dto.setOrderDate(order.getOrderDate());
                            dto.setItems(
                                    order.getOrderItems().stream()
                                            .map(
                                                    item -> {
                                                        OrderItemDto itemDto = new OrderItemDto();
                                                        itemDto.setId(item.getId());
                                                        itemDto.setProductName(
                                                                item.getProduct()
                                                                        .getName()); // N+1 для
                                                        // product
                                                        itemDto.setQuantity(item.getQuantity());
                                                        return itemDto;
                                                    })
                                            .collect(Collectors.toList())); // N+1 для orderItems и
                            // product
                            return dto;
                        })
                .collect(Collectors.toList());
    }

    /**
     * Оптимизированный метод с @EntityGraph
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getRecentOrdersWithEntityGraph() {
        LocalDate date = LocalDate.now().minusDays(30);
        List<Order> orders = orderRepository.findByOrderDateAfterWithEntityGraph(date);
        return orders.stream()
                .map(
                        order -> {
                            OrderDto dto = new OrderDto();
                            dto.setId(order.getId());
                            dto.setCustomerName(
                                    order.getCustomer().getName()); // Загружено через EntityGraph
                            dto.setOrderDate(order.getOrderDate());
                            dto.setItemsCount(
                                    order.getOrderItems().size()); // Загружено через EntityGraph
                            return dto;
                        })
                .collect(Collectors.toList());
    }

    /**
     * Оптимизированный метод с проекцией
     * Возвращает только необходимые данные без загрузки полных сущностей
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getRecentOrdersWithProjection() {
        LocalDate date = LocalDate.now().minusDays(30);
        List<OrderProjection> projections = orderRepository.findOrderProjectionsByDateAfter(date);
        return projections.stream()
                .map(
                        proj -> {
                            OrderDto dto = new OrderDto();
                            dto.setId(proj.getId());
                            dto.setCustomerName(proj.getCustomerName());
                            dto.setOrderDate(proj.getOrderDate());
                            dto.setItemsCount(proj.getItemsCount());
                            return dto;
                        })
                .collect(Collectors.toList());
    }

    /**
     * Полностью оптимизированный метод с @EntityGraph для всех связанных сущностей
     */
    @Transactional(readOnly = true)
    public List<OrderDetailDto> getOrderDetailsOptimized() {
        LocalDate date = LocalDate.now().minusDays(30);
        List<Order> orders = orderRepository.findByOrderDateAfterWithFullEntityGraph(date);
        return orders.stream()
                .map(
                        order -> {
                            OrderDetailDto dto = new OrderDetailDto();
                            dto.setId(order.getId());
                            dto.setCustomerName(
                                    order.getCustomer().getName()); // Загружено через EntityGraph
                            dto.setOrderDate(order.getOrderDate());
                            dto.setItems(
                                    order.getOrderItems().stream()
                                            .map(
                                                    item -> {
                                                        OrderItemDto itemDto = new OrderItemDto();
                                                        itemDto.setId(item.getId());
                                                        itemDto.setProductName(
                                                                item.getProduct()
                                                                        .getName()); // Загружено
                                                        // через
                                                        // EntityGraph
                                                        itemDto.setQuantity(item.getQuantity());
                                                        return itemDto;
                                                    })
                                            .collect(Collectors.toList())); // Все загружено через
                            // EntityGraph
                            return dto;
                        })
                .collect(Collectors.toList());
    }
}
