package com.future.common.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.BaseResponse;

/**
 * OpenFeign 的工具类
 */
public class FeignUtil {

    /**
     * 如果响应失败则抛出 BusinessException 异常
     *
     * 提示：为了解决 http 200 响应时有业务异常发生的情况，
     * 因为如果直接在 Feign Decoder 中抛出 BusinessException，
     * 会被包裹一层 FeignException 异常导致 try catch 代码更加臃肿
     *
     * @param response
     */
    public static void throwBizExceptionIfResponseFailed(BaseResponse response) throws BusinessException {
        if (response != null) {
            if (response.getErrorCode() > 0) {
                throw new BusinessException(
                        response.getErrorCode(),
                        response.getErrorMessage()
                );
            }
        }
    }
}
