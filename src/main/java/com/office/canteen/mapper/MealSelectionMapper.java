package com.office.canteen.mapper;

import com.office.canteen.domain.MealSelection;
import com.office.canteen.dto.MealSelectionDTO;

import java.time.LocalDate;

public class MealSelectionMapper {

    private MealSelectionMapper() {
        // prevent instantiation
    }

    public static MealSelectionDTO toDto(MealSelection entity) {
        MealSelectionDTO dto = new MealSelectionDTO();
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setMealDate(entity.getMealDate());
        dto.setMealType(entity.getMealType());
        return dto;
    }

    public static MealSelectionDTO empty(
            Long employeeId,
            LocalDate mealDate
    ) {
        MealSelectionDTO dto = new MealSelectionDTO();
        dto.setEmployeeId(employeeId);
        dto.setMealDate(mealDate);
        dto.setMealType(null); // NONE
        return dto;
    }
}