package com.office.canteen.exception;

public class MealSelectionLockedException extends RuntimeException {

    public MealSelectionLockedException(String message) {
        super(message);
    }
}