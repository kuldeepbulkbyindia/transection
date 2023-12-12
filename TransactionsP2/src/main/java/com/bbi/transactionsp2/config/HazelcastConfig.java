package com.bbi.transactionsp2.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastConfigCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class HazelcastConfig {
    @Autowired
    CacheManager cacheManager;
    final int timeToLiveSeconds = 60000;
    String[] cacheNames = {"'allOrdersWithUsers'", "'allOrders'", "'viewAllTransactions'", "'allOrdersWithUsersAndTransactions'","transactions", "'#orderId'", "'#userId + '-' + #orderId'"};

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        for (String cacheName : cacheNames) {
            config.getMapConfig(cacheName).setTimeToLiveSeconds(timeToLiveSeconds);
        }

        System.out.println("Hazelcast is enabled for 60 seconds");
        return Hazelcast.newHazelcastInstance(config);
    }



    @Scheduled(fixedRate = 60000) // 60 seconds
    public void evictAllCaches() {
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }
}
