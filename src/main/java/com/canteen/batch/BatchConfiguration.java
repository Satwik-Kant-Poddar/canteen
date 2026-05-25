package com.canteen.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * The core configuration class for the Spring Batch Job.
 * This class strings together the Reader, Processor, and Writer.
 */
@Configuration
public class BatchConfiguration {

    // --- 1. READER ---
    @Bean
    public ItemReader<Meal> reader() {
        // FlatFileItemReader is a standard reader pattern for Spring Batch to read text/csv files
        return new FlatFileItemReaderBuilder<Meal>()
                .name("mealItemReader")
                .resource(new ClassPathResource("meals.csv")) // our source file
                .delimited() // Indicates format is delimited (comma by default)
                .names("id", "name", "category", "calories") // The headers to map
                .linesToSkip(1) // Skip the first line since it holds header text
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Meal>() {{
                    // This maps the delimited fields straight onto the 'Meal' class properties automatically
                    setTargetType(Meal.class);
                }})
                .build();
    }

    // --- 2. PROCESSOR ---
    @Bean
    public MealItemProcessor processor() {
        // Instantiate the processor class we created
        return new MealItemProcessor();
    }

    // --- 3. WRITER ---
    @Bean
    public ItemWriter<Meal> writer() {
        // We'll use a simple Lambda-based ItemWriter that dumps output into standard out/console.
        // It receives a 'Chunk' (a List) of processed Items.
        return chunk -> {
            System.out.println("--- WRITING CHUNK ---");
            for (Meal meal : chunk) {
                System.out.println("Processing completed for writing context: " + meal);
            }
        };
    }

    // --- 4. STEP CONFIGURATION ---
    /**
     * A Job consists of one or more Steps.
     * This step defines that it reads 3 items at a time (chunk = 3).
     * It uses the reader, processor, and writer defined above.
     */
    @Bean
    public Step processMealsStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 ItemReader<Meal> reader,
                                 MealItemProcessor processor,
                                 ItemWriter<Meal> writer) {
        return new StepBuilder("processMealsStep", jobRepository)
                .<Meal, Meal> chunk(3, transactionManager) // Chunk defines how many items are processed before a transaction is committed
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // --- 5. JOB CONFIGURATION ---
    /**
     * The actual Job definition. 
     * It ties the Steps together in a sequence. 
     */
    @Bean
    public Job importUserJob(JobRepository jobRepository,
                             Step processMealsStep) {
        return new JobBuilder("importMealJob", jobRepository)
                .start(processMealsStep) // execution starts at the step we defined
                // .next(someOtherStep) // Can chain multiple steps
                .build();
    }
}
