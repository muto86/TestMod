package com.xjcmpt.mylibrary.http;

import java.util.HashMap;
import java.util.Map;

public interface IRequest {
    public String getBaseUrl();
    public String getMethod();
    public IEncrypt getEncrypt();
    public HashMap<String, Object> getParam();
    public Map<String, FilePair> getFilePair();
    public Map<String, String> getHeaders();
}
