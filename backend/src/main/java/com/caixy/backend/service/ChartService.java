package com.caixy.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caixy.backend.model.entity.Chart;

/**
 *
 */
public interface ChartService extends IService<Chart>
{

    Chart getChartByIdAndUserId(Long id, Long userId);
}
