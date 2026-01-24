package com.office.canteen.controller;

import com.office.canteen.domain.MealSelection;
import com.office.canteen.domain.MealType;
import com.office.canteen.service.MealSelectionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/meals")
public class MealSelectionController {

    private final MealSelectionService service;

    public MealSelectionController(MealSelectionService service) {
        this.service = service;
    }

    @PostMapping
    public MealSelection selectMeal(
            @RequestParam Long employeeId,
            @RequestParam String mealDate,
            @RequestParam MealType mealType
    ) {
        return service.selectMeal(
                employeeId,
                LocalDate.parse(mealDate),
                mealType
        );
    }

    @GetMapping
    public MealSelection getMealSelection(
            @RequestParam Long employeeId,
            @RequestParam String mealDate
    ) {
        return service.getMealSelection(
                        employeeId,
                        LocalDate.parse(mealDate)
                )
                .orElseGet(() -> {
                    MealSelection ms = new MealSelection();
                    ms.setEmployeeId(employeeId);
                    ms.setMealDate(LocalDate.parse(mealDate));
                    ms.setMealType(MealType.NONE);
                    return ms;
                });
    }
}