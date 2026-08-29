package com.example.demo.controller;

import com.example.demo.dto.ChatConversation;
import com.example.demo.service.DeliveryService;
import com.example.demo.service.DocumentIngestionService;
import com.example.demo.service.IncidentService;
import com.example.demo.service.RagChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final DocumentIngestionService documentIngestionService;
    private final ChatClient chatClient;
    private final RagChatService ragChatService;
    private final DeliveryService deliveryService;
    private final IncidentService incidentService;

    @PostMapping("/chat/upload")
    public String upload(@RequestParam MultipartFile file) {
        documentIngestionService.ingest(file.getResource());
        return "Đã upload thành công file " + file.getOriginalFilename();
    }

    @PostMapping("/rag/ask")
    public String chat(@RequestBody ChatConversation chatConversation) {
        return ragChatService.chat(chatConversation.conversationId(), chatConversation.content());
    }

    @PostMapping("/operations/chat")
    public String chatWithOperations(@RequestBody ChatConversation chatConversation) {
        return chatClient
                .prompt()
                .system("""
                        Bạn là Trợ lý AI Điều phối Vận hành Tự động (Operations Dispatcher Agent) thuộc hệ thống SmartHub của RikkeiExpress. Nhiệm vụ chính của bạn là tiếp nhận phản ánh sự cố từ khách hàng hoặc điều phối viên bằng ngôn ngữ tự nhiên, bóc tách dữ liệu có cấu trúc và tự động gọi các công cụ (Tools) để ghi nhận sự cố, cập nhật trạng thái đơn hàng trong hệ thống.
                        
                        === NGUYÊN TẮC BÓC TÁCH THỰC THỂ (ENTITY EXTRACTION) ===
                        Khi tiếp nhận yêu cầu, bạn phải phân tích và trích xuất đầy đủ 4 trường thông tin bắt buộc sau:
                        1. trackingCode: Mã vận đơn theo định dạng "RK-YYYY-XXXXX" (Ví dụ: "RK-2026-001").
                        2. incidentType: Phải chuẩn hóa chính xác thành 1 trong 3 giá trị:
                           - "DAMAGE": Áp dụng khi hàng vỡ nát, móp méo, ướt sũng, rách nát.
                           - "LATE_DELIVERY": Áp dụng khi đơn hàng bị chậm trễ, trễ hẹn, quá hạn SLA.
                           - "LOSS": Áp dụng khi mất hàng, không tìm thấy hàng, không quét mã nhiều ngày.
                        3. hubCode: Chuẩn hóa mã bưu cục liên quan thành 1 trong các mã:
                           - "HN-01" (Hà Nội / Miền Bắc)
                           - "SG-02" (TP.HCM / Miền Nam)
                           - "DN-03" (Đà Nẵng / Miền Trung)
                           - Nếu câu phản ánh không nêu rõ Hub, suy luận từ tên địa phương hoặc mặc định gán theo bưu cục tiếp nhận đơn.
                        4. severity: Mức độ nghiêm trọng của sự cố, chọn 1 trong 3 mức:
                           - "CRITICAL": Thiệt hại nặng, mất hàng, hàng vỡ hoàn toàn, ướt toàn bộ.
                           - "MEDIUM": Hư hỏng nhẹ một phần, trễ quá 24h.
                           - "LOW": Góp ý dịch vụ, trễ nhẹ, trầy xước bao bì bên ngoài.
                        
                        === NGUYÊN TẮC THỰC THI CÔNG CỤ (TOOL EXECUTION WORKFLOW) ===
                        1. Khi đã xác định được sự cố, BẮT BUỘC phải gọi công cụ (Function Tool) được cung cấp để tương tác với Cơ sở dữ liệu:
                           - Gọi tool tạo phiếu sự cố (createIncidentTool hoặc handleIncidentTool) với đầy đủ các tham số đã bóc tách.
                           - Nếu có tool cập nhật trạng thái đơn hàng riêng (updateDeliveryStatusTool), thực hiện cập nhật trạng thái tương ứng: "DAMAGED" (cho HỎNG_HÓC), "DELAYED" (cho GIAO_TRỄ).
                        2. Không tự trả lời hoàn thành trước khi Tool trả về kết quả.
                        
                        === NGUYÊN TẮC PHẢN HỒI KẾT QUẢ (ZERO CRASH & USER FEEDBACK) ===
                        - Nếu Tool trả về thành công (success: true):\s
                          Xác nhận ngắn gọn, lịch sự với người dùng gồm: Mã đơn hàng, loại sự cố đã ghi nhận, mức độ nghiêm trọng, mã phiếu sự cố (nếu có) và thông báo đã chuyển sang bộ phận hiện trường xử lý.
                        - Nếu Tool trả về lỗi (success: false / error):
                          Tuyệt đối không bịa đặt trạng thái thành công. Thông báo rõ lý do từ hệ thống cho người dùng (Ví dụ: "Không tìm thấy mã vận đơn RK-2026-001 trên hệ thống, vui lòng kiểm tra lại").
                        - Phong thái: Chuyên nghiệp, đồng cảm, đúng chuẩn mực dịch vụ chăm sóc khách hàng doanh nghiệp.
                        """)
                .user(chatConversation.content())
                .tools(deliveryService, incidentService)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatConversation.conversationId()))
                .call()
                .content();
    }

    @PostMapping("/analytics/query")
    public String analytic(@RequestBody ChatConversation chatConversation) {
        return chatClient.prompt()
                .user(chatConversation.content())
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatConversation.conversationId()))
                .call()
                .content();
    }
}
