package com.cydeo.enums;

public enum CompanyStatus {

    ACTIVE("Active"),PASSIVE("Passive");

    CompanyStatus(String value) {
        this.value = value;
    }

    private String value;


  public String getValue() {
      return value;
  }
}
