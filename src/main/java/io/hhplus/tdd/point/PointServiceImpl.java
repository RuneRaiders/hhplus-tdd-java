package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null){userPoint = userPoint.empty(id);}
        return userPoint;
    }

    @Override
    public List<PointHistory> selectPointHistoryAllByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    /*
     * 포인트 충전/사용 내역 추가
     * [ 정책 ]
     * 1. transactionType 값이 반드시 존재해야 함
     * */
    @Override
    public PointHistory insertPointHistory(long userId, long amount, TransactionType transactionType, long updateMillis) {

        if(transactionType == null) throw new IllegalArgumentException("transactionType 값이 존재하지 않습니다.");

        return pointHistoryTable.insert(userId, amount, transactionType, updateMillis);
    }

    /*
     * 포인트 충전
     * [ 정책 ]
     * 1. 충전할 포인트가 0보다는 커야 함
     * 2. 1회 충전 최대 : 1,000
     * 3. 최대 잔고 : 10,000
     * */
    @Override
    public UserPoint charge(long id, long point) {

        if(point <= 0L) throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        if(point > 1000L) throw new IllegalArgumentException("충전할 수 있는 포인트는 최대 1,000포인트 입니다.");

        UserPoint userPoint = selectUserPointById(id);
        long currentPoint = userPoint.point();

        if(currentPoint + point > 10000L) throw new IllegalArgumentException("충전 후 포인트는 최대 10,000 까지만 가능합니다.");

        UserPoint result = userPointTable.insertOrUpdate(id, currentPoint + point);
        insertPointHistory(id, point, TransactionType.CHARGE, System.currentTimeMillis());

        return result;
    }

    /*
     * 포인트 사용
     * [ 정책 ]
     * 1. 사용할 포인트가 0보다는 커야 함
     * 2. 잔고보다 많은 포인트를 사용할 수 없음
     * */
    @Override
    public UserPoint use(long id, long point) {

        UserPoint userPoint = selectUserPointById(id);
        long currentPoint = userPoint.point();

        if(point <= 0L) throw new IllegalArgumentException("포인트는 최소 1부터 사용할 수 있습니다.");
        if(currentPoint - point < 0L) throw new IllegalArgumentException("보유 중인 포인트보다 많은 포인트는 사용할 수 없습니다.");

        UserPoint result = userPointTable.insertOrUpdate(id, point);

        pointHistoryTable.insert(id, point, TransactionType.USE, System.currentTimeMillis());

        return result;
    }
}
