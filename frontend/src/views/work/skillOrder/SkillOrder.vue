<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="技能名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.snapshotTitle"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="服务人员"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.staffName"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="购买用户"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.userName"/>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add">新增</a-button>
        <a-button @click="batchDelete">删除</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table bordered ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="titleShow" slot-scope="text, record">
          <template>
            <a-badge status="processing" v-if="record.rackUp === 1"/>
            <a-badge status="error" v-if="record.rackUp === 0"/>
            <a-tooltip>
              <template slot="title">
                {{ record.title }}
              </template>
              {{ record.title.slice(0, 8) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="contentShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.content }}
              </template>
              {{ record.content.slice(0, 40) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon type="eye" theme="twoTone" twoToneColor="#4a9ff5" @click="viewDetail(record)" title="查看详情"></a-icon>
        </template>
      </a-table>
    </div>
    <!-- 订单详情弹窗 -->
    <a-modal
      v-model="detailVisible"
      title="订单详情"
      :width="900"
      :footer="null"
      @cancel="closeDetail"
      :bodyStyle="{ padding: '24px' }"
    >
      <div class="order-detail">
        <!-- 订单头部 -->
        <div class="order-header">
          <div class="header-left">
            <span class="order-label">订单号：</span>
            <span class="order-sn">{{ currentOrder.orderSn }}</span>
          </div>
          <div class="header-right">
            <a-tag :color="getOrderStatus(currentOrder.orderStatus).color" class="status-tag">
              {{ getOrderStatus(currentOrder.orderStatus).text }}
            </a-tag>
          </div>
        </div>

        <a-divider style="margin: 20px 0" />

        <!-- 订单内容 -->
        <div class="order-content">
          <a-row :gutter="24">
            <!-- 左侧信息 -->
            <a-col :span="16">
              <div class="info-block">
                <h3 class="block-title">
                  <a-icon type="file-text" theme="filled" style="color: #1890ff; margin-right: 8px;" />
                  服务信息
                </h3>
                <div class="info-grid">
                  <div class="info-item">
                    <span class="info-label">服务标题</span>
                    <span class="info-value">{{ currentOrder.title }}</span>
                  </div>
                  <div class="info-item full-width">
                    <span class="info-label">服务描述</span>
                    <div class="description-box">{{ currentOrder.description }}</div>
                  </div>
                  <div class="info-item">
                    <span class="info-label">单价</span>
                    <span class="info-value price">¥{{ currentOrder.price }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">总价</span>
                    <span class="info-value total-price">¥{{ currentOrder.totalAmount }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">计价单位</span>
                    <span class="info-value">{{ currentOrder.unit || '- -' }}</span>
                  </div>
                </div>
              </div>

              <div class="info-block" style="margin-top: 24px;">
                <h3 class="block-title">
                  <a-icon type="user" theme="filled" style="color: #52c41a; margin-right: 8px;" />
                  人员信息
                </h3>
                <div class="info-grid">
                  <div class="info-item">
                    <span class="info-label">服务人员</span>
                    <div class="avatar-name">
                      <a-avatar :src="currentOrder.staffImages" size="small" icon="user" />
                      <span class="name">{{ currentOrder.staffName }}</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <span class="info-label">购买用户</span>
                    <div class="avatar-name">
                      <a-avatar :src="currentOrder.userImages" size="small" icon="user" />
                      <span class="name">{{ currentOrder.userName }}</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <span class="info-label">联系电话</span>
                    <span class="info-value">{{ currentOrder.phone }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">用户 ID</span>
                    <span class="info-value">{{ currentOrder.buyerId }}</span>
                  </div>
                </div>
              </div>
            </a-col>

            <!-- 右侧图片和时间 -->
            <a-col :span="8">
              <div class="info-block">
                <h3 class="block-title">
                  <a-icon type="picture" theme="filled" style="color: #722ed1; margin-right: 8px;" />
                  技能图片
                </h3>
                <div class="image-box" v-if="currentOrder.images">
                  <a-image
                    :src="currentOrder.images"
                    width="100%"
                    :height="220"
                    :preview-src-list="[currentOrder.images]"                    style="border-radius: 8px;"
                  />
                </div>
                <div class="image-placeholder" v-else>
                  <a-icon type="picture" style="font-size: 56px; color: #d9d9d9; margin-bottom: 12px;" />
                  <p class="placeholder-text">暂无图片</p>
                </div>
              </div>

              <div class="info-block" style="margin-top: 24px;">
                <h3 class="block-title">
                  <a-icon type="clock-circle" theme="filled" style="color: #faad14; margin-right: 8px;" />
                  时间信息
                </h3>
                <div class="time-list">
                  <div class="time-item">
                    <div class="time-dot order-dot"></div>
                    <div class="time-content">
                      <span class="time-label">下单时间</span>
                      <span class="time-value">{{ currentOrder.createTime }}</span>
                    </div>
                  </div>
                  <div class="time-item">
                    <div class="time-dot pay-dot"></div>
                    <div class="time-content">
                      <span class="time-label">支付时间</span>
                      <span class="time-value">{{ currentOrder.payTime || '未支付' }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </a-col>
          </a-row>
        </div>
      </div>
    </a-modal>
    <bulletin-add
      v-if="bulletinAdd.visiable"
      @close="handleBulletinAddClose"
      @success="handleBulletinAddSuccess"
      :bulletinAddVisiable="bulletinAdd.visiable">
    </bulletin-add>
    <bulletin-edit
      ref="bulletinEdit"
      @close="handleBulletinEditClose"
      @success="handleBulletinEditSuccess"
      :bulletinEditVisiable="bulletinEdit.visiable">
    </bulletin-edit>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import BulletinAdd from './SkillOrderAdd.vue'
import BulletinEdit from './SkillOrderEdit.vue'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Bulletin',
  components: {BulletinAdd, BulletinEdit, RangeDate},
  data () {
    return {
      advanced: false,
      bulletinAdd: {
        visiable: false
      },
      bulletinEdit: {
        visiable: false
      },
      detailVisible: false,
      currentOrder: {},
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '订单号',
        dataIndex: 'orderSn',
        ellipsis: true
      }, {
        title: '服务标题',
        dataIndex: 'title',
        ellipsis: true
      }, {
        title: '服务描述',
        dataIndex: 'description',
        ellipsis: true
      }, {
        title: '单价',
        dataIndex: 'price',
        customRender: (text, row, index) => {
          if (text !== null) {
            return '¥' + text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '总价',
        dataIndex: 'totalAmount',
        customRender: (text, row, index) => {
          if (text !== null) {
            return '¥' + text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '单位',
        dataIndex: 'unit',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '订单状态',
        dataIndex: 'orderStatus',
        customRender: (text, row, index) => {
          switch (text) {
            case '0':
              return <a-tag color="orange">待支付</a-tag>
            case '1':
              return <a-tag color="green">已支付/待服务</a-tag>
            case '2':
              return <a-tag color="blue">服务中</a-tag>
            case '3':
              return <a-tag color="cyan">已完成</a-tag>
            case '4':
              return <a-tag color="red">已取消</a-tag>
            default:
              return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '服务人员',
        dataIndex: 'staffName',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '购买用户',
        dataIndex: 'userName',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '联系电话',
        dataIndex: 'phone',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '下单时间',
        dataIndex: 'createTime',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '支付时间',
        dataIndex: 'payTime',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    viewDetail (record) {
      this.currentOrder = record
      this.detailVisible = true
    },
    closeDetail () {
      this.detailVisible = false
      this.currentOrder = {}
    },
    getOrderStatus (status) {
      const statusMap = {
        '0': { text: '待支付', color: 'orange' },
        '1': { text: '已支付/待服务', color: 'green' },
        '2': { text: '服务中', color: 'blue' },
        '3': { text: '已完成', color: 'cyan' },
        '4': { text: '已取消', color: 'red' }
      }
      return statusMap[status] || { text: '- -', color: 'default' }
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.bulletinAdd.visiable = true
    },
    handleBulletinAddClose () {
      this.bulletinAdd.visiable = false
    },
    handleBulletinAddSuccess () {
      this.bulletinAdd.visiable = false
      this.$message.success('新增服务订单成功')
      this.search()
    },
    edit (record) {
      this.$refs.bulletinEdit.setFormValues(record)
      this.bulletinEdit.visiable = true
    },
    handleBulletinEditClose () {
      this.bulletinEdit.visiable = false
    },
    handleBulletinEditSuccess () {
      this.bulletinEdit.visiable = false
      this.$message.success('修改服务订单成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/skill-order/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      this.$get('/cos/skill-order/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";

.order-detail {
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
    border-radius: 8px;

    .header-left {
      .order-label {
        font-size: 14px;
        color: #666;
        margin-right: 8px;
      }

      .order-sn {
        font-size: 18px;
        font-weight: 600;
        color: #1890ff;
        letter-spacing: 1px;
      }
    }

    .status-tag {
      padding: 6px 16px;
      font-size: 15px;
      font-weight: 500;
    }
  }

  .order-content {
    .info-block {
      background: #fafafa;
      border-radius: 8px;
      padding: 20px;
      border: 1px solid #e8e8e8;

      .block-title {
        font-size: 16px;
        font-weight: 600;
        color: #262626;
        margin: 0 0 16px 0;
        padding-bottom: 12px;
        border-bottom: 2px solid #e8e8e8;
      }

      .info-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;

        .info-item {
          display: flex;
          flex-direction: column;

          &.full-width {
            grid-column: 1 / -1;
          }

          .info-label {
            font-size: 13px;
            color: #8c8c8c;
            margin-bottom: 6px;
            font-weight: 500;
          }

          .info-value {
            font-size: 14px;
            color: #262626;

            &.price {
              color: #fa8c16;
              font-weight: 600;
              font-size: 16px;
            }

            &.total-price {
              color: #f5222d;
              font-weight: 700;
              font-size: 18px;
            }
          }

          .description-box {
            background: #fff;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #e8e8e8;
            font-size: 14px;
            color: #595959;
            line-height: 1.8;
            max-height: 150px;
            overflow-y: auto;
          }

          .avatar-name {
            display: flex;
            align-items: center;
            gap: 8px;

            .name {
              font-size: 14px;
              color: #262626;
              font-weight: 500;
            }
          }
        }
      }

      .image-box {
        background: #fff;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .image-placeholder {
        background: #fafafa;
        border-radius: 8px;
        padding: 40px 20px;
        text-align: center;
        border: 2px dashed #d9d9d9;

        .placeholder-text {
          font-size: 14px;
          color: #bfbfbf;
          margin: 0;
        }
      }

      .time-list {
        .time-item {
          display: flex;
          align-items: flex-start;
          padding: 12px 0;

          &:not(:last-child) {
            border-bottom: 1px solid #e8e8e8;
          }

          .time-dot {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            margin-right: 12px;
            margin-top: 4px;
            flex-shrink: 0;

            &.order-dot {
              background: #1890ff;
              box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.2);
            }

            &.pay-dot {
              background: #52c41a;
              box-shadow: 0 0 0 3px rgba(82, 196, 26, 0.2);
            }
          }

          .time-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 4px;

            .time-label {
              font-size: 13px;
              color: #8c8c8c;
              font-weight: 500;
            }

            .time-value {
              font-size: 14px;
              color: #262626;
              font-weight: 500;
            }
          }
        }
      }
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .order-detail {
    .order-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }

    .order-content {
      .info-grid {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
