package com.devnest.board.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeFormatter {
    public static String format(LocalDateTime time) {
        if (time == null) return "";

        Duration duration = Duration.between(time, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) return seconds + "초 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86400) return (seconds / 3600) + "시간 전";
        return (seconds / 86400) + "일 전";
    }
}
