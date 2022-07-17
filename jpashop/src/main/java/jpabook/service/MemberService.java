package jpabook.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//롬복에서 지원하는 final이 붙은 필드를 주입받는 생성자 생성 annotation
@RequiredArgsConstructor
//읽기가 많은 경우 read only true로 전체를 설정하고 write가 필요한 경우에 @Transactionl을 따로 붙여줌
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

//    public MemberService(MemberRepository memberRepository){
//        this.memberRepository = memberRepository;
//    }
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> result = memberRepository.findByName(member.getName());
        if(!result.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

}
