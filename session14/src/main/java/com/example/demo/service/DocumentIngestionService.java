package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentIngestionService {
    private final PgVectorStore pgVectorStore;

    public void ingest(Resource resource) {
        if (resource == null || !resource.exists()) {
            throw new IllegalArgumentException("Resource is null or does not exist");
        }
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.get();
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(100)
                .withMinChunkLengthToEmbed(100)
                .withKeepSeparator(true)
                .withMaxNumChunks(10000)
                .build();
        pgVectorStore.accept(splitter.split(documents));
    }
}
