package com.office.canteen.repository;

import com.office.canteen.domain.MealSelection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealSelectionRepository
        extends JpaRepository<MealSelection, Long> {

    Optional<MealSelection> findByEmployeeIdAndMealDate(
            Long employeeId,
            LocalDate mealDate
    );

    List<MealSelection> findAllByEmployeeIdAndMealDateBetween(
            Long employeeId,
            LocalDate from,
            LocalDate to
    );

    List<MealSelection> findAllByMealDateBetweenOrderByMealDateAscEmployeeIdAsc(
            LocalDate from,
            LocalDate to
    );
}
