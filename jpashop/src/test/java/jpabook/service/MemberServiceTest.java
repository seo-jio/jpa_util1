package jpabook.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;


    public void 회원가입() throws Exception{
        Member member = new Member();
        member.setName("seojio");
        Long saveId = memberService.join(member);
        assertEquals(member, memberService.findOne(saveId));
    }
}