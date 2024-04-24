package com.caixy.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.backend.mapper.ChartMapper;
import com.caixy.backend.model.entity.Chart;
import com.caixy.backend.service.ChartService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService
{
    /**
     * 根据id和用户id查询图表
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/4/21 下午7:04
     */
    @Override
    public Chart getChartByIdAndUserId(Long id, Long userId)
    {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("userId", userId);
        return this.getOne(queryWrapper);
    }
}




