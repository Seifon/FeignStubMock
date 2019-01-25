package cn.seifon.example.feignstubmock.feign.stub;

import cn.seifon.example.feignstubmock.dto.YunxunSmsReqDto;
import cn.seifon.example.feignstubmock.dto.YunxunSmsRespDto;
import cn.seifon.example.feignstubmock.feign.YunxunSmsFeign;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: XiongFeng
 * @Description:
 * @Date: Created in 10:24 2019/1/7
 */
@Primary //注意：需要在原Feign接口@FeignClient注解加入primary = false 属性
@Component
@ConditionalOnProperty(name = "feign-stub.yunxun.sms.mode", havingValue = "stub")
public class YunxunSmsFeignStub implements YunxunSmsFeign {
    private static final Logger LOG = LoggerFactory.getLogger(YunxunSmsFeignStub.class);

    @Override
    public YunxunSmsRespDto send(YunxunSmsReqDto request) {
        YunxunSmsRespDto yunxunSmsRespDto = new YunxunSmsRespDto();
        yunxunSmsRespDto.setCode("0");
        yunxunSmsRespDto.setFailNum("0");
        yunxunSmsRespDto.setSuccessNum("1");
        yunxunSmsRespDto.setMsgId(String.valueOf(RandomUtils.nextLong(19000000000000000L, 19999999999999999L)));
        yunxunSmsRespDto.setTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        yunxunSmsRespDto.setErrorMsg("");

        String params = request.getParams();
        String[] paramSplit = StringUtils.split(params, ",");
        if (paramSplit[0].length() != 11) {
            yunxunSmsRespDto.setCode("107");
            yunxunSmsRespDto.setMsgId("");
            yunxunSmsRespDto.setErrorMsg("手机号码格式错误");
        }
        return yunxunSmsRespDto;
    }
}
