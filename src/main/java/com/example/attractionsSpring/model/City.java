package com.example.attractionsSpring.model;

import javax.persistence.*;
import java.util.List;

@Entity(name = "cityDataTable")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;
    @Column
    private String name;
    @Column
    private int countOfAttractions;

    @OneToMany
    @JoinColumn(name = "attractionList")
    private List<Attraction> attractionList;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attraction> getAttractionList() {
        return attractionList;
    }

    public void setAttractionList(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    public int getCountOfAttractions() {
        return countOfAttractions;
    }

    public void setCountOfAttractions(int countOfAttractions) {
        this.countOfAttractions = countOfAttractions;
    }
}
