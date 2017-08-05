package com.dewcis.mdss.model;

/**
 * Created by Arwin Kish on 12/12/2016.
 */
public class Household {
    private String name;
    private String content_desc;
    private int numOfMember;
    private int survey;
    private int type;


    public Household(int survey, String name, int numOfMember, String content_desc, int type) {
        this.survey = survey;
        this.name = name;
        this.numOfMember = numOfMember;
        this.content_desc = content_desc;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfMember() {
        return numOfMember;
    }

    public void setNumOfMember(int numOfMember) {
        this.numOfMember = numOfMember;
    }

    public String getContent() {
        return content_desc;
    }

    public void setContent(String content_desc) {
        this.content_desc = content_desc;
    }

    public int getSurvey() {
        return survey;
    }

    public void setSurvey(int survey) {
        this.survey = survey;
    }

    public int getType(){
        return this.type;
    }
    public void setType(int type){
        this.type = type;
    }

}
