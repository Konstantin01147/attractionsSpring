package com.example.attractionsSpring.model;

import javax.persistence.*;

@Entity
@Table(name = "attraction")
public class Attraction {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @Column
    private String attractionName;
    @Column
    private String info;
    @Column
    private Long cityId;

    public Attraction(Long id, String attractionName, String info, Long cityId) {
        Id = id;
        this.attractionName = attractionName;
        this.info = info;
        this.cityId = cityId;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}
