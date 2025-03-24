package io.hhplus.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PointTest {

    /*
    * 0. 내가 이번 과제에서 이해한 목표 및 규칙
    *  1) TDD 방법론에 따라, PointService 기본 기능 작성 전, 해당 기능의 실패 케이스를 찾아 테스트 코드로 먼저 작성한다. ( RED : 실패하는 테스트 작성 )
    *  2) 불확실성과 부수효과를 포함하지 않도록 테스트 코드를 작성한다. ( 기능에 집중된 순수함수 작성을 지향 )
    *  3) 테스트 작성을 통해 요구사항에 대해 명확히 이해한 상태로 코드를 설계한다.
    *
    * 1. 작성해야 할 테스트 코드 : 총 4가지 기본 기능 (포인트 조회, 포인트 충전/사용 내역 조회, 충전, 사용) 을 구현
    *   1) 포인트 조회 : point
    *     - id가 비어 있으면 실패한다.
    *     - id의 type이 long이 아닐 경우 실패한다.
    *
    *  2) 포인트 충전/사용 내역 조회 : history
    *     - id가 비어 있으면 실패한다.
    *     - id의 type이 long이 아닐 경우 실패한다.
    *     * PointHistory를 최대 20개까지만 반환하는 것을 규칙으로 선정
    *       - PointHistory가 0보다 작으면 실패한다.
    *       - PointHistory가 20개보다 크면 실패한다.
    *
    *  3) 충전 : charge
    *     - id가 비어 있으면 실패한다.
    *     - id의 type이 long이 아닐 경우 실패한다.
    *     - amount의 type이 long이 아닐 경우 실패한다.
    *     - amount가 0보다 작으면 실패한다.
    *     * 1회 충전 최대 금액을 1,000,000까지만 가능한 것으로 규칙을 선정
    *         - amount가 1,000,000보다 크면 실패한다.
    *
    *  4) 사용 : use
    *     - id가 비어 있으면 실패한다.
    *     - id의 type이 long이 아닐 경우 실패한다.
    *     - amount의 type이 long이 아닐 경우 실패한다.
    *     - amount가 0보다 작으면 실패한다.
    *     - 해당 id가 가지고 있는 amount보다 큰 값이 입력될 경우 실패한다.
    *
    * */

    @DisplayName("")
    @Test
    public void FirstTest(){
        String str = "aaa";
        assertEquals("aaa", str);
    }
}
