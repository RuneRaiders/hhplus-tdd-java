package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import java.util.List;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {

    // 포인트 충전/사용 내역 추가
    public void addPointHistory(PointHistoryTable pointHistoryTable, long userId, long amount, TransactionType type, long updateMillis) {
        pointHistoryTable.insert(userId, amount, type, updateMillis);
    }

    // 포인트 충전/사용 내역 조회
    public List<PointHistory> selectPointHistoryByUserId(PointHistoryTable pointHistoryTable, long userId){
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
