package com.example.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DetailsSaveVO {
    // 用户名：中文/字母/数字，1-10位
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    String username;

    // 性别：0（女）/1（男）
    @Min(0)
    @Max(1)
    int gender;

    // 手机号：中国大陆11位规范格式
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    String phone;

    // QQ号：5-13位数字（现实QQ号长度范围）
    @Pattern(regexp = "^\\d{5,13}$")
    String qq;

    // 微信号：6-20位，字母/数字/下划线/减号（微信官方规则）
    @Pattern(regexp = "^[a-zA-Z0-9_-]{6,20}$")
    String wx;

    // 个人简介：最多200字
    @Length(max = 200)
    String desc;
}