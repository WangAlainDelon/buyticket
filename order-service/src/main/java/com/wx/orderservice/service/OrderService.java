package com.wx.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx.orderservice.dao.OrderRepository;
import com.wx.orderservice.domain.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/4/11
 */
@Service
@EnableScheduling
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JmsTemplate jmsTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    @JmsListener(destination = "order:locked", containerFactory = "msgFactory")
    @Transactional
    public void handleTicketLock(String json) {
        //创建订单
        OrderDTO orderDTO = null;
        try {
            orderDTO = objectMapper.readValue(json, OrderDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (orderRepository.findOneByUuid(orderDTO.getUuid()) != null) {
            LOGGER.info("msg already process{}", orderDTO.getId());
        } else {
            OrderDTO orderDTO1 = createOrder(orderDTO);
            orderRepository.save(orderDTO1);
            orderDTO.setId(orderDTO1.getId());
        }
        orderDTO.setStatus("NEW");
        try {
            jmsTemplate.convertAndSend("order:pay", objectMapper.writeValueAsString(orderDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "order:finish", containerFactory = "msgFactory")
    @Transactional
    public void handleTicketFinish(String json) {
        OrderDTO orderDTO = null;
        try {
            orderDTO = objectMapper.readValue(json, OrderDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OrderDTO dto = orderRepository.findOneByUuid(orderDTO.getUuid());
        dto.setStatus("FINISH");
        orderRepository.save(dto);
    }

    /**
     * 锁票失败处理
     *
     * @param json
     */
    @JmsListener(destination = "order:ticket_lock_error", containerFactory = "msgFactory")
    @Transactional
    public void handleTicketLockError(String json) {
        OrderDTO orderDTO = null;
        try {
            orderDTO = objectMapper.readValue(json, OrderDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OrderDTO dto = orderRepository.findOneByUuid(orderDTO.getUuid());
        orderDTO.setStatus("FAIL");
        orderDTO.setReason("LOCK TICKET FAIL");
        orderRepository.save(dto);
    }

    /**
     * 扣费失败的处理
     *
     * @param json
     */
    @JmsListener(destination = "order:ticket_pay_error", containerFactory = "msgFactory")
    @Transactional
    public void handleTicketPaykError(String json) {
        OrderDTO orderDTO = null;
        try {
            orderDTO = objectMapper.readValue(json, OrderDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OrderDTO dto = orderRepository.findOneByUuid(orderDTO.getUuid());
        orderDTO.setStatus("FAIL");
        orderDTO.setReason("PAY TICKET FAIL");
        orderRepository.save(dto);
    }

    /**
     * 超时失败
     *
     * @param json
     */
    @JmsListener(destination = "order:timeout_error", containerFactory = "msgFactory")
    @Transactional
    public void handleTicketTimeOutError(String json) {
        OrderDTO orderDTO = null;
        try {
            orderDTO = objectMapper.readValue(json, OrderDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OrderDTO dto = orderRepository.findOneByUuid(orderDTO.getUuid());
        dto.setReason("TIME OUT");
        dto.setStatus("FAIL");
        orderRepository.save(dto);
    }


    private OrderDTO createOrder(OrderDTO orderDTO) {
        OrderDTO orderDTO1 = new OrderDTO();
        orderDTO1.setUuid(orderDTO.getUuid());
        orderDTO1.setAmount(orderDTO.getAmount());
        orderDTO1.setTitle(orderDTO.getTitle());
        orderDTO1.setUserId(orderDTO.getUserId());
        orderDTO1.setTicketNum(orderDTO.getTicketNum());
        orderDTO1.setStatus("NEW");
        return orderDTO1;
    }

    /**
     * 自动任务扫描超时的订单.10s钟检查一次
     */
    @Scheduled(fixedDelay = 1000L)
    public void checkTimeOutOrder() {
        //查询所有超时的订单,超时时间一分钟
        ZonedDateTime zonedDateTime = ZonedDateTime.now().minusMinutes(1L);
        List<OrderDTO> orderDTOS = orderRepository.findAllBystatusAndBeforeDate("NEW", zonedDateTime);
        orderDTOS.stream().forEach(orderDTO -> {
            try {
                jmsTemplate.convertAndSend("order:timeout_error", objectMapper.writeValueAsString(orderDTO));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
