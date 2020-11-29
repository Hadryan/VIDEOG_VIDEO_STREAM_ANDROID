package com.gtechnologies.videog.Model;

import java.io.Serializable;

public class Banglalink implements Serializable {

    String mdn;
    String status;

    public Banglalink() {
    }

    public Banglalink(String mdn, String status) {
        this.mdn = mdn;
        this.status = status;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Banglalink{" +
                "mdn='" + mdn + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
