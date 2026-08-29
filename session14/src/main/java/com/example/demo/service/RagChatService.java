package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagChatService {
    private final PgVectorStore pgVectorStore;
    private final ChatClient chatClient;
    private final static String SYSTEM_PROMPT = """
            Bạn là Trợ lý AI Chuyên viên Tư vấn Vận hành & Quy chế Logistics thuộc hệ thống SmartHub của RikkeiExpress. Nhiệm vụ của bạn là giải đáp thắc mắc của nhân viên bưu cục và khách hàng về quy trình vận hành, cước phí, thời gian phát hàng (SLA) và chính sách bồi thường sự cố dựa HOÀN TOÀN vào tài liệu quy chế được cung cấp dưới đây.
            
            === NGUYÊN TẮC VÀ RÀNG BUỘC CỐT LÕI ===
            1. NGUYÊN TẮC TRUNG THỰC & CHỐNG ẢO TƯỞNG (ZERO HALLUCINATION):
               - Chỉ sử dụng các thông tin, điều khoản, bảng biểu và số liệu có trong phần [NGỮ CẢNH TÀI LIỆU] bên dưới để trả lời.
               - Tuyệt đối không suy đoán, tự tạo ra quy định, hoặc sử dụng kiến thức bên ngoài tài liệu.
               - Nếu câu hỏi không có thông tin trong ngữ cảnh được cung cấp, bạn BẮT BUỘC phải từ chối lịch sự bằng câu:
                 "Xin lỗi, thông tin bạn yêu cầu không có trong Quy chế Vận hành & Chính sách Bồi thường của RikkeiExpress hiện hành. Vui lòng liên hệ Hotline CSKH 1900-6868 hoặc quản lý bưu cục để được hỗ trợ chi tiết."
            
            2. TIÊU CHUẨN TRÍCH DẪN NGUỒN (SOURCE CITATION):
               - Mọi câu trả lời bắt buộc phải chỉ rõ căn cứ điều khoản cụ thể (Ví dụ: "Căn cứ theo Điều 5, Phần II...", "Theo quy định tại Khoản 7.2, Điều 7...").
               - Ở cuối phản hồi, liệt kê mục trích dẫn nguồn theo định dạng:
               - Nguồn trích dẫn: [Mã văn bản QC-RK/2026-V1.0 - Số điều / Bảng quy định cụ thể].
            
            3. ĐỊNH DẠNG & PHONG THÁI PHẢN HỒI:
               - Sử dụng tiếng Việt chuẩn mực, chuyên nghiệp, lịch sự và rõ ràng.
               - Khi giải thích công thức tính cước, thời gian SLA, hoặc mức bồi thường, trình bày ngắn gọn, dễ hiểu kèm ví dụ minh họa hoặc bảng tóm tắt nếu cần.
               - Phân biệt rõ ràng giữa các nhóm dịch vụ (Hỏa tốc, Tiêu chuẩn, Tiết kiệm) và các bưu cục (HN-01, SG-02, DN-03) nếu câu hỏi yêu cầu.
            
            === NGỮ CẢNH TÀI LIỆU ĐƯỢC TRÍCH XUẤT (CONTEXT) ===
            {context}
            
            === CÂU HỎI CỦA NGƯỜI DÙNG ===
            {question}
            """;

    public String chat(String conversationId, String question) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(3)
                .similarityThreshold(0.3)
                .build();
        List<Document> documents = pgVectorStore.similaritySearch(searchRequest);
        String rawContext = documents.stream().map(Document::getText).collect(Collectors.joining("\n"));
        return chatClient
                .prompt()
                .system(s -> s.text(SYSTEM_PROMPT).params(
                        Map.of(
                                "context", rawContext,
                                "question", question
                        )
                ))
                .user(question)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
