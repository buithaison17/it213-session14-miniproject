package com.example.demo.service;

import com.example.demo.constant.DeliveryStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    @Tool(description = "Cập nhật trạng thái giao hàng")
    public Delivery updateDeliveryStatus(
            @ToolParam(description = "Mã theo dõi giao hàng")
            String trackingCode,
            @ToolParam(description = "Trạng thái giao hàng")
            DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setStatus(status);
        return deliveryRepository.save(delivery);
    }
}
