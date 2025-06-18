package com.casadabencao.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class MinisterioDto {
    private Long id;
    private String name;
    private String description;
    private String meetingDay;

    private List<Long> leaderIds;
    private List<String> leaderNames;

    private List<Long> viceLeaderIds;
    private List<String> viceLeaderNames;

    private List<String> activities;

    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String wall;

    public String getWall() {
        return wall;
    }

    public void setWall(String wall) {
        this.wall = wall;
    }
}
