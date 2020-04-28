package com.wx.ticketservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.wx.ticketservice.dao.TicketRepository;
import com.wx.ticketservice.domain.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * User: Mr.Wang
 * Date: 2020/4/11
 */
@Service
public class TicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @JmsListener(destination = "order:new", containerFactory = "msgFactory")
    @Transactional
    public void handleTicketLock(String msg) {
        //锁票
        OrderDTO orderDTO = null;
        try {
            orderDTO = objectMapper.readValue(msg, OrderDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int count = ticketRepository.lockTicket(orderDTO.getUserId(), orderDTO.getTicketNum());
        if (count > 0) {
            orderDTO.setStatus("TICKET_LOCK");
            try {
                jmsTemplate.convertAndSend("order:locked", objectMapper.writeValueAsString(orderDTO));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            // 锁票失败的处理,
            try {
                jmsTemplate.convertAndSend("order:ticket_lock_error", objectMapper.writeValueAsString(orderDTO));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    //扣费成功交票处理
    @JmsListener(destination = "order:ticket_move", containerFactory = "msgFactory")
    @Transactional
    public void handleTicketMove(String json) {
        OrderDTO orderDTO = null;
        try {
            orderDTO = objectMapper.readValue(json, OrderDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //把票已交给具体的购买人
        int moveTicket = ticketRepository.moveTicket(orderDTO.getUserId(), orderDTO.getTicketNum());
        if (moveTicket == 0) {
            LOGGER.info("msg already process {}", orderDTO);
        }
        orderDTO.setStatus("TICKET_MOVE");
        try {
            jmsTemplate.convertAndSend("order:finish", objectMapper.writeValueAsString(orderDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
