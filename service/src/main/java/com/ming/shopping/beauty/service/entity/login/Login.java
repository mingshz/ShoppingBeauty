package com.ming.shopping.beauty.service.entity.login;

import com.ming.shopping.beauty.service.entity.support.ManageLevel;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.crud.CrudFriendly;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ming.shopping.beauty.service.utils.Constant.DATE_COLUMN_DEFINITION;

/**
 * 可登录的角色
 * Created by helloztt on 2017/12/26.
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Setter
@Getter
public class Login implements UserDetails,CrudFriendly<Long> {
    /**
     * 商户超管及操作员
     */
    public static final String ROLE_MERCHANT_ROOT = "MERCHANT_ROOT";
    public static final String ROLE_MERCHANT_OPERATOR = "MERCHANT_OPERATOR";
    /**
     * 门店超管及操作员
     */
    public static final String ROLE_STORE_ROOT = "STORE_ROOT";
    public static final String ROLE_STORE_OPERATOR = "STORE_OPERATOR";
    public static final String ROLE_REPRESENT = "REPRESENT";
    /**
     * 审核门店
     */
    public static final String ROLE_AUDIT_ITEM = "AUDIT_ITEM";
    public static final String ROLE_MANAGE_ITEM = "MANAGE_ITEM";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 这个身份所关联的用户，通常应该是唯一的
     */
    @OneToOne
    private StandardWeixinUser wechatUser;

    @Column(length = 30)
    private String loginName;

    @Column
    private String nickName;
    /**
     * 可推荐
     */
    private boolean guidable;
    /**
     * 可能是个商户或商户管理员
     */
    @OneToOne
    private Merchant merchant;
    /**
     * 可能是个门店或门店管理员
     */
    @OneToOne
    private Store store;
    /**
     * 必定有 user ，但可能没激活
     */
    @OneToOne
    private User user;
    /**
     * 门店代表
     */
    @OneToOne
    private Represent represent;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<ManageLevel> levelSet;

    @Column(columnDefinition = DATE_COLUMN_DEFINITION)
    private LocalDateTime createTime;
    /**
     * 冻结或删除都应设置为 false
     */
    private boolean enabled = true;

    @Column(name = "`DELETE`")
    private boolean delete;
    /**
     * 是否是个超级管理员
     */
    private boolean manageable;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(levelSet)) {
            return Collections.emptySet();
        }
        Stream<String> fixed = levelSet.stream()
                .flatMap(level1 -> Stream.of(level1.roles()));
        return fixed
                .map(ManageLevel::roleNameToRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return loginName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !delete;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public void addLevel(ManageLevel... manageLevels) {
        if (levelSet == null) {
            levelSet = new HashSet<>();
        }
        levelSet.addAll(Arrays.asList(manageLevels));
    }


}
