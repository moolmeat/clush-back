package com.calendar.clush_back.common.request;

import lombok.Data;

@Data
public class PagingRequestDto {
    private int page;
    private int size;
}