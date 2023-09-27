package com.kapture.ticket.cache;

import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.entity.Ticket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketComponent {

    private final RedissonClient redissonClient;

    @Autowired
    public TicketComponent(RedissonClient redissonClient) {

        this.redissonClient = redissonClient;
    }

    public void createTicket(TicketDto ticket) {
        RMap<Integer, TicketDto> rMap = redissonClient.getMap("tickets");
        rMap.put(ticket.getId(), ticket);
    }

    public void updateTicket(int id, TicketDto updatedTicket) {
        RMap<Integer, TicketDto> rMap = redissonClient.getMap("tickets");

        if (rMap.containsKey(id)) {
            rMap.put(id, updatedTicket);
        }
    }

    public Ticket getTicketById(int id) {
        RMap<Integer, Ticket> rMap = redissonClient.getMap("tickets");

        return rMap.getOrDefault(id, null);
    }

    public void deleteTicket(int id) {
        RMap<Integer, TicketDto> rMap = redissonClient.getMap("tickets");

        rMap.remove(id);
    }
}

