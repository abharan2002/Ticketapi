package com.kapture.ticket.controller;

import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.exceptions.ApiResponse;
import com.kapture.ticket.exceptions.BadRequestException;
import com.kapture.ticket.exceptions.TicketNotFoundException;
import com.kapture.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping("/getTickets")
    public ResponseEntity<?> getAllTickets(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Ticket> tickets = ticketService.getAllTickets(page, size);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        try {
            Ticket ticket = ticketService.getTicketById(id);
            return ResponseEntity.ok(ticket);
        } catch (TicketNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket) {
        try {
            Ticket createdTicket = ticketService.createTicket(ticket);
            return ResponseEntity.ok(createdTicket);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable Long id, @RequestBody Ticket updatedTicket) {
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
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteTicket(id);
            return ResponseEntity.noContent().build();
        } catch (TicketNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }
}
