package com.example.jatdauree.src.domain.web.sales.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.web.sales.dao.SalesDao;
import com.example.jatdauree.src.domain.web.store.dao.StoreDao;
import com.example.jatdauree.src.domain.web.store.dto.StoreStartClose;
import com.example.jatdauree.src.domain.web.sales.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.jatdauree.config.BaseResponseStatus.POST_STORES_NOT_REGISTERD;

@Service
public class SalesService {

    private SalesDao salesDao;
    private StoreDao storeDao;

    @Autowired
    public SalesService(SalesDao salesDao, StoreDao storeDao) {
        this.salesDao = salesDao;
        this.storeDao = storeDao;
    }


    // 가게 오늘 총 매출량
    public TodayTotalSalesRes getTodayTotalSales(int sellerIdx) throws BaseException {
        // 0) 사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 1) 가게의 오늘 매출 조회
        try {
            TodayTotalSalesRes totalSales = salesDao.getTodayTotalSales(storeIdx);
            return totalSales;
        } catch (Exception e) {
            // 오늘 매출량이 없을 수도 있음.
            return new TodayTotalSalesRes(
                    sellerIdx,
                    new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                    0);
            //throw new BaseException(DATABASE_ERROR); //  가게의 오늘 총 매출 조회에 실패하였습니다.
        }


    }

    public YtoTdaySalesRes getFromYtoTdaySales(int sellerIdx) throws BaseException {
        // 0) 사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }
        // 1) 판매자 가게의 시작/종료 시간 확인
        StoreStartClose storeStartClose;
        try{
           storeStartClose = storeDao.getStoreOpenNClose(storeIdx);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //  : 판매자의 가게 매출 확인용 시간 범위 조회에 실패하였습니다.
        }

        // 2) 어제날짜 오늘날짜 구하기
        String yesterday = new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date().getTime() + 1000 * 60 * 60 * 24 * -1);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // 3) 어제 영업 시간 중 판매량
        List<SalesByTime> yesterdaySales, todaySales; // jdbctemplate.query 로 날리면 결과값없어도 빈 리스트 채워주는듯?
        try {
            yesterdaySales = salesDao.getFromYtoTdaySales(storeIdx, yesterday + " 00:00:00", yesterday + " 23:59:59");
        } catch (Exception e) { // 어제 판매가 없어서 결과가 안 나올때..
            try {
                todaySales = salesDao.getFromYtoTdaySales(storeIdx, today + " 00:00:00", today + " 23:59:59");
                return new YtoTdaySalesRes(storeIdx,
                        storeStartClose.getStore_open() ,
                        storeStartClose.getStore_close(), todaySales, null);
            } catch (Exception e2) {
                throw new BaseException(DATABASE_ERROR); // : 판매자의 가게에 어제/오늘 판매된 기록이 없습니다.
            }
        }

        // 4) 오늘 영업 시간 중 판매량
        try {
            todaySales = salesDao.getFromYtoTdaySales(storeIdx, today + " 00:00:00", today + " 23:59:59");
            return new YtoTdaySalesRes(storeIdx,
                    storeStartClose.getStore_open() ,
                    storeStartClose.getStore_close(), todaySales, yesterdaySales);
        } catch (Exception e) {// 오늘 판매량이 아직 없슬때
            return new YtoTdaySalesRes(storeIdx,
                    storeStartClose.getStore_open() ,
                    storeStartClose.getStore_close(), null, yesterdaySales);
        }
    }

    public String[] getLtoTWeekMontoSun(){ // 이번주 저번주 시작(월)/종료(일) 날짜 구하기
        // 1) 이번주 시작 날짜, 저번주 시작날짜 구하기기
        // 1-1) 오늘 날짜 달력에 적용
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTime(new Date());

        // 기준이 일요일부터 이번 주로 시작해서 월요일로 변환되도록 로직처리
        if(calendar.getTime().getDay() == 0){
            calendar.add(Calendar.DATE, -7);
        }

        // 1-2) 이번주 월요일
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Monday = 1
        String thisweekMon = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);  // Monday + 6 = Sunday(7)
        String thisweekSun = sdf.format(calendar.getTime());

        // 1-3) 저번주 월요일
        calendar.add(Calendar.DATE, -8); // Sunday(7) - 8 = -1 = Saturday(6)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Monday = 1
        String lastweekMon = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);  // Monday + 6 = Sunday(7)
        String lastweekSun = sdf.format(calendar.getTime());

        return new String[] {thisweekMon, thisweekSun, lastweekMon, lastweekSun};
    }

    // 빈 요일값 채워주기 (조회된 요일이 없는 매출은 0)
    public List<SalesByWeekDay> checkValidWeekDay(String weekStart, List<SalesByWeekDay> weekDayList) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date monday = sdf.parse(weekStart);
        int hour_24 = 1000 * 60 * 60 * 24;

        List<SalesByWeekDay> returnWeekDays = new ArrayList<>();

        SalesByWeekDay sunday = new SalesByWeekDay(sdf.format(monday.getTime() + hour_24 * 6)  ,0, 0);
        if (weekDayList.size() != 0){
            if (weekDayList.get(0).getDay() == 0) {// 일요일 맨 뒤로 빼기
                sunday = weekDayList.get(0);
                weekDayList.remove(0);

                if(weekDayList.size() == 6 && sunday != null){ // 모든요일에 값이 다 있을때 일요일을 그냥 맨뒤로 보내기
                    weekDayList.add(sunday);
                    return weekDayList;
                }
            }
        }
        else{ // 주간 매출이 없으면 각 요일별 매출 0원
            for(int i =1; i<=6; i++){ // 1(월요일) ~ 6(토요일)
                returnWeekDays.add(new SalesByWeekDay(sdf.format(monday.getTime() + hour_24 * (i - 1)), i, 0));
            }
            // 0(일요일)
            returnWeekDays.add(sunday);
            return returnWeekDays;
        }

        // 없는 요일 집어넣기
        int head = 1, tail = 6;
        while(weekDayList.size() != 0){
            if (head == weekDayList.get(0).getDay()){
                returnWeekDays.add(weekDayList.get(0));
                weekDayList.remove(0);
            }
            else{ // 넣어야할 요일 값이 현재 조회한 요일 리스트에 없는 경우
                returnWeekDays.add(new SalesByWeekDay(sdf.format(monday.getTime() + hour_24 * (head-1)), head, 0));
            }
            head ++;
        }

        for (int i = head ; i<= tail; i++){
            returnWeekDays.add(new SalesByWeekDay(sdf.format(monday.getTime() + hour_24 * (i - 1)), i, 0));
        }
        returnWeekDays.add(sunday);

        return returnWeekDays;
    }

    public YtoTWeekSalesRes getFromLtoTWeekSales(int sellerIdx) throws BaseException {// 0) 사용자 가게 조회
        // 0) 사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 1) 이번주/저번주 월요일~일요일 날짜 구하기
        // [thisweekMon(0), thisweekSun(1), lastweekMon(2), lastweekSun(3)];
        String[] weekDates = getLtoTWeekMontoSun();

        List<SalesByWeekDay> thisWeek, lastWeek;
        // 2) 이번 주 요일별 판매량
        try{
            thisWeek = salesDao.getFromLtoTWeekSales(storeIdx,
                    weekDates[0] + " 00:00:00",
                    weekDates[1] + " 23:59:59");
            thisWeek = checkValidWeekDay(weekDates[0], thisWeek);

        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 이번주 판매정보 조회를 실패하였습니다.
        }
        // 3) 저번 주 요일별 판매량
        try{
            lastWeek = salesDao.getFromLtoTWeekSales(storeIdx,
                    weekDates[2] + " 00:00:00",
                    weekDates[3] + " 23:59:59");
            lastWeek = checkValidWeekDay(weekDates[2], lastWeek);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 저번주 판매정보 조회를 실패하였습니다.
        }

        return new YtoTWeekSalesRes(storeIdx, thisWeek, lastWeek);
    }


    private class CompareItem{
        int totalSales;
        ItemSalesReOrNew itemSalesReOrNew;

        public CompareItem(int totalSales, ItemSalesReOrNew itemSalesReOrNew) {
            this.totalSales = totalSales;
            this.itemSalesReOrNew = itemSalesReOrNew;
        }

        public ItemSalesReOrNew getItemSalesReOrNew() {
            return itemSalesReOrNew;
        }
    }

    public List<ItemSalesReOrNew> sortHashValues(ArrayList<CompareItem> compItems){
        PriorityQueue<CompareItem> queue = new PriorityQueue<>(
                new Comparator<CompareItem>() {
                    @Override
                    public int compare(CompareItem o1, CompareItem o2) {
                        return o2.totalSales - o1.totalSales;
                    }
                }
        );

        queue.addAll(compItems);

        List<ItemSalesReOrNew> sortedSales = new ArrayList<>();
        while(!queue.isEmpty()){
            CompareItem temp = queue.poll();
            sortedSales.add(temp.getItemSalesReOrNew());
        }

        return sortedSales;

    }

    public MonthlyMenuSalesRes getMontlyMenuSales(int month, int sellerIdx) throws BaseException{
        // 0) 사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 해시 테이블사용
        HashMap<Integer, ItemSalesReOrNew> reOrderTables = new HashMap<>();
        List<ItemSalesReOrNew> itemSalesReorder, itemSalesNeworder;
        // 1) 가게 재 주문 조회
        try{
            itemSalesReorder =  salesDao.getMontlyMenuSales(month, storeIdx, 1);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 1-2) 가게 재 주문 해시테이블에 입력
        for (ItemSalesReOrNew items : itemSalesReorder){
            items.setMenuTotalSales(items.getMenuReOrderPrice());
            reOrderTables.put(items.getMenuIdx(), items);
        }

        // 2) 가게 재 주문 조회
        try{
            itemSalesNeworder =  salesDao.getMontlyMenuSales(month, storeIdx, 0);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }
        System.out.println(itemSalesReorder.toString());
        System.out.println(itemSalesNeworder.toString());

        // 3) 메뉴 별 신규/재 주문 매출 현황 조합 (hashMap)
        for (ItemSalesReOrNew item : itemSalesNeworder){
            if(reOrderTables.get(item.getMenuIdx()) != null){
                reOrderTables.get(item.getMenuIdx()).setMenuNewOrderCount(item.getMenuNewOrderCount());
                reOrderTables.get(item.getMenuIdx()).setMenuNewOrderPrice(item.getMenuNewOrderPrice());

                reOrderTables.get(item.getMenuIdx()). // 신규/재 주문 총 합산 매출
                        setMenuTotalSales(
                        item.getMenuNewOrderPrice() + reOrderTables.get(item.getMenuIdx()).getMenuTotalSales());
            }
            else{
                item.setMenuTotalSales(item.getMenuNewOrderPrice());
                reOrderTables.put(item.getMenuIdx(), item);
            }
        }

        // 판매 매출 기준 정렬 높-> 낮은 순
        ArrayList<CompareItem> compItems = new ArrayList<>();
        for( ItemSalesReOrNew item : reOrderTables.values()){
            compItems.add(new CompareItem(item.getMenuTotalSales(), item));
        }

        List<ItemSalesReOrNew> sortedItemSales = sortHashValues(compItems);

        return new MonthlyMenuSalesRes(storeIdx, month, sortedItemSales);
    }

    public MonthlyMenuOrdersRes getMontlyMenuOrders(int month, int sellerIdx) throws BaseException{
        // 0) 사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 1) 가게의 메뉴 총 주문 수 조회
        int totalMenuOrderCount = 0;
        try{
            totalMenuOrderCount = salesDao.getStoresTotalOrderCount(storeIdx, month);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 메뉴 별 주문 수 조회
        List<ItemSalesOrderRatio> orderRatios = null;
        try{
            orderRatios = salesDao.getMontlyMenuOrders(storeIdx, month);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 3) 메뉴 별 주문 비율 구하기
        int tempTotalCount = 0;
        try{
            if (orderRatios != null && totalMenuOrderCount != 0){
                for(ItemSalesOrderRatio itemSale : orderRatios){
                    tempTotalCount += itemSale.getMenuOrderCount();
                    double menuCharge = Math.round(itemSale.getMenuOrderCount() * 1.0 / totalMenuOrderCount * 10000) / 100.0;
                    itemSale.setMenuCharge(menuCharge);
                }
            }
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }
        if(tempTotalCount != totalMenuOrderCount){
            System.out.println("tempTotalCount: "+ tempTotalCount);
            System.out.println("totalMenuOrderCount: "+ totalMenuOrderCount);
            throw new BaseException(DATABASE_ERROR);
        }

        return new MonthlyMenuOrdersRes(storeIdx, month, totalMenuOrderCount ,orderRatios);

    }
}
