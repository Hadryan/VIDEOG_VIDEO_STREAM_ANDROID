package com.gtechnologies.videog.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionHistory {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("plan")
    @Expose
    private String plan;
    @SerializedName("activation_date")
    @Expose
    private String activationDate;
    @SerializedName("deactivation_date")
    @Expose
    private String deactivationDate;
    @SerializedName("present_status")
    @Expose
    private String presentStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(String deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public String getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(String presentStatus) {
        this.presentStatus = presentStatus;
    }

}