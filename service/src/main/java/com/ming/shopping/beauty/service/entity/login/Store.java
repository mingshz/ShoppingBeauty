package com.ming.shopping.beauty.service.entity.login;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.crud.CrudFriendly;
import me.jiangcai.jpa.entity.support.Address;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.ming.shopping.beauty.service.utils.Constant.DATE_COLUMN_DEFINITION;

/**
 * 门店
 *
 * @author lxf
 */
@Entity
@Getter
@Setter
public class Store implements CrudFriendly<Long> {

    @Id
    private Long id;
    /**
     * share primary key
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
    private Login login;

    @ManyToOne
    private Merchant merchant;
    /**
     * 所属门店
     */
    @ManyToOne
    private Store store;
    /**
     * 门店名称
     */
    @Column(length = 50)
    private String name;
    /**
     * 门店电话
     */
    @Column(length = 20)
    private String telephone;

    /**
     * 联系人
     */
    @Column(length = 50)
    private String contact;

    /**
     * 门店地址
     */
    private Address address;

    /**
     * 门店代表
     */
    @OneToMany
    private List<Represent> represents;

    @Column(columnDefinition = DATE_COLUMN_DEFINITION)
    private LocalDateTime createTime;
    /**
     * 冻结或删除都应设置为 false
     */
    private boolean enabled = true;

    /**
     * 是否是个门店
     *
     * @return
     */
    public boolean isManageable() {
        return store == null;
    }

    /**
     * 门店是否可用
     */
    public boolean isStoreUsable() {
        return (isManageable() && enabled)
                || (!isManageable() && store.enabled);
    }

    /**
     * 获取门店ID
     *
     * @return
     */
    public long getStoreId() {
        return isManageable() ? id : store.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store)) return false;
        Store store = (Store) o;
        return Objects.equals(id, store.getId());
    }
}
