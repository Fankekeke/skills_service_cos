package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 技能购买订单表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SkillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 购买者ID
     */
    private Long buyerId;

    /**
     * 购买的技能ID
     */
    private Long skillId;

    /**
     * 提供服务的员工ID
     */
    private Long staffId;

    /**
     * 交易时的技能标题快照
     */
    private String snapshotTitle;

    /**
     * 交易时的成交单价
     */
    private BigDecimal snapshotPrice;

    /**
     * 总支付金额
     */
    private BigDecimal totalAmount;

    /**
     * 状态: 0-待支付, 1-已支付/待服务, 2-服务中, 3-已完成, 4-已取消
     */
    private Integer orderStatus;

    /**
     * 支付时间
     */
    private String payTime;

    private String createTime;


}
