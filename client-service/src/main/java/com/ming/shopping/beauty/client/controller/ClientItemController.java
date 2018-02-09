package com.ming.shopping.beauty.client.controller;

import com.ming.shopping.beauty.service.entity.item.Item_;
import com.ming.shopping.beauty.service.entity.item.StoreItem;
import com.ming.shopping.beauty.service.entity.item.StoreItem_;
import com.ming.shopping.beauty.service.entity.login.Store_;
import com.ming.shopping.beauty.service.utils.Utils;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.crud.row.supplier.AntDesignPaginationDramatizer;
import me.jiangcai.crud.row.supplier.SingleRowDramatizer;
import me.jiangcai.lib.resource.service.ResourceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lxf
 */
@Controller
public class ClientItemController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 微信端门店项目列表
     *
     * @param storeId
     * @param itemType
     * @param lat
     * @param lon
     * @return
     */
    @GetMapping("/items")
    @RowCustom(distinct = true, dramatizer = AntDesignPaginationDramatizer.class)
    public RowDefinition<StoreItem> itemList(Long storeId, String itemType, Integer lat, Integer lon) {
        return new RowDefinition<StoreItem>() {

            @Override
            public Class<StoreItem> entityClass() {
                return StoreItem.class;
            }

            @Override
            public List<FieldDefinition<StoreItem>> fields() {
                return listField();
            }

            @Override
            public Specification<StoreItem> specification() {

                return (root, query, cb) -> {
                    List<Predicate> conditions = new ArrayList<>();
                    conditions.add(cb.equal(root.get(StoreItem_.deleted), false));
                    if (storeId != null) {
                        conditions.add(cb.equal(root.join(StoreItem_.store).get(Store_.id), storeId));
                    }
                    if (StringUtils.isNotBlank(itemType)) {
                        conditions.add(cb.like(root.join(StoreItem_.item).get(Item_.itemType), "%" + itemType + "%"));
                    }
                    return cb.and(conditions.toArray(new Predicate[conditions.size()]));
                };
            }
        };
    }

    /**
     * 微信端门店项目详情
     *
     * @param itemId
     * @return
     */
    @GetMapping("/items/{itemId}")
    @RowCustom(distinct = true, dramatizer = SingleRowDramatizer.class)
    public RowDefinition<StoreItem> itemDetail(@PathVariable("itemId") long itemId) {
        return new RowDefinition<StoreItem>() {
            @Override
            public Class<StoreItem> entityClass() {
                return StoreItem.class;
            }

            @Override
            public List<FieldDefinition<StoreItem>> fields() {
                List<FieldDefinition<StoreItem>> fieldDefinitions = listField();
                fieldDefinitions.add(
                        FieldBuilder.asName(StoreItem.class, "details")
                                .addSelect(root -> root.join(StoreItem_.item).get(Item_.richDescription))
                                .build()
                );
                return fieldDefinitions;
            }

            @Override
            public Specification<StoreItem> specification() {
                return (root, cq, cb) ->
                        cb.equal(root.get(StoreItem_.id), itemId);
            }
        };
    }


    private List<FieldDefinition<StoreItem>> listField() {
        return Arrays.asList(
                FieldBuilder.asName(StoreItem.class, "itemId")
                        .addSelect(root -> root.get(StoreItem_.id))
                        .build()
                , FieldBuilder.asName(StoreItem.class, "thumbnail")
                        .addSelect(root -> root.join(StoreItem_.item).get(Item_.mainImagePath))
                        .addFormat(Utils.formatResourcePathToURL(resourceService))
                        .build()
                , FieldBuilder.asName(StoreItem.class, "title")
                        .addSelect(root -> root.join(StoreItem_.item).get(Item_.name))
                        .build()
                , FieldBuilder.asName(StoreItem.class, "tel")
                        .addSelect(itemRoot -> itemRoot.join(StoreItem_.store).get(Store_.telephone))
                        .build()
                , FieldBuilder.asName(StoreItem.class, "address")
                        .addSelect(root -> root.join(StoreItem_.store, JoinType.LEFT).get(Store_.address))
                        .addFormat((data, type) -> data.toString())
                        .build()
                , FieldBuilder.asName(StoreItem.class, "type")
                        .addSelect(storeItemRoot -> storeItemRoot.join(StoreItem_.item).get(Item_.itemType))
                        .build()
                //TODO 距离还有问题
                        /*, FieldBuilder.asName(Item.class, "distance")
                                .build()*/
                , FieldBuilder.asName(StoreItem.class, "vipPrice")
                        .addSelect(root -> root.get(StoreItem_.salesPrice))
                        .build()
                , FieldBuilder.asName(StoreItem.class, "originalPrice")
                        .addSelect(root -> root.join(StoreItem_.item).get(Item_.price))
                        .build()
        );
    }
}
