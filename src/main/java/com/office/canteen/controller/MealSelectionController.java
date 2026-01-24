package com.office.canteen.controller;

import com.office.canteen.dto.MealSelectionDTO;
import com.office.canteen.mapper.MealSelectionMapper;
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
    public MealSelectionDTO selectMeal(@RequestBody MealSelectionDTO dto) {
        return MealSelectionMapper.toDto(
                service.selectMeal(
                        dto.getEmployeeId(),
                        dto.getMealDate(),
                        dto.getMealType()
                )
        );
    }

    @GetMapping
    public MealSelectionDTO getMealSelection(
            @RequestParam Long employeeId,
            @RequestParam LocalDate mealDate
    ) {
        return service.getMealSelection(employeeId, java.time.LocalDate.parse(mealDate))
                .map(MealSelectionMapper::toDto)
                .orElse(null);
    }
}