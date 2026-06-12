package com.cloud.runball.module.match_football_association.entity;

import java.util.List;

public class TeamGroupOption {

  private String title;

  private List<TeamOption> teamOptions;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<TeamOption> getTeamOptions() {
    return teamOptions;
  }

  public void setTeamOptions(List<TeamOption> teamOptions) {
    this.teamOptions = teamOptions;
  }
}
