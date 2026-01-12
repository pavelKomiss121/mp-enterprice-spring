/* @MENTEE_POWER (C)2024 */
package ru.mentee.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.ecommerce.entity.Customer;
import ru.mentee.ecommerce.entity.Order;
import ru.mentee.ecommerce.entity.OrderItem;
import ru.mentee.ecommerce.entity.Product;
import ru.mentee.ecommerce.repository.CustomerRepository;
import ru.mentee.ecommerce.repository.OrderRepository;
import ru.mentee.ecommerce.repository.ProductRepository;
import ru.mentee.ecommerce.service.OrderService;

@SpringBootTest(classes = ru.mentee.ecommerce.EcommerceApplication.class)
@Testcontainers
@TestPropertySource(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create",
            "spring.jpa.show-sql=true",
            "spring.jpa.properties.hibernate.generate_statistics=true",
            "spring.jpa.properties.hibernate.hbm2ddl.auto=create"
        })
class OptimizationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private OrderService orderService;

    @Autowired private OrderRepository orderRepository;

    @Autowired private CustomerRepository customerRepository;

    @Autowired private ProductRepository productRepository;

    @Autowired private SessionFactory sessionFactory;

    @Autowired private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();

        // Создание тестовых данных
        Customer customer1 = new Customer();
        customer1.setName("Customer 1");
        customer1 = customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setName("Customer 2");
        customer2 = customerRepository.save(customer2);

        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("100.00"));
        product1 = productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("200.00"));
        product2 = productRepository.save(product2);

        // Создание заказов
        Order order1 = new Order();
        order1.setCustomer(customer1);
        order1.setOrderDate(LocalDate.now().minusDays(10));
        order1 = orderRepository.save(order1);

        OrderItem item1 = new OrderItem();
        item1.setOrder(order1);
        item1.setProduct(product1);
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setOrder(order1);
        item2.setProduct(product2);
        item2.setQuantity(1);

        order1.getOrderItems().add(item1);
        order1.getOrderItems().add(item2);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setCustomer(customer2);
        order2.setOrderDate(LocalDate.now().minusDays(5));
        order2 = orderRepository.save(order2);

        OrderItem item3 = new OrderItem();
        item3.setOrder(order2);
        item3.setProduct(product1);
        item3.setQuantity(3);

        order2.getOrderItems().add(item3);
        orderRepository.save(order2);
    }

    @Test
    @DisplayName("Should не вызывать N+1 при получении заказов (оптимизированный метод)")
    @Transactional
    void shouldNotCauseNPlusOneWhenFetchingOrders() {
        // Given
        // Включаем статистику
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // When
        List<ru.mentee.ecommerce.dto.OrderDto> orders =
                orderService.getRecentOrdersFullyOptimized();

        // Then
        // Проверяем количество SQL запросов
        // Должен быть 1 запрос с JOIN FETCH
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("Should не вызывать N+1 при использовании @EntityGraph")
    @Transactional
    void shouldNotCauseNPlusOneWithEntityGraph() {
        // Given
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // When
        List<ru.mentee.ecommerce.dto.OrderDto> orders =
                orderService.getRecentOrdersWithEntityGraph();

        // Then
        // Должен быть 1 запрос с EntityGraph
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("Should не вызывать N+1 при использовании проекций")
    @Transactional
    void shouldNotCauseNPlusOneWithProjection() {
        // Given
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // When
        List<ru.mentee.ecommerce.dto.OrderDto> orders =
                orderService.getRecentOrdersWithProjection();

        // Then
        // Проекция должна выполнить 1 запрос
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("Should не вызывать N+1 при получении детальной информации о заказах")
    @Transactional
    void shouldNotCauseNPlusOneWhenFetchingOrderDetails() {
        // Given
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // When
        List<ru.mentee.ecommerce.dto.OrderDetailDto> orders =
                orderService.getOrderDetailsOptimized();

        // Then
        // Должен быть 1 запрос с полным EntityGraph
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("Should демонстрировать N+1 проблему в неоптимизированном методе")
    @Transactional
    void shouldDemonstrateNPlusOneProblem() {
        // Given
        // Создаем больше данных, чтобы @BatchSize не объединил все запросы
        // Создаем 15 заказов (больше чем batch size 10)
        for (int i = 3; i <= 15; i++) {
            Customer customer = new Customer();
            customer.setName("Customer " + i);
            customer = customerRepository.save(customer);

            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderDate(LocalDate.now().minusDays(i));
            order = orderRepository.save(order);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(productRepository.findAll().get(0));
            item.setQuantity(1);
            order.getOrderItems().add(item);
            orderRepository.save(order);
        }

        // Очищаем persistence context, чтобы данные не были загружены заранее
        entityManager.flush();
        entityManager.clear();

        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // When
        List<ru.mentee.ecommerce.dto.OrderDto> orders = orderService.getRecentOrders();

        // Then
        // Неоптимизированный метод должен вызвать N+1 запросов
        // Для 15+ заказов с @BatchSize(size=10):
        // - 1 запрос для заказов
        // - минимум 2 batch запроса для customer (15 заказов / 10 = 2 батча)
        // - минимум 2 batch запроса для orderItems
        // Итого минимум 5 запросов
        long queryCount = statistics.getPrepareStatementCount();

        assertThat(queryCount)
                .as(
                        "Неоптимизированный метод должен выполнить больше 1 SQL запроса. "
                                + "Для 15+ заказов с @BatchSize должно быть минимум 5 запросов. "
                                + "Фактически выполнено: %d",
                        queryCount)
                .isGreaterThan(1L);
        assertThat(orders.size()).isGreaterThanOrEqualTo(15);
    }
}
