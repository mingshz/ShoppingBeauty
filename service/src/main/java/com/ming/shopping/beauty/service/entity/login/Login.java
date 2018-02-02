package com.ming.shopping.beauty.service.entity.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ming.shopping.beauty.service.entity.support.ManageLevel;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.crud.CrudFriendly;
import me.jiangcai.wx.standard.entity.StandardWeixinUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.*;
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
public class Login implements UserDetails, CrudFriendly<Long> {
    /**
     * 商户超管及操作员
     */
    public static final String ROLE_MERCHANT_ROOT = "MERCHANT_ROOT";
    public static final String ROLE_MERCHANT_OPERATOR = "MERCHANT_OPERATOR";
    /**
     * 门店超管及操作员
     */
    public static final String ROLE_STORE_ROOT = "STORE_ROOT";
    //    public static final String ROLE_STORE_OPERATOR = "STORE_OPERATOR";
    public static final String ROLE_REPRESENT = "REPRESENT";
    /**
     * 审核项目
     */
    public static final String ROLE_AUDIT_ITEM = "AUDIT_ITEM";
    public static final String ROLE_MANAGE_ITEM = "MANAGE_ITEM";
    /**
     * 结算
     */
    public static final String ROLE_ROOT_SETTLEMENT = "ROOT_SETTLEMENT";
    public static final String ROLE_MERCHANT_SETTLEMENT = "MERCHANT_SETTLEMENT";
    /**
     * 平台管理员有哪些角色
     */
    public static final List<ManageLevel> rootLevel = Arrays.asList(
            ManageLevel.root
            , ManageLevel.rootSettlementManager
            , ManageLevel.rootItemManager
    );
    /**
     * 商户操作员有哪些角色
     */
    public static final List<ManageLevel> merchantLevel = Arrays.asList(
            ManageLevel.merchantRoot
            , ManageLevel.merchantSettlementManager
            , ManageLevel.merchantItemManager
    );

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
    @JsonIgnore
    private Merchant merchant;
    /**
     * 可能是个门店或门店管理员
     */
    @OneToOne
    @JsonIgnore
    private Store store;
    /**
     * 必定有 user ，但可能没激活
     */
    @OneToOne
    @JsonIgnore
    private User user;
    /**
     * 门店代表
     */
    @OneToOne
    @JsonIgnore
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

    public static Collection<? extends GrantedAuthority> getGrantedAuthorities(Set<ManageLevel> levelSet) {
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities(levelSet);
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
