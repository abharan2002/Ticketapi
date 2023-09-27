package com.kapture.ticket.repository;

import com.kapture.ticket.dto.TicketDto;
import com.kapture.ticket.entity.Ticket;
import com.kapture.ticket.util.QueryUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TicketRepositoryImpl implements TicketRepository {

    @Autowired
    private QueryUtil queryUtil;
    private final SessionFactory sessionFactory;
    @Autowired
    public TicketRepositoryImpl(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Ticket> findAllTickets(int clientId,int page, int size) {
        String query = "From Ticket where clientId = :CLIENT_ID";
        Map<String, Object> parametersToSet = new HashMap<>();
        parametersToSet.put("CLIENT_ID", clientId);

        return queryUtil.runQueryHelper(query, parametersToSet, Ticket.class, size, page);
    }

    @Override
    public List<Ticket> findTicketById(int id) {
        String query = "From Ticket where id = :ID";
        Map<String, Object> parametersToSet = new HashMap<>();
        parametersToSet.put("ID", id);

        return queryUtil.runQueryHelper(query, parametersToSet, Ticket.class, null, null);
    }

    public TicketDto createTicket(TicketDto ticketDto) {
        return queryUtil.saveOrUpdateTicket(ticketDto);
    }

    public Ticket updateTicket(Ticket ticket) {
        return queryUtil.executeUpdateQuery(ticket);
    }

    @Override
    public List<Ticket> searchTicketsByCriteria(Integer clientId, Integer ticketCode, String status) {
        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT * FROM tickets WHERE 1=1";

        if (clientId != null) {
            queryString += " AND client_id = :clientId";
        }
        if (ticketCode != null) {
            queryString += " AND ticket_code = :ticketCode";
        }
        if (status != null) {
            queryString += " AND status = :status";
        }

        NativeQuery<Ticket> query = session.createSQLQuery(queryString)
                .addEntity(Ticket.class);

        if (clientId != null) {
            query.setParameter("clientId", clientId);
        }
        if (ticketCode != null) {
            query.setParameter("ticketCode", ticketCode);
        }
        if (status != null) {
            query.setParameter("status", status);
        }

        return query.getResultList();
    }


    @Override
    public boolean deleteTicket(Ticket ticket) {
        return queryUtil.executeDeleteQuery(ticket);
    }



}
