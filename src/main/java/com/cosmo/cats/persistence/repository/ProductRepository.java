package com.cosmo.cats.persistence.repository;

import com.cosmo.cats.persistence.entity.ProductEntity;
import com.cosmo.cats.persistence.projection.ProductSalesReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByCategoryId(Long categoryId);

    @Query("SELECT p.name as productName, " +
           "c.name as categoryName, " +
           "COUNT(oi.id) as totalQuantitySold, " +
           "SUM(oi.pricePerUnit * oi.quantity) as totalRevenue, " +
           "AVG(CASE WHEN o.status = 'DELIVERED' THEN 5.0 ELSE 4.0 END) as averageRating " +
           "FROM ProductEntity p " +
           "JOIN p.category c " +
           "JOIN OrderItemEntity oi ON oi.product = p " +
           "JOIN oi.order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.name, c.name " +
           "ORDER BY COUNT(oi.id) DESC")
    Page<ProductSalesReport> findTopSellingProducts(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT p.name as productName, " +
           "c.name as categoryName, " +
           "COUNT(oi.id) as totalQuantitySold, " +
           "SUM(oi.pricePerUnit * oi.quantity) as totalRevenue, " +
           "AVG(CASE WHEN o.status = 'DELIVERED' THEN 5.0 ELSE 4.0 END) as averageRating " +
           "FROM ProductEntity p " +
           "JOIN p.category c " +
           "JOIN OrderItemEntity oi ON oi.product = p " +
           "JOIN oi.order o " +
           "WHERE c.id = :categoryId " +
           "AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.name, c.name " +
           "HAVING COUNT(oi.id) >= :minOrders " +
           "ORDER BY SUM(oi.pricePerUnit * oi.quantity) DESC")
    List<ProductSalesReport> findTopSellingProductsByCategory(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minOrders") Long minOrders);
}
