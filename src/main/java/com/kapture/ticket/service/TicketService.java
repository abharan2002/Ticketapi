package com.kapture.ticket.service;

import com.kapture.ticket.dto.SearchTicketReqDto;
import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.exceptions.TicketNotFoundException;
import com.kapture.ticket.repository.TicketRepository;
import com.kapture.ticket.repository.TicketRepositoryJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketService {

    private final TicketRepositoryJPA ticketRepositoryJPA;
    private final TicketRepository ticketRepository;

    public ResponseEntity<?> getAllTickets(String clientIdStr, String pageStr, String sizeStr) {
        try {
            int clientId = Integer.parseInt(clientIdStr);
            int page = Integer.parseInt(pageStr);
            int size = Integer.parseInt(sizeStr);

            if (page < 0 || size <= 0 || clientId < 0) {
                return ResponseEntity.badRequest().body("Invalid input parameters. 'page', 'size', and 'clientId' must be non-negative integers.");
            }

            List<Ticket> tickets = ticketRepository.findAllTickets(clientId, page, size);
            return ResponseEntity.ok(tickets);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid input parameters. 'page', 'size', and 'clientId' must be integers.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while fetching tickets.");
        }
    }

    public ResponseEntity<?> getTicketById(String idStr) {
        try {
            int id = Integer.parseInt(idStr);

            if (id < 0) {
                return ResponseEntity.badRequest().body("Invalid ID. ID must be a non-negative integer.");
            }

            Ticket ticket = findTicketById(id);
            return ResponseEntity.ok(ticket);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid ID format. ID must be a non-negative integer.");
        } catch (TicketNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Ticket findTicketById(int id) {
        List<Ticket> tickets = ticketRepository.findTicketById(id);

        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("Ticket with ID " + id + " not found");
        }

        return tickets.get(0);
    }

    public ResponseEntity<?> createTicket(TicketDto ticketDto) {
        try {
            if (ticketDto.getClientId() <= 0 || ticketDto.getTicketCode() <= 0) {
                return ResponseEntity.badRequest().body("Client ID and ticket code must be greater than zero.");
            }

            TicketDto createdTicket = ticketRepository.createTicket(ticketDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (Exception e) {
            throw new RuntimeException("Error creating ticket: " + e.getMessage(), e);
        }
    }


    public ResponseEntity<?> updateTicket(int id, Ticket updatedTicket) {
        try {
            if (updatedTicket.getClientId() < 0 || updatedTicket.getTicketCode() < 0) {
                return ResponseEntity.badRequest().body("Client ID and ticket code must be greater than or equal to zero.");
            }

            if (updatedTicket.getTitle() == null || updatedTicket.getStatus() == null ||
                    updatedTicket.getTitle().isEmpty() || updatedTicket.getStatus().isEmpty()) {
                return ResponseEntity.badRequest().body("Title and status must be non-empty strings.");
            }

            List<Ticket> existingTickets = ticketRepository.findTicketById(id);

            if (!existingTickets.isEmpty()) {
                Ticket existingTicket = existingTickets.get(0);
                existingTicket.setClientId(updatedTicket.getClientId());
                existingTicket.setTicketCode(updatedTicket.getTicketCode());
                existingTicket.setTitle(updatedTicket.getTitle());
                existingTicket.setStatus(updatedTicket.getStatus());

                Ticket updated = ticketRepository.updateTicket(existingTicket);
                return ResponseEntity.ok(updated);
            } else {
                throw new TicketNotFoundException("Ticket with ID " + id + " not found");
            }
        } catch (TicketNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
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

