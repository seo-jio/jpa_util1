package jpabook.jpashop.repository.order.query;

import lombok.Getter;

@Getter
public class OrderItemQueryDto {
    private Long orderId;
    private String name;  //아이템 이름
    private int orderPrice;
    private int count;

    public OrderItemQueryDto(Long id, String name, int orderPrice, int count) {
        this.orderId = id;
        this.name = name;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
