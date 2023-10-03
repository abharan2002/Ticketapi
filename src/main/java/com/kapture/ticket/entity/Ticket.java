package com.kapture.ticket.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "client_id")
    private int clientId;

    @Column(name = "ticket_code")
    private int ticketCode;

    @Column(name = "title")
    private String title;


    @Column(name = "status")
    private String status;
}

