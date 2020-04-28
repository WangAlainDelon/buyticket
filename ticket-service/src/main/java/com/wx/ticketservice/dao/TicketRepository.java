package com.wx.ticketservice.dao;

import com.wx.ticketservice.domain.TicketDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by mavlarn on 2018/1/20.
 */
public interface TicketRepository extends JpaRepository<TicketDTO, Long> {

    TicketDTO findOneByOwner(Long owner);

    @Modifying
    @Query("UPDATE ticket set lock_user = ?1 where lock_user is null and ticket_num =?2 and owner is null")
    int lockTicket(Long userId, Long ticketNum);

    @Modifying
    @Query("UPDATE ticket set owner= ?1 ,lock_user = null where lock_user=?1 and ticketNum=?2")
    int moveTicket(Long userId, Long ticketNum);
}
