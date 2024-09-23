package com.sparta.outsouringproject.statistics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.statistics.dto.StatisticsInfo;
import com.sparta.outsouringproject.statistics.entity.OrderHistory;
import com.sparta.outsouringproject.statistics.repository.OrderHistoryRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StatisticsServiceImplTest {

    @Autowired
    private StatisticsServiceImpl service;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void 일일_매출액_조회() throws Exception {
        // given
        createOrderHistory(2000L, 1L, 1L, LocalDateTime.now());
        createOrderHistory(3000L, 1L, 2L, LocalDateTime.now());
        createOrderHistory(5000L, 1L, 3L, LocalDateTime.now());

        // when
        Long dailySalesAmount = service.getDailySalesAmount();

        // then
        assertThat(dailySalesAmount).isEqualTo(10000L);
    }

    @Test
    public void 특정가게_일일_매출액_조회() throws Exception {
        // given
        createOrderHistory(2000L, 1L, 1L, LocalDateTime.now());
        createOrderHistory(3000L, 1L, 2L, LocalDateTime.now());
        createOrderHistory(5000L, 2L, 3L, LocalDateTime.now());

        // when
        Long dailySalesAmount = service.getDailySalesAmount(1L);

        // then
        assertThat(dailySalesAmount).isEqualTo(5000L);
    }

    @Test
    public void 일일_주문건수_조회() throws Exception {
        // given
        createOrderHistory(2000L, 1L, 1L, LocalDateTime.now());
        createOrderHistory(3000L, 1L, 2L, LocalDateTime.now());
        createOrderHistory(5000L, 1L, 3L, LocalDateTime.now());

        // when
        Long dailyOrderCount = service.getDailyOrderCount();

        // then
        assertThat(dailyOrderCount).isEqualTo(3);
    }

    @Test
    public void 특정가게_일일_주문건수_조회() throws Exception {
        // given
        createOrderHistory(2000L, 1L, 1L, LocalDateTime.now());
        createOrderHistory(3000L, 1L, 2L, LocalDateTime.now());
        createOrderHistory(5000L, 2L, 3L, LocalDateTime.now());

        // when
        Long dailyOrderCount = service.getDailyOrderCount(1L);

        // then
        assertThat(dailyOrderCount).isEqualTo(2);
    }

    @Test
    public void 월_매출액_조회() throws Exception {
        // given
        createOrderHistory(2000L, 1L, 1L, LocalDateTime.now());
        createOrderHistory(3000L, 1L, 2L, LocalDateTime.now());
        createOrderHistory(5000L, 1L, 3L, LocalDateTime.now());
        createOrderHistory(2000L, 1L, 4L, LocalDateTime.now().minusDays(1));
        createOrderHistory(3000L, 1L, 5L, LocalDateTime.now().minusDays(1));
        createOrderHistory(5000L, 1L, 6L, LocalDateTime.now().minusDays(1));

        // when
        Long monthlySalesAmount = service.getMonthlySalesAmount();

        // then
        assertThat(monthlySalesAmount).isEqualTo(20000L);
    }

    @Test
    public void 특정가게_월_매출액_조회() throws Exception {
        // given
        createOrderHistory(2000L, 1L, 1L, LocalDateTime.now());
        createOrderHistory(3000L, 1L, 2L, LocalDateTime.now());
        createOrderHistory(5000L, 1L, 3L, LocalDateTime.now());
        createOrderHistory(2000L, 2L, 4L, LocalDateTime.now().minusDays(1));
        createOrderHistory(3000L, 2L, 5L, LocalDateTime.now().minusDays(1));
        createOrderHistory(5000L, 2L, 6L, LocalDateTime.now().minusDays(1));

        // when
        Long monthlySalesAmount = service.getMonthlySalesAmount(2L);

        // then
        assertThat(monthlySalesAmount).isEqualTo(10000L);
    }

    @Test
    public void 월_주문건수_조회() throws Exception {
        // given
        createOrderHistory(2000L, 1L, 1L, LocalDateTime.now());
        createOrderHistory(3000L, 1L, 2L, LocalDateTime.now());
        createOrderHistory(5000L, 1L, 3L, LocalDateTime.now());
        createOrderHistory(2000L, 1L, 4L, LocalDateTime.now().minusDays(1));
        createOrderHistory(3000L, 1L, 5L, LocalDateTime.now().minusDays(1));
        createOrderHistory(5000L, 1L, 6L, LocalDateTime.now().minusDays(1));

        // when
        Long monthlyOrderCount = service.getMonthlyOrderCount();

        // then
        assertThat(monthlyOrderCount).isEqualTo(6);
    }

    @Test
    public void 특정가게_월_주문건수_조회() throws Exception {
        // given
        createOrderHistory(2000L, 1L, 1L, LocalDateTime.now());
        createOrderHistory(3000L, 1L, 2L, LocalDateTime.now());
        createOrderHistory(5000L, 2L, 3L, LocalDateTime.now());
        createOrderHistory(2000L, 2L, 4L, LocalDateTime.now().minusDays(1));
        createOrderHistory(3000L, 1L, 5L, LocalDateTime.now().minusDays(1));
        createOrderHistory(5000L, 1L, 6L, LocalDateTime.now().minusDays(1));

        // when
        Long monthlyOrderCount = service.getMonthlyOrderCount(2L);

        // then
        assertThat(monthlyOrderCount).isEqualTo(2);
    }

    @Test
    public void 가게이름불러오기_실패() throws Exception {
        // given
        Store store = new Store();
        ReflectionTestUtils.setField(store,"name", "BHC");
        store = storeRepository.save(store);

        // when
        assertThatThrownBy(() -> service.getStoreName(Long.MAX_VALUE))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage("존재하지 않는 가게입니다.");
        // then
    }

    @Test
    public void 가게이름불러오기_성공() throws Exception {
        // given
        Store store = new Store();
        ReflectionTestUtils.setField(store,"name", "BHC");
        store = storeRepository.save(store);
        // when
        String storeName = service.getStoreName(store.getStoreId());
        assertThat(storeName).isEqualTo("BHC");
        assertThat(storeName).isEqualTo(store.getName());
        // then
    }

    @Test
    public void 통계데이터_조회() throws Exception {
        // given

        // when
        StatisticsInfo statistics = service.getStatistics(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        // then
        assertThat(statistics.getTotalOrderCount()).isEqualTo(0L);
        assertThat(statistics.getTotalSalesAmount()).isEqualTo(0L);
    }

    @Test
    public void 특정가게_통계데이터_조회_성공() throws Exception {
        // given
        Store store = new Store();
        ReflectionTestUtils.setField(store,"name", "BHC");
        store = storeRepository.save(store);

        // when
        StatisticsInfo statistics = service.getStatistics(store.getStoreId(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        // then
        assertThat(statistics.getTotalOrderCount()).isEqualTo(0L);
        assertThat(statistics.getTotalSalesAmount()).isEqualTo(0L);
    }

    @Test
    public void 특정가게_통계데이터_조회_실패() throws Exception {
        // when then
        assertThatThrownBy(() -> service.getStatistics(Long.MAX_VALUE, LocalDate.now(), LocalDate.now()))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage("존재하지 않는 가게입니다.");

    }

    private Long createOrderHistory(long price, long storeId, long orderId, LocalDateTime soldDate) throws Exception {
        OrderHistory orderHistory = new OrderHistory(soldDate, storeId, orderId, 1L, 1L, "", 1L, price, price);
        return orderHistoryRepository.save(orderHistory).getId();
    }
}