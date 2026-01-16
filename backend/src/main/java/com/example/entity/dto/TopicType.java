package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

@Data
@TableName("db_topic_type")
public class TopicType implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String name;
    @TableField("`desc`")
    String desc;
    String color;
}
