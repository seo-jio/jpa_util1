package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


//1. 엔티티 직접 노출
@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();  //lazy loading 강제 초기화, 초기화 하지 않으면 null값이 나온다.
            order.getDelivery().getAddress();
            List<OrderItem> result = order.getOrderItems();
            result.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    //2. 엔티티 DTO로 변환
    //dto를 사용할 때는 안에 엔티티가 있으면 안된다. 완전히 엔티티와의 의존을 끊어야 한다!
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    //3. 패치 조인 최적화
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> all = orderRepository.findAllWithItem();
        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    //페이징 한계 돌파
    //xToONe 매핑은 패치 조인 전략을 사용하지만 OneToMany일 경우 "지연 로딩 + 배치"를 사용한다.
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> all = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    //jpa에서 DTO 직접 조회
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDto();
    }

    //컬렉션 조회 최적화
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }

    //플랫 데이터 최적화
    //어플리케이션에서 추가 작업이 큼
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Getter
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        private List<OrderItemDto> orderItemDtos;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItemDtos = order.getOrderItems().stream().  //orderItem도 dto에 담아서 return
                    map(o -> new OrderItemDto(o.getItem().getName(), o.getOrderPrice(), o.getCount()))
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto{
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(String name, int price, int count){
            this.itemName = name;
            this.orderPrice = price;
            this.count = count;
        }
    }
}
