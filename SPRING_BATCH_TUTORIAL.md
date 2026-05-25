# Spring Batch Core Concepts: A Canteen Data Example

Welcome to the Spring Batch learning project! This application is designed to demonstrate how batch processing flows using a relatable domain model: **Daily Canteen Meals**.

Spring Batch handles the reliable processing of large volumes of records (reading them, analyzing/transforming them, and writing them out). 

## 1. What makes up a Batch Job?

### The Job (`importMealJob`)
A `Job` is an entity that encapsulates an entire batch process. It is simply a container for `Steps`. 

### The Step (`processMealsStep`)
A `Job` has one or more `Step`s. A `Step` is a domain object that encapsulates an independent, sequential phase of a batch job. Every `Step` typically has an `ItemReader`, `ItemProcessor`, and `ItemWriter`.

> [!NOTE]
> Have a look at `BatchConfiguration.java`! You will see exactly how the `Job` Bean links to the `Step` Bean.

## 2. The Core Processing Triad

Spring Batch processes data via "chunk-based" processing. It reads data one by one, processes it one by one, but writes it in a *chunk* (a bulk list). Our project defines a chunk size of `3`.

### 1. ItemReader (`FlatFileItemReaderBuilder`)
- *Definition:* Responsible for providing data from a source, one item at a time.
- *In Our Code:* We use a built-in `FlatFileItemReader` to read from `src/main/resources/meals.csv`. It perfectly reads lines, skips headers, and applies the text fields directly into our `Meal.java` POJO using reflection (`BeanWrapperFieldSetMapper`).

### 2. ItemProcessor (`MealItemProcessor.java`)
- *Definition:* Represents the business processing of an item. The processor acts upon items *after* they are read but *before* they are written.
- *In Our Code:* Look at `MealItemProcessor.java`. It takes a `Meal`, takes the `Category` string, and forces it to be UPPERCASE. It then returns a transformed `Meal`. If a processor returns `null`, the item is discarded and not written.

### 3. ItemWriter (`ItemWriter<Meal>`)
- *Definition:* Writes the output of the processor to a destination. Notice how it takes a `List` of items at once? This is for optimization.
- *In Our Code:* We define a simple lambda function in `BatchConfiguration.java`. Once `3` (our defined chunk size) items are processed, they are sent here and we print them to the application output log.

---

## 3. The Metadata Repository
Spring Batch is robust because it tracks everything in a database. If your batch process fails on record 400 out of 1000, Spring Batch records the failure state in the database. When you restart the job, it picks up exactly at record 400!

In our application, we configured an in-memory database (`H2`) via `application.properties`. When you start the app, Spring Batch automatically boots up its required tables into H2!

## 4. Let's Run It!

Since you are running this from IntelliJ:

1. **Wait for Maven Sync:** Ensure IntelliJ finishes downloading the dependencies specified in the `pom.xml`. The Spring Boot plugin might take a moment.
2. **Execute Application:** Right-click inside `BatchApplication.java` and select `Run 'BatchApplication.main()'`.
3. **Check the Output:** Watch your debug console. You will see:
   - Spring Boot booting up.
   - H2 starting.
   - Spring Batch executing `importMealJob`.
   - The logger from our `MealItemProcessor` showing transformations.
   - Our `writer()` printing out `--- WRITING CHUNK ---` in blocks of three!

Happy learning! Feel free to modify `meals.csv`, change the chunk sizes, or add a second Step to practice!
