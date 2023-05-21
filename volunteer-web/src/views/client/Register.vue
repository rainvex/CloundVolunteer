<template>
  <el-container class="register-container">
    <p class="register-link"><span><router-link to="/">返回首页</router-link></span>|<span><router-link to="/login">已有帐号？前往登录</router-link></span></p>
    <el-main class="register-main" >
      <el-form :model="registerUser" ref="registerFormRef" :rules="validateRules" label-width="auto">
        <h5>账号信息</h5>
        <el-form-item label="邮箱" v-if="registerUser.registerType === '0'" prop="personalEmail">
          <el-input v-model="registerUser.personalEmail" placeholder="请填写邮箱" />
        </el-form-item>
        <el-form-item label="团队/单位邮箱" v-if="registerUser.registerType !== '0'" prop="organizationEmail">
          <el-input v-model="registerUser.organizationEmail" placeholder="请填写团队/单位邮箱" />
        </el-form-item>
        <el-form-item label="手机" v-if="registerUser.registerType === '0'" prop="personalPhone">
          <el-input v-model="registerUser.personalPhone" placeholder="请填写联系电话" />
        </el-form-item>
        <el-form-item label="固定电话" v-if="registerUser.registerType !== '0'" prop="organizationPhone">
          <el-input v-model="registerUser.organizationPhone" placeholder="请填写团队/单位固定电话（选填）" />
        </el-form-item>
        <el-form-item label="验证码" v-if="registerUser.registerType === '0'" prop="verifyCode">
          <el-input v-model="registerUser.verifyCode" placeholder="请填写手机验证码" />
          <el-button type="primary" style="margin-left: 20px" @click="getVerifyCode">获取验证码</el-button>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerUser.password" placeholder="请输入密码" show-password/>
        </el-form-item>
        <h5 v-if="registerUser.registerType !== '0'">负责人信息</h5>
        <el-form-item label="姓名" v-if="registerUser.registerType !== '0'" prop="headName">
          <el-input v-model="registerUser.headName" placeholder="请填写负责人姓名" />
        </el-form-item>
        <el-form-item label="邮箱" v-if="registerUser.registerType !== '0'" prop="personalEmail">
          <el-input v-model="registerUser.personalEmail" placeholder="请填写负责人个人邮箱" />
        </el-form-item>
        <el-form-item label="手机" v-if="registerUser.registerType !== '0'" prop="personalPhone">
          <el-input v-model="registerUser.personalPhone" placeholder="请填写负责人个人手机" />
        </el-form-item>
        <el-form-item label="验证码" v-if="registerUser.registerType !== '0'" prop="verifyCode">
          <el-input v-model="registerUser.verifyCode" placeholder="请填写验证码" />
          <el-button type="primary" style="margin-left: 20px" @click="getVerifyCode">获取验证码</el-button>
        </el-form-item>
        <el-form-item label="证件号码" v-if="registerUser.registerType !== '0'" prop="headCertificateNo">
          <el-input v-model="registerUser.headCertificateNo" placeholder="请填写居民身份证号" />
        </el-form-item>
        <el-form-item label="手持证件照" v-if="registerUser.registerType !== '0'" prop="headCertificatePicture">
          <el-upload action="http://localhost:8081/base/image/upload"
                     ref="certificatePicRef"
                     accept="image/jpeg,image/jpg,image/png"
                     list-type="picture" :limit="1"
                     :file-list="certificateFileList"
                     :before-upload="handleUploadBefore"
                     :on-success="handleCertificateUploadSuccess"
                     :on-remove="handleCertificateRemove"
                     :on-exceed="handleExceed">
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传大小小于2M，格式为jpg或png的照片
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <h5>基本信息</h5>
        <el-form-item label="用户名" v-if="registerUser.registerType === '0'" prop="username">
          <el-input v-model="registerUser.username" placeholder="请填写用户名"/>
        </el-form-item>
        <el-form-item label="政治面貌" v-if="registerUser.registerType === '0'" prop="political">
          <el-select v-model="registerUser.political" class="m-2" placeholder="请选择政治面貌">
            <el-option
                v-for="item in politicals"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="从业情况" v-if="registerUser.registerType === '0'" prop="professional">
          <el-select v-model="registerUser.professional" class="m-2" placeholder="请选择从业情况">
            <el-option
                v-for="item in professionals"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="最高学历" v-if="registerUser.registerType === '0'" prop="degree">
          <el-select v-model="registerUser.degree" class="m-2" placeholder="请选择最高学历">
            <el-option
                v-for="item in degrees"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="团队/单位名" v-if="registerUser.registerType !== '0'" prop="username">
          <el-input v-model="registerUser.username" placeholder="请填写团队/单位名"/>
        </el-form-item>
        <el-form-item label="团队/单位类型" v-if="registerUser.registerType !== '0'" prop="organizationType">
          <el-select v-model="registerUser.organizationType" class="m-2" placeholder="请选择团队类型">
            <el-option
                v-for="item in organizationType"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系地址" v-if="registerUser.registerType === '0'" prop="addressCode">
          <el-cascader
              v-model="registerUser.addressCode"
              :options="options"
              :props="{expandTrigger: 'hover'}"
              @change="chooseRegisterAddress"
              placeholder="请选择联系地址"
          />
        </el-form-item>
        <el-form-item label="所在地区" v-if="registerUser.registerType !== '0'" prop="addressCode">
          <el-cascader
              v-model="registerUser.addressCode"
              :options="options"
              :props="{expandTrigger: 'hover'}"
              @change="chooseRegisterAddress"
              placeholder="请选择所在地区"
          />
        </el-form-item>
        <el-form-item prop="addressDetail">
          <el-input :disabled="registerUser.addressCode === ''" v-model="registerUser.addressDetail" placeholder="请填写详细街道及门牌号（选填）" @change="addressDetailChange"/>
        </el-form-item>
        <el-form-item label="服务对象" v-if="registerUser.registerType === '1'" prop="serviceObjectArray">
          <el-checkbox-group v-model="registerUser.serviceObjectArray" @change="serviceObjectChange" style="display: flex;flex-wrap: wrap;">
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
        <el-form-item label="团队证明" v-if="registerUser.registerType !== '0'" prop="provePicture">
          <el-upload action="http://localhost:8081/base/image/upload"
                     ref="provePicRef"
                     accept="image/jpeg,image/jpg,image/png"
                     list-type="picture" :limit="1"
                     :file-list="proveFileList"
                     :before-upload="handleUploadBefore"
                     :on-success="handleProveUploadSuccess"
                     :on-remove="handleProveRemove"
                     :on-exceed="handleExceed">
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传大小小于2M，格式为jpg或png的照片
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="个人简介" v-if="registerUser.registerType === '0'" prop="introduction">
          <el-input v-model="registerUser.introduction" :rows="2" type="textarea" placeholder="请填写个人简介（选填）"/>
        </el-form-item>
        <el-form-item label="团队/单位简介" v-if="registerUser.registerType !== '0'" prop="introduction">
          <el-input v-model="registerUser.introduction" :rows="2" type="textarea" placeholder="请填写团队/单位简介（选填）"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitRegisterForm">立即注册</el-button>
          <el-button type="primary" @click="resetRegisterForm">重置表单</el-button>
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>

<script setup>
import { regionData,CodeToText } from 'element-china-area-data';
import { ElMessage,ElLoading } from "element-plus";
import { ref,reactive } from 'vue';
import { useRoute,useRouter } from "vue-router";
import myAxios from "../../utils/axios";

const route = useRoute();
const router = useRouter();
//注册表单对象
const registerUser = reactive({
  registerType: '', //注册用户的类型
  //无论注册类型，均显示
  username: '', //个人用户名或团队/单位名
  personalEmail: '', //个人邮箱
  organizationEmail: '', //团队/单位邮箱
  personalPhone: '', //个人手机
  organizationPhone: '', //团队/单位固定电话
  password: '', //密码
  verifyCode: '', //验证码
  addressCode: '', //地址码
  addressDetail: '', //详细地址
  address: '', //最终地址
  introduction: '', //个人/团队/单位简介
  //注册类型为志愿者
  political: '', //政治面貌
  professional: '', //从业情况
  degree: '', //最高学历
  //注册类型为团队
  serviceObjectArray: [], //服务对象数组
  serviceObject: '', //最终字符串类型的服务对象
  //注册类型为团队/单位
  organizationType: '', //团队/单位类型
  provePicture: '', //团队/单位证明图
  headName: '', //负责人姓名
  headCertificatePicture: '', //负责人证件照
  headCertificateNo: '', //负责人证件号
});
registerUser.registerType = route.params.registerType;
const registerFormRef = ref();
const certificatePicRef = ref();
const provePicRef = ref();
const certificateFileList = ref([]);
const proveFileList = ref([]);
//自定义检验规则
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
const validateCertificateNo = (rule, value, callback) => {
  if (value === '') {
    return callback(new Error('请输入负责人证件号'));
  } else if (value.length !== 18) {
    return callback(new Error('请输入正确的证件号'));
  } else {
    return callback();
  }
}
const validateCertificatePic = (rule, value, callback) => {
  if (registerUser.headCertificatePicture === '') {
    return callback(new Error('请上传负责人证件照'));
  } else {
    return callback();
  }
};
const validateProvePic = (rule, value, callback) => {
  if (registerUser.provePicture === '') {
    return callback(new Error('请上传团队/单位证明图'));
  } else {
    return callback();
  }
};
//表单校验对象
const validateRules = reactive({
  personalEmail: [
    {required: true, message: '请填写你的邮箱', trigger: 'blur'},
    {type: 'email', message: '请填写正确的邮箱格式', trigger: 'blur'}
  ],
  organizationEmail: [
    {required: true, message: '请填写团队/单位邮箱', trigger: 'blur'},
    {type: 'email', message: '请填写正确的邮箱格式', trigger: 'blur'}
  ],
  personalPhone: [
    {required: true, message: '请填写手机号', trigger: 'blur'},
    {validator: validatePhone, trigger: 'blur'}
  ],
  verifyCode: [
    {required: true, message: '请填写手机验证码', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请填写密码', trigger: 'blur'}
  ],
  headName: [
    {required: true, message: '请填写负责人姓名', trigger: 'blur'}
  ],
  headCertificateNo: [
    {required: true, validator: validateCertificateNo, trigger: 'blur'}
  ],
  headCertificatePicture: [
    {required: true, validator: validateCertificatePic, trigger: 'change'}
  ],
  username: [
    {required: true, message: '请填写用户名', trigger: 'blur'}
  ],
  political: [
    {required: true, message: '请选择政治面貌', trigger: 'change'}
  ],
  professional: [
    {required: true, message: '请选择从业情况', trigger: 'change'}
  ],
  degree: [
    {required: true, message: '请选择最高学历', trigger: 'change'}
  ],
  organizationType: [
    {required: true, message: '请选择团队类型', trigger: 'change'}
  ],
  addressCode: [
    {required: true, message: '请选择联系地址', trigger: 'change'}
  ],
  serviceObjectArray: [
    {required: true, message: '请选择服务对象', trigger: 'change'}
  ],
  provePicture: [
    {required: true, validator: validateProvePic, trigger: 'change'}
  ]
});
//地区选择器选项
const options = regionData;
//政治面貌数组
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
//从业情况数组
const professionals = [
  {
    value:'国家公务员',
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
//学历数组
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
//团队/单位类型
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

const chooseRegisterAddress = (value) => {
  let address = '';
  for (let i = 0; i < value.length; i++) {
    address += CodeToText[value[i]];//码转汉字
  }
  registerUser.address = address;
};
const addressDetailChange = (value) => {
  registerUser.address += value;
};
const getVerifyCode = () => {
  if (registerUser.personalPhone === '') {
    ElMessage.warning('请先填写手机号');
  } else {
    const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}(,1[3|4|5|6|7|8|9][0-9]\d{8})*$/;
    if (!reg.test(registerUser.personalPhone)) {
      ElMessage.warning('请输入正确的手机号');
      return;
    }
    let data = new FormData();
    data.append('phone',registerUser.personalPhone);
    data.append('verifyLogo','register');
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
const handleExceed = () => {
  ElMessage.warning('只能上传一张图片，若想上传新的图片请先将旧的图片移除');
};
const handleProveRemove = () => {
  let formData = new FormData();
  formData.append("pictureUrl",registerUser.provePicture);
  myAxios.post('/base/image/remove',formData).then(response => {
    if (response.data.code === 200) {
      registerUser.provePicture = '';
      proveFileList.value = [];
    } else {
      ElMessage.error("系统错误，图片删除失败，请重试");
    }
  });
};
const handleCertificateRemove = () => {
  let formData = new FormData();
  formData.append("pictureUrl",registerUser.headCertificatePicture);
  myAxios.post('/base/image/remove',formData).then(response => {
    if (response.data.code === 200) {
      registerUser.headCertificatePicture = '';
      certificateFileList.value = [];
    } else {
      ElMessage.error("系统错误，图片删除失败，请重试");
    }
  });
};
const handleCertificateUploadSuccess = (result) => {
  registerUser.headCertificatePicture = result.data.filepath;
  certificateFileList.value.push({name: result.data.filepath,url: result.data.filepath});
};
const handleProveUploadSuccess = (result) => {
  registerUser.provePicture = result.data.filepath;
  proveFileList.value.push({name: result.data.filepath,url: result.data.filepath});
};
const serviceObjectChange = (value) => {
  registerUser.serviceObject = value.toString();
};
const submitRegisterForm = () => {
  registerFormRef.value.validate((valid) => {
    if (valid) {
      myAxios.post('/base/register',registerUser).then(response => {
        if (response.data.code === 200) {
          const loading = ElLoading.service({lock: true, text: '注册成功，正在跳转登录页面'});
          setTimeout(() => {
            loading.close();
            router.push('/login');
            ElMessage.warning('志愿者团队和志愿单位注册成功需等管理员审核通过后方可登录');
          }, 1000);
        } else if (response.data.code === 420) {
          ElMessage.error('验证码不存在或已过期');
        } else if (response.data.code === 421) {
          ElMessage.warning('验证码不正确');
        } else {
          ElMessage.error('发生错误，注册失败，请重试');
        }
      })
    } else {
      ElMessage.warning('请完整填写信息');
    }
  })
};
const resetRegisterForm = () => {
  registerFormRef.value.resetFields();
  certificatePicRef.value.clearFiles();
  provePicRef.value.clearFiles();
};
</script>

<style lang="scss" scoped>
.register-container {
  width: 1250px;
  height: auto;
  margin: 30px auto;
  display: flex;
  flex-direction: column;
}
.register-link {
  width: 100%;
  text-align: right;
  color: #959393;
}
.register-link span {
  margin: 0 5px;
}
.register-link a {
  color: #959393;
  font-size: 13px;
  text-decoration: none;
}
.register-link a:hover {
  color: #1989fa;
}
:deep(.register-main) {
  width: 100%;
  height: auto;
  margin-top: 10px;
  background-color: #ffffff;

  .el-form {
    padding: 30px 80px;

    h5 {
      text-align: left;
      font-size: 18px;
      color: #1989fa;
      font-weight: bold;
      margin-bottom: 20px;
    }
    .el-input {
      width: 300px;
    }
    .el-textarea {
      width: 630px;
    }

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
}
</style>