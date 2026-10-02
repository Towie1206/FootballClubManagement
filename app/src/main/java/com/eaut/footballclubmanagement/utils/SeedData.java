package com.eaut.footballclubmanagement.utils;

import com.eaut.footballclubmanagement.models.Player;
import java.util.ArrayList;
import java.util.List;

public class SeedData {
    public static List<Player> getInitialPlayers() {
        List<Player> list = new ArrayList<>();
        list.add(new Player(1, "Nguyễn Quang Hải", "MF", 19, "Fit", 84, 7, 11, 18, 4, "FC Đông Á", 82, 85, 88, 86, 60, 72));
        list.add(new Player(2, "Nguyễn Tiến Linh", "FW", 22, "Fit", 83, 14, 4, 20, 5, "FC Đông Á", 81, 86, 75, 78, 45, 84));
        list.add(new Player(3, "Đoàn Văn Hậu", "DF", 5, "Injured", 81, 2, 5, 12, 1, "FC Đông Á", 84, 70, 78, 79, 82, 86));
        list.add(new Player(4, "Đặng Văn Lâm", "GK", 1, "Fit", 82, 0, 0, 19, 3, "FC Đông Á", 50, 40, 72, 45, 85, 84));
        list.add(new Player(5, "Nguyễn Hoàng Đức", "MF", 28, "Fit", 85, 6, 12, 21, 6, "FC Đông Á", 80, 81, 89, 87, 74, 82));
        list.add(new Player(6, "Quế Ngọc Hải", "DF", 3, "Fit", 82, 1, 2, 17, 2, "FC Đông Á", 72, 60, 76, 70, 85, 85));
        list.add(new Player(7, "Phạm Tuấn Hải", "FW", 10, "Fit", 83, 11, 6, 20, 3, "Free Agent", 86, 84, 79, 83, 55, 80));
        list.add(new Player(8, "Vũ Văn Thanh", "DF", 17, "Fit", 80, 3, 4, 16, 1, "Free Agent", 85, 73, 76, 78, 77, 81));
        list.add(new Player(9, "Nguyễn Tuấn Anh", "MF", 11, "Fit", 79, 1, 5, 14, 1, "Free Agent", 74, 72, 85, 84, 70, 68));
        list.add(new Player(10, "Bùi Tiến Dũng", "DF", 4, "Fit", 80, 1, 1, 15, 1, "Free Agent", 74, 55, 71, 68, 83, 81));
        return list;
    }
}
