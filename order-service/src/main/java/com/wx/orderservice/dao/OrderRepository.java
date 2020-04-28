package com.wx.orderservice.dao;


import com.wx.orderservice.domain.OrderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderDTO, Long> {

    OrderDTO findOneByUserId(Long userId);

    /**
     * 没有nativeQuery = true时，就不是原生sql，
     * 而其中的select * from xxx中xxx也不是数据库对应的真正的表名，
     * 而是对应的实体名，并且sql中的字段名也不是数据库中真正的字段名，而是实体的字段名
     *
     * @param uuid
     * @return
     */
    @Query(value = "select id,user_Id,title,uuid,ticket_num,status,amount,date_time ,reason from user_order   where uuid=?1", nativeQuery = true)
    OrderDTO findOneByUuid(String uuid);

    @Query(value = "select id,user_Id,title,uuid,ticket_num,status,amount,date_time ,reason from user_order where status=?1 and date_time < ?2 ", nativeQuery = true)
    List<OrderDTO> findAllBystatusAndBeforeDate(String aNew, ZonedDateTime zonedDateTime);
}
