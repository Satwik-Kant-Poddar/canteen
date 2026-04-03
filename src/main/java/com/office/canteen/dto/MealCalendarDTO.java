package com.office.canteen.dto;

import com.office.canteen.domain.MealType;

import java.time.LocalDate;

public class MealCalendarDTO {

    private LocalDate date;
    private String mealType; // VEG, NON_VEG, NO_MEAL, NONE

    public MealCalendarDTO(LocalDate date, String mealType) {
        this.date = date;
        this.mealType = mealType;
    }

    public MealCalendarDTO() {
    }

    public LocalDate getDate() {
        return date;
    }

    public String getMealType() {
        return mealType;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType.toString();
    }
}