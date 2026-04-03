package com.office.canteen.controller;

import com.office.canteen.dto.MealCalendarDTO;
import com.office.canteen.dto.MealRangeRequestDTO;
import com.office.canteen.dto.MealSelectionDTO;
import com.office.canteen.dto.MealSelectionRequestDTO;
import com.office.canteen.mapper.MealSelectionMapper;
import com.office.canteen.service.MealSelectionService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealSelectionController {

    private final MealSelectionService service;

    public MealSelectionController(MealSelectionService service) {
        this.service = service;
    }

    @PostMapping
    public MealSelectionDTO selectMeal(
            @Valid @RequestBody MealSelectionDTO dto) {
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
        return service.getMealSelection(employeeId, mealDate)
                .map(MealSelectionMapper::toDto)
                .orElseGet(() -> MealSelectionMapper.empty(employeeId,mealDate));
    }

    @GetMapping("/range")
    public List<MealCalendarDTO> getMealsInRange(
            @RequestParam Long employeeId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {
        return service.getMealSelectionsInRange(
                employeeId,
                fromDate,
                toDate
        );
    }

    @Transactional
    @PostMapping("/bulk")
    public void selectMealsBulk(
            @Valid @RequestBody List<MealSelectionRequestDTO> requests
    ) {
        requests.forEach(req ->
                service.selectMeal(
                        req.getEmployeeId(),
                        req.getMealDate(),
                        req.getMealType()
                )
        );
    }

    @PostMapping("/range")
    public void selectMealsForRange(
            @Valid @RequestBody MealRangeRequestDTO dto
    ) {
        service.selectMealForRange(
                dto.getEmployeeId(),
                dto.getFromDate(),
                dto.getToDate(),
                dto.getMealType()
        );
    }
}