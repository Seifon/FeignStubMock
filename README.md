#### 背景：

在项目开发中，会有调用第三方接口的场景。当开发时，对方不愿意提供测试服务器给我们调用，或者有的接口会按调用次数进行计费。当联调时，第三方的测试服务器也可能会出现不稳定，如果他们的服务挂了，我们就一直等着服务恢复，那么这就相当影响效率了。如果我们在开发时，就定义一个挡板或者mock服务，在发起调用时，不直接调到第三方接口，而是调到我们自己的挡板代码或者mock服务，这样就可以避免这些问题了。

> 优势：

- 挡板代码，不需要侵入业务代码，可以根据入参做一些动态结果返回
- 不需要专门开发一个挡板服务，并且在每次启动客户端都先启动挡板服务
- 可以自由选择使用挡板还是Mock数据

> Demo详细代码，已经提交到Github，欢迎star

Demo地址: https://github.com/Seifon/FeignStubMock

---

#### 一、下面我就以一个第三方SMS短信接口来做演示：

首先，我们写一个Feign客户端接口，正常调用第三方接口：

##### 1.定义一个SMS短信的Feign客户端接口：


```
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
    @PostMapping("/msg/variable/json")
    YunxunSmsRespDto send(@RequestBody YunxunSmsReqDto request);
}

```

> 注意：@FeignClient注解里面的primary属性一定要设置为false,这是为了防止在开启Feign挡板时，出现多个Feign客户端导致启动报错。

##### 2.写一个单元测试：

```
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
```

###### 3.1.我们输入一个正确的手机号，拿一个成功的结果：

```
2019-01-28 11:17:56.718 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] ---> POST http://smssh1.253.com/msg/variable/json HTTP/1.1
2019-01-28 11:17:56.719 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] Content-Type: application/json;charset=UTF-8
2019-01-28 11:17:56.720 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] Content-Length: 160
2019-01-28 11:17:56.720 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] 
2019-01-28 11:17:56.721 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] {"account":"XXXXXX","password":"XXXXXXX","msg":"登录验证码:{$var}，请不要对非本人透露。","params":"17311112222,123456","report":"true"}
2019-01-28 11:17:56.721 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] ---> END HTTP (160-byte body)
2019-01-28 11:17:56.958 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] <--- HTTP/1.1 200 OK (236ms)
2019-01-28 11:17:56.960 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] connection: keep-alive
2019-01-28 11:17:56.962 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] content-length: 109
2019-01-28 11:17:56.963 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] content-type: application/json;charset=UTF-8
2019-01-28 11:17:56.965 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] date: Mon, 28 Jan 2019 03:17:56 GMT
2019-01-28 11:17:56.966 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] 
2019-01-28 11:17:56.971 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] {"code":"0","failNum":"0","successNum":"1","msgId":"19012811175621982","time":"20190128111756","errorMsg":""}
2019-01-28 11:17:56.972 DEBUG 6920 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] <--- END HTTP (109-byte body)
{"code":"0","failNum":"0","successNum":"1","msgId":"19012811175621982","time":"20190128111756","errorMsg":""}
```

此时，我们可以根据日志，看到请求的地址也是第三方的url


###### 3.2.我们输入一个错误的手机号，拿一个失败的结果：

```
2019-01-28 11:21:15.300 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] ---> POST http://smssh1.253.com/msg/variable/json HTTP/1.1
2019-01-28 11:21:15.301 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] Content-Type: application/json;charset=UTF-8
2019-01-28 11:21:15.302 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] Content-Length: 152
2019-01-28 11:21:15.302 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] 
2019-01-28 11:21:15.303 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] {"account":"XXXXX","password":"XXXXXXX","msg":"登录验证码:{$var}，请不要对非本人透露。","params":"173,123456","report":"true"}
2019-01-28 11:21:15.303 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] ---> END HTTP (152-byte body)
2019-01-28 11:21:15.470 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] <--- HTTP/1.1 200 OK (165ms)
2019-01-28 11:21:15.471 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] connection: keep-alive
2019-01-28 11:21:15.473 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] content-length: 87
2019-01-28 11:21:15.474 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] content-type: application/json;charset=UTF-8
2019-01-28 11:21:15.476 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] date: Mon, 28 Jan 2019 03:21:15 GMT
2019-01-28 11:21:15.477 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] 
2019-01-28 11:21:15.483 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] {"code":"107","msgId":"","time":"20190128112115","errorMsg":"手机号码格式错误"}
2019-01-28 11:21:15.484 DEBUG 5288 --- [           main] c.s.e.f.feign.YunxunSmsFeign             : [YunxunSmsFeign#send] <--- END HTTP (87-byte body)
{"code":"107","msgId":"","time":"20190128112115","errorMsg":"手机号码格式错误"}
```

当我们知道了两种情况下出现的结果，那么我们就可以模拟响应结果啦。小技巧：我们可以先跟对方调接口，把各种响应报文保存下来，方便后面直接mock数据

---

#### 二、接下来进入挡板编写环节：

##### 1.编写一个YunxunSmsFeignStub类，并实现YunxunSmsFeign接口：

```
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
 * @Author: Seifon
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

        //模拟正常响应结果
        yunxunSmsRespDto.setCode("0");
        yunxunSmsRespDto.setFailNum("0");
        yunxunSmsRespDto.setSuccessNum("1");
        yunxunSmsRespDto.setMsgId(String.valueOf(RandomUtils.nextLong(19000000000000000L, 19999999999999999L)));
        yunxunSmsRespDto.setTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        yunxunSmsRespDto.setErrorMsg("");

        String params = request.getParams();
        String[] paramSplit = StringUtils.split(params, ",");
        if (paramSplit[0].length() != 11) {
            //模拟错误响应结果
            yunxunSmsRespDto.setCode("107");
            yunxunSmsRespDto.setMsgId("");
            yunxunSmsRespDto.setErrorMsg("手机号码格式错误");
        }
        return yunxunSmsRespDto;
    }
}
```

> 注意：必须标注@Primary注解,否则启动会报错。@ConditionalOnProperty的作用就是根据application.yaml配置的相关属性，判断是否注入Spring容器

##### 2.application.yaml文件，加入下面的配置：

```
sms:
    url: 'http://smssh1.253.com'

#yunxun：代表第三方系统名称，sms：代表业务名称，mode:代表Stub模式，url：代表mock服务地址
feign-stub:
    yunxun:
        sms:
            mode: 'stub'
```

##### 3.为了区分返回的内容是挡板结果，我们可以写一个AOP切面打印日志：


```
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: Seifon
 * @Description:
 * @Date: Created in 10:24 2019/1/7
 */
@Aspect
@Component
public class FeignStubAspect {

    private static final Logger LOG = LoggerFactory.getLogger(FeignStubAspect.class);

    @Pointcut("execution(* cn.seifon.example.feignstubmock..stub.*.*(..))")
    public void pointCut(){}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp){
        String name = StringUtils.join(pjp.getTarget().getClass().getName(), ".", pjp.getSignature().getName());
        LOG.info("-----【{}】---- 进入挡板模式... request: 【{}】", name, JSON.toJSON(pjp.getArgs()));
        try {
            Object proceed = pjp.proceed();
            LOG.info("-----【{}】---- 退出挡板模式... request: 【{}】, response: 【{}】", name, JSON.toJSON(pjp.getArgs()), JSON.toJSON(proceed));
            return proceed;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

}

```

###### 4.1.运行之前写的单元测试代码（输入一个正确的手机号）：

```
2019-01-28 11:32:51.255  INFO 7488 --- [           main] c.s.e.f.aspect.FeignStubAspect           : -----【cn.seifon.example.feignstubmock.feign.stub.YunxunSmsFeignStub.send】---- 进入挡板模式... request: 【[{"msg":"登录验证码:{$var}，请不要对非本人透露。","password":"XXXXXXX","report":"true","params":"13011112222,123456","account":"XXXXXXX"}]】
2019-01-28 11:32:51.975  INFO 7488 --- [           main] c.s.e.f.aspect.FeignStubAspect           : -----【cn.seifon.example.feignstubmock.feign.stub.YunxunSmsFeignStub.send】---- 退出挡板模式... request: 【[{"msg":"登录验证码:{$var}，请不要对非本人透露。","password":"XXXXXXX","report":"true","params":"13011112222,123456","account":"XXXXXXX"}]】, response: 【{"code":"0","failNum":"0","successNum":"1","msgId":"19148964234899564","time":"20190128113251","errorMsg":""}】
{"code":"0","failNum":"0","successNum":"1","msgId":"19148964234899564","time":"20190128113251","errorMsg":""}
```

###### 4.2.运行之前写的单元测试代码（输入一个错误的手机号）：

```
2019-01-28 11:35:27.177  INFO 15204 --- [           main] c.s.e.f.aspect.FeignStubAspect           : -----【cn.seifon.example.feignstubmock.feign.stub.YunxunSmsFeignStub.send】---- 进入挡板模式... request: 【[{"msg":"登录验证码:{$var}，请不要对非本人透露。","password":"XXXXXXX","report":"true","params":"130,123456","account":"XXXXXXX"}]】
2019-01-28 11:35:27.900  INFO 15204 --- [           main] c.s.e.f.aspect.FeignStubAspect           : -----【cn.seifon.example.feignstubmock.feign.stub.YunxunSmsFeignStub.send】---- 退出挡板模式... request: 【[{"msg":"登录验证码:{$var}，请不要对非本人透露。","password":"XXXXXXX","report":"true","params":"130,123456","account":"XXXXXXX"}]】, response: 【{"code":"107","failNum":"0","successNum":"1","msgId":"","time":"20190128113527","errorMsg":"手机号码格式错误"}】
{"code":"107","failNum":"0","successNum":"1","msgId":"","time":"20190128113527","errorMsg":"手机号码格式错误"}
```

以上代码就完成了一个stub挡板功能，可有时候，我们已经拿到第三方接口的返回报文，并切不想去写一大段Stub代码。那么这个时候，我们就可以选择下面的Mock方式去完成我们的功能。

---

#### 三、接下来进入Mock环节：

##### 1. 首先准备一个mock服务，这里我就用自己比较喜欢的一个mock工具（mock-json-server）给大家演示：

###### 1.1 安装nodejs：


```
参看官网：http://nodejs.cn/
```

###### 1.2 安装mock-json-server：

```
npm install -g mock-json-server
```

###### 1.3 新建mock数据文件(命名为：data.json)：

```
{
  "/msg/variable/json": {
    "post": {
      "code":"0",
      "failNum":"0",
      "successNum":"1",
      "msgId":"19012516213625881",
      "time":"20190125162136",
      "errorMsg":""
    }
  }
}
```

###### 1.4 运行：

```
mock-json-server {path}/data.json --port=1240

{path}替换为存放data.json的绝对路径
```

###### 1.5 如果显示如下结果，就代表mock服务运行成功：

```
JSON Server running at http://localhost:1240/
```


> mock-json-server具体使用文档，请参考：https://www.npmjs.com/package/mock-json-server




##### 2. 准备工作做好后，接下来，就进入Mock正式环节：

###### 2.1 首先，我们定义一个YunxunSmsFeignMock接口，并且继承YunxunSmsFeign接口
```
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

```

注意：必须标注@Primary注解，否则启动时会报错。@FeignClient里的name属性不能跟原Feign接口名称相同，如果相同会启动报错。@ConditionalOnProperty的作用就是根据application.yaml配置的相关属性，判断是否注入Spring容器


###### 2.2 application.yaml文件，加入下面的配置：
```
sms:
    url: 'http://smssh1.253.com'

#生产环境请勿添加此配置。mode说明：''-不开启, 'mock'-mock模式, 'stub'-stub模式。url说明：只有mock模式需要配置调试url。fund为第三方机构，repayment是业务名称
#yunxun：代表第三方系统名称，sms：代表业务名称，mode:代表挡板模式，url：代表mock服务地址
feign-stub:
    yunxun:
        sms:
            mode: 'mock'
            mockUrl: "http://localhost:1240"
```

###### 2.3 为了区分返回的内容是Mock结果，我们可以写一个AOP切面打印日志：

```
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: Seifon
 * @Description:
 * @Date: Created in 10:24 2019/1/7
 */
@Aspect
@Component
public class FeignMockAspect {

    private static final Logger LOG = LoggerFactory.getLogger(FeignMockAspect.class);

    @Pointcut("execution(* cn.seifon.example.feignstubmock..mock.*.*(..))")
    public void pointCut(){}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp){
        String name = StringUtils.join(pjp.getTarget().getClass().getName(), ".", pjp.getSignature().getName());
        LOG.info("-----【{}】---- 进入Mock模式... request: 【{}】", name, JSON.toJSON(pjp.getArgs()));
        try {
            Object proceed = pjp.proceed();
            LOG.info("-----【{}】---- 退出Mock模式... request: 【{}】, response: 【{}】", name, JSON.toJSON(pjp.getArgs()), JSON.toJSON(proceed));
            return proceed;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

}
```


###### 2.4 运行之前的单元测试类，得到如下结果：

```
2019-01-28 16:16:35.567  INFO 8976 --- [           main] c.s.e.f.aspect.FeignMockAspect           : -----【com.sun.proxy.$Proxy95.send】---- 进入Mock模式... request: 【[{"msg":"登录验证码:{$var}，请不要对非本人透露。","password":"XXXXXXX","report":"true","params":"13011112222,123456","account":"XXXXXXX"}]】
2019-01-28 16:16:35.934 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] ---> POST http://localhost:1240/msg/variable/json HTTP/1.1
2019-01-28 16:16:35.935 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] Content-Type: application/json;charset=UTF-8
2019-01-28 16:16:35.936 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] Content-Length: 152
2019-01-28 16:16:35.936 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] 
2019-01-28 16:16:35.937 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] {"account":"XXXXXXX","password":"XXXXXXX","msg":"登录验证码:{$var}，请不要对非本人透露。","params":"13011112222,123456","report":"true"}
2019-01-28 16:16:35.937 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] ---> END HTTP (152-byte body)
2019-01-28 16:16:36.021 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] <--- HTTP/1.1 200 OK (82ms)
2019-01-28 16:16:36.021 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] access-control-allow-origin: *
2019-01-28 16:16:36.022 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] connection: keep-alive
2019-01-28 16:16:36.023 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] content-length: 109
2019-01-28 16:16:36.023 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] content-type: application/json; charset=utf-8
2019-01-28 16:16:36.024 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] date: Mon, 28 Jan 2019 08:16:36 GMT
2019-01-28 16:16:36.024 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] etag: W/"6d-XqhLoZB8r6IRF2Lb6CWoIVVNhIQ"
2019-01-28 16:16:36.025 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] x-content-type-options: nosniff
2019-01-28 16:16:36.026 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] x-powered-by: Express
2019-01-28 16:16:36.027 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] 
2019-01-28 16:16:36.030 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] {"code":"0","failNum":"0","successNum":"1","msgId":"19012516213625881","time":"20190125162136","errorMsg":""}
2019-01-28 16:16:36.030 DEBUG 8976 --- [           main] c.s.e.f.feign.mock.YunxunSmsFeignMock    : [YunxunSmsFeignMock#send] <--- END HTTP (109-byte body)
2019-01-28 16:16:36.227  INFO 8976 --- [           main] c.s.e.f.aspect.FeignMockAspect           : -----【com.sun.proxy.$Proxy95.send】---- 退出Mock模式... request: 【[{"msg":"登录验证码:{$var}，请不要对非本人透露。","password":"XXXXXXX","report":"true","params":"13011112222,123456","account":"XXXXXXX"}]】, response: 【{"code":"0","failNum":"0","successNum":"1","msgId":"19012516213625881","time":"20190125162136","errorMsg":""}】
{"code":"0","failNum":"0","successNum":"1","msgId":"19012516213625881","time":"20190125162136","errorMsg":""}
```

说明：此时我们根据日志，会发现feign调用的url已经变为我们的Mock服务地址了。同理，如果要返回失败结果，只需要修改data.json文件，再次调用后，即可得到我们想要的结果了。

---

#### 四、结语：

如果有什么需要改进的地方，或者不正确的地方，请在评论里面提出并指正。谢谢！


> Demo详细代码，已经提交到Github，欢迎star

Demo地址: https://github.com/Seifon/FeignStubMock

项目结构,如图：
![](https://github.com/Seifon/FeignStubMock/raw/master/package_tree.png)

原文地址：[http://www.seifon.cn/2019/01/28/Feign-Stub挡板和Mock/](http://www.seifon.cn/2019/01/28/Feign-Stub挡板和Mock/)
