package cn.seifon.example.feignstubmock.dto;

/**
 * @Author: Xiongfeng
 * @Description:
 * @Date: Created in  2019/1/22
 */
public class YunxunSmsReqDto {

    private String account;
    private String password;
    private String msg;
    private String params;
    private String report;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
