package cn.seifon.example.feignstubmock;

import cn.seifon.example.feignstubmock.dto.YunxunSmsReqDto;
import cn.seifon.example.feignstubmock.dto.YunxunSmsRespDto;
import cn.seifon.example.feignstubmock.feign.YunxunSmsFeign;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignStubMockApplicationTests {
    @Autowired
    private YunxunSmsFeign yunxunSmsFeign;

    @Test
    public void feignStubMockTest() {
        YunxunSmsReqDto yunxunSmsReqDto=new YunxunSmsReqDto();
        yunxunSmsReqDto.setAccount("XXXXXXX");
        yunxunSmsReqDto.setPassword("XXXXXXX");
        yunxunSmsReqDto.setMsg("登录验证码:{$var}，请不要对非本人透露。");
        yunxunSmsReqDto.setParams("13011112222,123456");
        yunxunSmsReqDto.setReport("true");

        YunxunSmsRespDto send = yunxunSmsFeign.send(yunxunSmsReqDto);

        //打印结果
        System.out.println(JSON.toJSON(send));
    }

}

