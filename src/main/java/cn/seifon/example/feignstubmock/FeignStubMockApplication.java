package cn.seifon.example.feignstubmock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
public class FeignStubMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignStubMockApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        //用于调用"提供者"的方法
        return new RestTemplate();
    }

    @Bean("urlRestTemplate")
    public RestTemplate urlRestTemplate() {
        //用于调用"提供者"的方法
        return new RestTemplate();
    }
}

