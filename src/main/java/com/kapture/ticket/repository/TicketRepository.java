package com.kapture.ticket.repository;

import com.kapture.ticket.dto.SearchTicketReqDto;
import com.kapture.ticket.entity.Ticket;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface TicketRepository {

    List<Ticket> findAllTickets(int clientId,int page,int size);
    List<Ticket> findTicketById(int id);

    Ticket createTicket(Ticket ticket);
    Ticket updateTicket(Ticket ticket);
    List<Ticket> searchTicketsByCriteria(Integer clientId, Integer ticketCode, String status);


}
