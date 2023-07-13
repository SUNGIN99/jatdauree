package com.example.jatdauree.src.domain.sales.dto;

import com.example.jatdauree.src.domain.sales.service.SalesService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class aa {


    public static void main(String[] args) throws ParseException {

        class CompareItem{
            int totalSales;
            ItemSalesReOrNew itemSalesReOrNew;

            public CompareItem(int totalSales, ItemSalesReOrNew itemSalesReOrNew) {
                this.totalSales = totalSales;
                this.itemSalesReOrNew = itemSalesReOrNew;
            }
        }

        PriorityQueue<CompareItem> queue = new PriorityQueue<>(
                new Comparator<CompareItem>() {
                    @Override
                    public int compare(CompareItem o1, CompareItem o2) {
                        return o2.totalSales - o1.totalSales;
                    }
                }
        );

        queue.add(new CompareItem(2000, null));
        queue.add(new CompareItem(3000, null));
        queue.add(new CompareItem(4000, null));
        queue.add(new CompareItem(5000, null));


        Object[] a = queue.toArray();

        /*while(!queue.isEmpty()) {
            CompareItem a = queue.poll();
            System.out.println(a.totalSales + ", " +a.itemSalesReOrNew );
        }*/
    }
}
