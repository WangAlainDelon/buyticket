package com.wx.ticketservice.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by mavlarn on 2018/1/20.
 */
public class OrderDTO {

    private Long id;
    private String uuid;
    private Long userId;
    private String title;
    private Long ticketNum;
    private String status;
    private int amount;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(Long ticketNum) {
        this.ticketNum = ticketNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", ticketNum=" + ticketNum +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                '}';
    }
}
