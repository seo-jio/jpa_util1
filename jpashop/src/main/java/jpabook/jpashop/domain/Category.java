package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    //jpa가 지원하는 다대다 매핑 사용 시 중간 테이블이 생성되는데, 중간 테이블에 컬럼을 추가할 수 없고 세밀하게 쿼리 실행이
    //어렵기 때문에 실무에서는 다대다 매핑을 사용하지 않는다. (일대다 + 다대다 매핑으로 풀어서 해결)
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    @ManyToOne  //카테고리 구조(자기 자신과 매핑)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //연관관계 메소드//
    public void addChildCategory(Category category){
        this.child.add(category);
        category.setParent(this);
    }
}
