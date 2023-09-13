package com.kapture.ticket.repository;

import com.kapture.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface TicketRepositoryJPA extends JpaRepository<Ticket, Long> {

}
