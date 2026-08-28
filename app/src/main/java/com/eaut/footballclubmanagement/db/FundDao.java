package com.eaut.footballclubmanagement.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FundDao {
    @Insert
    void insert(FundTransaction transaction);

    @Query("SELECT * FROM fund_transactions ORDER BY id DESC")
    List<FundTransaction> getAllTransactions();

    @Query("SELECT SUM(CASE WHEN isIncome = 1 THEN amount ELSE -amount END) FROM fund_transactions")
    Integer getTotalBalance();
}
