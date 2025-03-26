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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    private static final long ANY_ID = 1L;
    private static final long ANY_POINT = 1000L;
    private static final long ANY_TIMEMILLIS = System.currentTimeMillis();

    /*
    * userPoint 객체 자체에 대한 테스트 : 객체 생성 성공
    * */
    @Test
    public void userPoint_객체_생성_성공(){
        // given
        UserPoint userPoint = UserPoint.empty(ANY_ID);

        // when & then
        assertThat(userPoint).isNotNull();
        assertThat(ANY_ID).isEqualTo(userPoint.id());
    }

    /*
     * userPoint 객체 자체에 대한 테스트 : 객체 조회 간 미등록 되어 있을 경우 실패
     * */
    @Test
    public void userPoint_객체_조회_간_UserPointTable에_미등록되어_있을_경우_조회_실패(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ) {
        // given
        long nonExistedId = 99L;

        // when
        when(userPointTable.selectById(nonExistedId)).thenReturn(UserPoint.empty(nonExistedId)); // 값이 비어 있을 경우 default 객체 반환
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);
        UserPoint userPoint = pointService.selectUserPointById(nonExistedId);

        // then
        assertThat(nonExistedId).isEqualTo(userPoint.id());
    }

    /*
     * userPoint 객체 자체에 대한 테스트 : 객체 조회 간 등록 되어 있을 경우 성공
     * */
    @Test
    public void userPoint_객체_조회_간_UserPointTable에_등록되어_있을_경우_조회_성공(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ) {
        // given
        UserPoint userPoint = new UserPoint(77L, 100L, System.currentTimeMillis());

        // when
        when(userPointTable.selectById(userPoint.id())).thenReturn(userPoint);
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        UserPoint resultUserPoint = pointService.selectUserPointById(userPoint.id());

        // then
        assertThat(resultUserPoint.id()).isEqualTo(userPoint.id());
        assertThat(resultUserPoint.point()).isEqualTo(userPoint.point());
    }

    /*
     * userPoint 충전에 대한 테스트 : 충전 성공
     * */
    @Test
    public void userPoint_충전_성공(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, System.currentTimeMillis());

        // when
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        // then
        assertDoesNotThrow(() -> pointService.charge(userPoint.id(), 1000L));
    }

    /*
     * userPoint 충전에 대한 테스트 : 0 이하 충전 시 실패
     * */
    @Test
    public void userPoint_충전_간_0_이하_충전_시_실패(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        UserPoint userPoint = new UserPoint(ANY_ID, 0L, System.currentTimeMillis());

        // when
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        // then
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> pointService.charge(userPoint.id(), 0L)),
            () -> assertThrows(IllegalArgumentException.class, () -> pointService.charge(userPoint.id(), -1L))
        );
    }

    /*
     * userPoint 충전에 대한 테스트 : 최대 충전 포인트 (1,000) 초과 시 실패
     * */
    @Test
    public void userPoint_충전_간_1000_초과_충전_시_실패(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        UserPoint userPoint = new UserPoint(ANY_ID, 0L, System.currentTimeMillis());

        // when
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.charge(userPoint.id(), 1001L));
    }

    /*
     * userPoint 충전에 대한 테스트 : 충전 후 최대 한도 포인트 (10,000) 초과 충전 시 실패
     * */
    @Test
    public void userPoint_충전_후_포인트가_10000_초과_시_실패(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        UserPoint userPoint = new UserPoint(ANY_ID, 9000L, System.currentTimeMillis());

        // when
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.charge(userPoint.id(), 1001L));
    }

    /*
     * userPoint 결제에 대한 테스트 : 잔고 포인트가 결제 포인트 이상일 시, 성공
     * */
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    public void userPoint_결제_간_잔고가_결제_포인트_이상일_시_성공(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, System.currentTimeMillis());

        // when
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        when(userPointTable.selectById(userPoint.id())).thenReturn(new UserPoint(ANY_ID, 1000L, System.currentTimeMillis()));
        when(userPointTable.insertOrUpdate(userPoint.id(), 1000L)).thenReturn(userPoint);

        // then
        assertDoesNotThrow(() -> pointService.use(userPoint.id(), 1000L));
    }

    /*
     * userPoint 결제에 대한 테스트 : 결제 포인트가 0 이하일 시, 실패
     * */
    @Test
    public void userPoint_결제_간_0_이하_결제_시_실패(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        UserPoint userPoint = new UserPoint(ANY_ID, 0L, System.currentTimeMillis());

        // when
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        // then
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> pointService.use(userPoint.id(), 0L)),
            () -> assertThrows(IllegalArgumentException.class, () -> pointService.use(userPoint.id(), -1L))
        );
    }

    /*
     * userPoint 결제에 대한 테스트 : 잔고 포인트가 결제 포인트 미만일 시, 실패
     * */
    @Test
    public void userPoint_결제_간_잔고_포인트가_결제_포인트_미만일_시_실패(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, System.currentTimeMillis());

        // when
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.use(userPoint.id(), 1001L));
    }

    /*
     * pointHistory에 대한 테스트 : pointHistory 추가 시, 성공
     * */
    @Test
    public void pointHistory_추가_간_성공(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        PointHistory pointHistory = new PointHistory(1L, ANY_ID, ANY_POINT, TransactionType.CHARGE, ANY_TIMEMILLIS);

        // when
        when(pointHistoryTable.insert(ANY_ID, ANY_POINT, TransactionType.CHARGE, ANY_TIMEMILLIS)).thenReturn(pointHistory);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        PointHistory resultPointHistory = pointService.insertPointHistory(ANY_ID, ANY_POINT, TransactionType.CHARGE, ANY_TIMEMILLIS);

        // then
        assertEquals(pointHistory, resultPointHistory);
    }

    /*
     * pointHistory에 대한 테스트 : pointHistory 추가 간, transactionType 누락 시, 실패
     * */
    @Test
    public void pointHistory_추가_간_transactionType이_비어_있을_시_실패(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given

        // when
        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.insertPointHistory(ANY_ID, ANY_POINT, null, ANY_TIMEMILLIS));
    }

    /*
     * pointHistory에 대한 테스트 : pointHistory 조회 간, 성공
     * */
    @Test
    public void pointHistory_조회_간_성공(
        @Mock UserPointTable userPointTable, @Mock PointHistoryTable pointHistoryTable
    ){
        // given
        PointHistory pointHistory1 = new PointHistory(1L, ANY_ID, 1000L, TransactionType.CHARGE, ANY_TIMEMILLIS);
        PointHistory pointHistory2 = new PointHistory(2L, ANY_ID, 2000L, TransactionType.CHARGE, ANY_TIMEMILLIS);
        PointHistory pointHistory3 = new PointHistory(3L, ANY_ID, 3000L, TransactionType.CHARGE, ANY_TIMEMILLIS);

        List<PointHistory> pointHistoryList = List.of(pointHistory1, pointHistory2, pointHistory3);

        // when
        when(pointHistoryTable.selectAllByUserId(ANY_ID)).thenReturn(pointHistoryList);

        PointService pointService = new PointServiceImpl(userPointTable, pointHistoryTable);

        List<PointHistory> resultpointHistoryList = pointService.selectPointHistoryAllByUserId(ANY_ID);

        // then
        assertEquals(pointHistoryList, resultpointHistoryList);
    }

}
