package com.canteen.batch;

/**
 * A standard POJO (Plain Old Java Object) to represent our domain model.
 * In Spring Batch, this is often used to map rows from a CSV file into Java objects.
 * 
 * For instance, our meals.csv has: id, name, category, calories
 */
public class Meal {

    private String id;
    private String name;
    private String category;
    private int calories;

    public Meal() {
        // Default constructor is required by Spring Batch's BeanWrapperFieldSetMapper
        // to instantiate the object via reflection before setting properties.
    }

    public Meal(String id, String name, String category, int calories) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.calories = calories;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", calories=" + calories +
                '}';
    }
}
