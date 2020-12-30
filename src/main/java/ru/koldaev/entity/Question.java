package ru.koldaev.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String answer;
    @ElementCollection
    @CollectionTable(name = "answersVariant")
    @Column(name = "answers")
    @OrderColumn
    protected List<String> variantsAnswer = new ArrayList<String>();

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public List<String> getVariantsAnswer() {
        return variantsAnswer;
    }
    public void setVariantsAnswer(ArrayList<String> variantsAnswer) {
        this.variantsAnswer = variantsAnswer;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
