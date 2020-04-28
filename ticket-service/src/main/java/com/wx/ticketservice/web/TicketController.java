package com.wx.ticketservice.web;

import com.wx.ticketservice.dao.TicketRepository;
import com.wx.ticketservice.domain.TicketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;


@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @PostConstruct
    public void init() {
        if (ticketRepository.count() > 0) {
            return;
        }
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setName("no.1");
        ticketRepository.save(ticketDTO);
    }

    @Autowired
    private TicketRepository ticketRepository;
}
