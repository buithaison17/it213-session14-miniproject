package com.example.demo.dto;

import com.example.demo.constant.IncidentType;
import com.example.demo.constant.Severity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record CreateIncidentDto(
        @JsonProperty
        @JsonPropertyDescription("Mã vận đơn")
        String trackingCode,
        @JsonProperty
        @JsonPropertyDescription("Tình trạng sự cố")
        IncidentType incidentType,
        @JsonProperty
        @JsonPropertyDescription("Mã bưu cục")
        String hubCode,
        @JsonProperty
        @JsonPropertyDescription("Mức độ nghiêm trọng")
        Severity severity
) {
}
