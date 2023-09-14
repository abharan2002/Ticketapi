package com.kapture.ticket.dto;

import lombok.Data;

@Data
public class TicketDto {
    private Integer id;
    private Integer clientId;
    private Integer ticketCode;
    private String title;
    private String status;
}
