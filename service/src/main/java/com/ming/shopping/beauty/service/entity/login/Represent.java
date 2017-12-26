package com.ming.shopping.beauty.service.entity.login;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.wx.model.Gender;

import javax.persistence.*;

/**
 * 门店代表
 * @author lxf
 */
@Entity
@Setter
@Getter
public class Represent extends Login {
    /**
     * 性别
     */
    private Gender gender;
}