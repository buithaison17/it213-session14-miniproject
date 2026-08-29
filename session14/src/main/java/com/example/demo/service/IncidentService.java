package com.example.demo.service;

import com.example.demo.dto.CreateIncidentDto;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.Incident;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncidentService {
    private final DeliveryRepository deliveryRepository;
    private final IncidentRepository incidentRepository;

    @Tool(description = "Tạo phiếu sự cố")
    public Incident createIncident(CreateIncidentDto createIncidentDto) {
        Delivery delivery = deliveryRepository.findByTrackingCode(createIncidentDto.trackingCode())
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        Incident incident = Incident.builder()
                .delivery(delivery)
                .incidentType(createIncidentDto.incidentType())
                .hubCode(createIncidentDto.hubCode())
                .severity(createIncidentDto.severity())
                .build();
        return incidentRepository.save(incident);
    }
}
