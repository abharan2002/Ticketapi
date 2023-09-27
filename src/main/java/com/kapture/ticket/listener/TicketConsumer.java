package com.kapture.ticket.listener;
import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kapture.ticket.constants.appConstants.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketConsumer {
    private final TicketRepository ticketRepository;
    private final Logger logger = LoggerFactory.getLogger(TicketConsumer.class);
    @KafkaListener(topics = TOPIC, groupId = GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void consumeTicket(TicketDto ticketDto) {
        try {
            logger.info("Consumed Ticket" + ticketDto);
            System.out.println("Received Ticket: " + ticketDto);
            ticketRepository.createTicket(ticketDto);
        } catch (Exception e) {
            logger.error("Error while consuming ticket: " + e.getMessage(), e);
        }
    }

}
