package com.office.canteen.dto;

import com.office.canteen.domain.MealType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MealSelectionRequestDTO {

    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate mealDate;

    @NotNull
    private MealType mealType;

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
}
