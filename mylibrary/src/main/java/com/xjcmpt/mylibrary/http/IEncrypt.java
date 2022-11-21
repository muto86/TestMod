package com.xjcmpt.mylibrary.http;

import java.util.Map;

public interface IEncrypt {
    public String encrypt(String urlPath, Map<String, Object> params);
    public String dencrypt();
}
