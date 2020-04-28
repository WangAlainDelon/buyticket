//package com.wx.userservice.dao;
//
//import com.wx.userservice.domain.UserDTO;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//
///**
// * Created by mavlarn on 2018/1/20.
// */
//public interface UserRepository extends JpaRepository<UserDTO, Long> {
//
//    UserDTO findOneByUserName(String userName);
//
//    UserDTO findOneById(Long userId);
//
//    @Modifying
//    @Query("UPDATE user set deposit= deposit-?1 where id  =?2")
//    void change(Integer deposit, Long amount);
//}
