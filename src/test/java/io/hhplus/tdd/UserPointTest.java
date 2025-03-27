package io.hhplus.tdd;

import io.hhplus.tdd.exception.ExceededMaxPointException;
import io.hhplus.tdd.exception.InvalidPointException;
import io.hhplus.tdd.point.UserPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UserPointTest {

    private static final long ANY_ID = 1L;
    private static final long ANY_POINT = 1000L;
    private static final long ANY_TIMEMILLIS = System.currentTimeMillis();

    private UserPoint userPoint;

    @BeforeEach
    void 계정_기본_설정(){
        userPoint = new UserPoint(ANY_ID, ANY_POINT, ANY_TIMEMILLIS);
    }

    @Nested
    class 포인트_충전{

        @Test
        void 포인트_충전_성공(){
            UserPoint resultPoint = userPoint.charge(100L);
            assertThat(resultPoint.point()).isEqualTo(1100L);
        }

        @Test
        void 포인트_충전_금액_0일시_RunTimeException_리턴(){
            assertThatThrownBy(() -> userPoint.charge(0L)).isInstanceOf(InvalidPointException.class);
        }

        @Test
        void 포인트_충전_금액_음수일시_RunTimeException_리턴(){
            assertThatThrownBy(() -> userPoint.charge(-1L)).isInstanceOf(InvalidPointException.class);
        }

        @Test
        void 포인트_충전_금액_1일시_성공(){
            UserPoint resultPoint = userPoint.charge(1L);
            assertThat(resultPoint.point()).isEqualTo(1001L);
        }

        @Test
        void 포인트_충전_한도_초과시_RunTimeException_리턴(){
            assertThatThrownBy(() -> userPoint.charge(9001L)).isInstanceOf(ExceededMaxPointException.class);
        }

    }

    @Nested
    class 포인트_사용{

        @Test
        void 포인트_사용_성공(){
            UserPoint resultPoint = userPoint.use(100L);
            assertThat(resultPoint.point()).isEqualTo(900L);
        }

        @Test
        void 포인트_충전_금액_0일시_RunTimeException_리턴(){
            assertThatThrownBy(() -> userPoint.use(0L)).isInstanceOf(InvalidPointException.class);
        }

        @Test
        void 포인트_사용_금액_음수일시_RunTimeException_리턴(){
            assertThatThrownBy(() -> userPoint.use(-1L)).isInstanceOf(InvalidPointException.class);
        }

        @Test
        void 포인트_사용_금액_1일시_성공(){
            UserPoint resultPoint = userPoint.use(1L);
            assertThat(resultPoint.point()).isEqualTo(999L);
        }

        @Test
        void 포인트_사용_간_보유금액보다_많을시_RunTimeException_리턴(){
            assertThatThrownBy(() -> userPoint.use(1001L)).isInstanceOf(ExceededMaxPointException.class);
        }
    }

}
