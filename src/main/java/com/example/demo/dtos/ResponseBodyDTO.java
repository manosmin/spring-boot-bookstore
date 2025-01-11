package com.example.demo.dtos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.example.demo.models.Book;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseBodyDTO {
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    private int status;
    private String message;
    private List<FieldErrorDTO> errors;
    private List<Book> data;
    private MetadataDTO metadata;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class FieldErrorDTO {
        private String field;
        private String message;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MetadataDTO {
        private Integer page;
        private Integer size;
        private Integer totalPages;
        private Long totalItems;
    }
}
