package ru.koldaev.entity;

import javax.persistence.*;
import java.util.*;

@Entity
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long authorId;
    private String authorName;
    private String testName;
    private Boolean isActive = false;
    //Список id вопросов для этого теста
    @ElementCollection
    @CollectionTable(name = "questionsList")
    @Column(name = "question")
    @OrderColumn
    protected List<Long> questions = new ArrayList<>();
    //Количество вариантов ответа на 1 вопрос
    private Integer countVariants;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<User> listUser = new HashSet<>();

    //(ID пользователя, Лучший результат в %)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resultsCurrentTest")
    @MapKeyColumn(name = "Test")
    @Column(name = "bestResult")
    private Map<Long, Integer> resultCurrentTest = new HashMap<>();

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getAuthorId() {
        return authorId;
    }
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    public List<Long> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Long> questions) {
        this.questions = questions;
    }
    public String getTestName() {
        return testName;
    }
    public void setTestName(String testName) {
        this.testName = testName;
    }
    public Integer getCountVariants() {
        return countVariants;
    }
    public void setCountVariants(Integer countVariants) {
        this.countVariants = countVariants;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }


    public Set<User> getListUser() {
        return listUser;
    }

    public void setListUser(Set<User> listUser) {
        this.listUser = listUser;
    }

    public Map<Long, Integer> gerResultCurrentTest() {
        return resultCurrentTest;
    }

    public void setResultCurrentTest(Map<Long, Integer> resultTests) {
        this.resultCurrentTest = resultTests;
    }
}
