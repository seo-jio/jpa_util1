package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //1. 엔티티를 직접 파라미터 바인딩하여 사용한 경우
    //엔티티와 api 스펙을 분리해야 한다.(엔티티 변경 시 side effect를 예방 할 수 있다)
    //api에서는 엔티티를 파라미터로 json 바인딩 써서 사용하면 안된다!(dto 객체를 만들어서 사용해야 한다.)
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //==api에서 엔티티가 아닌 dto를 따로 생성하서 사용하는 이유==//
    //1. 엔티티를 직접 파라미터 바인딩 하면 엔티티 변경 시 모든 api 스펙을 변경해야 한다. 그러나 dto 사용 시 엔티티를 변경하면 컴파일 오류가 떠서 이를 확인할 수 있다.
    //2. dto 사용시 개발자가 엔티티의 어느 필드를 request로 받는 지 확인할 수 있다. 엔티티 사용 시 어디까지 받는지 확인 X
    //3. @NotEmpty와 같은 제약조건을 엔티티가 아닌 dto에 걸어 엔티티를 여러 api에 맞도록 사용할 수 있다.

    //2. dto를 사용하여 엔티티와 api 스펙을 분리한 경우
    @PostMapping("api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //Put을 사용한 업데이트
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        //인자로 식별자만을 넘겨줌
        memberService.update(id, request.getName());

        //response로 잘 변경되었는지 변경한 멤버의 정보를 dto에 담아서 넘겨준다.
        Member member = memberService.findOne(id);
        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    //엔티티에서는 롬복 사용을 자제하지만(getter 정도) dto에서는 롬복을 자주 활용한다.
    //엔티티를 외부에 노출하지 않기 위해 별도의 dto 생성
    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
    }

    @Data //lombok의 getter,setter, toString 등 모든 걸 다 종합한 어노테이션
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
