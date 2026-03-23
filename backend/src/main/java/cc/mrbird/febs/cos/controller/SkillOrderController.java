package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.SkillOrder;
import cc.mrbird.febs.cos.service.ISkillOrderService;
import cn.hutool.core.date.DateUtil;
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
@RequestMapping("/cos/skill-order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SkillOrderController {

    private final ISkillOrderService skillOrderService;

    /**
     * 分页获取技能订单信息
     *
     * @param page       分页对象
     * @param skillOrder 技能订单信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<SkillOrder> page, SkillOrder skillOrder) {
        return R.ok(skillOrderService.queryPage(page, skillOrder));
    }

    /**
     * 获取ID获取技能订单详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(skillOrderService.getById(id));
    }

    /**
     * 新增技能订单信息
     *
     * @param skillOrder 技能订单信息
     * @return 结果
     */
    @PostMapping
    public R save(SkillOrder skillOrder) {
        skillOrder.setCreateTime(DateUtil.formatDateTime(new Date()));
        return R.ok(skillOrderService.save(skillOrder));
    }

    /**
     * 修改技能订单信息
     *
     * @param skillOrder 技能订单信息
     * @return 结果
     */
    @PutMapping
    public R edit(SkillOrder skillOrder) {
        return R.ok(skillOrderService.updateById(skillOrder));
    }

    /**
     * 删除技能订单信息
     *
     * @param ids ids
     * @return 技能订单信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(skillOrderService.removeByIds(ids));
    }
}
