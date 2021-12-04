package by.innowisegroup.session.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;


    private String name;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName="id")
    @EqualsAndHashCode.Exclude
    private Author author;
}