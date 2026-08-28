package com.eaut.footballclubmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.eaut.footballclubmanagement.models.Message;
import com.eaut.footballclubmanagement.network.ApiService;
import com.eaut.footballclubmanagement.network.RetrofitClient;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiCoachActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText etChatInput;
    private Button btnSend;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_coach);

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        etChatInput = findViewById(R.id.etChatInput);
        btnSend = findViewById(R.id.btnSend);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        
        chatAdapter = new ChatAdapter(new ArrayList<>());
        recyclerViewChat.setAdapter(chatAdapter);

        // Lời chào ban đầu của AI (Nhấn mạnh về RAG và Phân tích)
        chatAdapter.addMessage(new Message("Chào bầu sô! Tôi là Trợ lý AI (Tích hợp RAG). Tôi đã nạp toàn bộ Dữ liệu Cầu thủ thực tế của đội vào não. Bầu sô muốn gợi ý đội hình hay phân tích chiến thuật nào?", false));

        btnSend.setOnClickListener(v -> sendMessage(null));
        
        // Gán sự kiện Gợi ý nhanh
        findViewById(R.id.btnSuggest7).setOnClickListener(v -> sendMessage("Dựa vào OVR của đội hiện tại, hãy gợi ý cho tôi đội hình đá sân 7 tối ưu nhất kèm sơ đồ."));
        findViewById(R.id.btnSuggestMVP).setOnClickListener(v -> sendMessage("Hãy phân tích dữ liệu Bàn thắng và MVP của đội để chọn ra 3 cầu thủ đang có phong độ cao nhất lúc này."));
        findViewById(R.id.btnSuggestTactic).setOnClickListener(v -> sendMessage("Nhìn vào danh sách cầu thủ, đội ta đang yếu ở tuyến nào? Hãy phân tích chi tiết."));
    }

    private void sendMessage(String overridePrompt) {
        String prompt = overridePrompt != null ? overridePrompt : etChatInput.getText().toString().trim();
        if (prompt.isEmpty()) return;

        // 1. Thêm tin nhắn của User
        chatAdapter.addMessage(new Message(prompt, true));
        etChatInput.setText("");
        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);

        // Khóa nút để chờ AI
        btnSend.setEnabled(false);
        btnSend.setText("...");

        // 2. Gọi API gửi cho AI
        ApiService.AiRequest request = new ApiService.AiRequest(prompt);
        RetrofitClient.getApiService().askAiCoach(request).enqueue(new Callback<ApiService.AiResponse>() {
            @Override
            public void onResponse(Call<ApiService.AiResponse> call, Response<ApiService.AiResponse> response) {
                btnSend.setEnabled(true);
                btnSend.setText("GỬI");

                if (response.isSuccessful() && response.body() != null) {
                    // Thêm tin nhắn của AI
                    chatAdapter.addMessage(new Message(response.body().reply, false));
                    recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                } else {
                    chatAdapter.addMessage(new Message("Lỗi: Không lấy được chiến thuật từ trợ lý.", false));
                }
            }

            @Override
            public void onFailure(Call<ApiService.AiResponse> call, Throwable t) {
                btnSend.setEnabled(true);
                btnSend.setText("GỬI");
                Toast.makeText(AiCoachActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
