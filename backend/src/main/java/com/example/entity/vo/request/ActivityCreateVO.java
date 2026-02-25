package com.example.entity.vo.request;

import lombok.Data;
import java.util.Date;

@Data
public class ActivityCreateVO {
    private String title;
    private String description;
    private Date eventTime;
    private String location;
    private String organizer;
    private Integer maxPeople;
    private String poster;
}