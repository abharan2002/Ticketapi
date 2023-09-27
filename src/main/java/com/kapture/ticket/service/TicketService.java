package com.kapture.ticket.service;

import com.kapture.ticket.dto.SearchTicketReqDto;
import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.exceptions.TicketNotFoundException;
import com.kapture.ticket.repository.TicketRepository;
import com.kapture.ticket.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kapture.ticket.constants.appConstants.TOPIC;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketService {

    private final TicketRepository ticketRepository;
    private final KafkaTemplate<String, TicketDto> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(TicketService.class);

    public ResponseEntity<?> getAllTickets(String clientIdStr, String pageStr, String sizeStr) {
        try {
            int clientId = Integer.parseInt(clientIdStr);
            int page = Integer.parseInt(pageStr);
            int size = Integer.parseInt(sizeStr);
            if (page < 0 || size <= 0 || clientId < 0) {
                logger.error("Invalid input parameters. 'page', 'size', and 'clientId' must be non-negative integers.");
                return ResponseEntity.badRequest().body("Invalid input parameters. 'page', 'size', and 'clientId' must be non-negative integers.");
            }

            List<Ticket> tickets = ticketRepository.findAllTickets(clientId, page, size);
            return ResponseEntity.ok(tickets);
        } catch (NumberFormatException e) {
            logger.error("Invalid input parameters. 'page', 'size', and 'clientId' must be integers.");
            return ResponseEntity.badRequest().body("Invalid input parameters. 'page', 'size', and 'clientId' must be integers.");
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching tickets.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while fetching tickets.");
        }
    }

    public ResponseEntity<?> getTicketById(String idStr) {
        try {
            int id = Integer.parseInt(idStr);

            if (id < 0) {
                logger.error("Invalid ID. ID must be a non-negative integer.");
                return ResponseEntity.badRequest().body("Invalid ID. ID must be a non-negative integer.");
            }

            Ticket ticket = findTicketById(id);
            return ResponseEntity.ok(ticket);
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format. ID must be a non-negative integer.");
            return ResponseEntity.badRequest().body("Invalid ID format. ID must be a non-negative integer.");
        } catch (TicketNotFoundException e) {
            logger.error(e.getMessage());
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
                logger.error("Client ID and ticket code must be greater than zero.");
                return ResponseEntity.badRequest().body("Client ID and ticket code must be greater than zero.");
            }

            TicketDto createdTicket = ticketRepository.createTicket(ticketDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (Exception e) {
            logger.error("Error creating ticket: " + e.getMessage(), e);
            throw new RuntimeException("Error creating ticket: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<?> updateTicket(int id, TicketDto updatedTicket) {
        try {
            if (updatedTicket.getClientId() < 0 || updatedTicket.getTicketCode() < 0) {
                logger.error("Client ID and ticket code must be greater than or equal to zero.");
                return ResponseEntity.badRequest().body("Client ID and ticket code must be greater than or equal to zero.");
            }

            if (updatedTicket.getTitle() == null || updatedTicket.getStatus() == null ||
                    updatedTicket.getTitle().isEmpty() || updatedTicket.getStatus().isEmpty()) {
                logger.error("Title and status must be non-empty strings.");
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
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    public List<Ticket> searchTicketsByCriteria(String clientIdStr, String ticketCodeStr, String status) {
        Integer clientId = null;
        Integer ticketCode = null;

        try {
            if (clientIdStr != null) {
                clientId = Integer.parseInt(clientIdStr);
            }
            if (ticketCodeStr != null) {
                ticketCode = Integer.parseInt(ticketCodeStr);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid input parameters. 'clientId' and 'ticketCode' must be integers.", e);
            throw new IllegalArgumentException("Invalid input parameters. 'clientId' and 'ticketCode' must be integers.");
        }

        SearchTicketReqDto searchCriteria = new SearchTicketReqDto();
        searchCriteria.setClientId(clientId);
        searchCriteria.setTicketCode(ticketCode);
        searchCriteria.setStatus(status);

        return ticketRepository.searchTicketsByCriteria(
                searchCriteria.getClientId(),
                searchCriteria.getTicketCode(),
                searchCriteria.getStatus()
        );
    }


    public ResponseEntity<?> createTicketWithKafka(TicketDto ticketDto) {
        try {
            if (ticketDto.getClientId() <= 0 || ticketDto.getTicketCode() <= 0) {
                logger.error("Client ID and ticket code must be greater than zero.");
                return ResponseEntity.badRequest().body("Client ID and ticket code must be greater than zero.");
            }
            kafkaTemplate.send(TOPIC, ticketDto);
            return ResponseEntity.status(HttpStatus.OK).body(ticketDto);
        } catch (Exception e) {
            logger.error("Error creating ticket with Kafka: " + e.getMessage(), e);
            throw new RuntimeException("Error creating ticket with Kafka: " + e.getMessage(), e);
        }
    }
    public ResponseEntity<?> deleteTicket(String id) {
        try {
            if (Integer.parseInt(id) > 0) {
                Ticket ticket = ticketRepository.findTicketById(Integer.parseInt(id)).get(0);
                if (ticket == null) throw new TicketNotFoundException("Ticket Not Found");
                if (ticketRepository.deleteTicket(ticket))
                    return ResponseHandler.generateResponse("Ticket Deleted Successfully", HttpStatus.OK, ticket);
                else
                    throw new RuntimeException();
            } else {
                return ResponseHandler.generateResponse("Enter Valid,ID", HttpStatus.BAD_REQUEST);
            }
        } catch (ResourceNotFoundException e) {
            String message = e.getMessage();
            return ResponseHandler.generateResponse(message, HttpStatus.NOT_FOUND);
        } catch (NumberFormatException e) {
            return ResponseHandler.generateResponse("Enter valid parameter", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Failed to Delete the Ticket", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
