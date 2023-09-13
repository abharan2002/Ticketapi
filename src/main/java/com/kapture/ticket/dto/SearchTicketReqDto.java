package com.kapture.ticket.dto;

import lombok.Data;

@Data
public class SearchTicketReqDto {
    private Integer clientId;
    private Integer ticketCode;
    private String status;
}