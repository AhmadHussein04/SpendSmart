package com.example.SpendSmart.Service;

import com.example.SpendSmart.DTA.*;
import com.example.SpendSmart.Entity.Category;
import com.example.SpendSmart.Entity.Savings;
import com.example.SpendSmart.Reposotory.CategoryRepository;
import com.example.SpendSmart.Reposotory.SavingRepository;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.lang.Double.parseDouble;

@Service
public class SavingsService {

    private JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();

    @Autowired
    private SavingRepository savingsRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    public Savings saveSaving(Map<String, Object> payload) {
        Savings saving = new Savings();

        String expenseTitle = (String) payload.get("expenseTitle");
        String normalizedTitle = expenseTitle.toLowerCase();
        saving.setExpenseTitle(normalizedTitle);


        LocalDate date = LocalDate.parse(payload.get("date").toString());
        saving.setDate(date);

        String currencySymbol = (String) payload.get("currencySymbol");
        saving.setCurrencySymbol(currencySymbol);


        String description = (String) payload.get("text");
        saving.setText(description);

        Double savingmoney= parseDouble((String) payload.get("savingmoney"));
        saving.setSavingmoney(savingmoney);

        Double amountgoalouble=parseDouble((String) payload.get("goalamount"));
        saving.setGoalamount(amountgoalouble);

        int userId = Integer.parseInt(payload.get("userId").toString());

        String categoryName = (String) payload.get("categoryName");
        String normalizedTitln = categoryName.toLowerCase();

        saving.setCategoryName(normalizedTitln);
        Optional<Category> categoryOpt = categoryRepository.findByCategoryNameAndUserId(normalizedTitln, userId);
        if (categoryOpt.isPresent()) {
            saving.setCategory(categoryOpt.get());

            Iterable<Savings> SavingOnDate = savingsRepository.findAllByDate(date);
            Optional<Savings> similarSavings= findSimilarExpense(SavingOnDate, normalizedTitle,date,saving.getCategory().getCategoryId());
            if (similarSavings.isPresent()) {
                Savings sav = similarSavings.get();
                sav.setSavingmoney(sav.getSavingmoney() + savingmoney);
                return savingsRepository.save(sav);
            } else {
                return savingsRepository.save(saving);

            }
        }else{
            Category cat= categoryService.createCategory(normalizedTitln,userId);
            saving.setCategory(cat);

            return savingsRepository.save(saving);        }

    }

    public Double getCurrentMonthSavings(int userId) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return savingsRepository.findTotalSavingsByUserAndDate(userId, startDate, endDate);
    }

    public double getWeeklySvings(int userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        return savingsRepository.findTotalSavingsByUserAndDate(userId, startOfWeek, endOfWeek);
    }
    private Optional<Savings> findSimilarExpense(Iterable<Savings> savings, String targetTitle, LocalDate targetDate, int targetCategoryId) {
        double threshold = 0.5;
        for (Savings sav : savings) {
            double similarity = jaroWinkler.apply(sav.getExpenseTitle(), targetTitle);
            boolean sameDate = sav.getDate().isEqual(targetDate);
            boolean sameCategory;
            if(targetCategoryId==sav.getCategory().getCategoryId()){
                sameCategory=true;
            }else{
                sameCategory=false;

            }

            if (similarity > threshold && sameDate && sameCategory) {
                return Optional.of(sav);
            }
        }
        return Optional.empty();
    }

    @Async
    public CompletableFuture<List<MonthlySavingDTO>> getSavingByCategoryAndUser(String categoryName, int userId) {
        return CompletableFuture.supplyAsync(() -> {

            List<Object[]> results = savingsRepository.findSavingGroupedByMonth(categoryName, userId);

            Map<String, List<SavingForAll>> monthExpensesMap = new LinkedHashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            double sum = 0;
            for (Object[] result : results) {
                double goalamount = Double.parseDouble(result[4].toString());
                String expenseTitle = (String) result[0];
                String categoryNameResult = (String) result[1];
                LocalDate localDate = (LocalDate) result[2];
                double savingMoney = Double.parseDouble(result[3].toString());
                sum = savingsRepository.findtotalsavings(categoryName, userId);

                Date date = convertLocalDateToDate(localDate);

                String monthYear = new SimpleDateFormat("yyyy-MM").format(date);


                SavingForAll savingDetails = new SavingForAll(expenseTitle, categoryNameResult, sdf.format(date), savingMoney, goalamount);

                monthExpensesMap.computeIfAbsent(monthYear, k -> new ArrayList<>()).add(savingDetails);
            }

            List<MonthlySavingDTO> monthlySavingDTOS = new ArrayList<>();
            for (Map.Entry<String, List<SavingForAll>> entry : monthExpensesMap.entrySet()) {
                String monthYearKey = entry.getKey();
                List<SavingForAll> savingDetails = entry.getValue();

                String[] monthYearParts = monthYearKey.split("-");
                String year = monthYearParts[0];
                String month = monthYearParts[1];

                LocalDate validDate = LocalDate.parse(year + "-" + month + "-01");

                String monthName = validDate.getMonth().toString();
                monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

                monthlySavingDTOS.add(new MonthlySavingDTO(monthName, savingDetails, sum));
            }

            return monthlySavingDTOS;
        });
    }

    private Date convertLocalDateToDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}

