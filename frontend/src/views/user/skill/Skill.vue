
<template>
  <div class="skill-page" style="width: 100%">
    <a-card :bordered="false" class="card-area">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <a-input-search
          v-model="searchKey"
          placeholder="请输入服务标题或描述"
          enter-button="搜索"
          size="large"
          @search="handleSearch"          style="max-width: 500px; margin: 0 auto;"
        />
      </div>

      <!-- 技能列表 -->
      <div class="skill-list">
        <a-row :gutter="24">
          <a-col
            v-for="skill in skillList"
            :key="skill.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
            class="skill-col"
          >
            <a-card
              hoverable
              class="skill-card"
              @click="viewDetail(skill)"
            >
              <a-badge :status="skill.status === 1 ? 'success' : 'error'" slot="cover" :text="skill.status === 1 ? '正常' : '下架'" />
              <img
                :src="skill.images || 'https://via.placeholder.com/300x200?text=No+Image'"
                :alt="skill.title"
                class="skill-image"
              />
              <div class="skill-info">
                <h3 class="skill-title">{{ skill.title }}</h3>
                <div class="skill-desc">{{ skill.description.substring(0, 60) }}...</div>
                <div class="skill-footer">
                  <div class="price-section">
                    <span class="price-label">价格</span>
                    <span class="price-value">¥{{ skill.price }}</span>
                    <span class="price-unit">/{{ skill.unit }}</span>
                  </div>
                  <div class="staff-info">
                    <a-avatar :src="skill.staffImages" size="small" icon="user" />
                    <span class="staff-name">{{ skill.staffName }}</span>
                  </div>
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>

        <!-- 空状态 -->
        <a-empty v-if="skillList.length === 0" description="暂无服务技能" />
      </div>
    </a-card>

    <!-- 详情弹窗 -->
    <a-modal
      v-model="detailVisible"
      :footer="null"
      :width="900"
      @cancel="closeDetail"
      :bodyStyle="{ padding: '0' }"
    >
      <div class="detail-container">
        <!-- 头部图片 -->
        <div class="detail-header">
          <a-image
            :src="currentSkill.images || 'https://via.placeholder.com/800x300?text=Service+Image'"
            :height="300"
            :preview-src-list="[currentSkill.images]"
            fit="cover"
          />
          <a-badge
            :status="currentSkill.status === 1 ? 'success' : 'error'"
            :text="currentSkill.status === 1 ? '正常服务中' : '已下架'"
            class="status-badge"
          />
        </div>

        <!-- 详情内容 -->
        <div class="detail-content">
          <a-row :gutter="24">
            <!-- 左侧信息 -->
            <a-col :span="16">
              <h1 class="detail-title">{{ currentSkill.title }}</h1>

              <div class="price-block">
                <div class="main-price">
                  <span class="currency">¥</span>
                  <span class="amount">{{ currentSkill.price }}</span>
                  <span class="unit">/{{ currentSkill.unit }}</span>
                </div>
              </div>

              <a-divider />

              <div class="section">
                <h3 class="section-title">
                  <a-icon type="file-text" theme="filled" style="color: #1890ff; margin-right: 8px;" />
                  服务描述
                </h3>
                <div class="description-content">{{ currentSkill.description }}</div>
              </div>

              <a-divider />

              <div class="section">
                <h3 class="section-title">
                  <a-icon type="clock-circle" theme="filled" style="color: #faad14; margin-right: 8px;" />
                  时间信息
                </h3>
                <div class="time-row">
                  <span class="time-label">创建时间：</span>
                  <span class="time-value">{{ currentSkill.createTime }}</span>
                </div>
                <div class="time-row">
                  <span class="time-label">更新时间：</span>
                  <span class="time-value">{{ currentSkill.updateTime }}</span>
                </div>
              </div>
            </a-col>

            <!-- 右侧服务人员 -->
            <a-col :span="8">
              <div class="staff-card">
                <div class="staff-avatar">
                  <a-avatar :src="currentSkill.staffImages" :size="80" icon="user" />
                </div>
                <h3 class="staff-card-name">{{ currentSkill.staffName }}</h3>
                <p class="staff-code">工号：{{ currentSkill.staffCode }}</p>

                <a-divider />

                <a-button type="primary" size="large" block @click="buySkill" :disabled="currentSkill.status !== 1">
                  <a-icon type="shopping-cart" />
                  {{ currentSkill.status === 1 ? '立即购买' : '已下架' }}
                </a-button>
                <a-button size="large" block style="margin-top: 12px;" @click="contactStaff">
                  <a-icon type="customer-service" />
                  联系服务人员
                </a-button>
              </div>
            </a-col>
          </a-row>
        </div>
      </div>
    </a-modal>

    <!-- 购买确认弹窗 -->
    <a-modal
      v-model="buyVisible"
      title="确认购买"
      :width="500"
      @ok="confirmBuy"
      @cancel="closeBuy"
      ok-text="确认支付"
      cancel-text="取消"
    >
      <div class="buy-confirm">
        <a-alert
          message="请确认您要购买的服务"
          type="info"
          show-icon          style="margin-bottom: 16px;"
        />

        <div class="buy-item">
          <div class="buy-item-title">{{ currentSkill.title }}</div>
          <div class="buy-item-price">
            <span class="label">单价：</span>
            <span class="price">¥{{ currentSkill.price }}</span>
            <span class="unit">/{{ currentSkill.unit }}</span>
          </div>
        </div>

        <a-divider />

        <a-form :form="buyForm" layout="vertical">
          <a-form-item label="购买数量">
            <a-input-number
              v-decorator=['quantity', { initialValue: 1, rules: [{ required: true, message: '请输入购买数量' }] }]
            :min="1"
            :max="99"
            style="width: 100%"
            @change="onQuantityChange"
            />
          </a-form-item>

          <a-divider />

          <div class="total-price">
            <span class="label">总计：</span>
            <span class="amount">¥{{ totalPrice }}</span>
          </div>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script>import {mapState} from 'vuex'

export default {
  name: "Skill",
  data () {
    return {
      searchKey: '',
      skillList: [],
      detailVisible: false,
      buyVisible: false,
      currentSkill: {},
      buyForm: null,
      quantity: 1,
      totalPrice: 0
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  mounted () {
    this.buyForm = this.$form.createForm(this)
    this.querySkillList()
  },
  methods: {
    querySkillList () {
      this.loading = true
      this.$get('/cos/staff-skill/querySkillList', {
        key: this.searchKey
      }).then((r) => {
        this.skillList = r.data.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
        // 模拟数据用于测试
        this.skillList = [
          {
            id: 1,
            staffId: 501,
            title: '高级 Java 开发服务',
            description: '提供企业级 Java 应用开发、系统架构设计、微服务改造、性能优化等专业服务。精通 Spring Boot、Spring Cloud、MyBatis 等主流框架，熟悉 MySQL、Redis、RabbitMQ 等中间件的使用和调优。',
            price: 500.00,
            unit: '天',
            status: 1,
            images: 'https://example.com/images/skill_java_001.jpg',
            createTime: '2026-03-20 10:30:00',
            updateTime: '2026-03-25 14:20:00',
            staffName: '张三丰',
            staffCode: 'STAFF001',
            staffImages: 'https://example.com/images/staff_zhangsan.jpg'
          },
          {
            id: 2,
            staffId: 502,
            title: 'Python 数据分析与可视化',
            description: '使用 Pandas、NumPy、Matplotlib、Seaborn 等工具进行数据清洗、数据处理、分析和可视化展示。可提供 Jupyter Notebook 交互式分析报告，支持 Excel、CSV、数据库等多种数据源。',
            price: 300.00,
            unit: '小时',
            status: 1,
            images: 'https://example.com/images/skill_python_002.jpg',
            createTime: '2026-03-21 09:15:00',
            updateTime: '2026-03-26 11:30:00',
            staffName: '王五',
            staffCode: 'STAFF002',
            staffImages: 'https://example.com/images/staff_wangwu.jpg'
          },
          {
            id: 3,
            staffId: 503,
            title: 'UI/UX 界面设计',
            description: '提供专业的移动端和 Web 端界面设计服务，包括原型设计、交互设计、视觉设计、设计规范制定等全流程服务。熟练使用 Figma、Sketch、Adobe XD 等设计工具。',
            price: 800.00,
            unit: '次',
            status: 1,
            images: 'https://example.com/images/skill_ui_003.jpg',
            createTime: '2026-03-22 13:45:00',
            updateTime: '2026-03-27 08:50:00',
            staffName: '刘亦菲',
            staffCode: 'STAFF003',
            staffImages: 'https://example.com/images/staff_liuyifei.jpg'
          },
          {
            id: 4,
            staffId: 504,
            title: '微信小程序开发',
            description: '专业承接微信小程序前端开发、后端接口开发、云开发等服务。支持原生开发和 Uni-app 跨平台开发，可快速实现小程序从 0 到 1 的上线。',
            price: 1000.00,
            unit: '次',
            status: 1,
            images: 'https://example.com/images/skill_wechat_004.jpg',
            createTime: '2026-03-23 16:20:00',
            updateTime: '2026-03-26 17:40:00',
            staffName: '赵敏',
            staffCode: 'STAFF004',
            staffImages: 'https://example.com/images/staff_zhaomin.jpg'
          },
          {
            id: 5,
            staffId: 505,
            title: '英语翻译服务',
            description: '提供专业英语笔译服务，包括技术文档、商务合同、学术论文、产品说明书等各类文件的翻译。英语专业八级，具有 CATTI 二级口译证书。',
            price: 150.00,
            unit: '小时',
            status: 0,
            images: 'https://example.com/images/skill_translation_005.jpg',
            createTime: '2026-03-18 11:00:00',
            updateTime: '2026-03-24 15:30:00',
            staffName: '李寻欢',
            staffCode: 'STAFF005',
            staffImages: 'https://example.com/images/staff_lixunhuan.jpg'
          }
        ]
      })
    },
    handleSearch () {
      this.querySkillList()
    },
    viewDetail (skill) {
      this.currentSkill = skill
      this.detailVisible = true
    },
    closeDetail () {
      this.detailVisible = false
      this.currentSkill = {}
    },
    buySkill () {
      if (!this.currentUser || !this.currentUser.userId) {
        this.$message.warning('请先登录后再购买')
        return
      }
      this.quantity = 1
      this.totalPrice = this.currentSkill.price
      this.buyForm.resetFields()
      this.buyForm.setFieldsValue({
        quantity: 1
      })
      this.detailVisible = false
      this.buyVisible = true
    },
    onQuantityChange (value) {
      this.quantity = value
      this.totalPrice = (this.currentSkill.price * value).toFixed(2)
    },
    confirmBuy () {
      this.buyForm.validateFields((err, values) => {
        if (!err) {
          const orderData = {
            skillId: this.currentSkill.id,
            staffId: this.currentSkill.staffId,
            snapshotTitle: this.currentSkill.title,
            snapshotPrice: this.currentSkill.price,
            totalAmount: this.totalPrice,
            quantity: values.quantity
          }

          this.$post('/cos/skill-order/create', orderData).then((r) => {
            this.$message.success('订单创建成功，正在跳转支付...')
            this.closeBuy()
            // TODO: 跳转到支付页面
          }).catch(() => {
            this.$message.error('订单创建失败，请重试')
          })
        }
      })
    },
    closeBuy () {
      this.buyVisible = false
    },
    contactStaff () {
      this.$message.info('联系服务人员功能开发中...')
    }
  }
}
</script>

<style lang="less" scoped>.skill-page {
  min-height: calc(100vh - 164px);
  background: #f5f7fa;
  padding: 24px 0;

  .search-bar {
    text-align: center;
    margin-bottom: 32px;
  }

  .skill-list {
    .skill-col {
      margin-bottom: 24px;
    }

    .skill-card {
      transition: all 0.3s;
      cursor: pointer;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      .skill-image {
        width: 100%;
        height: 200px;
        object-fit: cover;
        border-radius: 4px 4px 0 0;
      }

      .skill-info {
        padding: 16px 0;

        .skill-title {
          font-size: 16px;
          font-weight: 600;
          color: #262626;
          margin: 0 0 8px 0;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .skill-desc {
          font-size: 13px;
          color: #8c8c8c;
          line-height: 1.6;
          margin-bottom: 12px;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .skill-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .price-section {
            .price-label {
              font-size: 12px;
              color: #8c8c8c;
              margin-right: 4px;
            }

            .price-value {
              font-size: 20px;
              font-weight: 700;
              color: #f5222d;
            }

            .price-unit {
              font-size: 12px;
              color: #8c8c8c;
              margin-left: 2px;
            }
          }

          .staff-info {
            display: flex;
            align-items: center;
            gap: 6px;

            .staff-name {
              font-size: 13px;
              color: #595959;
            }
          }
        }
      }
    }
  }

  .detail-container {
    .detail-header {
      position: relative;

      .status-badge {
        position: absolute;
        top: 16px;
        right: 16px;
        background: rgba(255, 255, 255, 0.9);
        padding: 8px 16px;
        border-radius: 4px;
      }
    }

    .detail-content {
      padding: 32px;

      .detail-title {
        font-size: 28px;
        font-weight: 600;
        color: #262626;
        margin: 0 0 24px 0;
      }

      .price-block {
        background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
        padding: 24px;
        border-radius: 8px;
        margin-bottom: 24px;

        .main-price {
          .currency {
            font-size: 20px;
            color: #f5222d;
            font-weight: 600;
            margin-right: 4px;
          }

          .amount {
            font-size: 42px;
            color: #f5222d;
            font-weight: 700;
          }

          .unit {
            font-size: 16px;
            color: #8c8c8c;
            margin-left: 8px;
          }
        }
      }

      .section {
        margin-bottom: 24px;

        .section-title {
          font-size: 18px;
          font-weight: 600;
          color: #262626;
          margin: 0 0 16px 0;
        }

        .description-content {
          font-size: 15px;
          color: #595959;
          line-height: 1.8;
          white-space: pre-wrap;
        }

        .time-row {
          margin-bottom: 12px;

          .time-label {
            font-size: 14px;
            color: #8c8c8c;
            margin-right: 8px;
          }

          .time-value {
            font-size: 14px;
            color: #262626;
          }
        }
      }

      .staff-card {
        background: #fafafa;
        padding: 32px 24px;
        border-radius: 12px;
        text-align: center;
        border: 1px solid #e8e8e8;

        .staff-avatar {
          margin-bottom: 16px;
        }

        .staff-card-name {
          font-size: 20px;
          font-weight: 600;
          color: #262626;
          margin: 0 0 8px 0;
        }

        .staff-code {
          font-size: 14px;
          color: #8c8c8c;
          margin: 0 0 24px 0;
        }
      }
    }
  }

  .buy-confirm {
    padding: 8px 0;

    .buy-item {
      margin-bottom: 16px;

      .buy-item-title {
        font-size: 16px;
        font-weight: 600;
        color: #262626;
        margin-bottom: 8px;
      }

      .buy-item-price {
        .label {
          font-size: 14px;
          color: #8c8c8c;
          margin-right: 8px;
        }

        .price {
          font-size: 24px;
          font-weight: 700;
          color: #f5222d;
        }

        .unit {
          font-size: 14px;
          color: #8c8c8c;
          margin-left: 4px;
        }
      }
    }

    .total-price {
      text-align: right;

      .label {
        font-size: 16px;
        color: #8c8c8c;
        margin-right: 12px;
      }

      .amount {
        font-size: 28px;
        font-weight: 700;
        color: #f5222d;
      }
    }
  }
}
</style>
