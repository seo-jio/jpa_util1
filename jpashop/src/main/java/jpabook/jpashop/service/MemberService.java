package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true) //읽기가 많은 경우 read only true로 전체를 설정하고 write가 필요한 경우에 @Transactionl을 따로 붙여줌
@RequiredArgsConstructor //롬복에서 지원하는 final이 붙은 필드를 주입받는 생성자 생성 annotation
public class MemberService {

    private final MemberRepository memberRepository;

//    @Autowired
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

    //dirty checking을 사용하며 update만 수행하고 따로 member를 리턴하지 않는다.
    //커멘드와 퀴리를 분리하자!! => 내부에서 변경(사이드 이펙트)가 일어나는 메서드인지, 아니면 내부에서 변경이 전혀 일어나지 않는 메서드인지 명확히 분리
    //커맨드 : 객체의 내부 상태를 변경하지만 값을 반환하지 않는다(Setter)
    //쿼리 : 객체 내부 상태를 변경하지 않고 객체의 값을 반환하기만 한다.(Getter)
    //insert는 id만 반환하고(아무것도 없으면 조회가 안되니), update는 아무것도 반환하지 않고, 조회는 내부의 변경이 없는 메서드로 설계
    @Transactional
    public void update(Long id, String name) {
        Member findMember = memberRepository.findOne(id);
        findMember.setName(name);
    }
}
