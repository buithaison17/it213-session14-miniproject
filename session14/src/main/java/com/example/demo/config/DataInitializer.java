package com.example.demo.config;

import com.example.demo.constant.DeliveryStatus;
import com.example.demo.entity.Delivery;
import com.example.demo.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DeliveryRepository deliveryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (deliveryRepository.count() == 0) {
            List<Delivery> sampleDeliveries = List.of(
                    // 1. Đơn test kịch bản HỎNG_HÓC tại kho Hà Nội[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-001")
                            .customerName("Nguyễn Văn An")
                            .hubCode("HN-01")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(new BigDecimal("1500000.00"))
                            .build(),

                    // 2. Đơn test kịch bản GIAO_TRỄ tại kho Hồ Chí Minh[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-002")
                            .customerName("Trần Thị Mai")
                            .hubCode("SG-02")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(new BigDecimal("350000.00"))
                            .build(),

                    // 3. Đơn test kịch bản THẤT_LẠC tại kho Đà Nẵng[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-003")
                            .customerName("Lê Hoàng Long")
                            .hubCode("DN-03")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(BigDecimal.ZERO)
                            .build(),

                    // 4. Đơn đã giao thành công tại Hub HN-01[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-004")
                            .customerName("Phạm Minh Đức")
                            .hubCode("HN-01")
                            .status(DeliveryStatus.DELIVERED)
                            .codAmount(new BigDecimal("480000.00"))
                            .build(),

                    // 5. Đơn COD giá trị cao test kiểm soát đối soát tại SG-02[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-005")
                            .customerName("Hoàng Thu Trang")
                            .hubCode("SG-02")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(new BigDecimal("12500000.00"))
                            .build(),

                    // 6. Đơn test phát lại nhiều lần tại DN-03[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-006")
                            .customerName("Võ Quốc Hưng")
                            .hubCode("DN-03")
                            .status(DeliveryStatus.DELAYED)
                            .codAmount(new BigDecimal("720000.00"))
                            .build(),

                    // 7. Đơn hỏa tốc nội thành gặp sự cố móp méo tại HN-01[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-007")
                            .customerName("Đỗ Bảo Ngọc")
                            .hubCode("HN-01")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(new BigDecimal("2100000.00"))
                            .build(),

                    // 8. Đơn đã ghi nhận trạng thái hỏng hóc từ trước tại SG-02[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-008")
                            .customerName("Ngô Tuấn Kiệt")
                            .hubCode("SG-02")
                            .status(DeliveryStatus.DAMAGED)
                            .codAmount(BigDecimal.ZERO)
                            .build(),

                    // 9. Đơn không COD đang trung chuyển liên tỉnh qua DN-03[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-009")
                            .customerName("Bùi Phương Thảo")
                            .hubCode("DN-03")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(BigDecimal.ZERO)
                            .build(),

                    // 10. Đơn giao thành công hoàn tất thu hộ tại HN-01[cite: 1, 2]
                    Delivery.builder()
                            .trackingCode("RK-2026-010")
                            .customerName("Dương Hải Đăng")
                            .hubCode("HN-01")
                            .status(DeliveryStatus.DELIVERED)
                            .codAmount(new BigDecimal("890000.00"))
                            .build()
            );

            deliveryRepository.saveAll(sampleDeliveries);
            log.info("Initialized {} sample delivery records successfully.", sampleDeliveries.size());
        }
    }
}