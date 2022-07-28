package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Beer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class BeerRepository {
    private final EntityManager em;

    public Long save(Beer beer){
        em.persist(beer);
        return beer.getId();
    }
}
