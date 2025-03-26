package io.hhplus.tdd.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PointService pointService;  // Mock 객체를 생성하고, 스프링 컨텍스트에 등록

    @Autowired
    ObjectMapper objectMapper;  // Object to JSON & JSON to Object를 위해 사용

    private static final long ANY_ID = 1L;
    private static final long ANY_POINT = 1000L;
    private static final long ANY_TIMEMILLIS = System.currentTimeMillis();

    /*
    * 1. HTTP 요청/응답 검증 : 클라이언트가 보낼 수 있는 다양한 HTTP 요청(GET, POST, PUT, DELETE 등)에 대한 응답이 올바르게 처리되는지 확인
    * 2. 상태 코드 검증 : 각 요청에 대해 적절한 HTTP 상태 코드(200, 201, 400, 404, 500 등)가 반환되는지 체크
    * */

    /*
    * @Nested : 중첩 클래스를 이용해 계층적으로 테스트를 작성, 테스트의 그룹화를 위해 사용
    * */

    @Nested
    class 포인트_조회 {
        @Test
        void 특정_유저의_포인트를_조회_성공_시_UserPoint_리턴() throws Exception {

            // given
            UserPoint userPoint = UserPoint.empty(ANY_ID);

            // when
            when(pointService.selectUserPointById(ANY_ID)).thenReturn(userPoint);

            // then
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/point/1");   // "/point/1"로 GET 요청 수행
            mockMvc.perform(builder)                                                                    // MockMvc를 사용하여 HTTP 요청을 실행
                .andExpect(status().isOk())                                                             // 상태 코드 200인 성공적인 응답을 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))                           // 응답 내용의 contentType 이 APPLICATION_JSON과 같을 것으로 기대
                .andExpect(jsonPath("$.id").value(ANY_ID))                                    // JSON 속성 "id"의 값이 ANY_ID의 값과 같을 것으로 기대
                .andExpect(jsonPath("$.point").value(0L))                        // JSON 속성 "point"의 값이 0L 과 같을 것으로 기대
                .andDo(print());                                                                        // 실행 결과를 임의의 출력 대상에 출력 : 출력 대상 미지정 시, 표준 출력(System.out)이 출력 대상이 됨
        }
    }

    @Nested
    class 포인트_내역_조회{
        @Test
        void 특정_유저의_포인트_충전_이용_내역을_조회_성공_시_List_PointHistory_리턴() throws Exception {

            // given
            PointHistory pointHistory1 = new PointHistory(1L, ANY_ID, 1000L, TransactionType.CHARGE, System.currentTimeMillis());
            PointHistory pointHistory2 = new PointHistory(1L, ANY_ID, 1500L, TransactionType.CHARGE, System.currentTimeMillis());
            PointHistory pointHistory3 = new PointHistory(1L, ANY_ID, 2000L, TransactionType.CHARGE, System.currentTimeMillis());

            List<PointHistory> pointHistoryList = List.of(pointHistory1, pointHistory2, pointHistory3);

            // when
            when(pointService.selectPointHistoryAllByUserId(ANY_ID)).thenReturn(pointHistoryList);

            // then
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/point/1/histories");         // "/point/1/histories"로 GET 요청 수행
            MvcResult mvcResult = mockMvc.perform(builder)                                                              // MockMvc를 사용하여 HTTP 요청을 실행
                .andExpect(status().isOk())                                                                             // 상태 코드 200인 성공적인 응답을 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))                                           // 응답 내용의 contentType 이 APPLICATION_JSON과 같을 것으로 기대
                .andReturn();                                                                                           // 반환할 MvcResult를 설정

            String result = mvcResult.getResponse().getContentAsString();                                               // MockMvc의 HTTP 테스트 결과 응답 body값을 String 형태로 반환

            /*
             * readValue() : 역직렬화할 타입을 명시하기 위한 객체
             * TypeReference : Jackson 라이브러리를 사용하여 JSON을 역직렬화할 때, 제네릭 타입 정보를 보존하기 위해 사용
             * */
            List<PointHistory> resultList = objectMapper.readValue(result, new TypeReference<List<PointHistory>>() {});

            assertThat(pointHistoryList).isEqualTo(resultList);
        }

        @Test
        void 특정_유저의_포인트_충전_이용_내역을_조회_간_내역이_없을_시_비어있는_List_PointHistory_리턴() throws Exception {

            // given

            // when
            when(pointService.selectPointHistoryAllByUserId(ANY_ID)).thenReturn(List.of());

            // then
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/point/"+ANY_ID+"/histories");    // "/point/1/histories"로 GET 요청 수행
            MvcResult mvcResult = mockMvc.perform(builder)                                                                  // MockMvc를 사용하여 HTTP 요청을 실행
                .andExpect(status().isOk())                                                                                 // 상태 코드 200인 성공적인 응답을 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))                                               // 응답 내용의 contentType 이 APPLICATION_JSON과 같을 것으로 기대
                .andReturn();                                                                                               // 반환할 MvcResult를 설정

            String result = mvcResult.getResponse().getContentAsString();                                                   // MockMvc의 HTTP 테스트 결과 응답 body값을 String 형태로 반환

            /*
             * readValue() : 역직렬화할 타입을 명시하기 위한 객체
             * TypeReference : Jackson 라이브러리를 사용하여 JSON을 역직렬화할 때, 제네릭 타입 정보를 보존하기 위해 사용
             * */
            List<PointHistory> resultList = objectMapper.readValue(result, new TypeReference<List<PointHistory>>() {});

            assertThat(0).isEqualTo(resultList.size());
        }
    }

    @Nested
    class 포인트_충전{
        @Test
        void 특정_유저의_포인트_충전_성공_시_충전_금액이_반영된_UserPoint_리턴() throws Exception {

            // given
            UserPoint userPoint = UserPoint.empty(ANY_ID);
            long chargePoint = 1L;

            // when
            when(pointService.charge(ANY_ID, ANY_POINT)).thenReturn(new UserPoint(ANY_ID, userPoint.point() + chargePoint, ANY_TIMEMILLIS));

            // then
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch("/point/"+ANY_ID+"/charge");   // "/point/1/charge"로 PATCH 요청 수행
            MvcResult mvcResult = mockMvc.perform(                                                                        // MockMvc를 사용하여 HTTP 요청을 실행
                    builder
                        .content(objectMapper.writeValueAsString(chargePoint))                                            // request 내 content를 String 타입으로 변환
                        .contentType(MediaType.APPLICATION_JSON)                                                          // request 내 contentType을 APPLICATION_JSON으로 설정
                )
                .andExpect(status().isOk())                                                                               // 상태 코드 200인 성공적인 응답을 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))                                             // 응답 내용의 contentType 이 APPLICATION_JSON과 같을 것으로 기대
                .andReturn();

            String result = mvcResult.getResponse().getContentAsString();                                                 // MockMvc의 HTTP 테스트 결과 응답 body값을 String 형태로 반환
            UserPoint resultUserPoint = objectMapper.readValue(result, UserPoint.class);                                  // readValue() : 역직렬화할 타입을 명시하기 위한 객체

            assertThat(userPoint.id()).isEqualTo(resultUserPoint.id());
            assertThat(userPoint.point() + chargePoint).isEqualTo(resultUserPoint.point());
        }

        // TODO 특정_유저의_포인트_충전_간_0포인트_충전_시_IllegalArgumentException_예외를_리턴
//        @Test
//        void 특정_유저의_포인트_충전_간_0포인트_충전_시_IllegalArgumentException_예외를_리턴() throws Exception {
//
//            // when
//            when(pointService.charge(ANY_ID, 0L)).thenThrow(new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다."));
//
//            // then
//            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch("/point/"+ANY_ID+"/charge");   // "/point/1/charge"로 PATCH 요청 수행
//            mockMvc.perform(                                                                                              // MockMvc를 사용하여 HTTP 요청을 실행
//                    builder
//                        .content("0")
//                        .contentType(MediaType.APPLICATION_JSON)                                                          // request 내 contentType을 APPLICATION_JSON으로 설정
//                )
//                .andDo(print())
//                .andExpect(status().isInternalServerError())                                                              // 상태 코드 500인 Bad Request를 기대
//                .andExpect(res)                                                              // 상태 코드 500인 Bad Request를 기대
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));                                            // 응답 내용의 contentType 이 APPLICATION_JSON과 같을 것으로 기대
//        }
    }





    /*
     * 특정 유저의 포인트를 사용 : 성공 시 사용 금액이 반영된 UserPoint 리턴
     * */
    @Test
    void 특정_유저의_포인트_사용_성공_시_사용_금액이_반영된_UserPoint_리턴() throws Exception {

        // given
        UserPoint userPoint = new UserPoint(ANY_ID, ANY_POINT, ANY_TIMEMILLIS);
        long usePoint = 1L;

        // when
        when(pointService.use(ANY_ID, usePoint)).thenReturn(new UserPoint(ANY_ID, userPoint.point() - usePoint, ANY_TIMEMILLIS));

        // then
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch("/point/"+ANY_ID+"/use");      // "/point/1/use"로 PATCH 요청 수행
        MvcResult mvcResult = mockMvc.perform(                                                                        // MockMvc를 사용하여 HTTP 요청을 실행
                builder
                    .content(objectMapper.writeValueAsString(usePoint))                                               // request 내 content를 String 타입으로 변환
                    .contentType(MediaType.APPLICATION_JSON)                                                          // request 내 contentType을 APPLICATION_JSON으로 설정
            )
            .andExpect(status().isOk())                                                                               // 상태 코드 200인 성공적인 응답을 기대
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))                                             // 응답 내용의 contentType 이 APPLICATION_JSON과 같을 것으로 기대
            .andReturn();

        String result = mvcResult.getResponse().getContentAsString();                                                 // MockMvc의 HTTP 테스트 결과 응답 body값을 String 형태로 반환
        UserPoint resultUserPoint = objectMapper.readValue(result, UserPoint.class);                                  // readValue() : 역직렬화할 타입을 명시하기 위한 객체

        assertThat(userPoint.id()).isEqualTo(resultUserPoint.id());
        assertThat(userPoint.point() - usePoint).isEqualTo(resultUserPoint.point());

    }

}
