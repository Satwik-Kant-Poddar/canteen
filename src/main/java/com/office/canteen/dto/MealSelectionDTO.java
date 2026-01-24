package com.office.canteen.dto;

import com.office.canteen.domain.MealType;

import java.time.LocalDate;

public class MealSelectionDTO {
    private Long employeeId;
    private LocalDate mealDate;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    private MealType mealType;

}
