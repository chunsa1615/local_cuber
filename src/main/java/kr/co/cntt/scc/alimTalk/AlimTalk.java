package kr.co.cntt.scc.alimTalk;

import java.util.Date;

import lombok.Data;

@Data
public class AlimTalk {

    public AlimTalk() {
        
    }
    
    public AlimTalk(String branchId) {
        //this.order_id = order_id;
    	this.branchId = branchId;
    }

    //스터디코디
    private String branchId;
    private String branchType;
    private String name;    
    private String time;
    private String entry_type;        
    private String roomName;
    private String deskName;
    private String memberName;
    private String phone_number;
    private String school;
    private String registration;
    private String amount;
    
    // 알림톡 관련 정보
    private String type;
    private String sender_key;
    private String template_cd;
    private String template_message;
    private String message;

    // 기타
    private String link_nm;
    private String link_url;

    @Data
    public static class Request {
        String serial_number;
        String sender_key;
        String phone_number;
        String template_code;
        String message;
        String response_method;
        int timeout;
        Attachment attachment;
    }

    @Data
    public static class Response {
        String code;
        String received_at;
        String message;

        public Response() {
            this.code = "0000";
            this.message = "SUCCESS";
        }
    }

    @Data
    public static class Attachment {
        Button button;
    }

    @Data
    public static class Button {
        String name;
        String url;

        Button(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
}
