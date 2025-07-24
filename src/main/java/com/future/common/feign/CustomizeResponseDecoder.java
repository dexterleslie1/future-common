//package com.future.common.feign;
//
//
//import com.future.common.exception.BusinessException;
//import com.future.common.http.BaseResponse;
//import feign.FeignException;
//import feign.Response;
//import feign.codec.Decoder;
//import feign.jackson.JacksonDecoder;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//
///**
// * 正常 http 200 响应解码器，判断响应 json 中的 errorCode 是否大于 0
// */
//public class CustomizeResponseDecoder implements Decoder {
//    private final Decoder defaultDecoder = new JacksonDecoder();
//
//    @Override
//    public Object decode(Response response, Type type) throws IOException, FeignException {
//        // 1. 先通过默认解码器解析响应体为ObjectResponse
//        Object result = defaultDecoder.decode(response, type);
//
//        // 2. 检查是否为ObjectResponse类型（根据业务接口返回类型调整）
//        if (result instanceof BaseResponse) {
//            BaseResponse baseResponse = (BaseResponse) result;
//            // 3. 若errorCode > 0，抛出BusinessException
//            if (baseResponse.getErrorCode() > 0) {
//                throw new BusinessException(
//                        baseResponse.getErrorCode(),
//                        baseResponse.getErrorMessage()
//                );
//            }
//        }
//        return result;
//    }
//}
