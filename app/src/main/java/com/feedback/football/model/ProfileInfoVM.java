package com.feedback.football.model;


import com.google.gson.annotations.SerializedName;

public class ProfileInfoVM {

    @SerializedName("activeProfiles")
    private String[] activeProfiles;

    @SerializedName("ribbonEnv")
    private String ribbonEnv;

    ProfileInfoVM(String[] activeProfiles, String ribbonEnv) {
        this.activeProfiles = activeProfiles;
        this.ribbonEnv = ribbonEnv;
    }

    public ProfileInfoVM() {
    }

    public String[] getActiveProfiles() {
        return activeProfiles;
    }

    public String getRibbonEnv() {
        return ribbonEnv;
    }
}
