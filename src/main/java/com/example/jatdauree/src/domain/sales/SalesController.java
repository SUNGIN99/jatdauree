package com.example.jatdauree.src.domain.sales;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.sales.dto.MonthlyMenuSalesRes;
import com.example.jatdauree.src.domain.sales.dto.TodayTotalSalesRes;
import com.example.jatdauree.src.domain.sales.dto.YtoTWeekSalesRes;
import com.example.jatdauree.src.domain.sales.dto.YtoTdaySalesRes;
import com.example.jatdauree.src.domain.sales.service.SalesService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jat/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private JwtService jwtService;

    @ResponseBody
    @GetMapping ("/today")
    public BaseResponse<TodayTotalSalesRes> getTodayTotalSales() {
        try{
            int sellerIdx = jwtService.getUserIdx();

            TodayTotalSalesRes totalSalesRes = salesService.getTodayTotalSales(sellerIdx);
            return new BaseResponse<>(totalSalesRes);

        } catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }

    }

    @ResponseBody
    @GetMapping("/yesterday")
    public BaseResponse<YtoTdaySalesRes> getFromYtoTdaySales(){
        try{
            int sellerIdx = jwtService.getUserIdx();

            YtoTdaySalesRes ytoTdaySalesRes = salesService.getFromYtoTdaySales(sellerIdx);
            return new BaseResponse<>(ytoTdaySalesRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/weekday")
    public BaseResponse<YtoTWeekSalesRes> getFromLtoTWeekSales(){
        try{
            int sellerIdx = jwtService.getUserIdx();

            YtoTWeekSalesRes ytoTWeekSalesRes = salesService.getFromLtoTWeekSales(sellerIdx);
            return new BaseResponse<>(ytoTWeekSalesRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/monthly")
    public BaseResponse<MonthlyMenuSalesRes> getMontlyMenuSales(@RequestParam int month){
        try{
            int sellerIdx = jwtService.getUserIdx();

            MonthlyMenuSalesRes monthlyMenuSalesRes = salesService.getMontlyMenuSales(month, sellerIdx);
            return new BaseResponse<>(monthlyMenuSalesRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


}
