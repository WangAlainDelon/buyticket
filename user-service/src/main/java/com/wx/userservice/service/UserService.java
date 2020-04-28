//package com.wx.userservice.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.wx.userservice.dao.PayInfoRepository;
//import com.wx.userservice.dao.UserRepository;
//import com.wx.userservice.domain.OrderDTO;
//import com.wx.userservice.domain.PayInfo;
//import com.wx.userservice.domain.UserDTO;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//
///**
// * User: Mr.Wang
// * Date: 2020/4/11
// */
//@Service
//public class UserService {
//    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private JmsTemplate jmsTemplate;
//
//    @Autowired
//    private PayInfoRepository payInfoRepository;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//
//    @JmsListener(destination = "order:pay", containerFactory = "msgFactory")
//    @Transactional
//    public void handleOrderPay(String msg) {
//        OrderDTO orderDTO = null;
//        try {
//            orderDTO = objectMapper.readValue(msg, OrderDTO.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        PayInfo payInfo = payInfoRepository.findOneByOrderId(orderDTO.getId());
//        if (payInfo != null) {
//            LOGGER.info("order already paid");
//        } else {
//            UserDTO userDTO = userRepository.findOneById(orderDTO.getUserId());
//            if (userDTO.getDeposit() - orderDTO.getAmount() <= 0) {
//                //余额不足
//                LOGGER.info("Sorry, your credit is running low");
//                return;
//            }
//            //利用数据库加锁扣费
//            userRepository.change(orderDTO.getAmount(), userDTO.getId());
//            PayInfo payInfo1 = new PayInfo();
//            payInfo1.setAmount(orderDTO.getAmount());
//            payInfo1.setOrderId(orderDTO.getId());
//            payInfo1.setStatus("PAID");
//            payInfoRepository.save(payInfo1);
//        }
//        orderDTO.setStatus("PAID");
//        try {
//            jmsTemplate.convertAndSend("order:ticket_move", objectMapper.writeValueAsString(orderDTO));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//}
