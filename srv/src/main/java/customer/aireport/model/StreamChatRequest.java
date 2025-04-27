package customer.aireport.model;

import lombok.Data;

@Data
public class StreamChatRequest {
    private  String id;
    private  Boolean isActiveEntity;
    private  String content;
    private  String locale;
}