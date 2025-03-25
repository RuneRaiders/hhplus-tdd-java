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
    public UserPoint point(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> history(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    /*
     * 포인트 충전
     * [ 정책 ]
     * 1. 충전할 포인트가 0보다는 커야 함
     * 2. 최대 잔고 : 1,000,000
     * */
    @Override
    public UserPoint charge(long id, long point) {

//        if(point <= 0){
//            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
//        }

        UserPoint userPoint = userPointTable.selectById(id);

        if(userPoint == null){userPoint = userPoint.empty(id);}

        long currentPoint = userPoint.point();

//        if(currentPoint + point > 100000){
//            throw new IllegalArgumentException("충전 후 포인트는 최대 1,000,000까지만 가능합니다.");
//        }

        userPointTable.insertOrUpdate(id, currentPoint + point);
        pointHistoryTable.insert(id, point, TransactionType.CHARGE, System.currentTimeMillis());

        return userPoint;
    }

    /*
     * 포인트 사용
     * [ 정책 ]
     * 1. 사용할 포인트가 0보다는 커야 함
     * 2. 잔고보다 많은 포인트를 사용할 수 없음
     * */
    @Override
    public UserPoint use(long id, long point) {

        UserPoint userPoint = userPointTable.selectById(id);

        if(userPoint == null){userPoint = userPoint.empty(id);}

        long currentPoint = userPoint.point();

        userPointTable.insertOrUpdate(id, point);
        pointHistoryTable.insert(id, point, TransactionType.USE, System.currentTimeMillis());

        return userPoint;
    }
}
