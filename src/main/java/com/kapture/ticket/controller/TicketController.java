package com.kapture.ticket.controller;

import com.kapture.ticket.dto.SearchTicketReqDto;
import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.exceptions.BadRequestException;
import com.kapture.ticket.exceptions.TicketNotFoundException;
import com.kapture.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/get-all-tickets")
    public ResponseEntity<?> getAllTickets(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) int clientId) {
        try {
            List<Ticket> tickets = ticketService.getAllTickets(clientId, page, size);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @GetMapping("/get-ticket-by-id/{id}")
    public ResponseEntity<?> getTicketsById(@PathVariable int id) {
        try {
            List<Ticket> tickets = ticketService.findTicketById(id);
            return ResponseEntity.ok(tickets);
        } catch (TicketNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/create-ticket/")
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket) {
        try {
            Ticket createdTicket = ticketService.createTicket(ticket);
            return ResponseEntity.ok(createdTicket);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-ticket/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable int id, @RequestBody Ticket updatedTicket) {
        try {
            Ticket updated = ticketService.updateTicket(id, updatedTicket);
            return ResponseEntity.ok(updated);
        } catch (TicketNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @GetMapping("/search")
    public List<Ticket> searchTicketsByCriteria(
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) Integer ticketCode,
            @RequestParam(required = false) String status
    ) {
        SearchTicketReqDto searchCriteria = new SearchTicketReqDto();
        searchCriteria.setClientId(clientId);
        searchCriteria.setTicketCode(ticketCode);
        searchCriteria.setStatus(status);

        return ticketService.searchTicketsByCriteria(searchCriteria);
    }
}
