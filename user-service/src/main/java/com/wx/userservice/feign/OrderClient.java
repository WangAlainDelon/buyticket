package com.wx.userservice.feign;


import com.wx.userservice.domain.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by mavlarn on 2018/2/14.
 */
@FeignClient(value = "order", path = "/api/order")
public interface OrderClient {

    @GetMapping("/{id}")
    OrderDTO getMyOrder(@PathVariable(name = "id") Long id);

    @PostMapping("")
    OrderDTO create(@RequestBody OrderDTO dto);
}
