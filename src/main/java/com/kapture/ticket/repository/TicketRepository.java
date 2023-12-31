package com.kapture.ticket.repository;

import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.entity.Ticket;


import java.util.List;

public interface TicketRepository {

    List<Ticket> findAllTickets(int clientId,int page,int size);
    List<Ticket> findTicketById(int id);

    TicketDto createTicket(TicketDto ticketDto);
    Ticket updateTicket(Ticket ticket);
    List<Ticket> searchTicketsByCriteria(Integer clientId, Integer ticketCode, String status);

    boolean deleteTicket(Ticket ticket);

}
