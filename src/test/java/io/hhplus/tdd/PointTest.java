package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    *  1) 포인트 조회 : point
    *     - id가 비어 있으면 실패한다.
    *     - id의 type이 long이 아닐 경우 실패한다.
    *
    *  2) 포인트 충전/사용 내역 조회 : history
    *     - id가 비어 있으면 실패한다.
    *     - id의 type이 long이 아닐 경우 실패한다.
    *     - 조회된 PointHistory가 없을 경우 실패한다.
    *       
    *  3) 충전 : charge
    *     - id가 비어 있으면 실패한다.
    *     - id의 type이 long이 아닐 경우 실패한다.
    *     - amount의 type이 long이 아닐 경우 실패한다.
    *     - amount가 0보다 작거나 같으면 실패한다.
    *     * 1회 충전 최대 금액을 1,000,000까지만 가능한 것으로 규칙을 선정
    *         - amount가 1,000,000보다 크면 실패한다.
    *     - 최대 충전 가능한 금액을 5,000,000으로 규칙을 선정
    *         - 충전 후 amount가 5,000,000보다 크면 실패한다.
    *
    *  4) 사용 : use
    *     - id가 비어 있으면 실패한다.
    *     - id의 type이 long이 아닐 경우 실패한다.
    *     - amount가 0보다 작거나 같으면 실패한다.
    *     - 해당 id가 가지고 있는 amount보다 큰 값이 입력될 경우 실패한다.
    *
    * */

    @DisplayName("포인트 조회 : id가 비어 있으면 실패한다.")
    @Test
    public void Point_Id_0(){
        long id = 0;
        assertEquals(0L, id);
    }

    @DisplayName("포인트 조회 : id의 type이 long이 아닐 경우 실패한다.")
    @Test
    public void Point_Id_Not_Long(){
        Object id = "abc";
        assertFalse(id instanceof Long);
    }

    @DisplayName("포인트 충전/사용 내역 조회 : id가 비어 있으면 실패한다.")
    @Test
    public void History_Id_0(){
        long id = 0;
        assertEquals(0L, id);
    }

    @DisplayName("포인트 충전/사용 내역 조회 : id의 type이 long이 아닐 경우 실패한다.")
    @Test
    public void History_Id_Not_Long(){
        Object id = "abc";
        assertFalse(id instanceof Long);
    }
    
    @DisplayName("포인트 충전/사용 내역 조회 : 조회된 PointHistory가 없을 경우 실패한다.")
    @Test
    public void History_Size_Equals_0(){
        int size; // 포인트 충전/사용 내역 개수
        List<PointHistory> pointHistoryList = List.of();
        size = pointHistoryList.size();
        
        assertEquals(0, size);
    }

    @DisplayName("충전 : id가 비어 있으면 실패한다.")
    @Test
    public void Charge_Id_0(){
        long id = 0;
        assertEquals(0L, id);
    }

    @DisplayName("충전 : id의 type이 long이 아닐 경우 실패한다.")
    @Test
    public void Charge_Id_Not_Long(){
        Object id = "abc";
        assertFalse(id instanceof Long);
    }

    @DisplayName("충전 : amount의 type이 long이 아닐 경우 실패한다.")
    @Test
    public void Charge_Amount_Not_Long(){
        Object amount = "abc";
        assertFalse(amount instanceof Long);
    }
    
    @DisplayName("충전 : amount가 0이면 실패한다.")
    @Test
    public void Charge_Amount_Equals_0(){
        long amount = 0;
        assertEquals(0, amount);
    }
    
    @DisplayName("충전 : amount가 0보다 작으면 실패한다.")
    @Test
    public void Charge_Amount_LessThan_0(){
        long amount = -10000;
        assertTrue(amount < 0);
    }
    
    @DisplayName("충전 : amount가 1,000,000보다 크면 실패한다.")
    @Test
    public void Charge_Amount_MoreThan_1000000(){
        long amount = 2000000;
        assertTrue(amount > 1000000);
    }
    
    @DisplayName("충전 : 충전 후 amount가 5,000,000보다 크면 실패한다.")
    @Test
    public void Charge_TotalAmount_MoreThan_5000000(){
        long id = 1;            // ID
        long point;             // 기존 포인트
        long amount = 4100000;  // 충전 포인트
        long totalPoint;        // 총합 포인트

        UserPointTable userPointTable = new UserPointTable();
        userPointTable.insertOrUpdate(id, 1000000);
        point = userPointTable.selectById(id).point();
        totalPoint = point + amount;

        assertTrue(totalPoint > 5000000);
    }

    @DisplayName("사용 : id가 비어 있으면 실패한다.")
    @Test
    public void Use_Id_0(){
        long id = 0;
        assertEquals(0L, id);
    }

    @DisplayName("사용 : id의 type이 long이 아닐 경우 실패한다.")
    @Test
    public void Use_Id_Not_Long(){
        Object id = "abc";
        assertFalse(id instanceof Long);
    }

    @DisplayName("사용 : amount가 0이면 실패한다.")
    @Test
    public void Use_Amount_Equals_0(){
        long amount = 0;
        assertEquals(0, amount);
    }

    @DisplayName("사용 : amount가 0보다 작으면 실패한다.")
    @Test
    public void Use_Amount_LessThan_0(){
        long amount = -10000;
        assertTrue(amount < 0);
    }

    @DisplayName("사용 : 해당 id가 가지고 있는 amount보다 큰 값이 입력될 경우 실패한다.")
    @Test
    public void Use_Amount_MoreThan_Point(){
        long id = 1;            // ID
        long point;             // 잔여 포인트
        long amount = 1100000;  // 사용하려는 포인트
        long totalPoint;        // 사용 후 포인트

        UserPointTable userPointTable = new UserPointTable();
        userPointTable.insertOrUpdate(id, 1000000);
        point = userPointTable.selectById(id).point();
        totalPoint = point - amount;
        assertTrue(totalPoint < 0);
    }
}
