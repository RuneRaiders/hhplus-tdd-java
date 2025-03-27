package io.hhplus.tdd.controller;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointConcurrentTest {

    /*
    * 동시성 제어 테스트 : 멀티스레드 환경에서 공유 자원에 대한 동시 접근이 잘 제어되는지 확인
    * */

    private static final Logger log = LoggerFactory.getLogger(PointConcurrentTest.class);

    @Autowired
    PointService pointService;

    private static final long ANY_ID = 1L;
    private static final long ANY_POINT = 100L;

    @Nested
    class 동시성_제어 {

        @DisplayName("10개의 스레드를 생성하여 100Point를 10번 충전할 경우 1,000포인트가 된다.")
        @Test
        void 단일_계정_다중_포인트_충전_간_동시성_제어_테스트() throws InterruptedException{

            // given
            int threadCount = 10;
            int taskCount = 10;

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);    // ExecutorService 생성
            CountDownLatch countDownLatch = new CountDownLatch(taskCount);                  // CountDownLatch 생성

            // when
            for(int i = 0; i < taskCount; i++){
                final int index = i;
                executorService.submit(() -> {
                    try {
                        log.info("START {} ", index);
                        pointService.charge(ANY_ID, ANY_POINT); // ANY_POINT : 100 포인트를 10번 충전
                        log.info("END {} ", index);
                    } finally {
                        countDownLatch.countDown();             // 작업 완료 알림
                        log.info("latch count {} ", countDownLatch.getCount());
                    }
                });
            }

            countDownLatch.await();     // 모든 작업이 완료될 때까지 대기
            executorService.shutdown(); // ExecutorService 종료

            // then
            UserPoint resultUserPoint = pointService.selectUserPointById(ANY_ID);                       // 계정 조회
            List<PointHistory> pointHistoryList = pointService.selectPointHistoryAllByUserId(ANY_ID);   // 히스토리 조회

            assertAll(
                () -> assertThat(ANY_POINT * taskCount).isEqualTo(resultUserPoint.point()),
                () -> assertThat(pointHistoryList.size()).isEqualTo(taskCount)
            );
        }

        @DisplayName("10개의 스레드를 생성하여 10개의 계정이 100Point를 충전한다.")
        @Test
        void 다중_계정_포인트_충전_간_동시성_제어_테스트() throws InterruptedException{

            // given
            long[] users = {1L,2L,3L,4L,5L,6L,7L,8L,9L,10L};
            int threadCount = users.length; // 10

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);    // ExecutorService 생성
            CountDownLatch countDownLatch = new CountDownLatch(threadCount);                // CountDownLatch 생성
            CyclicBarrier barrier = new CyclicBarrier(threadCount);                         // CyclicBarrier 생성

            // when
            for(long userId : users){
                executorService.submit(() -> {
                    log.info("USER {} ", userId);
                    try {
                        barrier.await();                        // 모든 스레드들이 대기 상태에 빠짐
                        log.info("START {} ", userId);
                        pointService.charge(userId, ANY_POINT); // ANY_POINT : 100 포인트
                        log.info("END {} ", userId);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    } finally {
                        countDownLatch.countDown();             // 작업 완료 알림
                        log.info("latch count {} ", countDownLatch.getCount());
                    }
                });
            }

            countDownLatch.await();     // 모든 작업이 완료될 때까지 대기
            executorService.shutdown(); // ExecutorService 종료

            // then
            for(long userId : users){
                UserPoint userPoint = pointService.selectUserPointById(userId);                             // 계정 조회
                List<PointHistory> pointHistoryList = pointService.selectPointHistoryAllByUserId(userId);   // 히스토리 조회
                log.info("userId={} point={} history={}", userId, userPoint.point(), pointHistoryList);     // 각 정보가 잘 들어갔는지 확인하기 위한 로그 조회
                assertAll(
                    () -> assertThat(userPoint.point()).isEqualTo(ANY_POINT),
                    () -> assertThat(pointHistoryList.size()).isEqualTo(1)
                );
            }
        }
    }
}
