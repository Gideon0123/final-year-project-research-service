package com.example.RESEARCH_SERVICE.config;

import com.example.RESEARCH_SERVICE.utils.CacheNames;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(
            ObjectMapper objectMapper
    ) {

        ObjectMapper mapper = objectMapper.copy();

        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(mapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );
    }

}

//@Configuration
//@RequiredArgsConstructor
//public class CacheConfig {
//
//    private final RedisCacheConfiguration cacheConfiguration;
//
//    @Bean
//    public RedisCacheManager cacheManager(
//            RedisConnectionFactory connectionFactory
//    ) {
//
//        Map<String, RedisCacheConfiguration> configurations = new HashMap<>();
//
//        configurations.put(
//                CacheNames.RESEARCH_CATEGORY,
//                cacheConfiguration.entryTtl(Duration.ofHours(6))
//        );
//
//        configurations.put(
//                CacheNames.RESEARCH_CATEGORY_SEARCH,
//                cacheConfiguration.entryTtl(Duration.ofMinutes(30))
//        );
//
//        configurations.put(
//                CacheNames.RESEARCH_PAPER,
//                cacheConfiguration.entryTtl(Duration.ofMinutes(20))
//        );
//
//        configurations.put(
//                CacheNames.RESEARCH_PAPER_SEARCH,
//                cacheConfiguration.entryTtl(Duration.ofMinutes(10))
//        );
//
//        return RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(cacheConfiguration)
//                .withInitialCacheConfigurations(configurations)
//                .transactionAware()
//                .build();
//    }
//}