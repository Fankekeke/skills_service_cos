package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.SkillOrder;
import cc.mrbird.febs.cos.dao.SkillOrderMapper;
import cc.mrbird.febs.cos.service.ISkillOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class SkillOrderServiceImpl extends ServiceImpl<SkillOrderMapper, SkillOrder> implements ISkillOrderService {

    /**
     * 分页获取技能订单信息
     *
     * @param page       分页对象
     * @param skillOrder 技能订单信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<SkillOrder> page, SkillOrder skillOrder) {
        return baseMapper.queryPage(page, skillOrder);
    }
}
