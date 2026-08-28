package com.eaut.footballclubmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eaut.footballclubmanagement.models.Message;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.isUser()) {
            holder.layoutUserMessage.setVisibility(View.VISIBLE);
            holder.layoutAiMessage.setVisibility(View.GONE);
            holder.tvUserText.setText(message.getText());
        } else {
            holder.layoutUserMessage.setVisibility(View.GONE);
            holder.layoutAiMessage.setVisibility(View.VISIBLE);
            holder.tvAiText.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutAiMessage, layoutUserMessage;
        TextView tvAiText, tvUserText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutAiMessage = itemView.findViewById(R.id.layoutAiMessage);
            layoutUserMessage = itemView.findViewById(R.id.layoutUserMessage);
            tvAiText = itemView.findViewById(R.id.tvAiText);
            tvUserText = itemView.findViewById(R.id.tvUserText);
        }
    }
}
