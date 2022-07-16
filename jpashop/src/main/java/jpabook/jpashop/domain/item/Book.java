package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
//@DiscriminatorValue("B") //구분자 지정
@Getter @Setter
public class Book extends Item{
    private String artist;
    private String etc;
}