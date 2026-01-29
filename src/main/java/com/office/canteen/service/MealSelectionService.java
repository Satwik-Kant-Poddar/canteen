package com.office.canteen.service;

import com.office.canteen.domain.MealSelection;
import com.office.canteen.domain.MealType;
import com.office.canteen.dto.MealSelectionDTO;
import com.office.canteen.exception.MealSelectionLockedException;
import com.office.canteen.mapper.MealSelectionMapper;
import com.office.canteen.repository.MealSelectionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MealSelectionService {

    private final MealSelectionRepository repository;

    public MealSelectionService(MealSelectionRepository repository) {
        this.repository = repository;
    }



    public MealSelection selectMeal(Long employeeId,
                                    LocalDate mealDate,
                                    MealType mealType) {
        if (mealDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Meal date cannot be in the past"
            );
        }

        LocalDate lockDate = LocalDate.now().plusDays(7);
        if (!mealDate.isAfter(lockDate)) {
            throw new MealSelectionLockedException(
                    "Meal selection is locked 7 days before the date"
            );
        }

        MealSelection selection =
                repository.findByEmployeeIdAndMealDate(employeeId, mealDate)
                        .orElseGet(() -> {
                            MealSelection ms = new MealSelection();
                            ms.setEmployeeId(employeeId);
                            ms.setMealDate(mealDate);
                            return ms;
                        });

        selection.setMealType(
                mealType == null ? MealType.NO_MEAL : mealType
        );
        return repository.save(selection);
    }

    public Optional<MealSelection> getMealSelection(
            Long employeeId,
            LocalDate mealDate
    ) {
        return repository.findByEmployeeIdAndMealDate(employeeId, mealDate);
    }

    public List<MealSelectionDTO> getMealSelectionsInRange(
            Long employeeId,
            LocalDate from,
            LocalDate to
    ) {
        List<MealSelectionDTO> result = new ArrayList<>();

        LocalDate date = from;
        while (!date.isAfter(to)) {

            Optional<MealSelection> selection =
                    repository.findByEmployeeIdAndMealDate(employeeId, date);

            MealSelectionDTO dto = selection
                    .map(MealSelectionMapper::toDto)
                    .orElse(MealSelectionMapper.empty(employeeId, date));

            result.add(dto);
            date = date.plusDays(1);
        }

        return result;
    }

    public void selectMealForRange(
            Long employeeId,
            LocalDate from,
            LocalDate to,
            MealType mealType
    ) {
        LocalDate date = from;

        while (!date.isAfter(to)) {
            selectMeal(employeeId, date, mealType);
            date = date.plusDays(1);
        }
    }
}