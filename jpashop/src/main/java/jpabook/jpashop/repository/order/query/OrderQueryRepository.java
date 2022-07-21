package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    //dto 안에 컬렉션이 있을 경우 일대다 매핑이기 때문에 컬렉션 값을 불러오는 메소드를 만들어 두 번의 쿼리를 통해 dto를 채운다.
    //n + 1문제 발생한다.
    public List<OrderQueryDto> findOrderQueryDto() {
        List<OrderQueryDto> result = findOrders();  //컬렉션을 제외한 나머지 값을 채움
        result.forEach(o -> {  //컬렉션 값을 직접 쿼리를 보내서 채워줌
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }


    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        //sql의 in을 사용하기 위해 id 값으로 이루어진 리스트 생성
        List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

        //sql의 in을 사용해 한번에 모든 item을 찾아온다.(기존에는 item마다 쿼리가 발생했음)
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        //찾아온 orderItems를 key값으로 orderId를 갖는 map으로 변환하여 사용
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream().collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        //OrderQueryDto에 OrderQueryItemDto를 채워준다.
        result.stream().forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    //한방 쿼리로 모든 테이블을 조인해서 결과 값을 가져온다. 쿼리가 한번만 나감
    //페이징 불가능
    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                                " join o.member m" +
                                " join o.delivery d" +
                                " join o.orderItems oi" +
                                " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
