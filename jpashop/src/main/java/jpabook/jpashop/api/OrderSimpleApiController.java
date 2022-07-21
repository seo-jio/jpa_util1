package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import jpabook.jpashop.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.build.Plugin;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;


    /**
     * xToOne(ManyToOne, OneToOne)
     * Order
     * Order -> Member
     * Order -> Delivery
     */

    //엔티티 직접 노출
    //양방향 연관관계인 필드는 한 쪽을 @JsonIgnore로 무시해야 한다.
    //엔티티가 변하면 api 스펙이 변함
    //불필요한 필드까지 모두 조회 된다.(sql이 너무 많이 나감)
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();  //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }
        return all;
    }

    //Order를 불러온 후 dto로 변환 시 Member와 Delivery를 필요로 하는데 이때 추가 sql문이 발생한다.
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        //N + 1 문제 발생 -> 1 + 회원 N + 배송 N
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> collect = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return collect;
    }

    //패치 조인 전략을 활용해서 Order를 불러올 때 Member와 Delivery 한 번에 불러온다.
    //sql이 한 번만 나감
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> result = orderRepository.findAllWithMemberDelivery();
        return result.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
    }

    //api용 레포지토리와 dto를 따로 생성하여 사용
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4(){
        List<OrderSimpleQueryDto> result = orderSimpleQueryRepository.findOrderDto();
        return result;
    }

    //==정리==//
    //1. v3, v4방식은 트레이드 오프 관계로 v3방식은 재사용성이 좋고 v4는 성능이 좋다.
    //2. v3와 v4의 성능차이는 크지 않으므로(보통 join에서 성능이 결정되지만 join은 동일하므로) v3 방식으로 먼저 코드를 짠다.

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }
    }
}
