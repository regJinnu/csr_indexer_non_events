package com.gdn.qa.x_search.api.test.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BwaSearchEventModel {
    private String accountid;
    private String userid;
    private String sessionid;
    private String searchinternalkeyword;
    private String searchinternalcategoryid;
    private String searchinternalcategoryname;
    private String clientmemberid;
    private String pageurl;
    private String pagetype;
    private String device;
    private String devicetype;
    private String browser;
    private String browserversion;
}