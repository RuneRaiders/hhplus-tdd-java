package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

@Service
public class PointServiceImpl implements PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    private static final ReentrantLock lock = new ReentrantLock();

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
        lock.lock(); // 락을 획득

        try {
            UserPoint userPoint = userPointTable.selectById(id);
            UserPoint chargedUserPoint = userPoint.charge(point);
            userPointTable.insertOrUpdate(id, chargedUserPoint.point());
            insertPointHistory(id, point, TransactionType.CHARGE, System.currentTimeMillis());
            return chargedUserPoint;
        } finally {
            lock.unlock(); // 락을 해제
        }
    }

    @Override
    public UserPoint use(long id, long point) {
        lock.lock(); // 락을 획득

        try {
            UserPoint userPoint = userPointTable.selectById(id);
            UserPoint usedUserPoint = userPoint.use(point);
            userPointTable.insertOrUpdate(id, usedUserPoint.point());
            insertPointHistory(id, point, TransactionType.USE, System.currentTimeMillis());
            return usedUserPoint;
        } finally {
            lock.unlock(); // 락을 해제
        }
    }
}
