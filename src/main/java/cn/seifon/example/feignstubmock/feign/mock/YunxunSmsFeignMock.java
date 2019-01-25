package cn.seifon.example.feignstubmock.feign.mock;

import cn.seifon.example.feignstubmock.feign.YunxunSmsFeign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @Author: XiongFeng
 * @Description:
 * @Date: Created in  2018/5/25
 */
@Primary //注意：需要在原Feign接口@FeignClient注解加入primary = false 属性
@Component
@ConditionalOnProperty(name = "feign-stub.yunxun.sms.mode", havingValue = "mock")
@FeignClient(name = "fs-core", url = "${feign-stub.yunxun.sms.mode.url}" ,path = "/")
public interface YunxunSmsFeignMock extends YunxunSmsFeign {

}
