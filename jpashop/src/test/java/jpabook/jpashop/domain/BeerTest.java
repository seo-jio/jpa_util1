package jpabook.jpashop.domain;

import jpabook.jpashop.repository.BeerRepository;
import jpabook.jpashop.repository.TasteEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //스프링과 함께 테스트
@SpringBootTest
@Transactional //테스트가 끝나면 롤백(db반영 X)
public class BeerTest {

    @Autowired BeerRepository beerRepository;
    @Autowired TasteEntityRepository tasteEntityRepository;

    @Test
    public void BeerTest() throws Exception {
        //given
        Beer beer = new Beer();
        TasteEntity tasteEntity1 = new TasteEntity(Taste.SOUR);
        TasteEntity tasteEntity2 = new TasteEntity(Taste.SWEET);
        tasteEntityRepository.save(tasteEntity1);
        tasteEntityRepository.save(tasteEntity2);

        beer.addTaste(tasteEntity1);
        beer.addTaste(tasteEntity2);
        beerRepository.save(beer);
        //when
        List<TasteEntity> result = beer.getTastes();
        for (TasteEntity tasteEntity : result) {
            System.out.println("tasteEntity.getTaste() = " + tasteEntity.getTaste());
        }

        //then
        Assertions.assertThat(beer.getTastes().get(0).getTaste()).isEqualTo(Taste.SOUR);
        Assertions.assertThat(beer.getTastes().get(1).getTaste()).isEqualTo(Taste.SWEET);
    }
}