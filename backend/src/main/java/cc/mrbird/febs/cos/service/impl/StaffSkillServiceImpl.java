package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.StaffSkill;
import cc.mrbird.febs.cos.dao.StaffSkillMapper;
import cc.mrbird.febs.cos.service.IStaffSkillService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
public class StaffSkillServiceImpl extends ServiceImpl<StaffSkillMapper, StaffSkill> implements IStaffSkillService {

    /**
     * 分页获取服务技能信息
     *
     * @param page       分页对象
     * @param staffSkill 服务技能信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<StaffSkill> page, StaffSkill staffSkill) {
        return baseMapper.queryPage(page, staffSkill);
    }

    /**
     * 获取技能列表
     *
     * @param key 关键字
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> querySkillList(String key) {
        return baseMapper.querySkillList(key);
    }
}
