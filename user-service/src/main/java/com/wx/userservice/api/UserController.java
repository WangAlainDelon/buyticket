//package com.wx.userservice.api;
//
//import com.wx.userservice.dao.UserRepository;
//import com.wx.userservice.domain.UserDTO;
//import com.wx.userservice.feign.OrderClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//@RestController
//@RequestMapping("/api/customer")
//public class UserController {
//    @Autowired
//    private UserRepository userRepository;
//
//    @PostConstruct
//    public void init() {
//        UserDTO one = userRepository.findOneById(1L);
//        if (Objects.isNull(one)) {
//            UserDTO userDTO = new UserDTO();
//            userDTO.setUserName("zhangsan");
//            userDTO.setPassWord("111111");
//            userDTO.setRole("UserDTO");
//            userDTO.setDeposit(1000);
//            userRepository.save(userDTO);
//        }
//
//    }
//
//    @Autowired
//    private OrderClient orderClient;
//
//
//    @PostMapping()
//    public UserDTO create(@RequestBody UserDTO customer) {
//        return userRepository.save(customer);
//    }
//
//    @GetMapping()
//    public List<UserDTO> getAll() {
//        return userRepository.findAll();
//    }
//
//
//
//}
