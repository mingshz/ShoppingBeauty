package com.ming.shopping.beauty.service.model.definition;

import com.ming.shopping.beauty.service.entity.item.Item_;
import com.ming.shopping.beauty.service.entity.item.StoreItem;
import com.ming.shopping.beauty.service.entity.item.StoreItem_;
import com.ming.shopping.beauty.service.entity.login.Store_;
import com.ming.shopping.beauty.service.utils.Utils;
import lombok.Getter;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.lib.resource.service.ResourceService;

import javax.persistence.criteria.JoinType;
import java.util.Arrays;
import java.util.List;

/**
 * @author lxf
 */
@Getter
public class ClientStoreItemModel implements DefinitionModel<StoreItem> {

    private final List<FieldDefinition<StoreItem>> definitions;

    public ClientStoreItemModel(ResourceService resourceService) {
        super();
        definitions = Arrays.asList(
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
