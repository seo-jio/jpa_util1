package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @RequestMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    //MemberForm에서 @NotEmpty를 사용하여 값을 무조건 채워주도록 한다.
    //@Valid가 @NotEmpty 조건을 확인한다
    //BindingResult를 통해 검증한 값에 에러(예외)의 유무를 확인한다.
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult bindingResult){
        if(bindingResult.hasErrors()){  //예외 발생 시 다시 createMemberForm으로 돌아간다.(이 경우 폼에 입력한 값들은 그대로 존재)
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }
}
