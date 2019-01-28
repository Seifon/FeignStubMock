package cn.seifon.example.feignstubmock.feign.mock;

import cn.seifon.example.feignstubmock.feign.YunxunSmsFeign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @Author: Seifon
 * @Description:
 * @Date: Created in 10:24 2019/1/7
 */
@Primary //注意：需要在原Feign接口@FeignClient注解加入primary = false 属性
@Component
@ConditionalOnProperty(name = "feign-stub.yunxun.sms.mode", havingValue = "mock")
@FeignClient(name = "smsclient-mock", url = "${feign-stub.yunxun.sms.mockUrl}" ,path = "/")
public interface YunxunSmsFeignMock extends YunxunSmsFeign {

}
