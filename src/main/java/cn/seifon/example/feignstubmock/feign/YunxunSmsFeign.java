package cn.seifon.example.feignstubmock.feign;

import cn.seifon.example.feignstubmock.dto.YunxunSmsReqDto;
import cn.seifon.example.feignstubmock.dto.YunxunSmsRespDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: Seifon
 * @Description:
 * @Date: Created in 10:24 2019/1/7
 */
@FeignClient(name = "smsclient", url = "${sms.url}", primary = false)
public interface YunxunSmsFeign {

    /**
     *
     * @param request
     * @return {"code":"0","failNum":"0","successNum":"1","msgId":"19012516213625881","time":"20190125162136","errorMsg":""}
     * @return {"code":"107","msgId":"","time":"20190125162358","errorMsg":"手机号码格式错误"}
     */
    @PostMapping
    YunxunSmsRespDto send(@RequestBody YunxunSmsReqDto request);
}
