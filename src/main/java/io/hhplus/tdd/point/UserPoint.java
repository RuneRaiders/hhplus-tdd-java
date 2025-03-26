package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static final long MAX_PER_POINT = 1000L;
    public static final long MAX_TOTAL_POINT = 10000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    // 책임 분리 : 충전
    public UserPoint charge(long point){
        if (point <= 0) throw new RuntimeException("충전할 포인트는 0보다 커야 합니다.");

        if(point > MAX_PER_POINT){
            throw new RuntimeException("한 번에 충전할 수 있는 포인트는 최대 1,000포인트 입니다.");
        }

        long totalPoint = this.point + point;
        if(totalPoint > MAX_TOTAL_POINT){
            throw new RuntimeException("충전할 수 있는 포인트는 최대 10,000포인트 입니다.");
        }

        return new UserPoint(this.id, totalPoint, System.currentTimeMillis());
    }

    // 책임 분리 : 사용
    public UserPoint use(long point){
        if (point <= 0) throw new RuntimeException("사용할 포인트는 0보다 커야 합니다.");

        if(this.point < point){
            throw new RuntimeException("충전할 수 있는 포인트는 최대 1,000포인트 입니다.");
        }

        long totalPoint = this.point - point;
        if(totalPoint < 0L){
            throw new RuntimeException("보유 중인 포인트보다 많은 포인트는 사용할 수 없습니다.");
        }

        return new UserPoint(this.id, totalPoint, System.currentTimeMillis());
    }

}

