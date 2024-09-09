package com.calendar.clush_back.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "페이징 요청 DTO")
public class PagingRequestDto {

    @Schema(description = "페이지 번호", example = "1")
    private int page;

    @Schema(description = "페이지 크기", example = "20")
    private int size;
}