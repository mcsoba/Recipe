package hu.nye.szakacskonyv;

public class Steps {
    String stepNum;
    String description;

    public Steps() {
    }

    public Steps(String stepNum, String description) {
        this.stepNum = stepNum;
        this.description = description;
    }

    public String getStepNum() {
        return stepNum;
    }

    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
