package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    // 포인트 조회
    public long getPoint(UserPointTable userPointTable, long id) {
        UserPoint userPoint = userPointTable.selectById(id);
        return userPoint.point();
    }

    // 포인트 충전
    public void chargePoint(UserPointTable userPointTable, long id, long point) {
        long userPoint = this.getPoint(userPointTable, id);
        userPointTable.insertOrUpdate(id, userPoint + point);
    }

    // 포인트 사용
    public void usePoint(UserPointTable userPointTable, long id, long point) {
        long userPoint = this.getPoint(userPointTable, id);
        userPointTable.insertOrUpdate(id, userPoint - point);
    }
}
