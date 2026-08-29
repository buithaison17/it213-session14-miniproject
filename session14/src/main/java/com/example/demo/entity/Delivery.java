package com.example.demo.entity;

import com.example.demo.constant.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tracking_code", unique = true, nullable = false)
    private String trackingCode;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String hubCode;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal codAmount = BigDecimal.ZERO;
    @CreationTimestamp
    private LocalDateTime createAt;
}
