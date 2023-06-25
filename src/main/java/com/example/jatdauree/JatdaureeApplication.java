package com.example.jatdauree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// exclude는 나중에 DB스키마 연결하면 제거할것
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class JatdaureeApplication {

	public static void main(String[] args) {

		SpringApplication.run(JatdaureeApplication.class, args);

		long heapSize = Runtime.getRuntime().totalMemory();
		System.out.println("HEAP Size(M) : "+ heapSize / (1024*1024) + " MB");
	}

}
