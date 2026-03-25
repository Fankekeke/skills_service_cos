<template>
  <div class="work-container">
    <a-row style="margin: 0 auto;" :gutter="20">
      <a-col :span="12" :offset="6">
        <a-row style="margin-top: 35px">
          <a-col :span="24" class="form-title-wrapper" v-if="nextFlag == 1">
            <div class="title-section">
              <div class="title-icon">📝</div>
              <h2 class="form-main-title">发布个人需求</h2>
              <p class="form-subtitle">请填写您的需求信息，我们将为您匹配合适的服务者</p>
            </div>
            <div class="form-content-wrapper">
              <a-form :form="form" layout="vertical">
                <a-row :gutter="20">
                  <a-col :span="8">
                    <a-form-item label='需求名称' class="enhanced-form-item">
                      <a-input
                        v-decorator="[
                                 'orderName',
                                 { rules: [{ required: true, message: '请输入需求名称!' }] }
                               ]"
                        placeholder="请输入需求名称"
                        class="enhanced-input"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="8">
                    <a-form-item label='需求类型' class="enhanced-form-item">
                      <a-input
                        v-decorator="[
                                 'goodsType',
                                 { rules: [{ required: true, message: '请输入需求类型!' }] }
                               ]"
                        placeholder="请输入需求类型"
                        class="enhanced-input"
                      />
                    </a-form-item>
                  </a-col>

                  <!-- 预估价格 -->
                  <a-col :span="8">
                    <a-form-item label='预估价格' class="enhanced-form-item">
                      <a-input-number
                        class="enhanced-input-number"
                        v-decorator="['forecastPrice']"
                        :min="0"
                        :step="0.01"
                        placeholder="请输入金额"
                        style="width: 100%"
                      />
                    </a-form-item>
                  </a-col>

                  <a-col :span="24"></a-col>

                  <a-col :span="24">
                    <a-form-item label='需求描述' class="enhanced-form-item">
                      <a-textarea
                        :rows="4"
                        v-decorator="[
                                 'content',
                                 { rules: [{ required: true, message: '请输入需求描述!' }] }
                               ]"
                        placeholder="请详细描述您的需求..."
                        class="enhanced-textarea"
                      />
                    </a-form-item>
                  </a-col>

                  <a-col :span="24">
                    <a-form-item label='需求图片' class="enhanced-form-item upload-form-item">
                      <div class="upload-wrapper">
                        <a-upload
                          name="avatar"
                          action="http://127.0.0.1:9527/file/fileUpload/"
                          list-type="picture-card"
                          :file-list="fileList"
                          @preview="handlePreview"
                          @change="picHandleChange"
                          class="custom-upload"
                        >
                          <div v-if="fileList.length < 8" class="upload-content">
                            <div class="upload-icon">
                              <a-icon type="plus" />
                            </div>
                            <div class="upload-text">
                              <span>点击上传图片</span>
                              <p>最多可上传 8 张</p>
                            </div>
                          </div>
                        </a-upload>
                        <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
                          <img alt="example" style="width: 100%" :src="previewImage" />
                        </a-modal>
                      </div>
                    </a-form-item>
                  </a-col>

                  <a-col :span="24">
                    <a-form-item label='需求视频' class="enhanced-form-item upload-form-item">
                      <div class="upload-wrapper">
                        <a-upload
                          name="avatar"
                          action="http://127.0.0.1:9527/file/fileUpload/"
                          list-type="picture-card"
                          :file-list="videoFileList"
                          @preview="handlePreviewVideo"
                          @change="picHandleChangeVideo"
                          class="custom-upload"
                        >
                          <div v-if="videoFileList.length < 8" class="upload-content">
                            <div class="upload-icon video-icon">
                              <a-icon type="video-camera" />
                            </div>
                            <div class="upload-text">
                              <span>点击上传视频</span>
                              <p>最多可上传 8 个</p>
                            </div>
                          </div>
                        </a-upload>
                        <a-modal :visible="previewVisibleVideo" :footer="null" @cancel="handleCancelVideo">
                          <img alt="example" style="width: 100%" :src="previewImageVideo" />
                        </a-modal>
                      </div>
                    </a-form-item>
                  </a-col>
                </a-row>
                <div class="submit-button-wrapper">
                  <a-button
                    type="primary"
                    @click="fetch"
                    class="submit-button"
                  >
                    <a-icon type="send" style="margin-right: 8px;"/>
                    立即发布
                  </a-button>
                </div>
              </a-form>
            </div>
          </a-col>
        </a-row>
      </a-col>
    </a-row>
    <Map :orderData="orderMapView.merchantInfo"
         @close="handleorderMapViewClose"
         :orderShow="orderMapView.visiable">
    </Map>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import VehicleView from './VehicleView.vue'
import Map from './Map.vue'
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'Work',
  components: {Map, VehicleView},
  data () {
    return {
      nextFlag: 1,
      fileList: [],
      flawFileList: [],
      videoFileList: [],
      addressList: [],
      previewVisible: false,
      previewImage: '',
      previewVisibleFlaw: false,
      previewImageFlaw: '',
      previewVisibleVideo: false,
      previewImageVideo: '',
      formItemLayout,
      form: this.$form.createForm(this),
      orderMapView: {
        merchantInfo: null,
        visiable: false
      },
      key: '',
      roomList: [],
      roomTypeList: [],
      loading: false,
      vehicleView: {
        visiable: false,
        data: null
      },
      rentView: {
        visiable: false,
        data: null
      },
      startDate: null,
      endDate: null,
      orderInfo: null,
      discountId: null,
      aiRecognitionResult: null,
      showAiResult: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  mounted () {
    this.selectAddress()
    // 确保加载marked.js库
    if (typeof window.marked === 'undefined') {
      const script = document.createElement('script')
      script.src = 'https://cdn.jsdelivr.net/npm/marked/marked.min.js'
      document.head.appendChild(script)
    }
  },
  methods: {
    renderMarkdown (content) {
      if (!content) {
        return ''
      }
      if (window.marked) {
        try {
          return window.marked.parse(content)
        } catch (error) {
          console.error('Markdown解析错误:', error)
          return content
        }
      }
      return 'Marked.js 库未加载！'
    },
    aiHandleChange ({ file }) {
      if (file.response !== undefined) {
        console.log(file.response.code)
        console.log(file.status)
        if (file.response.code === 500 && file.status === 'done') {
          this.$message.error(file.response.msg)
        } else if (file.response.code === 0 && file.status === 'done') {
          // 存储识别结果并在界面展示
          this.aiRecognitionResult = file.response.data
          this.showAiResult = true
          this.$message.success('需求识别成功，请确认识别信息')

          // 自动填充表单字段（如果有返回的数据）
          if (file.response.msg) {
            const data = file.response.msg
            this.aiRecognitionResult = data
          }
        }
      }
    },
    orderPay () {
      let data = this.orderInfo
      data.discountId = this.discountId
      delete data.discountInfos
      this.$post('/cos/pay/alipay', data).then((r) => {
        // console.log(r.data.msg)
        // 添加之前先删除一下，如果单页面，页面不刷新，添加进去的内容会一直保留在页面中，二次调用form表单会出错
        const divForm = document.getElementsByTagName('div')
        if (divForm.length) {
          document.body.removeChild(divForm[0])
        }
        const div = document.createElement('div')
        div.innerHTML = r.data.msg // data就是接口返回的form 表单字符串
        // console.log(div.innerHTML)
        document.body.appendChild(div)
        document.forms[0].setAttribute('target', '_self') // 新开窗口跳转
        document.forms[0].submit()
      })
    },
    handleChange (value) {
      let afterOrderPrice = this.orderInfo.orderPrice
      if (!value) {
        this.orderInfo.afterOrderPrice = afterOrderPrice
      } else {
        this.orderInfo.discountInfos.forEach(e => {
          if (e.id === value) {
            if (e.type == 1) {
              this.orderInfo.afterOrderPrice = afterOrderPrice - e.discountPrice
            } else {
              this.orderInfo.afterOrderPrice = afterOrderPrice * e.rebate / 10
            }
          }
        })
      }
      // this.discountId = value
      // console.log(this.discountId)
    },
    selectAddress () {
      this.$get(`/cos/address-info/listByUserId/${this.currentUser.userId}`).then((r) => {
        this.addressList = r.data.data
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    handleCancelFlaw () {
      this.previewVisibleFlaw = false
    },
    async handlePreviewFlaw (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImageFlaw = file.url || file.preview
      this.previewVisibleFlaw = true
    },
    picHandleChangeFlaw ({ fileList }) {
      this.flawFileList = fileList
    },
    handleCancelVideo () {
      this.previewVisibleVideo = false
    },
    async handlePreviewVideo (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImageVideo = file.url || file.preview
      this.previewVisibleVideo = true
    },
    picHandleChangeVideo ({ fileList }) {
      this.videoFileList = fileList
    },
    handlevehicleViewClose () {
      this.vehicleView.visiable = false
    },
    handlevehicleViewSuccess () {
      this.vehicleView.visiable = false
      this.$message.success('添加订单成功')
    },
    orderSuccess () {
      this.rentView.visiable = false
      this.$message.success('添加订单成功')
      this.fetch()
    },
    collectDelete (id) {
      this.$delete(`/cos/collect-info/${id}`).then((r) => {
        this.$message.success('取消收藏成功')
        this.fetch()
      })
    },
    collectAdd (roomCode) {
      let data = { userId: this.currentUser.userId, roomCode }
      this.$post(`/cos/collect-info`, data).then((r) => {
        this.$message.success('成功')
        this.fetch()
      })
    },
    view (record) {
      // if (record.currentStatus === '0') {
      //   this.$message.warn('此商家不在营业时间内')
      //   return false
      // }
      this.orderMapView.merchantInfo = record
      this.orderMapView.visiable = true
    },
    handleorderMapViewClose () {
      this.orderMapView.visiable = false
    },
    getWorkStatusList () {
      this.$get(`/cos/order-info/selectMerchantList`, { key: this.key }).then((r) => {
        this.roomList = r.data.data
      })
    },
    fetch () {
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        images.push(image.response)
      })

      let flawImages = []
      this.flawFileList.forEach(image => {
        flawImages.push(image.response)
      })

      let videoList = []
      this.videoFileList.forEach(image => {
        videoList.push(image.response)
      })

      this.form.validateFields((err, values) => {
        // 当服务方式为上门(1)时，取货地址为必填项
        // if (values.orderMethod === '1' && !values.startAddressId) {
        //   this.$message.warn('请选择取货地址')
        //   return false
        // }
        values.flawImages = flawImages.length > 0 ? flawImages.join(',') : null
        values.video = videoList.length > 0 ? videoList.join(',') : null
        values.images = images.length > 0 ? images.join(',') : null
        values.userId = this.currentUser.userId
        values.orderType = 1
        values.orderMethod = 1
        if (!err) {
          this.loading = true
          this.$post('/cos/order-info', {
            ...values
          }).then((r) => {
            this.$message.success('添加订单成功')
            setTimeout(() => {
              this.$router.push('/user/order')
            }, 500)
          })
        }
      })
    }
  }
}
</script>

<style scoped> /* 容器背景渐变 */
.work-container {
  background: linear-gradient(135deg, #f6f8fb 0%, #eef1f5 100%);
  min-height: calc(100vh - 120px);
  padding-bottom: 30px;
}

.page-padding {
  padding: 24px 20px;
}

/* 主卡片样式 */
.main-card {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  background: #ffffff;
  overflow: hidden;
}

/* 标题区域 */
.form-title-wrapper {
  margin-bottom: 0;
}

.title-section {
  text-align: center;
  padding: 32px 20px 24px;
  background: linear-gradient(135deg, #6deca9 0%, #0c880c 100%);
  margin: -16px -16px 24px -16px;
  border-radius: 0;
  position: relative;
  overflow: hidden;
}

.title-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-10%, -10%) scale(1.05); }
}

.title-icon {
  font-size: 48px;
  margin-bottom: 12px;
  position: relative;
  z-index: 1;
  display: inline-block;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.form-main-title {
  color: #ffffff;
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
  position: relative;
  z-index: 1;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.form-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin: 0;
  position: relative;
  z-index: 1;
}

/* 表单内容区域 */
.form-content-wrapper {
  padding: 12px 24px 32px;
}

/* 表单项增强样式 */
.enhanced-form-item {
  margin-bottom: 24px;
}

.enhanced-form-item >>> .ant-form-item-label > label {
  color: #2d3748;
  font-weight: 600;
  font-size: 15px;
  position: relative;
  padding-left: 12px;
}

.enhanced-form-item >>> .ant-form-item-label > label::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

/* 输入框样式 */
.enhanced-input >>> .ant-input,
.enhanced-textarea >>> .ant-input {
  border-radius: 10px;
  border: 2px solid #e2e8f0;
  padding: 12px 16px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #ffffff;
}

.enhanced-input >>> .ant-input:hover,
.enhanced-textarea >>> .ant-input:hover {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.enhanced-input >>> .ant-input:focus,
.enhanced-textarea >>> .ant-input:focus {
  border-color: #764ba2;
  box-shadow: 0 0 0 3px rgba(118, 75, 162, 0.15);
}

.enhanced-input-number >>> .ant-input-number {
  width: 100%;
  border-radius: 10px;
  border: 2px solid #e2e8f0;
  padding: 12px 16px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.enhanced-input-number >>> .ant-input-number:hover {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.enhanced-input-number >>> .ant-input-number:focus {
  border-color: #764ba2;
  box-shadow: 0 0 0 3px rgba(118, 75, 162, 0.15);
}

/* 上传组件样式 */
.upload-form-item >>> .ant-form-item-label > label {
  display: flex;
  align-items: center;
}

.upload-wrapper {
  width: 100%;
}

.custom-upload >>> .ant-upload.ant-upload-select-picture-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 2px dashed #cbd5e0;
  border-radius: 12px;
  padding: 24px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.custom-upload >>> .ant-upload.ant-upload-select-picture-card:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, #eef2ff 0%, #e6f7ff 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.upload-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.upload-icon .anticon {
  font-size: 32px;
  color: #ffffff;
}

.custom-upload >>> .ant-upload.ant-upload-select-picture-card:hover .upload-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.video-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
}

.custom-upload >>> .ant-upload.ant-upload-select-picture-card:hover .video-icon {
  box-shadow: 0 6px 16px rgba(245, 87, 108, 0.4);
}

.upload-text {
  text-align: center;
  color: #718096;
}

.upload-text span {
  display: block;
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 4px;
}

.upload-text p {
  margin: 0;
  font-size: 12px;
  color: #a0aec0;
}

/* 已上传的文件列表样式 */
.custom-upload >>> .ant-upload-list-picture-card .ant-upload-list-item {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.custom-upload >>> .ant-upload-list-picture-card .ant-upload-list-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 提交按钮 */
.submit-button-wrapper {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px dashed #e2e8f0;
}

.submit-button {
  background: linear-gradient(135deg, #1be36b 0%, #19864b 100%);
  border: none;
  border-radius: 12px;
  padding: 14px 60px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  height: auto;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.5);
}

.submit-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .form-main-title {
    font-size: 22px;
  }

  .title-icon {
    font-size: 36px;
  }

  .submit-button {
    padding: 12px 40px;
    font-size: 15px;
  }
}

</style>
