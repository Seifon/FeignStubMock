package cn.seifon.example.feignstubmock.dto;

/**
 * @Author: Xiongfeng
 * @Description:
 * @Date: Created in  2019/1/23
 */
public class YunxunSmsRespDto {

    private String code;

    private String failNum;

    private String successNum;

    private String msgId;

    private String time;

    private String errorMsg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFailNum() {
        return failNum;
    }

    public void setFailNum(String failNum) {
        this.failNum = failNum;
    }

    public String getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(String successNum) {
        this.successNum = successNum;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
