package com.eaut.footballclubmanagement;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eaut.footballclubmanagement.db.AppDatabase;
import com.eaut.footballclubmanagement.db.FundDao;
import com.eaut.footballclubmanagement.db.FundTransaction;
import com.eaut.footballclubmanagement.utils.SecurityUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FundActivity extends AppCompatActivity {

    private TextView tvTotalBalance;
    private RecyclerView recyclerViewFund;
    private FundAdapter adapter;
    private List<FundTransaction> transactions = new ArrayList<>();
    private SharedPreferences prefs;
    private FundDao fundDao;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund);

        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        fundDao = AppDatabase.getDatabase(this).fundDao();
        executorService = Executors.newSingleThreadExecutor();
        
        // Cài đặt mã PIN mặc định (đã được băm SHA-256) nếu chưa có
        // "123456" = 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
        if (!prefs.contains("admin_pin_hash")) {
            String defaultHash = SecurityUtils.hashSHA256("123456");
            prefs.edit().putString("admin_pin_hash", defaultHash).apply();
        }

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        recyclerViewFund = findViewById(R.id.recyclerViewFund);
        recyclerViewFund.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FundAdapter(transactions);
        recyclerViewFund.setAdapter(adapter);

        findViewById(R.id.btnIncome).setOnClickListener(v -> showPinDialog(true));
        findViewById(R.id.btnExpense).setOnClickListener(v -> showPinDialog(false));

        loadTransactions();
    }

    private void showPinDialog(boolean isIncome) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🔐 Xác thực Quyền Thủ Quỹ");
        builder.setMessage("Vui lòng nhập mã PIN Admin để thực hiện giao dịch.");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText etPin = new EditText(this);
        etPin.setHint("Nhập mã PIN 6 số");
        etPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        layout.addView(etPin);

        builder.setView(layout);
        builder.setPositiveButton("XÁC NHẬN", (dialog, which) -> {
            String enteredPin = etPin.getText().toString();
            String enteredHash = SecurityUtils.hashSHA256(enteredPin);
            String savedHash = prefs.getString("admin_pin_hash", "");
            
            // So sánh 2 mã băm (Tuyệt đối không lưu và so sánh chuỗi thô)
            if (enteredHash.equals(savedHash)) {
                showTransactionDialog(isIncome);
            } else {
                android.widget.Toast.makeText(FundActivity.this, "Mã PIN sai! Từ chối quyền truy cập.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("HỦY", null);
        builder.show();
    }

    private void loadTransactions() {
        // Tác vụ I/O Database KHÔNG ĐƯỢC chạy trên Main Thread
        executorService.execute(() -> {
            List<FundTransaction> dbList = fundDao.getAllTransactions();
            Integer balanceObj = fundDao.getTotalBalance();
            int balance = (balanceObj != null) ? balanceObj : 0;

            // Cập nhật UI trên Main Thread
            runOnUiThread(() -> {
                transactions.clear();
                transactions.addAll(dbList);
                adapter.notifyDataSetChanged();

                DecimalFormat formatter = new DecimalFormat("#,###");
                tvTotalBalance.setText(formatter.format(balance) + " ₫");
                
                if (balance < 0) {
                    tvTotalBalance.setTextColor(getResources().getColor(R.color.phui_error));
                } else {
                    tvTotalBalance.setTextColor(getResources().getColor(R.color.phui_accent));
                }
            });
        });
    }

    private void showTransactionDialog(boolean isIncome) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isIncome ? "THU QUỸ" : "CHI QUỸ");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        EditText etReason = new EditText(this);
        etReason.setHint("Lý do (VD: Trà đá, Thu tiền sân)");
        layout.addView(etReason);

        EditText etAmount = new EditText(this);
        etAmount.setHint("Số tiền (VNĐ)");
        etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etAmount);

        builder.setView(layout);
        builder.setPositiveButton("LƯU", (dialog, which) -> {
            String reason = etReason.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            if (!reason.isEmpty() && !amountStr.isEmpty()) {
                int amount = Integer.parseInt(amountStr);
                String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                
                FundTransaction newTx = new FundTransaction(reason, amount, isIncome, date);
                
                executorService.execute(() -> {
                    fundDao.insert(newTx);
                    loadTransactions(); // Load lại data từ DB
                });
            }
        });
        builder.setNegativeButton("HỦY", null);
        builder.show();
    }

    // --- INNER CLASS (ADAPTER) ---
    class FundAdapter extends RecyclerView.Adapter<FundAdapter.ViewHolder> {
        List<FundTransaction> list;
        DecimalFormat formatter = new DecimalFormat("#,###");

        FundAdapter(List<FundTransaction> list) { this.list = list; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fund, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FundTransaction t = list.get(position);
            holder.tvReason.setText(t.reason);
            holder.tvDate.setText(t.timestamp); // Tên field trong DB là timestamp
            
            String sign = t.isIncome ? "+ " : "- ";
            holder.tvAmount.setText(sign + formatter.format(t.amount));
            holder.tvAmount.setTextColor(t.isIncome ? getResources().getColor(R.color.phui_accent) : getResources().getColor(R.color.phui_error));
        }

        @Override public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvReason, tvDate, tvAmount;
            ViewHolder(View itemView) {
                super(itemView);
                tvReason = itemView.findViewById(R.id.tvFundReason);
                tvDate = itemView.findViewById(R.id.tvFundDate);
                tvAmount = itemView.findViewById(R.id.tvFundAmount);
            }
        }
    }
}
