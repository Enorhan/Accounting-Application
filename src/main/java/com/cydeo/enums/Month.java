package com.cydeo.enums;

public enum Month {
    JANUARY("January"),FEBRUARY("February"),MARCH("March"),APRIL("April"),MAY("May"),JUNE("June"),JULY("July"),AUGUST("August"),SEPTEMBER("September"),OCTOBER("October"),NOVEMBER("November"),DECEMBER("December");
    private String value;

    Month(String value) {
        this.value = value;
    }
}
