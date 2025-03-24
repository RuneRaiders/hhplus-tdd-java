package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PointHistoryRepository {

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private PointHistory pointHistory;

    // 포인트 충전/사용 내역 추가
    public void addPointHistory(long userId, long amount, TransactionType type, long updateMillis) {
        pointHistory.addPointHistory(pointHistoryTable, userId, amount, type, updateMillis);
    }

    // 포인트 충전/사용 내역 조회
    public List<PointHistory> selectPointHistoryByUserId(long userId) {
        return pointHistory.selectPointHistoryByUserId(pointHistoryTable, userId);
    }

}
