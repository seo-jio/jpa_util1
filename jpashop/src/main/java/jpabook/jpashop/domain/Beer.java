package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Beer {

    @Id @GeneratedValue
    @Column(name = "beer_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "beer")
    private List<TasteEntity> tastes = new ArrayList<>();

    public void addTaste(TasteEntity tasteEntity){
        tastes.add(tasteEntity);
        tasteEntity.setBeer(this);
    }
}
