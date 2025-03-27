package io.hhplus.tdd;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PointHistoryTest {

    private static final long ANY_ID = 1L;
    private static final long ANY_USER_ID = 1L;
    private static final long ANY_POINT = 100L;
    private static final long ANY_TIMEMILLIS = System.currentTimeMillis();

    @Nested
    class 내역_생성{

        @Test
        void 충전_내역_생성_성공(){
            PointHistory pointHistory = new PointHistory(ANY_ID, ANY_USER_ID, 1L, TransactionType.CHARGE, ANY_TIMEMILLIS);
            assertAll(
                () -> assertThat(pointHistory.userId()).isEqualTo(ANY_ID),
                () -> assertThat(pointHistory.amount()).isEqualTo(1L),
                () -> assertThat(pointHistory.type()).isEqualTo(TransactionType.CHARGE),
                () -> assertThat(pointHistory.updateMillis()).isEqualTo(ANY_TIMEMILLIS)
            );
        }

        @Test
        void 사용_내역_생성_성공(){
            PointHistory pointHistory = new PointHistory(ANY_ID, ANY_USER_ID, 1L, TransactionType.USE, ANY_TIMEMILLIS);
            assertAll(
                () -> assertThat(pointHistory.userId()).isEqualTo(ANY_ID),
                () -> assertThat(pointHistory.amount()).isEqualTo(1L),
                () -> assertThat(pointHistory.type()).isEqualTo(TransactionType.USE),
                () -> assertThat(pointHistory.updateMillis()).isEqualTo(ANY_TIMEMILLIS)
            );
        }
    }

    @Nested
    class 타입_유효성_검증{

        @Test
        void 타입_CHARGE_설정_성공(){
            PointHistory pointHistory = new PointHistory(ANY_ID, ANY_USER_ID, 1L, TransactionType.CHARGE, ANY_TIMEMILLIS);
            assertThat(pointHistory.type()).isEqualTo(TransactionType.CHARGE);
        }

        @Test
        void 타입_USE_설정_성공(){
            PointHistory pointHistory = new PointHistory(ANY_ID, ANY_USER_ID, 1L, TransactionType.USE, ANY_TIMEMILLIS);
            assertThat(pointHistory.type()).isEqualTo(TransactionType.USE);
        }

        @Test
        void 타입_NULL_설정_실패(){
            assertThatThrownBy(() -> new PointHistory(ANY_ID, ANY_USER_ID, 1L, null, ANY_TIMEMILLIS)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
