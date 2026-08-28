package com.eaut.footballclubmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PlayerFormActivity extends AppCompatActivity {

    private TextInputEditText etPlayerName, etJerseyNumber;
    private AutoCompleteTextView etPosition;
    private TextInputLayout tilPlayerName, tilJerseyNumber, tilPosition;
    private Button btnSavePlayer;
    private TextView tvFormTitle;
    private boolean isEditMode = false;
    private int playerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_form);

        tvFormTitle = findViewById(R.id.tvFormTitle);
        etPlayerName = findViewById(R.id.etPlayerName);
        etJerseyNumber = findViewById(R.id.etJerseyNumber);
        etPosition = findViewById(R.id.etPosition);
        
        tilPlayerName = findViewById(R.id.tilPlayerName);
        tilJerseyNumber = findViewById(R.id.tilJerseyNumber);
        tilPosition = findViewById(R.id.tilPosition);
        btnSavePlayer = findViewById(R.id.btnSavePlayer);

        // Thiết lập Dropdown cho Vị trí thi đấu
        String[] positions = new String[]{"FW (Tiền đạo)", "MF (Tiền vệ)", "DF (Hậu vệ)", "GK (Thủ môn)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, positions);
        etPosition.setAdapter(adapter);

        if (getIntent().hasExtra("PLAYER_ID")) {
            isEditMode = true;
            playerId = getIntent().getIntExtra("PLAYER_ID", -1);
            tvFormTitle.setText("SỬA HỒ SƠ");
            etPlayerName.setText(getIntent().getStringExtra("PLAYER_NAME"));
            etJerseyNumber.setText(String.valueOf(getIntent().getIntExtra("PLAYER_JERSEY", 0)));
            
            String posExtra = getIntent().getStringExtra("PLAYER_POSITION");
            if (posExtra != null) {
                for (String p : positions) {
                    if (p.startsWith(posExtra)) {
                        etPosition.setText(p, false);
                        break;
                    }
                }
            }
        }

        // Tắt lỗi khi người dùng bắt đầu gõ lại
        android.text.TextWatcher clearErrorWatcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPlayerName.setErrorEnabled(false);
                tilJerseyNumber.setErrorEnabled(false);
                tilPosition.setErrorEnabled(false);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        };
        etPlayerName.addTextChangedListener(clearErrorWatcher);
        etJerseyNumber.addTextChangedListener(clearErrorWatcher);
        etPosition.addTextChangedListener(clearErrorWatcher);

        btnSavePlayer.setOnClickListener(v -> savePlayer());
    }

    private void savePlayer() {
        String name = etPlayerName.getText().toString().trim();
        String jerseyStr = etJerseyNumber.getText().toString().trim();
        String positionFull = etPosition.getText().toString().trim();

        // 1. Validation Logic
        boolean isValid = true;
        
        if (name.isEmpty()) {
            tilPlayerName.setError("Tên không được để trống");
            isValid = false;
        } else if (name.length() < 2) {
            tilPlayerName.setError("Tên quá ngắn");
            isValid = false;
        }

        int jersey = -1;
        if (jerseyStr.isEmpty()) {
            tilJerseyNumber.setError("Số áo không được để trống");
            isValid = false;
        } else {
            try {
                jersey = Integer.parseInt(jerseyStr);
                if (jersey < 1 || jersey > 99) {
                    tilJerseyNumber.setError("Số áo phải từ 1 đến 99");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilJerseyNumber.setError("Số áo không hợp lệ");
                isValid = false;
            }
        }

        if (positionFull.isEmpty()) {
            tilPosition.setError("Vui lòng chọn vị trí");
            isValid = false;
        }

        if (!isValid) return;

        // Lấy mã vị trí gốc (VD: "FW (Tiền đạo)" -> "FW")
        String position = positionFull.split(" ")[0];

        Player player = new Player(playerId, name, position, jersey, "Fit", 75, 0, 0);

        btnSavePlayer.setEnabled(false); // Trạng thái Loading (chống click nhiều lần)
        btnSavePlayer.setText("ĐANG LƯU...");

        if (isEditMode) {
            RetrofitClient.getApiService().updatePlayer(playerId, player).enqueue(new Callback<Player>() {
                @Override
                public void onResponse(Call<Player> call, Response<Player> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(PlayerFormActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        btnSavePlayer.setEnabled(true);
                        btnSavePlayer.setText("LƯU CẦU THỦ");
                        Toast.makeText(PlayerFormActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Player> call, Throwable t) {
                    btnSavePlayer.setEnabled(true);
                    btnSavePlayer.setText("LƯU CẦU THỦ");
                    Toast.makeText(PlayerFormActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            RetrofitClient.getApiService().addPlayer(player).enqueue(new Callback<Player>() {
                @Override
                public void onResponse(Call<Player> call, Response<Player> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(PlayerFormActivity.this, "Thêm cầu thủ thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        btnSavePlayer.setEnabled(true);
                        btnSavePlayer.setText("LƯU CẦU THỦ");
                        Toast.makeText(PlayerFormActivity.this, "Lỗi tạo mới", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Player> call, Throwable t) {
                    btnSavePlayer.setEnabled(true);
                    btnSavePlayer.setText("LƯU CẦU THỦ");
                    Toast.makeText(PlayerFormActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
