package com.bbi.transactionsp2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableCaching
public class TransactionsP2Application {

    public static void main(String[] args) {
        SpringApplication.run(TransactionsP2Application.class, args);
    }

}
