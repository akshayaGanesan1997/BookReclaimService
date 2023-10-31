package com.books.bookmarketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.books.bookmarketplace.entity")
public class BookMarketPlaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookMarketPlaceApplication.class, args);
	}

}
