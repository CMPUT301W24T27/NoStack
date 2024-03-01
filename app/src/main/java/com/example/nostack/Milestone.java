public class Milestone {
    private String milestone_title;
    private Integer milestone_value;
    
    public String getMilestone_title() {
        return milestone_title;
    }

    public void setMilestone_title(String milestone_title) {
        this.milestone_title = milestone_title;
    }

    public Integer getMilestone_value() {
        return milestone_value;
    }

    public void setMilestone_value(String milestone_value) {
        this.milestone_value = milestone_value;
    }
    public Milestone(String milestone_title, LocalDateTime milestone_value) {
        this.milestone_title = milestone_title;
        this.milestone_value = milestone_value;
    }
}
