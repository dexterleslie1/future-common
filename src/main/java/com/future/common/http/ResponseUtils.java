package com.future.common.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.json.JSONUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseUtils {

    /**/
    public final static int ValidateCodeError = 30001;
    /**/
    public final static int UsernameOrPasswordError = 30002;

    /**
     * 響應客戶端成功
     *
     * @param response
     * @param dataObject
     */
    public static void writeSuccessResponse(HttpServletResponse response, Object dataObject) {
        if (dataObject instanceof List || dataObject instanceof ArrayList) {
            if (dataObject != null) {
                List l = (List) dataObject;
                for (int i = 0; i < l.size(); i++) {
                    if (l.get(i) == null)
                        l.remove(i);
                }
            }
        }
        ObjectResponse<Object> ar = new ObjectResponse<>();
        ar.setData(dataObject);
        write(response, HttpServletResponse.SC_OK, ar);
    }

    public static String toSuccessJson(Object dataObject) {
        if (dataObject instanceof List || dataObject instanceof ArrayList) {
            if (dataObject != null) {
                List l = (List) dataObject;
                for (int i = 0; i < l.size(); i++) {
                    if (l.get(i) == null)
                        l.remove(i);
                }
            }
        }
        ObjectResponse<Object> ar = new ObjectResponse<>();
        ar.setData(dataObject);
        String jsonStr = toJson(ar);
        return jsonStr;
    }

    public static String toSuccessJson(String key, Object dataObject) {
        if (dataObject instanceof List || dataObject instanceof ArrayList) {
            if (dataObject != null) {
                List l = (List) dataObject;
                for (int i = 0; i < l.size(); i++) {
                    if (l.get(i) == null)
                        l.remove(i);
                }
            }
        }
        ObjectResponse<Map<String, Object>> ar = new ObjectResponse<>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, dataObject);
        ar.setData(map);
        String jsonStr = toJson(ar);
        return jsonStr;
    }

    public static String toFailJSON(BusinessException be) {
        ObjectResponse<Object> ar = new ObjectResponse<>();
        ar.setErrorCode(be.getErrorCode());
        ar.setErrorMessage(be.getMessage());
        String jsonStr = toJson(ar);
        return jsonStr;
    }

    public static String toFailJSON(String errorMessage) {
        BusinessException be = new BusinessException(errorMessage);
        String jsonStr = toFailJSON(be);
        return jsonStr;
    }

    public static String toJson(ObjectResponse response) {
        String JSON = null;
        try {
            JSON = JSONUtil.ObjectMapperInstance.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            //
        }
        return JSON;
    }

    /**
     * 響應客戶端失敗
     *
     * @param response
     * @param be
     */
    public static void writeFailResponse(HttpServletResponse response, int httpStatusCode, BusinessException be) {
        writeFailResponse(response, httpStatusCode, be.getErrorCode(), be.getErrorMessage(), be.getData());
    }

    /**
     * 響應客戶端失敗
     *
     * @param response
     * @param errorMessage
     */
    public static void writeFailResponse(HttpServletResponse response, String errorMessage) {
        writeFailResponse(response, HttpServletResponse.SC_BAD_REQUEST, ErrorCodeConstant.ErrorCodeCommon, errorMessage, null);
    }

    /**
     * 響應客戶端失敗
     *
     * @param response
     * @param errorCode
     * @param errorMessage
     */
    public static void writeFailResponse(HttpServletResponse response, int errorCode, String errorMessage) {
        writeFailResponse(response, HttpServletResponse.SC_BAD_REQUEST, errorCode, errorMessage, null);
    }

    /**
     * 響應客戶端失敗
     *
     * @param response
     * @param errorCode
     * @param errorMessage
     */
    public static void writeFailResponse(HttpServletResponse response,
                                         int httpStatusCode,
                                         int errorCode,
                                         String errorMessage,
                                         Object dataObject) {
        ObjectResponse<Object> objectObjectResponse = new ObjectResponse<>();
        objectObjectResponse.setErrorCode(errorCode);
        objectObjectResponse.setErrorMessage(errorMessage);
        objectObjectResponse.setData(dataObject);
        write(response, httpStatusCode, objectObjectResponse);
    }

    /**
     * 寫 AjaxResponse響應客戶端請求
     *
     * @param response
     * @param ar
     */
    private static void write(HttpServletResponse response, int httpStatusCode, ObjectResponse ar) {
        String jsonStr = toJson(ar);
        write(response, httpStatusCode, jsonStr);
    }

    private static void write(HttpServletResponse response, int httpStatusCode, BaseResponse baseResponse) {
        try {
            String JSON = JSONUtil.ObjectMapperInstance.writeValueAsString(baseResponse);
            write(response, httpStatusCode, JSON);
        } catch (JsonProcessingException e) {
            // 这里基本不可能抛出异常
            write(response, httpStatusCode, "");
        }
    }

    /**
     * 寫JSON 字符串到響應流
     *
     * @param response
     * @param httpStatusCode
     * @param jsonString
     */
    public static void write(HttpServletResponse response, int httpStatusCode, String jsonString) {
        response.setStatus(httpStatusCode);
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(jsonString.getBytes("utf-8"));
            os.flush();
            os.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    //os.flush();
                    os.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 业务成功处理后调用此函数返回成功数据 ObjectResponse<T>
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ObjectResponse<T> successObject(T data) {
        ObjectResponse<T> response = new ObjectResponse<>();
        response.setData(data);
        return response;
    }

    /**
     * 业务成功处理后调用此函数返回成功数据 ListResponse<T>
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ListResponse<T> successList(List<T> data) {
        ListResponse<T> response = new ListResponse<>();
        response.setData(data);
        return response;
    }

    /**
     * 业务成功处理后调用此函数返回成功数据 PageResponse<T>
     *
     * @param data
     * @param currentPage
     * @param size
     * @param totalRecord
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> successPage(List<T> data, int currentPage, int size, int totalRecord) {
        PageResponse<T> response = new PageResponse<>();
        response.setData(data);
        response.setPageSize(size);
        response.setTotalRecord(totalRecord);
        response.setPageNum(currentPage);
        response.setData(data);
        return response;
    }

    /**
     * 业务处理失败后调用此函数返回失败信息 ObjectResponse<T>
     *
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> ObjectResponse<T> failObject(int errorCode, String errorMessage) {
        ObjectResponse<T> response = new ObjectResponse<>();
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }

    /**
     * 业务处理失败后调用此函数返回失败信息 ListResponse<T>
     *
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> ListResponse<T> failList(int errorCode, String errorMessage) {
        ListResponse<T> response = new ListResponse<>();
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }

    /**
     * 业务处理失败后调用此函数返回失败信息 PageResponse<T>
     *
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> failPage(int errorCode, String errorMessage) {
        PageResponse<T> response = new PageResponse<>();
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
