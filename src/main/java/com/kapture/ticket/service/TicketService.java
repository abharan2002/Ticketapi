package com.kapture.ticket.service;

import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.exceptions.BadRequestException;
import com.kapture.ticket.exceptions.TicketNotFoundException;
import com.kapture.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    public Page<Ticket> getAllTickets(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ticketRepository.findAll(pageable);
    }

    public Ticket getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            throw new TicketNotFoundException("Ticket not found with ID: " + id);
        }
        return ticket;
    }

    public Ticket createTicket(Ticket ticket) {
        if (ticket == null) {
            throw new BadRequestException("Invalid ticket data");
        }
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Long id, Ticket updatedTicket) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + id));

        existingTicket.setClientId(updatedTicket.getClientId());
        existingTicket.setTicketCode(updatedTicket.getTicketCode());
        existingTicket.setTitle(updatedTicket.getTitle());
        existingTicket.setStatus(updatedTicket.getStatus());

        return ticketRepository.save(existingTicket);
    }

    public void deleteTicket(Long id) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + id));

        ticketRepository.delete(existingTicket);
    }
}
