package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PointServiceImpl implements PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointServiceImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable){
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public UserPoint selectUserPointById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> selectPointHistoryAllByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    @Override
    public PointHistory insertPointHistory(long userId, long amount, TransactionType transactionType, long updateMillis) {
        return pointHistoryTable.insert(userId, amount, transactionType, updateMillis);
    }

    @Override
    public UserPoint charge(long id, long point) {

        UserPoint result = userPointTable.insertOrUpdate(id, point);
        insertPointHistory(id, point, TransactionType.CHARGE, System.currentTimeMillis());

        return result;
    }

    @Override
    public UserPoint use(long id, long point) {

        UserPoint result = userPointTable.insertOrUpdate(id, point);
        insertPointHistory(id, point, TransactionType.USE, System.currentTimeMillis());

        return result;
    }
}
