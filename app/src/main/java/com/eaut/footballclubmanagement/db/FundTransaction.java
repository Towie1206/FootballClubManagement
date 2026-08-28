package com.eaut.footballclubmanagement.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fund_transactions")
public class FundTransaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String reason;
    public int amount;
    public boolean isIncome;
    public String timestamp;

    public FundTransaction(String reason, int amount, boolean isIncome, String timestamp) {
        this.reason = reason;
        this.amount = amount;
        this.isIncome = isIncome;
        this.timestamp = timestamp;
    }
}
