package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.entity.StaffSkill;
import cc.mrbird.febs.cos.service.IStaffInfoService;
import cc.mrbird.febs.cos.service.IStaffSkillService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/staff-skill")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffSkillController {

    private final IStaffSkillService staffSkillService;

    private final IStaffInfoService staffInfoService;

    /**
     * 分页获取服务技能信息
     *
     * @param page       分页对象
     * @param staffSkill 服务技能信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<StaffSkill> page, StaffSkill staffSkill) {
        return R.ok(staffSkillService.queryPage(page, staffSkill));
    }

    /**
     * 获取ID获取服务技能详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(staffSkillService.getById(id));
    }

    /**
     * 新增服务技能信息
     *
     * @param staffSkill 服务技能信息
     * @return 结果
     */
    @PostMapping
    public R save(StaffSkill staffSkill) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, staffSkill.getStaffId()));
        staffSkill.setStaffId(Long.valueOf(staffInfo.getId()));
        staffSkill.setCreateTime(DateUtil.formatDateTime(new Date()));
        return R.ok(staffSkillService.save(staffSkill));
    }

    /**
     * 修改服务技能信息
     *
     * @param staffSkill 服务技能信息
     * @return 结果
     */
    @PutMapping
    public R edit(StaffSkill staffSkill) {
        return R.ok(staffSkillService.updateById(staffSkill));
    }

    /**
     * 删除服务技能信息
     *
     * @param ids ids
     * @return 服务技能信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(staffSkillService.removeByIds(ids));
    }
}
