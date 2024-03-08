package com.example.nostack.utils;

public class Milestone {
    private String milestoneTitle;
    private Integer milestoneValue;
    
    public String getMilestoneTitle() {
        return milestoneTitle;
    }

    public void setMilestoneTitle(String milestoneTitle) {
        this.milestoneTitle = milestoneTitle;
    }

    public Integer getMilestoneValue() {
        return milestoneValue;
    }

    public void setMilestoneValue(Integer milestoneValue) {
        this.milestoneValue = milestoneValue;
    }
    public Milestone(String milestoneTitle, Integer milestoneValue) {
        this.milestoneTitle = milestoneTitle;
        this.milestoneValue = milestoneValue;
    }
}
