package com.example.SpendSmart.Service;


import com.example.SpendSmart.DTA.ExpenseForCategory;
import com.example.SpendSmart.DTA.ExpenseForMonthlyCategory;
import com.example.SpendSmart.DTA.IncomeInfo;
import com.example.SpendSmart.Entity.Expense;
import com.example.SpendSmart.Entity.Income;
import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Reposotory.CategoryRepository;
import com.example.SpendSmart.Reposotory.IncomeRepository;
import com.example.SpendSmart.Reposotory.UserRegisterRepostray;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IncomeService {

    private JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();

    @Autowired
    private IncomeRepository IncomeRepository;

    @Autowired
    private UserRegisterRepostray userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Income createIncome(Map<String, Object> payload) {
        Income income = new Income();
        int userId = Integer.parseInt(payload.get("userId").toString());
        Optional<RegiUser> user = userRepository.findById(userId);

        if (user.isPresent()) {

            String incomeTitle = (String) payload.get("incomeTitle");
            String normalizedTitle = incomeTitle.toLowerCase();
            income.setIncomeTitle(normalizedTitle);

            String category = (String) payload.get("category");
            income.setCategory(category);


            LocalDate date = LocalDate.parse(payload.get("date").toString());
            income.setDate(date);

            String currencySymbol = (String) payload.get("currencySymbol");
            income.setCurrencySymbol(currencySymbol);


            String description = (String) payload.get("text");
            income.setText(description);

            Double incomemoney = Double.parseDouble((String) payload.get("incomemoney"));
            income.setIncomemoney(incomemoney);

            income.setUser(user.get());


            Iterable<Income> expensesOnDate = IncomeRepository.findAllByDate(date);
            Optional<Income> similarIncome = findSimilarIncome(expensesOnDate, normalizedTitle,date,income.getIncomeId());
            if (similarIncome.isPresent()) {
                Income income1 = similarIncome.get();
                income1.setIncomemoney(income1.getIncomemoney() + incomemoney);
                return IncomeRepository.save(income1);
            } else {

                return IncomeRepository.save(income);


            }
        } else {
            return null;
        }
    }

    private Optional<Income> findSimilarIncome(Iterable<Income> incom,String targetTitle, LocalDate targetDate, int id) {
        double threshold = 0.5;
        for (Income income : incom) {
            double similarity = jaroWinkler.apply(income.getIncomeTitle(), targetTitle);
            boolean sameDate = income.getDate().isEqual(targetDate);
            boolean sameCategory;
            if (id==income.getIncomeId()) {
                sameCategory = true;
            } else {
                sameCategory = false;
            }
            if (similarity > threshold && sameDate && sameCategory) {
                return Optional.of(income);
            }
        }
        return Optional.empty();
    }

    public Double getCurrentMonthIncome(int userId) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return IncomeRepository.findTotalIncomeByUserAndDate(userId, startDate, endDate);
    }

    public double getWeeklyIncome(int userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        return IncomeRepository.findTotalIncomeByUserAndDate(userId, startOfWeek, endOfWeek);
    }

    public List<Double> getTotalIncomeForLast4Weeks(int userId) {
        List<Double> weeklyIncome = new ArrayList<>();
        double sum = 0;

        for (int i = 0; i < 4; i++) {
            LocalDate startDate = LocalDate.now().minusWeeks(i + 1);
            LocalDate endDate = startDate.plusWeeks(1);
            Double weeklyTotal = IncomeRepository.findTotalIncomeByUserAndDate(userId, startDate, endDate);
            sum = weeklyTotal + sum;
            weeklyIncome.add(weeklyTotal != null ? weeklyTotal : 0.0);
        }
        weeklyIncome.add(sum);
        Collections.reverse(weeklyIncome);
        return weeklyIncome;

    }

   public List<Map<String, Object>> getIncomeForLast7days(int userId) {
       List<Map<String, Object>> dailyIncome = new ArrayList<>();
       double sum = 0;
       LocalDate today = LocalDate.now();
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");

       for (int i = 6; i >= 0; i--) {
           LocalDate date = today.minusDays(i);
           Double dailyTotal = IncomeRepository.findTotalIncomeByUserAndDate(userId, date, date);
           sum = dailyTotal + sum;

           Map<String, Object> dayData = new HashMap<>();
           dayData.put("date", date.format(formatter)); // Get the day of the week (Monday, Tuesday, etc.)
           dayData.put("income", dailyTotal != null ? dailyTotal : 0.0);

           dailyIncome.add(dayData);
       }

       Map<String, Object> totalData = new HashMap<>();
       totalData.put("date", "Total");
       totalData.put("income", sum);
       dailyIncome.add(totalData);

       return dailyIncome;
   }
    public List<Double> getTotalIncomeForLast6Months(int userId) {
        List<Double> monthlyIncome = new ArrayList<>();
        double sum = 0;
        for (int i = 0; i < 6; i++) {
            LocalDate startDate = LocalDate.now().minusMonths(i + 1);
            LocalDate endDate = startDate.plusMonths(1);
            Double monthlyTotal = IncomeRepository.findTotalIncomeByUserAndDate(userId, startDate, endDate);
            sum = monthlyTotal + sum;
            monthlyIncome.add(monthlyTotal != null ? monthlyTotal : 0.0);
        }
        monthlyIncome.add(sum);
        Collections.reverse(monthlyIncome);
        return monthlyIncome;
    }

    public List<IncomeInfo> getSavingOfMonth(int userId) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // Fetch the savings data for the user in the current month
        List<IncomeInfo> savingsList = IncomeRepository.findSavingMoneyByUserAndDate(userId, startDate, endDate);

        // Capitalize the income title for each entry
        for (IncomeInfo income : savingsList) {
            // Apply capitalization to the income title if needed
            income.setIncomeTitle(capitalizeFirstLetter(income.getIncomeTitle()));
        }

        return savingsList;
    }



    public List<ExpenseForMonthlyCategory> getIncomeCategorySumsByMonthAndYearForUser(int userId) {
        List<Object[]> results = IncomeRepository.findByUserId(userId);

        Map<String, Map<String, Double>> monthCategorySumsMap = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Object[] result : results) {
            String categoryName = (String) result[1];
            LocalDate localDate = (LocalDate) result[2];
            double expenseMoney = Double.parseDouble(result[3].toString());

            // Capitalize the category name (or any other title like incomeTitle)
            categoryName = capitalizeFirstLetter(categoryName);

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

                // Capitalize category name if necessary
                category = capitalizeFirstLetter(category);

                expenseDetails.add(new ExpenseForCategory(category, sum));
            }

            monthlyExpenseDTOs.add(new ExpenseForMonthlyCategory(monthName, expenseDetails));
        }

        return monthlyExpenseDTOs;
    }

    private Date convertLocalDateToDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;  // Return the original text if it's null or empty
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}