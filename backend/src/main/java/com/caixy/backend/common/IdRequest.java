package com.caixy.backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 适用于仅有Id操作的请求类
 *
 * @name: com.caixy.backend.common.IdRequest
 * @author: CAIXYPROMISE
 * @since: 2024-04-25 20:02
 **/
@Data
public class IdRequest implements Serializable
{
    /**
     * Id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
