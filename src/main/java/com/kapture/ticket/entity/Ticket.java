package com.kapture.ticket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tickets")
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")

    // change into int
    private int id;

    @Column(name = "client_id", nullable = false)
    private int clientId;

    @Column(name = "ticket_code", nullable = false, unique = true)
    private int ticketCode;

    @Column(name = "title", nullable = false)
    private String title;


    @Column(name = "status", nullable = false)
    private String status;
}

