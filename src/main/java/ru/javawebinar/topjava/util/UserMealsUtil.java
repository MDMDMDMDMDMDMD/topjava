package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> mapDatesCaloricSum = new HashMap<>();
        for (UserMeal meal : meals) {
            mapDatesCaloricSum.put(meal.getDateTime().toLocalDate(), mapDatesCaloricSum.getOrDefault(meal.getDateTime().toLocalDate(), 0) + meal.getCalories());
        }

        List<UserMealWithExcess> mealExceeded = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealExceeded.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), mapDatesCaloricSum.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }

        return mealExceeded;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> caloriesSumByDate = meals.stream().collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate()
                , Collectors.summingInt(UserMeal::getCalories)));

        return         meals.stream()
                .filter(um->TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(),startTime,endTime))
                .map(um->new UserMealWithExcess(um.getDateTime(),um.getDescription(),um.getCalories()
                        ,caloriesSumByDate.get(um.getDateTime().toLocalDate())>caloriesPerDay))
                .collect(Collectors.toList());
    }
}
