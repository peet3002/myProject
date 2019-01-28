package com.example.peetp.myproject.model;

public class Static {
    private Integer study_problem, personal_problem, occupation_problem;

    public Static() {
    }

    public Static(Integer study_problem, Integer personal_problem, Integer occupation_problem) {
        this.study_problem = study_problem;
        this.personal_problem = personal_problem;
        this.occupation_problem = occupation_problem;
    }

    public Integer getStudy_problem() {
        return study_problem;
    }

    public void setStudy_problem(Integer study_problem) {
        this.study_problem = study_problem;
    }

    public Integer getPersonal_problem() {
        return personal_problem;
    }

    public void setPersonal_problem(Integer personal_problem) {
        this.personal_problem = personal_problem;
    }

    public Integer getOccupation_problem() {
        return occupation_problem;
    }

    public void setOccupation_problem(Integer occupation_problem) {
        this.occupation_problem = occupation_problem;
    }
}
