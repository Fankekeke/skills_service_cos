package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.StaffSkill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface StaffSkillMapper extends BaseMapper<StaffSkill> {

    /**
     * 分页获取服务技能信息
     *
     * @param page       分页对象
     * @param staffSkill 服务技能信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<StaffSkill> page, @Param("staffSkill") StaffSkill staffSkill);
}
