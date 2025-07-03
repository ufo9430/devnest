package com.devnest.board.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatisticsVo {
    private long allCount;
    private long waitingCount;
    private long solvedCount;
    private long todayCount;
}
