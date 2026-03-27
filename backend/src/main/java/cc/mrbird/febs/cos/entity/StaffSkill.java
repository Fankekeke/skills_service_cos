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
 * 员工技能表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StaffSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 关联sys_user的用户ID
     */
    private Long staffId;

    /**
     * 技能标题
     */
    private String title;

    /**
     * 详细服务描述
     */
    private String description;

    /**
     * 当前设置价格
     */
    private BigDecimal price;

    /**
     * 计价单位: 次/小时/天
     */
    private String unit;

    /**
     * 状态: 0-下架, 1-正常
     */
    private Integer status;

    private String images;

    private String createTime;

    private String updateTime;


}
