<template>
  <!--个人资料组件-->
  <div class="common-layout">
    <el-container>
      <!--个人资料页面头部-->
      <el-header style="height: auto;background-color: #ffffff;">
        <el-row class="demo-avatar demo-basic" style="justify-content: flex-start;align-items: center">
          <el-avatar :size="70" :src="userProfile.userAvatar" style="margin: 15px;"/>
          <div style="text-align: left">
            <h3 style="margin-bottom: 5px;">{{ userProfile.userNickName }}</h3>
            <div style="font-size: 14px;color: #959393;" v-if="userProfile.userType === 0">所属志愿者团队：<span v-text="userProfile.userTeam ? userProfile.userTeam : '暂无'"></span></div>
            <div style="font-size: 14px;color: #959393;" v-if="userProfile.userType === 0">参与活动数：<span>{{ userProfile.userActiveCount }}</span></div>
            <div style="font-size: 14px;color: #959393;" v-if="userProfile.userType === 1">总参与活动数：<span>{{ userProfile.userActiveCount }}</span></div>
            <div style="font-size: 14px;color: #959393;" v-if="userProfile.userType === 1">志愿者人数：<span>{{ userProfile.userCount }}</span></div>
            <div style="font-size: 14px;color: #959393;" v-if="userProfile.userType === 2">发布活动数：<span>{{ userProfile.userActiveCount }}</span></div>
          </div>
          <div style="margin: 0 150px;font-size: 14px;color: #959393;padding-top: 26px;text-align: left">
            <div v-if="userProfile.userType === 0">违约次数：<span>{{ userProfile.userBreak }}</span></div>
            <div v-if="userProfile.userType !== 2">志愿总时长：<span>{{ userProfile.userHours }}</span></div>
          </div>
        </el-row>
      </el-header>
      <!--个人资料页面资料显示-->
      <el-main class="profile-main" style="padding: 10px 0 0;">
        <el-card class="profile-main-base" style="border: 0;box-shadow: none;">
          <template #header>
            <h3 style="font-size: 20px;text-align: left;">基本信息</h3>
          </template>
          <div style="text-align: left;padding-left: 10px;">
            <div class="user-info-item">用户昵称<span>{{ userProfile.userNickName }}</span></div>
            <div class="user-info-item">邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;箱<span>{{ userProfile.userEmail }}</span></div>
            <div class="user-info-item">联系电话<span>{{ userProfile.userPhone }}</span></div>
            <div class="user-info-item">联系地址<span>{{ userProfile.userAddress }}</span></div>
            <div class="user-info-item" v-if="userProfile.userType === 0">政治面貌<span>{{ userProfile.userPolitical }}</span></div>
            <div class="user-info-item" v-if="userProfile.userType === 0">从业情况<span>{{ userProfile.userProfessional }}</span></div>
            <div class="user-info-item" v-if="userProfile.userType === 0">最高学历<span>{{ userProfile.userDegree }}</span></div>
            <div class="user-info-item" v-if="userProfile.userType === 1">服务对象<span>{{ userProfile.userServiceObj }}</span></div>
            <div class="user-info-item">注册时间<span>{{ userProfile.userRegisterTime }}</span></div>
            <div class="user-info-item" v-if="userProfile.userType !== 0">团队/单位类型<span>{{ userProfile.userClassic }}</span></div>
            <div class="user-info-item" v-if="userProfile.userType === 0">个人简介<span v-text="userProfile.userIntroduction ? userProfile.userIntroduction : '暂未填写'"></span></div>
            <div class="user-info-item" v-if="userProfile.userType !== 0">团队/单位简介<span v-text="userProfile.userIntroduction ? userProfile.userIntroduction : '暂未填写'"></span></div><div class="user-info-item" v-if="userProfile.userType !== 0">团队/单位证明<span><el-image :src="userProfile.userProve" fit="cover" style="width: 270px;vertical-align: text-top;"/></span></div>
          </div>
          <el-button type="primary" @click="openUpdateFormDialog" v-if="store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType">修改信息</el-button>
        </el-card>
        <el-card class="profile-main-real" v-if="userProfile.userType === 0">
          <template #header>
            <h3 style="font-size: 20px;text-align: left;">实名信息</h3>
          </template>
          <div style="text-align: left;padding-left: 10px;">
            <div class="user-info-item">姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名<span v-text="userProfile.userName ? userProfile.userName : '暂未进行实名认证'"></span></div>
            <div class="user-info-item">性&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;别<span v-text="userProfile.userGender ? userProfile.userGender : '暂未进行实名认证'"></span></div>
            <div class="user-info-item">民&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;族<span v-text="userProfile.userNational ? userProfile.userNational : '暂未进行实名认证' "></span></div>
            <div class="user-info-item">出生日期<span v-text="userProfile.userBirth ? userProfile.userBirth : '暂未进行实名认证'"></span></div>
            <div class="user-info-item">籍贯住址<span v-text="userProfile.userNative ? userProfile.userNative : '暂未进行实名认证'"></span></div>
            <div class="user-info-item">身份证号<span v-text="userProfile.userCertificate ? userProfile.userCertificate : '暂未进行实名认证'"></span></div>
          </div>
          <el-button type="primary" @click="router.push('/user-detail/auth')" v-if="store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType">前往实名认证</el-button>
        </el-card>
        <el-card class="profile-main-head" v-if="userProfile.userType !== 0">
          <template #header>
            <h3 style="font-size: 20px;text-align: left;">负责人信息</h3>
          </template>
          <div style="text-align: left;padding-left: 10px;">
            <div class="user-info-item">姓名<span>{{ userProfile.userHeadName }}</span></div>
            <div class="user-info-item">邮箱<span>{{ userProfile.userHeadEmail }}</span></div>
            <div class="user-info-item">手机<span>{{ userProfile.userHeadPhone }}</span></div>
            <div class="user-info-item">身份证号<span>{{ userProfile.userHeadCertificate }}</span></div>
          </div>
          <el-button type="primary" @click="updateHeadInfo('update')" v-if="store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType">修改信息</el-button>
          <el-button type="primary" @click="updateHeadInfo('replace')" v-if="store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType">更换负责人</el-button>
        </el-card>
      </el-main>
    </el-container>
    <!--用户资料修改窗口-->
    <el-dialog v-model="infoDialogFormVisible" title="个人资料修改" center draggable width="550px">
      <el-form :model="updateForm" ref="updateFormRef" :rules="updateFormRules" label-width="auto">
        <el-form-item prop="avatar">
          <el-upload class="avatar-uploader"
                     accept="image/jpeg,image/jpg,image/png"
                     action="http://localhost:8081/base/image/upload"
                     @mouseover="mouseOverAvatar"
                     @mouseleave="mouseLeaveAvatar"
                     :show-file-list="false"
                     :on-success="avatarSuccess"
                     :before-upload="handleUploadBefore"
                     list-type="picture">
            <span v-show="isShowShadow">
              <el-icon :size="20" color="#ffffff" style="margin-left: 32px;margin-top: 32px;"><Camera /></el-icon>
            </span>
            <img :src="updateForm.avatar" style="width: 100%;height: 100%;border-radius: 50%;" alt="加载失败"/>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户昵称" prop="username">
          <el-input v-model="updateForm.username" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;箱" prop="email">
          <el-input v-model="updateForm.email" placeholder="请输入邮箱"/>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="updateForm.phone" placeholder="请输入联系电话"/>
        </el-form-item>
        <el-form-item label="联系地址" prop="address">
          <el-input v-model="updateForm.address" placeholder="请输入详细街道及门牌号"/>
        </el-form-item>
        <el-form-item label="政治面貌" v-if="store.state.loginUser.userType === 0" prop="political">
          <el-select v-model="updateForm.political" class="m-2" placeholder="请选择政治面貌" style="width: 230px;">
            <el-option
                v-for="item in politicals"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="从业情况" v-if="store.state.loginUser.userType === 0" prop="professional">
          <el-select v-model="updateForm.professional" class="m-2" placeholder="请选择从业情况" style="width: 230px;">
            <el-option
                v-for="item in professionals"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="最高学历" v-if="store.state.loginUser.userType === 0" prop="degree">
          <el-select v-model="updateForm.degree" class="m-2" placeholder="请选择最高学历" style="width: 230px;">
            <el-option
                v-for="item in degrees"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="团队/单位类型" v-if="store.state.loginUser.userType !== 0" prop="organizationType">
          <el-select v-model="updateForm.organizationType" class="m-2" placeholder="请选择团队类型" style="width: 230px;">
            <el-option
                v-for="item in organizationType"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务对象" v-if="store.state.loginUser.userType === 1" prop="serviceObjectArray">
          <el-checkbox-group v-model="updateForm.serviceObjectArray" @change="serviceObjectChange" style="display: flex;flex-wrap: wrap;">
            <el-checkbox label="儿童" />
            <el-checkbox label="妇女" />
            <el-checkbox label="老年人" />
            <el-checkbox label="青少年" />
            <el-checkbox label="婴幼儿" />
            <el-checkbox label="孤寡老人" />
            <el-checkbox label="残障人士" />
            <el-checkbox label="病患者" />
            <el-checkbox label="优抚对象" />
            <el-checkbox label="贫困家庭" />
            <el-checkbox label="特殊群体" />
            <el-checkbox label="农村居民" />
            <el-checkbox label="城镇居民" />
            <el-checkbox label="社会公众" />
            <el-checkbox label="其他" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="个人简介" v-if="store.state.loginUser.userType === 0"  prop="introduction">
          <el-input v-model="updateForm.introduction" :rows="2" type="textarea" placeholder="请输入个人简介"/>
        </el-form-item>
        <el-form-item label="团队/单位简介" v-if="store.state.loginUser.userType !== 0"  prop="introduction">
          <el-input v-model="updateForm.introduction" :rows="2" type="textarea" placeholder="请输入团队/单位简介"/>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancelSubmit('updateForm')">取消</el-button>
        <el-button type="primary" @click="submitUpdateForm">确认修改</el-button>
      </span>
      </template>
    </el-dialog>
    <!--负责人信息修改-->
    <el-dialog v-model="headDialogFormVisible" title="负责人资料修改" center draggable width="550px">
      <el-form :model="updateHead" ref="updateHeadRef" :rules="updateHeadRules" label-width="100px">
        <el-form-item label="姓名" prop="name" v-if="updateOrReplace === 'replace'">
          <el-input v-model="updateHead.name" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="personalEmail">
          <el-input v-model="updateHead.personalEmail" placeholder="请输入负责人个人邮箱" />
        </el-form-item>
        <el-form-item label="手机" prop="personalPhone">
          <el-input v-model="updateHead.personalPhone" placeholder="请输入负责人个人手机" />
        </el-form-item>
        <el-form-item label="验证码" prop="verifyCode">
          <el-input v-model="updateHead.verifyCode" placeholder="请输入验证码" :disabled="updateHead.personalPhone === ''">
            <template #suffix>
              <span id="getVerifyCode" @click="getVerifyCode">获取验证码</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="证件号码" prop="certificateNo" v-if="updateOrReplace === 'replace'">
          <el-input v-model="updateHead.certificateNo" placeholder="请输入居民身份证号" />
        </el-form-item>
        <el-form-item label="手持证件照" prop="certificatePicture" v-if="updateOrReplace === 'replace'">
          <el-upload id="updateCertificatePic"
                     ref="uploadRef"
                     action="http://localhost:8081/base/image/upload"
                     accept="image/jpeg,image/jpg,image/png"
                     list-type="picture" :limit="1"
                     :file-list="uploadFileList"
                     :before-upload="handleUploadBefore"
                     :on-success="certificateSuccess"
                     :on-remove="certificateRemove"
                     :on-exceed="certificateExceed">
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传大小小于2M，格式为jpg或png的照片
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelSubmit('updateHead')">取消</el-button>
          <el-button type="primary" @click="submitUpdateHead">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, toRefs, watch} from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import {ElLoading, ElMessage} from "element-plus";
import { Camera } from '@element-plus/icons'
import myAxios from "../../../utils/axios";
//变量/常量
const router = useRouter();
const store = useStore();
let infoDialogFormVisible = ref(false);
let headDialogFormVisible = ref(false);
const isShowShadow = ref(false);
const updateOrReplace = ref('');
const updateFormRef = ref();
const updateHeadRef = ref();
const uploadRef = ref();
const uploadFileList = ref([]);
const updateForm = reactive({
  avatar: '',
  username: '',
  email: '',
  phone: '',
  address: '',
  political: '',
  professional: '',
  degree: '',
  serviceObjectArray: [],
  serviceObject: '',
  organizationType: '',
  introduction: '',
  userId: '',
  userType: ''
});
const updateHead = reactive({
  name: '',
  personalEmail: '',
  personalPhone: '',
  verifyCode: '',
  certificateNo: '',
  certificatePicture: '',
  userId: '',
  userType: '',
  updateHeadType: ''
});
const politicals = [
  {
    value: '中共党员',
    label: '中共党员'
  },
  {
    value: '民主党派',
    label: '民主党派'
  },
  {
    value: '共青团员',
    label: '共青团员'
  },
  {
    value: '群众',
    label: '群众'
  },
  {
    value: '其他',
    label: '其他'
  },
];
const professionals = [
  {
    value: '国家公务员',
    label: '国家公务员'
  },
  {
    value: '专业技术人员',
    label: '专业技术人员'
  },
  {
    value: '职员',
    label: '职员'
  },
  {
    value: '企业管理人员',
    label: '企业管理人员'
  },
  {
    value: '工人',
    label: '工人'
  },
  {
    value: '农民',
    label: '农民'
  },
  {
    value: '学生',
    label: '学生'
  },
  {
    value: '现役军人',
    label: '现役军人'
  },
  {
    value: '自由职业者',
    label: '自由职业者'
  },
  {
    value: '个体经营者',
    label: '个体经营者'
  },
  {
    value: '无业人员',
    label: '无业人员'
  },
  {
    value: '退（离）休人员',
    label: '退（离）休人员'
  },
  {
    value: '其他',
    label: '其他'
  }
];
const degrees = [
  {
    value: '小学',
    label: '小学'
  },
  {
    value: '初中',
    label: '初中'
  },{
    value: '高中（职高/中专/技校）',
    label: '高中（职高/中专/技校）'
  },
  {
    value: '大专',
    label: '大专'
  },
  {
    value: '本科',
    label: '本科'
  },
  {
    value: '第二学士学位',
    label: '第二学士学位'
  },
  {
    value: '硕士研究生',
    label: '硕士研究生'
  },
  {
    value: '博士研究生',
    label: '博士研究生'
  }
];
const organizationType = [
  {
    value: '党政机关',
    label: '党政机关'
  },
  {
    value: '教育事业单位',
    label: '教育事业单位'
  },
  {
    value: '卫生事业单位',
    label: '卫生事业单位'
  },
  {
    value: '科技事业单位',
    label: '科技事业单位'
  },
  {
    value: '文化事业单位',
    label: '文化事业单位'
  },
  {
    value: '社会福利事业单位',
    label: '社会福利事业单位'
  },
  {
    value: '居民/村民委员会',
    label: '居民/村民委员会'
  },
  {
    value: '社会团体',
    label: '社会团体'
  },
  {
    value: '社会服务机构',
    label: '社会服务机构'
  },
  {
    value: '基金会',
    label: '基金会'
  },
  {
    value: '其他',
    label: '其他'
  }
];
const validateAvatar = (rule ,value, callback) => {
  if (updateForm.avatar === '') {
    return callback(new Error('头像不能为空'));
  }
  return callback();
};
const validateCertificatePic = (rule ,value, callback) => {
  if (updateHead.certificatePicture === '') {
    return callback(new Error('证件照不能为空'));
  }
  return callback();
};
const validatePhone = (rule, value, callback) => {
  if (value !== '') {
    const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}(,1[3|4|5|6|7|8|9][0-9]\d{8})*$/;
    if (!reg.test(value)) {
      return callback(new Error('请填写正确的手机号码'));
    } else {
      return callback(); // *验证成功的地方必须callback()
    }
  }else {
    return callback(new Error('请填写手机号'));
  }
};
const updateFormRules = reactive({
  avatar: [
    {required: true, validator: validateAvatar,trigger: 'change'}
  ],
  username: [
    {required: true, message: '用户名不能为空', trigger: 'blur'}
  ],
  email: [
    {required: true, message: '邮箱不能为空', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur'}
  ],
  phone: [
    {required: true, validator: validatePhone, trigger: 'blur'}
  ],
  address: [
    {required: true, message: '联系地址不能为空', trigger: 'blur'}
  ],
  political: [
    {required: true, message: '政治面貌不能为空', trigger: 'change'}
  ],
  professional: [
    {required: true, message: '从业情况不能为空', trigger: 'change'}
  ],
  degree: [
    {required: true, message: '最高学历不能为空', trigger: 'change'}
  ],
  serviceObjectArray: [
    {required: true, message: '服务对象不能为空', trigger: 'change'}
  ],
  organizationType: [
    {required: true, message: '团队类型不能为空', trigger: 'change'}
  ]
});
const updateHeadRules = reactive({
  name: [
    {required: true, message: '负责人姓名不能为空', trigger: 'blur'}
  ],
  personalEmail: [
    {required: true, message: '负责人邮箱不能为空', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur'}
  ],
  personalPhone: [
    {required: true, validator: validatePhone, trigger: 'blur'}
  ],
  verifyCode: [
    {required: true, message: '手机验证码不能为空', trigger: 'blur'}
  ],
  certificateNo: [
    {required: true, message: '证件号码不能为空', trigger: 'blur'}
  ],
  certificatePicture: [
    {required: true, validator: validateCertificatePic, trigger: 'change'}
  ]
});
//获取存放后台数据
const data = reactive({
  userProfile: ''
});
const getUserProfile = () => {
  const formData = new FormData();
  formData.append("userId",store.state.detail.userKey);
  formData.append("userType",store.state.detail.userType);
  myAxios.post('/base/getUserProfile',formData).then(response => {
    if (response.data.code === 200) {
      data.userProfile = response.data.data.userProfile;
    } else if (response.data.code === 430) {
      ElMessage.error('该用户不存在或账号已被停用');
    }
  })
};
onMounted(() => {
  getUserProfile()
})
//当在查看其他用户信息时点击查看个人信息，用户详情页内容由其他用户变化成自己
watch(
    store.state.detail,
    (newValue) => {
      const formData = new FormData();
      formData.append("userId",newValue.userKey);
      formData.append("userType",newValue.userType);
      myAxios.post('/base/getUserProfile',formData).then(response => {
        if (response.data.code === 200) {
          data.userProfile = response.data.data.userProfile;
        } else if (response.data.code === 430) {
          ElMessage.error('该用户不存在或账号已被停用');
        }
      })
    },
    {
      deep:true, // 深度监听
      immediate:true // 立即执行
    }
)
const { userProfile } = toRefs(data);


//方法
const mouseOverAvatar = () => {
  isShowShadow.value = true;
};
const mouseLeaveAvatar = () => {
  isShowShadow.value = false;
};
const serviceObjectChange = (value) => {
  updateForm.serviceObject = value.toString();
};
const handleUploadBefore = (file) => {
  const isLimit2M = file.size / 1024 / 1024 < 2;
  const isJpgOrPng = (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png');
  if (!isLimit2M) {
    ElMessage.warning('上传图片大小不能超过 2MB!');
  }
  if (!isJpgOrPng) {
    ElMessage.warning('上传图片格式必须为jpg或png');
  }
  return isLimit2M && isJpgOrPng;
};
const avatarSuccess = (result) => {
  updateForm.avatar = result.data.filepath;
};
const certificateExceed = () => {
  ElMessage.warning('只能上传一张图片，若想上传新的图片请先将旧的图片移除');
};
const certificateSuccess = (result) => {
  updateHead.certificatePicture = result.data.filepath;
  uploadFileList.value.push({name:result.data.filepath,url: result.data.filepath});
};
const certificateRemove = () => {
  let formData = new FormData();
  formData.append("pictureUrl",updateHead.certificatePicture);
  myAxios.post('/base/image/remove',formData).then(response => {
    if (response.data.code === 200) {
      updateHead.certificatePicture = '';
      uploadFileList.value = [];
    } else {
      ElMessage.error("系统错误，图片删除失败，请重试");
    }
  });
};
const getVerifyCode = () => {
  if (updateHead.personalPhone === '') {
    ElMessage.warning('请先填写手机号');
  } else {
    const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}(,1[3|4|5|6|7|8|9][0-9]\d{8})*$/;
    if (!reg.test(updateHead.personalPhone)) {
      ElMessage.warning('请输入正确的手机号');
      return;
    }
    let data = new FormData();
    data.append('phone',updateHead.personalPhone);
    data.append('verifyLogo','update');
    const loading = ElLoading.service({lock: true, text: '正在发送验证码，请稍后'});
    myAxios.post('/base/getVerifyCode',data).then(response => {
      loading.close();
      if (response.data.code === 200) {
        ElMessage.success('发送验证码成功，请注意查收');
      } else if (response.data.code === 400) {
        ElMessage.warning('手机号为空');
      } else if (response.data.code === 410) {
        ElMessage.error('该用户不存在');
      } else if (response.data.code === 411) {
        ElMessage.warning('操作过于频繁，请一分钟后再次获取');
      } else {
        ElMessage.error('系统错误，请重试');
      }
    })
  }
};
const updateHeadInfo = (option) => {
  headDialogFormVisible.value = true;
  updateHead.userId = store.state.loginUser.userId;
  updateHead.userType = store.state.loginUser.userType;
  updateHead.updateHeadType = option;
  if (option === 'update') {
    updateOrReplace.value = 'update';
    updateHead.personalEmail = data.userProfile.userHeadEmail;
    updateHead.personalPhone = data.userProfile.userHeadPhone;
  } else if (option === 'replace') {
    updateOrReplace.value = 'replace';
    updateHead.personalEmail = '';
    updateHead.personalPhone = '';
  }
};
const openUpdateFormDialog = () => {
  infoDialogFormVisible.value = true;
  updateForm.userId = store.state.loginUser.userId;
  updateForm.userType = store.state.loginUser.userType;
  updateForm.avatar = data.userProfile.userAvatar;
  updateForm.username = data.userProfile.userNickName;
  updateForm.email = data.userProfile.userEmail;
  updateForm.phone = data.userProfile.userPhone;
  updateForm.address = data.userProfile.userAddress;
  if (data.userProfile.userPolitical) updateForm.political = data.userProfile.userPolitical;
  if (data.userProfile.userProfessional) updateForm.professional = data.userProfile.userProfessional;
  if (data.userProfile.userDegree) updateForm.degree = data.userProfile.userDegree;
  if (data.userProfile.userClassic) updateForm.organizationType = data.userProfile.userClassic;
  if (data.userProfile.userServiceObj) updateForm.serviceObjectArray = data.userProfile.userServiceObj.split(',');
  if (data.userProfile.userIntroduction) updateForm.introduction = data.userProfile.userIntroduction;
}
const submitUpdateForm = () => {
  updateFormRef.value.validate((valid) => {
    if (valid) {
      myAxios.post('/base/updateUser',updateForm).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('修改信息成功');
          infoDialogFormVisible.value = false;
          updateFormRef.value.resetFields();
          store.commit('setToken',response.data.data.newToken);
          store.commit('setLoginUser',response.data.data.newToken);
          location.reload();
        } else {
          ElMessage.error('出现错误，修改失败');
        }
      });
    }else {
      ElMessage.warning('请完善信息再提交');
    }
  })
};
const submitUpdateHead = () => {
  updateHeadRef.value.validate((valid) => {
    if (valid) {
      myAxios.post('/base/updateHead',updateHead).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('修改信息成功');
          headDialogFormVisible.value = false;
          updateHeadRef.value.resetFields();
          uploadRef.value.clearFiles();
          getUserProfile();
        } else if (response.data.code === 420) {
          ElMessage.error('验证码不存在或已过期');
        } else if (response.data.code === 421) {
          ElMessage.error('验证码不正确');
        } else {
          ElMessage.error('更新出错，请重试');
        }
      })
    }else {
      ElMessage.warning( '请完善信息再提交');
    }
  })
};
const cancelSubmit = (val) => {
  if (val === 'updateForm') {
    infoDialogFormVisible.value = false;
    updateFormRef.value.resetFields();
  } else if (val === 'updateHead') {
    headDialogFormVisible.value = false;
    if (uploadFileList.value.length !== 0) certificateRemove();
    updateHeadRef.value.resetFields();
    uploadRef.value.clearFiles();
  }
}
</script>

<style lang="scss" scoped>
:deep(.el-dialog) {
  .el-input,.el-textarea{
    width: 90%;
  }
}
:deep(#updateCertificatePic) {
  .el-upload-list--picture {
    margin: 10px 0;

    .el-upload-list__item {
      width: 350px;
      margin: 0 auto;
      padding: 20px;
    }
    .el-upload-list__item-thumbnail {
      width: 300px;
      height: auto;
    }
  }
}
:deep(.el-upload-list--picture){
  .el-upload-list__item-thumbnail{
    margin-top: 10px;
  }
}
:deep(.avatar-uploader) {
  margin-left: 90px;

  .el-upload.el-upload--picture {
    position: relative;
    width: 85px;
    height: 85px;
    border: 1px solid #e9e7e7;
    border-radius: 50%;

    span {
      width: 100%;
      height: 100%;
      border-radius:50%;
      position:absolute;
      background-color: rgba(0,0,0,0.4);
      display: flex;
    }
  }
}
.el-input,.el-textarea {
  width: 90%;
}
.user-info-item {
  margin-bottom: 25px;
  font-size: 15px
}
.user-info-item span {
  margin-left: 40px;
  font-size: 14px;
}
.profile-main-real,.profile-main-head {
  border: 0;
  box-shadow: none;
  margin-top: 10px;
}
#getVerifyCode {
  cursor: pointer;
}
#getVerifyCode:hover {
  color: #1989fa;
}
</style>