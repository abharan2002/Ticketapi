package com.kapture.ticket.util;

import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.service.TicketService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QueryUtil {

    @Autowired
    private SessionFactory sessionFactory;
    private final Logger logger = LoggerFactory.getLogger(TicketService.class);

    public  <T> List<T> runQueryHelper(String queryString, Map<String, Object> parametersToSet, Class<T> className, Integer limit, Integer offset) {
        List<T> list = null;
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<T> query = session.createQuery(queryString, className);
            if (parametersToSet != null && !parametersToSet.isEmpty()) {
                parametersToSet.forEach(query::setParameter);
            }

            if (limit != null && offset != null) {
                query.setMaxResults(limit);
                query.setFirstResult(offset);
            }

            list = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Ticket ticketDtoToTicket(TicketDto ticketDto){
        Ticket ticket = new Ticket();
        ticket.setTicketCode(ticketDto.getTicketCode());
        ticket.setClientId(ticketDto.getClientId());
        ticket.setTitle(ticketDto.getTitle());
        ticket.setStatus(ticketDto.getStatus());
        return ticket;
    }

    public TicketDto ticketToTicketDto(Ticket ticket){
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(ticket.getId());
        ticketDto.setTicketCode(ticket.getTicketCode());
        ticketDto.setClientId(ticket.getClientId());
        ticketDto.setTitle(ticket.getTitle());
        ticketDto.setStatus(ticket.getStatus());
        return ticketDto;
    }
    public TicketDto saveOrUpdateTicket(TicketDto ticketDto) {
        Session session = null;
        Transaction txn = null;
        try {
            Ticket ticket = ticketDtoToTicket(ticketDto);
            session = sessionFactory.openSession();
            txn = session.beginTransaction();
            session.saveOrUpdate(ticket);
            txn.commit();
            ticketDto.setId(ticket.getId());
            return ticketDto;
        } catch (Exception e) {
            if (txn != null) {
                txn.rollback();
            }
            logger.error("Error in saveOrUpdateEmployee(): ", e);
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }


    public Ticket executeUpdateQuery(Ticket ticket) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            int clientId = ticket.getClientId();
            int ticketCode = ticket.getTicketCode();
            String title = ticket.getTitle();
            String status = ticket.getStatus();

            String queryString = "UPDATE tickets SET client_id = :clientId, ticket_code = :ticketCode, title = :title, status = :status WHERE id = :ticketId";

            Map<String, Object> parametersToSet = new HashMap<>();
            parametersToSet.put("clientId", clientId);
            parametersToSet.put("ticketCode", ticketCode);
            parametersToSet.put("title", title);
            parametersToSet.put("status", status);
            parametersToSet.put("ticketId", ticket.getId());

            NativeQuery<?> query = session.createNativeQuery(queryString);
            if (parametersToSet != null && !parametersToSet.isEmpty()) {
                parametersToSet.forEach(query::setParameter);
            }

            int rowsAffected = query.executeUpdate();

            session.getTransaction().commit();

            if (rowsAffected > 0) {
                return ticket;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public <T> boolean executeDeleteQuery(T classObj) {
        boolean success = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            session.delete(classObj);
            tx.commit();
            success = true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error("Exception in saveOrUpdate()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return success;
    }

}
