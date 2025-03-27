package io.hhplus.tdd.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.PointServiceImpl;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;
    private PointService pointService;

    private static final long ANY_ID = 1L;
    private static final long ANY_POINT = 1000L;
    private static final long ANY_TIMEMILLIS = System.currentTimeMillis();

    @BeforeEach
    void 기본_설정(){
        userPointTable = mock(UserPointTable.class);
        pointHistoryTable = mock(PointHistoryTable.class);
        pointService = new PointServiceImpl(userPointTable, pointHistoryTable);
    }

    @Nested
    class 포인트_조회{

        @Test
        void 포인트_조회_성공_시_UserPoint_반환(){

            // given
            UserPoint userPoint = new UserPoint(ANY_ID, ANY_POINT, ANY_TIMEMILLIS);

            // when
            when(userPointTable.selectById(ANY_ID)).thenReturn(userPoint);

            // then
            UserPoint resultPoint = pointService.selectUserPointById(ANY_ID);
            assertThat(userPoint).isEqualTo(resultPoint);

        }
        @Test
        void 포인트_조회_실패_시_OL_반환(){

            // given
            UserPoint userPoint = UserPoint.empty(ANY_ID);

            // when
            when(userPointTable.selectById(ANY_ID)).thenReturn(userPoint);

            // then
            UserPoint resultPoint = pointService.selectUserPointById(ANY_ID);
            assertThat(userPoint.point()).isEqualTo(0L);

        }
    }

    @Nested
    class 포인트_충전{

        @Test
        void 포인트_충전_성공_시_UserPoint_및_PointHistory_반환(){

            // given
            long chargePoint = 1L;
            UserPoint userPoint = new UserPoint(ANY_ID, ANY_POINT, ANY_TIMEMILLIS);
            UserPoint chargedUserPoint = userPoint.charge(chargePoint);

            // when
            when(userPointTable.insertOrUpdate(ANY_ID, chargePoint)).thenReturn(chargedUserPoint);
            UserPoint resultUserPoint = pointService.charge(ANY_ID, chargePoint);

            // then
            assertAll(
                () -> assertThat(resultUserPoint.point()).isEqualTo(ANY_POINT + chargePoint),
                () -> assertThat(resultUserPoint.id()).isEqualTo(ANY_ID),
                () -> assertThat(resultUserPoint.updateMillis()).isPositive()
            );

            verify(userPointTable).insertOrUpdate(ANY_ID, chargePoint);
            verify(pointHistoryTable).insert(
                eq(ANY_ID),
                eq(chargePoint),
                eq(TransactionType.CHARGE),
                anyLong()
            );
        }
    }

    @Nested
    class 포인트_사용{
        @Test
        void 포인트_사용_성공_시_UserPoint_및_PointHistory_반환(){

            // given
            long usePoint = 1L;
            UserPoint userPoint = new UserPoint(ANY_ID, ANY_POINT, ANY_TIMEMILLIS);
            UserPoint usedUserPoint = userPoint.use(usePoint);

            // when
            when(userPointTable.insertOrUpdate(ANY_ID, usePoint)).thenReturn(usedUserPoint);
            UserPoint resultUserPoint = pointService.use(ANY_ID, usePoint);

            // then
            assertAll(
                () -> assertThat(resultUserPoint.point()).isEqualTo(ANY_POINT - usePoint),
                () -> assertThat(resultUserPoint.id()).isEqualTo(ANY_ID),
                () -> assertThat(resultUserPoint.updateMillis()).isPositive()
            );

            verify(userPointTable).insertOrUpdate(ANY_ID, usePoint);
            verify(pointHistoryTable).insert(
                eq(ANY_ID),
                eq(usePoint),
                eq(TransactionType.USE),
                anyLong()
            );
        }
    }

    @Nested
    class 포인트_내역_조회{
        @Test
        void 포인트_내역_조회_성공_시_PointHistory_반환(){

            // given
            List<PointHistory> pointHistoryList = List.of(
                new PointHistory(1L, ANY_ID, 100L, TransactionType.CHARGE, ANY_TIMEMILLIS),
                new PointHistory(2L, ANY_ID, 200L, TransactionType.CHARGE, ANY_TIMEMILLIS)
            );

            // when
            when(pointHistoryTable.selectAllByUserId(ANY_ID)).thenReturn(pointHistoryList);
            List<PointHistory> resultPointHistoryList = pointService.selectPointHistoryAllByUserId(ANY_ID);

            // then
            assertAll(
                () -> assertThat(resultPointHistoryList.size()).isEqualTo(2),
                () -> assertThat(resultPointHistoryList).isEqualTo(pointHistoryList)
            );
        }

        @Test
        void 포인트_내역_조회_실패_시_빈_리스트_반환(){
            // given
            // when
            when(pointHistoryTable.selectAllByUserId(ANY_ID)).thenReturn(List.of());
            List<PointHistory> resultPointHistoryList = pointService.selectPointHistoryAllByUserId(ANY_ID);

            // then
            assertThat(resultPointHistoryList).isNotNull().isEmpty();
        }
    }

}
