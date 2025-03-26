package io.hhplus.tdd.point;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
    public PointHistory {
        if(type == null){
            throw new IllegalArgumentException("transactionType 값이 존재하지 않습니다.");
        }
    }
}
