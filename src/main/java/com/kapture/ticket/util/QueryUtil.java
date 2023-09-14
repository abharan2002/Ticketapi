package com.kapture.ticket.util;

import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.entity.Ticket;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
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

    public TicketDto executeSaveTicketQuery(TicketDto ticketDto) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            int clientId = ticketDto.getClientId();
            int ticketCode = ticketDto.getTicketCode();
            String title = ticketDto.getTitle();
            String status = ticketDto.getStatus();

            String queryString = "INSERT INTO tickets (client_id, ticket_code, title, status) " +
                    "VALUES (:clientId, :ticketCode, :title, :status)";

            Map<String, Object> parametersToSet = new HashMap<>();
            parametersToSet.put("clientId", clientId);
            parametersToSet.put("ticketCode", ticketCode);
            parametersToSet.put("title", title);
            parametersToSet.put("status", status);

            TypedQuery<?> query = session.createNativeQuery(queryString);
            if (parametersToSet != null && !parametersToSet.isEmpty()) {
                parametersToSet.forEach(query::setParameter);
            }

            int rowsAffected = query.executeUpdate();

            session.getTransaction().commit();

            if (rowsAffected > 0) {
                return ticketDto;
            } else {
                return null; // Return null if the insert didn't affect any rows
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

            session.getTransaction().commit(); // Commit the transaction after the query

            if (rowsAffected > 0) {
                return ticket;
            } else {
                return null; // Return null if the update didn't affect any rows
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
