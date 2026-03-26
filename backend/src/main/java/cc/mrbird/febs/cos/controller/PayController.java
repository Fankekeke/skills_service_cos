package cc.mrbird.febs.cos.controller;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AlipayBean;
import cc.mrbird.febs.cos.entity.OrderInfo;
import cc.mrbird.febs.cos.entity.SkillOrder;
import cc.mrbird.febs.cos.service.IOrderInfoService;
import cc.mrbird.febs.cos.service.ISkillOrderService;
import cc.mrbird.febs.cos.service.PayService;
import cn.hutool.core.date.DateUtil;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/cos/pay")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PayController {

    private final PayService payService;

    private final IOrderInfoService orderInfoService;

    private final ISkillOrderService skillOrderService;

    /**
     * 新增订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @PostMapping("/alipay")
    public R saveOrder(OrderInfo orderInfo) throws AlipayApiException {
        orderInfo.setCode("ORD-" + System.currentTimeMillis());
        orderInfo.setIntegral(orderInfo.getAfterOrderPrice());
        orderInfoService.saveOrder(orderInfo);
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(orderInfo.getCode());
        alipayBean.setSubject(orderInfo.getCode() + System.currentTimeMillis());
        alipayBean.setTotal_amount(orderInfo.getAfterOrderPrice().toString());
        alipayBean.setBody("");
        String result = payService.aliPay(alipayBean);
        return R.ok(result);
    }

    /**
     * 新增技能订单信息
     *
     * @param skillOrder 技能订单信息
     * @return 结果
     */
    @PostMapping("/skill/alipay")
    public R saveSkillOrder(SkillOrder skillOrder) throws AlipayApiException {
        skillOrder.setOrderSn("SN-" + System.currentTimeMillis());
        skillOrder.setCreateTime(DateUtil.formatDateTime(new Date()));
        skillOrder.setOrderStatus(0);
        skillOrderService.save(skillOrder);

        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(skillOrder.getOrderSn());
        alipayBean.setSubject(skillOrder.getOrderSn() + System.currentTimeMillis());
        alipayBean.setTotal_amount(skillOrder.getTotalAmount().toString());
        alipayBean.setBody("");
        String result = payService.aliPay(alipayBean);
        return R.ok(result);
    }


    /**
     * 阿里支付
     * @param subject
     * @param body
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "/test")
    public R alipay(String outTradeNo, String subject, String totalAmount, String body, Integer discountId) throws AlipayApiException {
        OrderInfo orderInfo = orderInfoService.getOne(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getCode, outTradeNo));
        orderInfo.setDiscountId(discountId);
        orderInfo.setAfterOrderPrice(new BigDecimal(totalAmount));
        orderInfo.setIntegral(orderInfo.getAfterOrderPrice());
        orderInfoService.updateById(orderInfo);
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(outTradeNo);
        alipayBean.setSubject(subject);
        alipayBean.setTotal_amount(totalAmount);
        alipayBean.setBody(body);
        String result = payService.aliPay(alipayBean);
        return R.ok(result);
    }

}
