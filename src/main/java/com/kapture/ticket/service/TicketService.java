package com.kapture.ticket.service;

import com.kapture.ticket.dto.SearchTicketReqDto;
import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.exceptions.BadRequestException;
import com.kapture.ticket.exceptions.TicketNotFoundException;
import com.kapture.ticket.repository.TicketRepository;
import com.kapture.ticket.repository.TicketRepositoryJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketService {

    private final TicketRepositoryJPA ticketRepositoryJPA;
    private final TicketRepository ticketRepository;

    public List<Ticket> getAllTickets(int clientId, int page, int size) {
        return ticketRepository.findAllTickets(clientId,page,size);
    }


    public List<Ticket> findTicketById(int id) {
        return ticketRepository.findTicketById(id);
    }


public Ticket createTicket(Ticket ticket) {
    try {
        return ticketRepository.createTicket(ticket);
    } catch (Exception e) {
        throw new RuntimeException("Error creating ticket: " + e.getMessage(), e);
    }
}


    public Ticket updateTicket(int id, Ticket updatedTicket) {
        try {
            List<Ticket> existingTickets = ticketRepository.findTicketById(id);

            if (!existingTickets.isEmpty()) {
                Ticket existingTicket = existingTickets.get(0);
                existingTicket.setClientId(updatedTicket.getClientId());
                existingTicket.setTicketCode(updatedTicket.getTicketCode());
                existingTicket.setTitle(updatedTicket.getTitle());
                existingTicket.setStatus(updatedTicket.getStatus());

                return ticketRepository.updateTicket(existingTicket);
            } else {
                throw new TicketNotFoundException("Ticket with ID " + id + " not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

        public List<Ticket> searchTicketsByCriteria(SearchTicketReqDto searchCriteria) {
            return ticketRepository.searchTicketsByCriteria(
                    searchCriteria.getClientId(),
                    searchCriteria.getTicketCode(),
                    searchCriteria.getStatus()
            );
        }
    }

