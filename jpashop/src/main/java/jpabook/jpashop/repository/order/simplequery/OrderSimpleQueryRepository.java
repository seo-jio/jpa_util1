package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
//레포지토리는 가급적 순수한 엔티티를 조회하기 위하여 api용(화면용) 레포지토리와 dto를 따로 생성하여 사용한다.
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDto(){
        return em.createQuery("select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                    "from Order o" +
                                    " join o.member m" +
                                    " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
