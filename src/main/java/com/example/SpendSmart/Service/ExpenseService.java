package com.example.SpendSmart.Service;

import com.example.SpendSmart.DTA.*;
import com.example.SpendSmart.Entity.Category;
import com.example.SpendSmart.Entity.Expense;
import com.example.SpendSmart.Reposotory.CategoryRepository;
import com.example.SpendSmart.Reposotory.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;


import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ExpenseService {
    private JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;


    public List<Double> getTotalExpensesForLast4Weeks(int userId, LocalDate startDate) {

        List<Double> weeklyExpenses = new ArrayList<>();
        double sum = 0;

        LocalDate firstWeekStart = startDate.minusDays(7);
        Double firstWeekTotal = expenseRepository.findTotalExpenseMoneyByUserAndDate(userId, firstWeekStart, startDate);
        double firstWeekExpense = (firstWeekTotal != null) ? firstWeekTotal : 0.0;
        weeklyExpenses.add(firstWeekExpense);
        sum += firstWeekExpense;

        for (int i = 1; i <= 3; i++) {
            LocalDate currentEndDate = firstWeekStart.minusDays(1).minusWeeks(i - 1);
            LocalDate currentStartDate = currentEndDate.minusDays(6); // Start of the week

            Double weeklyTotal = expenseRepository.findTotalExpenseMoneyByUserAndDate(userId, currentStartDate, currentEndDate);
            double weeklyExpense = (weeklyTotal != null) ? weeklyTotal : 0.0;

            weeklyExpenses.add(weeklyExpense);
            sum += weeklyExpense;
        }

        weeklyExpenses.add(sum);

        return weeklyExpenses;
    }





    public List<Map<String, Object>> getTotalExpensesForLast7days(int userId, String startDate) {
        List<Map<String, Object>> dailyIncome = new ArrayList<>();
        double sum = 0;
        LocalDate start = LocalDate.parse(startDate);  // Convert startDate string to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE"); // 3-letter day abbreviation

        for (int i = 0; i < 7; i++) {
            LocalDate date = start.minusDays(i);  // Calculate the date from startDate minus the number of days
            Double dailyTotal = expenseRepository.findTotalExpenseMoneyByUserAndDate(userId, date, date);
            sum += dailyTotal != null ? dailyTotal : 0.0;

            Map<String, Object> dayData = new HashMap<>();

            // Get the abbreviated day name (Mon, Tue, Wed, etc.)
            String dayOfWeek = date.format(formatter);  // Abbreviation (e.g., Mon, Tue, Wed)
            dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();  // Capitalize first letter

            dayData.put("date", dayOfWeek);  // Get the capitalized abbreviated day name
            dayData.put("expense", dailyTotal != null ? dailyTotal : 0.0);

            dailyIncome.add(dayData);
        }

        Map<String, Object> totalData = new HashMap<>();
        totalData.put("date", "Total");
        totalData.put("expense", sum);
        dailyIncome.add(totalData);

        return dailyIncome;
    }

    public List<Map<String, Object>> getTotalExpensesForLast6Months(int userId, LocalDate startDate) {
        List<Map<String, Object>> monthlyExpenses = new ArrayList<>();
        double sum = 0;

        // Loop through the last 6 months from the start date
        for (int i = 0; i < 6; i++) {  // 6 months (e.g., Jan, Feb, Mar, Apr...)
            LocalDate currentMonthStart = startDate.minusMonths(i);  // Start of current month
            LocalDate currentMonthEnd = currentMonthStart.plusMonths(1).minusDays(1); // End of current month

            Double monthlyTotal = expenseRepository.findTotalExpenseMoneyByUserAndDate(userId, currentMonthStart, currentMonthEnd);
            sum += monthlyTotal != null ? monthlyTotal : 0.0;

            Map<String, Object> monthData = new HashMap<>();
            // Get the abbreviated month name (e.g., Jan, Feb, Mar, etc.) and capitalize the first letter only
            String monthName = currentMonthStart.getMonth().toString().substring(0, 3);  // First three letters of month
            monthData.put("month", monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase());  // Capitalized abbreviation (e.g., Jan, Feb)
            monthData.put("expense", monthlyTotal != null ? monthlyTotal : 0.0);

            monthlyExpenses.add(monthData);
        }

        // Adding total expenses for the 6 months
        Map<String, Object> totalData = new HashMap<>();
        totalData.put("month", "Total");
        totalData.put("expense", sum);
        monthlyExpenses.add(totalData);

        return monthlyExpenses;
    }
    public Expense addExpense(Map<String, Object> payload) throws IllegalArgumentException {
        Expense expense = new Expense();

        String expenseTitle = (String) payload.get("expenseTitle");
        String normalizedTitle = expenseTitle.trim().toLowerCase();

        expense.setExpenseTitle(normalizedTitle);

        LocalDate date = LocalDate.parse(payload.get("date").toString());
        expense.setDate(date);

        String currencySymbol = (String) payload.get("currencySymbol");
        expense.setCurrencySymbol(currencySymbol);

        String description = (String) payload.get("text");
        expense.setText(description);

        Double expenseMoney = Double.parseDouble((String) payload.get("expenseMoney"));
        expense.setExpensemoney(expenseMoney);


        int userId = Integer.parseInt(payload.get("userId").toString());
        System.out.println("The user ID is: " + userId);


        String categoryName = (String) payload.get("categoryName");
        String normalizedTitln = categoryName.toLowerCase();
        expense.setCategoryName(normalizedTitln);

        Optional<Category> categoryOpt = categoryRepository.findByCategoryNameAndUserId(normalizedTitln, userId);
       if (categoryOpt.isPresent()) {
           expense.setCategory(categoryOpt.get());
           System.out.println("63: " + categoryOpt.get().getCategoryId() + " " + categoryOpt.get().getCategoryName() + "  " + expense.getExpenseTitle());


           Iterable<Expense> expensesOnDate = expenseRepository.findAllByDate(date);
           Optional<Expense> similarExpense = findSimilarExpense(expensesOnDate, normalizedTitle, date, expense.getCategory().getCategoryId());

           if (similarExpense.isPresent()) {
               Expense expensen = similarExpense.get();
               expensen.setExpensemoney(expensen.getExpensemoney() + expenseMoney);
               return expenseRepository.save(expensen);
           } else {
               return expenseRepository.save(expense);
           }
       }else{
           Category cat= categoryService.createCategory(normalizedTitln,userId);
           expense.setCategory(cat);

           return expenseRepository.save(expense);
       }
    }

    private Optional<Expense> findSimilarExpense(Iterable<Expense> expenses, String targetTitle, LocalDate targetDate, int targetCategoryId) {
        double threshold = 0.5;
        for (Expense expense : expenses) {
            double similarity = jaroWinkler.apply(expense.getExpenseTitle(), targetTitle);
            boolean sameDate = expense.getDate().isEqual(targetDate);
            boolean sameCategory;
            if (targetCategoryId == expense.getCategory().getCategoryId()) {
                sameCategory = true;
            } else {
                sameCategory = false;
            }
            if (similarity > threshold && sameDate && sameCategory) {
                return Optional.of(expense);
            }
        }
        return Optional.empty();
    }

    public Double getCurrentMonthExpenses(int userId) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return expenseRepository.findTotalExpenseMoneyByUserAndDate(userId, startDate, endDate);
    }

    public double getWeeklyExpense(int userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        return expenseRepository.findTotalExpenseMoneyByUserAndDate(userId, startOfWeek, endOfWeek);
    }

    public Double getWeeklyFoodExpenses(int userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        Double foodExpenses = expenseRepository.findWeeklyFoodExpenses(userId, startOfWeek, endOfWeek);
        return foodExpenses;
    }


    public List<Exoensefo> getExpenseOfMonth(int userId) {
        // Get the start and end date of the current month
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // Fetch expenses data for the given user in the current month
        List<Exoensefo> expenses = expenseRepository.findExpenseMoneyByUserAndDate(userId, startDate, endDate);

        // Capitalize the first letter of each expense title
        for (Exoensefo expense : expenses) {
            // Capitalize the first letter of the expense title
            expense.setExpenseTitle(capitalizeFirstLetter(expense.getExpenseTitle()));
        }

        return expenses;
    }


    @Async
    public CompletableFuture<List<MonthlyExpenseDTO>> getExpensesByCategoryAndUser(String categoryName, int userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Object[]> results = expenseRepository.findExpensesGroupedByMonth(categoryName, userId);

            Map<String, List<ExpenseForAll>> monthExpensesMap = new LinkedHashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (Object[] result : results) {
                String expenseTitle = (String) result[0];
                String categoryNameResult = (String) result[1];
                LocalDate localDate = (LocalDate) result[2];

                double expenseMoney = Double.parseDouble(result[3].toString());
                String text = (String) result[4];


                Date date = convertLocalDateToDate(localDate);

                String monthYear = new SimpleDateFormat("yyyy-MM").format(date);

                // Capitalize the first letter of the expense title
                expenseTitle = capitalizeFirstLetter(expenseTitle);

                ExpenseForAll expenseDetails = new ExpenseForAll(expenseTitle, categoryNameResult, sdf.format(date), expenseMoney,text);

                monthExpensesMap.computeIfAbsent(monthYear, k -> new ArrayList<>()).add(expenseDetails);
            }

            List<MonthlyExpenseDTO> monthlyExpenseDTOs = new ArrayList<>();
            for (Map.Entry<String, List<ExpenseForAll>> entry : monthExpensesMap.entrySet()) {
                String monthYearKey = entry.getKey();
                List<ExpenseForAll> expenseDetails = entry.getValue();

                String[] monthYearParts = monthYearKey.split("-");
                String year = monthYearParts[0];
                String month = monthYearParts[1];

                LocalDate validDate = LocalDate.parse(year + "-" + month + "-01");

                String monthName = validDate.getMonth().toString();
                monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

                monthlyExpenseDTOs.add(new MonthlyExpenseDTO(monthName, expenseDetails));
            }

            return monthlyExpenseDTOs;
        });
    }
    private Date convertLocalDateToDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public List<ExpenseForMonthlyCategory> getCategorySumsByMonthAndYearForUser(int userId) {
        List<Object[]> results = expenseRepository.findByCategory_User_Id(userId);

        Map<String, Map<String, Double>> monthCategorySumsMap = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Object[] result : results) {
            String categoryName = (String) result[1];
            LocalDate localDate = (LocalDate) result[2];
            double expenseMoney = Double.parseDouble(result[3].toString());

            Date date = convertLocalDateToDate(localDate);
            String monthYear = new SimpleDateFormat("yyyy-MM").format(date);

            monthCategorySumsMap.computeIfAbsent(monthYear, k -> new HashMap<>())
                    .merge(categoryName, expenseMoney, Double::sum);
        }

        List<ExpenseForMonthlyCategory> monthlyExpenseDTOs = new ArrayList<>();
        for (Map.Entry<String, Map<String, Double>> entry : monthCategorySumsMap.entrySet()) {
            String monthYearKey = entry.getKey();
            Map<String, Double> categorySums = entry.getValue();

            String[] monthYearParts = monthYearKey.split("-");
            String year = monthYearParts[0];
            String month = monthYearParts[1];

            LocalDate validDate = LocalDate.parse(year + "-" + month + "-01");

            String monthName = validDate.getMonth().toString();
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

            List<ExpenseForCategory> expenseDetails = new ArrayList<>();
            for (Map.Entry<String, Double> categoryEntry : categorySums.entrySet()) {
                String category = categoryEntry.getKey();
                Double sum = categoryEntry.getValue();

                expenseDetails.add(new ExpenseForCategory(category, sum));
            }

            monthlyExpenseDTOs.add(new ExpenseForMonthlyCategory(monthName, expenseDetails));
        }

        return monthlyExpenseDTOs;
    }

    public List<MonthlyExpenseDTO> getExpensesByUser(int userId) {
        // Fetch the grouped expenses for the user (without filtering by category)
        List<Object[]> results = expenseRepository.findExpensesByCategoryAndUserId(userId);

        // Map to hold the expenses grouped by month (in "yyyy-MM" format)
        Map<String, List<ExpenseForAll>> monthExpensesMap = new LinkedHashMap<>();

        // Date format to handle date conversion and formatting
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Process each result and map it to the corresponding month
        for (Object[] result : results) {
            String expenseTitle = (String) result[0];
            String categoryNameResult = (String) result[1];
            LocalDate localDate = (LocalDate) result[2];
            double expenseMoney = Double.parseDouble(result[3].toString());
            String text = (String) result[4];

            // Convert LocalDate to Date for proper formatting
            Date date = convertLocalDateToDate(localDate);

            // Generate the month-year string in "yyyy-MM" format
            String monthYear = new SimpleDateFormat("yyyy-MM").format(date);

            // Capitalize the first letter of the expense title (optional)
            expenseTitle = capitalizeFirstLetter(expenseTitle);

            // Create ExpenseForAll object to store the details
            ExpenseForAll expenseDetails = new ExpenseForAll(expenseTitle, categoryNameResult, sdf.format(date), expenseMoney, text);

            // Group expenses by the month-year key
            monthExpensesMap.computeIfAbsent(monthYear, k -> new ArrayList<>()).add(expenseDetails);
        }

        // List to store the MonthlyExpenseDTO objects
        List<MonthlyExpenseDTO> monthlyExpenseDTOs = new ArrayList<>();

        // Convert the map to the DTO structure
        for (Map.Entry<String, List<ExpenseForAll>> entry : monthExpensesMap.entrySet()) {
            String monthYearKey = entry.getKey();
            List<ExpenseForAll> expenseDetails = entry.getValue();

            // Split the monthYearKey to get year and month
            String[] monthYearParts = monthYearKey.split("-");
            String year = monthYearParts[0];
            String month = monthYearParts[1];

            // Create a valid LocalDate to get the month name
            LocalDate validDate = LocalDate.parse(year + "-" + month + "-01");

            // Get the month name, capitalize the first letter
            String monthName = validDate.getMonth().toString();
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

            // Add the constructed MonthlyExpenseDTO to the result list
            monthlyExpenseDTOs.add(new MonthlyExpenseDTO(monthName, expenseDetails));
        }

        return monthlyExpenseDTOs;
    }

    public String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;  // Return the original text if it's null or empty
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}