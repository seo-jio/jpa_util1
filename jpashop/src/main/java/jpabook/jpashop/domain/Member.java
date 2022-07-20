package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

//    @NotEmpty  //값을 필수로 받음, javax의 @Valid annotation으로 검증
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") //컬렉션은 필드에서 초기화 후 추후 변경 X, because 하이버네이트의 영속
    private List<Order> orders = new ArrayList<>();

}
