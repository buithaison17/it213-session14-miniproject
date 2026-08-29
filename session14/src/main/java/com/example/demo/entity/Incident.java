package com.example.demo.entity;

import com.example.demo.constant.IncidentType;
import com.example.demo.constant.Severity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incidents")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "tracking_code", referencedColumnName = "tracking_code", nullable = false)
    private Delivery delivery;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IncidentType incidentType;
    @Column(nullable = false)
    private String hubCode;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Severity severity;
}
