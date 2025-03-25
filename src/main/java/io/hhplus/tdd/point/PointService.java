package io.hhplus.tdd.point;

import java.util.List;

public interface PointService {

    // 특정 유저의 포인트를 조회하는 기능
    public UserPoint point(long id);

    // 특정 유저의 포인트 충전/이용 내역을 조회하는 기능
    public List<PointHistory> history(long userId);

    // 특정 유저의 포인트를 충전하는 기능
    public UserPoint charge(long id, long point);

    // 특정 유저의 포인트를 사용하는 기능
    public UserPoint use(long id, long point);
}
