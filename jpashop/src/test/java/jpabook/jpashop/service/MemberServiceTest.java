package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.management.MemoryManagerMXBean;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //스프링과 함께 테스트
@SpringBootTest
@Transactional //테스트가 끝나면 롤백(db반영 X)
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
//    @Rollback(value = false) //rollback을 사용하지 않고 db에 반영
    public void 회원가입() throws Exception{
        Member member = new Member();
        member.setName("seojio");
        Long saveId = memberService.join(member);
        em.flush();  //insert query가 제대로 나가는지 확인하기 위하여 추가
        assertEquals(member, memberService.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class) //테스트시 발생할 예상 오류를 지정
    public void 중복_회원_예외() throws Exception{
        Member member1 = new Member();
        member1.setName("seojio");

        Member member2 = new Member();
        member2.setName("seojio");

        memberService.join(member1);
        memberService.join(member2);

        fail("예외가 발생해야 한다.");
    }
}