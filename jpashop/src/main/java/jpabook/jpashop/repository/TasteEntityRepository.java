package jpabook.jpashop.repository;

import jpabook.jpashop.domain.TasteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

@Repository
@RequiredArgsConstructor
public class TasteEntityRepository {

    private final EntityManager em;

    public Long save(TasteEntity tasteEntity){
        em.persist(tasteEntity);
        return tasteEntity.getId();
    }
}
