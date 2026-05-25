package com.canteen.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * The 'Processor' phase of Spring Batch.
 * 
 * After an item is read by the ItemReader, it's passed here. 
 * An ItemProcessor transforms the input object into the output object.
 * In this case, both input and output are 'Meal', meaning we simply modify the existing object.
 * 
 * If you need to filter an item (i.e., you don't want it written out), 
 * you can simply return null from the process() method!
 */
public class MealItemProcessor implements ItemProcessor<Meal, Meal> {

    private static final Logger log = LoggerFactory.getLogger(MealItemProcessor.class);

    @Override
    public Meal process(final Meal meal) throws Exception {
        // Example Transformation Logic:
        // We want to uppercase the meal category for consistency
        String originalCategory = meal.getCategory();
        String transformedCategory = originalCategory != null ? originalCategory.toUpperCase() : "UNKNOWN";

        // We use the same object but modify its properties. 
        // Best practice in strict functional systems might be creating a new instance, 
        // but mutating the passed instance is common and acceptable for simple POJOs.
        final Meal transformedMeal = new Meal(meal.getId(), meal.getName(), transformedCategory, meal.getCalories());

        // We log the transformation so we can see it working when the app runs
        log.info("Converting (" + meal + ") into (" + transformedMeal + ")");

        return transformedMeal;
    }
}
