package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserPointRepository {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private UserPoint userPoint;

    // 포인트 조회
    public UserPoint getUserPoint(long id) {
        return userPoint.getPoint(userPointTable, id);
    }

    /*
    * 포인트 충전
    * [ 정책 ]
    * 1. 충전할 포인트가 0보다는 커야 함
    * 2. 최대 잔고 : 5,000,000
    * */
    public void chargePoint(long id, long point) {

    }

    /*
    * 포인트 사용
    * [ 정책 ]
    * 1. 사용할 포인트가 0보다는 커야 함
    * 2. 잔고보다 많은 포인트를 사용할 수 없음
    * */
    public void usePoint(long id, long point) {

    }

}
