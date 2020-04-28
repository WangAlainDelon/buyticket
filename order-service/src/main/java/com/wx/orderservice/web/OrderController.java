package com.wx.orderservice.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.wx.orderservice.dao.OrderRepository;
import com.wx.orderservice.domain.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private JmsTemplate jmsTemplate;

    private TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public void create(@RequestBody OrderDTO orderDTO) {
        //创建订单
        orderDTO.setUuid(uuidGenerator.generate().toString());
        try {
            jmsTemplate.convertAndSend("order:new", objectMapper.writeValueAsString(orderDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

}
