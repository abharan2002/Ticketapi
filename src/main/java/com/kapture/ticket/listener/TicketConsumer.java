package com.kapture.ticket.listener;
import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketConsumer {
    private final TicketRepository ticketRepository;
    private final Logger logger = LoggerFactory.getLogger(TicketConsumer.class);
//    @KafkaListener(topics = "TICKET_TOPIC" , groupId = "tickets")
//    public void consumeTicket(String message){
//        logger.info("Consumed Ticket");
//        System.out.println("Received Ticket: " + message);
//    }
    @KafkaListener(topics = "TICKET_TOPIC" , groupId = "tickets",containerFactory = "kafkaListenerContainerFactory")
    public void consumeTicket(TicketDto ticketDto){
        logger.info("Consumed Ticket" + ticketDto);
        System.out.println("Received Ticket: " + ticketDto);
        ticketRepository.createTicket(ticketDto);
    }
}
