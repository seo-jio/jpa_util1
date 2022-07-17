package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

//@RunWith(SpringRunner.class)  //junit5는 RunWith 불필요!
@SpringBootTest
public class MemberRepositoryTest {

//    @Autowired
//    MemberRepository memberRepository;
//
//    @Test
//    @Transactional //db와 연관된 작업은 항상 트랜잭션 안에서 수행되어져야 한다.
//    @Rollback(value = false) //롤백 설정
//    public void testMember() throws Exception {
//        //given
//        Member member = new Member();
//        member.setUsername("seojio");
//
//        //when
//        memberRepository.save(member);
//        Member findMember = memberRepository.find(member.getId());
//
//        //then
//        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//
//        //jpa가 한 트랜잭션 안에서 같은 id값을 갖는 엔티티의 동일성 보장
//        Assertions.assertThat(findMember).isEqualTo(member);
//
//    }

}