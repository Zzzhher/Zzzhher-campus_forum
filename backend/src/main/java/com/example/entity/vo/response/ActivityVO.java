package com.example.entity.vo.response;

import lombok.Data;
import java.util.Date;

@Data
public class ActivityVO {
    private Integer id;
    private String title;
    private String description;
    private Date eventTime;
    private String location;
    private String organizer;
    private Integer maxPeople;
    private String poster;
    private Integer uid;
    private Date createTime;
    private Integer status;
    private Integer positiveFeedback;
    private Integer negativeFeedback;
    private String username;
    private String avatar;
    private Integer participantCount;
    private Integer checkInCount;
}