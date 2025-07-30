package com.loopers.domain.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderItem is a Querydsl query type for OrderItem
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QOrderItem extends BeanPath<OrderItem> {

    private static final long serialVersionUID = 2013320400L;

    public static final QOrderItem orderItem = new QOrderItem("orderItem");

    public final StringPath name = createString("name");

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<Integer> productPrice = createNumber("productPrice", Integer.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public QOrderItem(String variable) {
        super(OrderItem.class, forVariable(variable));
    }

    public QOrderItem(Path<? extends OrderItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderItem(PathMetadata metadata) {
        super(OrderItem.class, metadata);
    }

}

