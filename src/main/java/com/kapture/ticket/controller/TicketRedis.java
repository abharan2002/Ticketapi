package com.kapture.ticket.controller;

import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.repository.TicketRepository;
import com.kapture.ticket.service.TicketService;
import com.kapture.ticket.util.QueryUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/redis")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketRedis {
    private QueryUtil queryUtil;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final RedissonClient redissonClient;

    @GetMapping("get-ticket-by-id/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable int id) {
        RMap<Integer, TicketDto> rMap = redissonClient.getMap("tickets");
        TicketDto ticket=  rMap.getOrDefault(id, null);
        if (ticket != null) {
            System.out.println("inside Redis");
            return ResponseEntity.ok(ticket);
        } else {
            List<Ticket> tickets = ticketRepository.findTicketById(id);
            Ticket ticket1 = tickets.get(0);
            ticket = queryUtil.ticketToTicketDto(ticket1);
            if (ticket != null) {
                rMap.put(id, ticket);
                return ResponseEntity.ok(ticket1);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }
    @PutMapping("/update-ticket/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable int id, @RequestBody TicketDto updatedTicket) {
        ResponseEntity<?> responseEntity = ticketService.updateTicket(id, updatedTicket);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            RMap<Integer, TicketDto> rMap = redissonClient.getMap("tickets");
            if (rMap.containsKey(id)) {

                rMap.put(id, updatedTicket);
            }
        }
        return responseEntity;
    }

    @PostMapping("/create-ticket")
    public ResponseEntity<?> createTicket(@RequestBody TicketDto ticket) {
        TicketDto newTicket = ticketRepository.createTicket(ticket);

        if (newTicket != null) {
            RMap<Integer, TicketDto> rMap = redissonClient.getMap("tickets");
            rMap.put(ticket.getId(), newTicket);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-ticket/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable String id) {
        ResponseEntity<?> responseEntity = ticketService.deleteTicket(id);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            RMap<Integer, TicketDto> rMap = redissonClient.getMap("tickets");
            rMap.remove(Integer.parseInt(id));
        }

        return responseEntity;
    }
}
