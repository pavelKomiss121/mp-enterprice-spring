/* @MENTEE_POWER (C)2024 */
package ru.mentee.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.ecommerce.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {}
