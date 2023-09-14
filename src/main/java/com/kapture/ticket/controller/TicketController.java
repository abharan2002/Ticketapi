package com.kapture.ticket.controller;

import com.kapture.ticket.dto.SearchTicketReqDto;
import com.kapture.ticket.dto.TicketDto;
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
    public ResponseEntity<?> getAllTickets(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "10") String size,
            @RequestParam(required = false) String clientId
    ) {
        return ticketService.getAllTickets(clientId, page, size);
    }

    @GetMapping("/get-ticket-by-id/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable String id) {
        return ticketService.getTicketById(id);
    }


    @PostMapping("/create-ticket/")
    public ResponseEntity<?> createTicket(@RequestBody TicketDto ticketDto) {
        return ticketService.createTicket(ticketDto);
    }

    @PutMapping("/update-ticket/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable int id, @RequestBody Ticket updatedTicket) {
        return ticketService.updateTicket(id, updatedTicket);
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
