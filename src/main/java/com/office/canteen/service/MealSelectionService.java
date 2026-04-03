package com.office.canteen.service;

import com.office.canteen.domain.MealSelection;
import com.office.canteen.domain.MealType;
import com.office.canteen.dto.MealCalendarDTO;
import com.office.canteen.dto.MealSelectionDTO;
import com.office.canteen.exception.MealSelectionLockedException;
import com.office.canteen.mapper.MealSelectionMapper;
import com.office.canteen.repository.MealSelectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MealSelectionService {

    private final MealSelectionRepository repository;

    public MealSelectionService(MealSelectionRepository repository) {
        this.repository = repository;
    }



    public MealSelection selectMeal(Long employeeId,
                                    LocalDate mealDate,
                                    MealType mealType) {
        LocalDate today = LocalDate.now();
        if (mealDate.isBefore(today)) {
            throw new IllegalArgumentException(
                    "Meal date cannot be in the past"
            );
        }

        LocalDate lockDate = today.plusDays(7);
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

    public List<MealCalendarDTO> getMealSelectionsInRange(
            Long employeeId,
            LocalDate from,
            LocalDate to
    ) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("fromDate cannot be after toDate");
        }

        List<MealSelection> selections =
                repository.findAllByEmployeeIdAndMealDateBetween(
                        employeeId, from, to
                );

        Map<LocalDate, MealSelection> selectionByDate =
                selections.stream()
                        .collect(Collectors.toMap(
                                MealSelection::getMealDate,
                                Function.identity()
                        ));

        List<MealCalendarDTO> result = new ArrayList<>();

        LocalDate date = from;
        while (!date.isAfter(to)) {

            MealSelection selection = selectionByDate.get(date);

            String mealType = (selection == null)
                    ? "NONE"                      // no record
                    : selection.getMealType().name();

            result.add(new MealCalendarDTO(date, mealType));
            date = date.plusDays(1);
        }

        return result;
    }

    @Transactional
    public void selectMealForRange(
            Long employeeId,
            LocalDate from,
            LocalDate to,
            MealType mealType
    ) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("fromDate cannot be after toDate");
        }

        LocalDate date = from;

        while (!date.isAfter(to)) {
            selectMeal(employeeId, date, mealType);
            date = date.plusDays(1);
        }
    }
}