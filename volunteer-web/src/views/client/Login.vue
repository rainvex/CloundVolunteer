<template>
  <el-container class="login-container">
    <img src="../../assets/loginbg.png" alt="" style="height: 570px;margin-right: 100px;"/>
    <el-tabs
        model-value="emailLogin"
        type="card"
        class="demo-tabs">
      <el-tab-pane label="邮箱登录" name="emailLogin">
        <el-form :model="loginUserForEmail" ref="loginForEmailRef" :rules="loginForEmailRules" label-width="150">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="loginUserForEmail.email" placeholder="请输入邮箱">
              <template #prefix>
                <el-icon :size="17" class="el-input__icon"><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input show-password v-model="loginUserForEmail.password" placeholder="请输入密码">
              <template #prefix>
                <el-icon :size="17" class="el-input__icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-radio-group v-model="loginUserForEmail.userType" style="margin-left: 13px;">
              <el-radio :label="0">志愿者</el-radio>
              <el-radio :label="1">志愿者团队</el-radio>
              <el-radio :label="2">志愿单位</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <span><router-link style="text-decoration: none;color: #1989fa" to="/register-option">还未注册？立即注册</router-link></span>
            <span style="margin-left: 123px;"><router-link style="text-decoration: none;color: #1989fa" to="">忘记密码？</router-link></span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitLoginForEmail">登录</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="手机登录" name="phoneLogin">
        <el-form :model="loginUserForPhone" ref="loginForPhoneRef" :rules="loginForPhoneRules" label-width="150">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="loginUserForPhone.phone" placeholder="请输入个人或负责人手机号">
              <template #prefix>
                <el-icon :size="17" class="el-input__icon"><Iphone /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="验证码" prop="verifyCode">
            <el-input v-model="loginUserForPhone.verifyCode" placeholder="请输入手机验证码">
              <template #prefix>
                <el-icon :size="17" class="el-input__icon"><ChatSquare /></el-icon>
              </template>
              <template #suffix>
                <span id="getVerifyCode" @click="getVerifyCode">获取验证码</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-radio-group v-model="loginUserForPhone.userType" style="margin-left: 13px;">
              <el-radio :label="0">志愿者</el-radio>
              <el-radio :label="1">志愿者团队</el-radio>
              <el-radio :label="2">志愿单位</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <span><router-link style="text-decoration: none;color: #1989fa" to="/register-option">还未注册？立即注册</router-link></span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitLoginForPhone">登录</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-container>
</template>

<script setup>
import { Lock,Message,Iphone,ChatSquare } from '@element-plus/icons';
import { reactive, ref } from 'vue';
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { ElLoading, ElMessage } from "element-plus";
import myAxios from "../../utils/axios";

const store = useStore();
const router = useRouter();
const loginForEmailRef = ref();
const loginForPhoneRef = ref();
const loginUserForEmail = reactive({
  email: '',
  password: '',
  userType: 0,
  loginType: 0
});
const loginUserForPhone = reactive({
  phone: '',
  verifyCode: '',
  userType: 0,
  loginType: 1
});
const validatePhone = (rule, value, callback) => {
  if (value !== '') {
    const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}(,1[3|4|5|6|7|8|9][0-9]\d{8})*$/;
    if (!reg.test(value)) {
      return callback(new Error('请输入正确的手机号码'));
    } else {
      return callback(); // *验证成功的地方必须callback()
    }
  }else {
    return callback(new Error('请输入手机号'));
  }
};
const loginForEmailRules = reactive({
  email: [
    {required: true,message: '请输入邮箱',trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur'}
  ],
  password: [
    {required: true,message: '请输入密码',trigger: 'blur'}
  ]
});
const loginForPhoneRules = reactive({
  phone: [
    {required: true,message: '请输入手机号',trigger: 'blur'},
    {validator: validatePhone,trigger: 'blur'}
  ],
  verifyCode: [
    {required: true,message: '请输入验证码',trigger: 'blur'}
  ]
});

const getVerifyCode = () => {
  if (loginUserForPhone.phone === '') {
    ElMessage.warning('请先填写手机号');
  } else {
    const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}(,1[3|4|5|6|7|8|9][0-9]\d{8})*$/;
    if (!reg.test(loginUserForPhone.phone)) {
      ElMessage.warning('请输入正确的手机号');
      return;
    }
    let data = new FormData();
    data.append('phone',loginUserForPhone.phone);
    data.append('userType',loginUserForPhone.userType);
    data.append('verifyLogo','login');
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
const submitLoginForEmail = () => {
  loginForEmailRef.value.validate((valid) => {
    if (valid) {
      const loading = ElLoading.service({lock: true, text: '正在登录，请稍后'});
      myAxios.post('/base/login',loginUserForEmail).then(response => {
        loading.close();
        if (response.data.code === 200) {
          store.commit('setToken',response.data.data.token);
          store.commit('setLoginUser',response.data.data.token);
          ElMessage.success('登录成功');
          router.push('/');
        } else if (response.data.code === 430) {
          ElMessage.error('账号不存在或已被停用');
        }
      });
    } else {
      ElMessage.error('请完整填写登录信息');
    }
  })
};
const submitLoginForPhone = () => {
  loginForPhoneRef.value.validate((valid) => {
    if (valid) {
      const loading = ElLoading.service({lock: true, text: '正在登录，请稍后'});
      myAxios.post('/base/login',loginUserForPhone).then(response => {
        loading.close();
        if (response.data.code === 200) {
          store.commit('setToken',response.data.data.token);
          store.commit('setLoginUser',response.data.data.token);
          ElMessage.success('登录成功');
          router.push('/');
        } else if (response.data.code === 430) {
          ElMessage.error('账号不存在或已被停用');
        } else if (response.data.code === 420) {
          ElMessage.error('验证码不存在或已过期');
        } else if (response.data.code === 421) {
          ElMessage.error('验证码输入不正确');
        }
      });
    } else {
      ElMessage.error('请完整填写登录信息');
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100%;
  justify-content: center;
  align-items: center;
}
.demo-tabs {
  width: 400px;
  height: 400px;
  background-color: #e3e4e5;
}
:deep(.el-tabs--card) {
  #tab-emailLogin {
    width: 200px;
  }
  #tab-phoneLogin {
    width: 200px;
  }
  .el-tabs__header {
    border-bottom: 0;

    .el-tabs__nav {
      border: none;

      .el-tabs__item {
        background-color: #c9c8c8;
        color: #959393;
      }

      .el-tabs__item.is-active {
        background-color: #e3e4e5;
        color: #626060;
        border-bottom-color: #e3e4e5;
      }
    }
  }
}
:deep(.el-form) {
  padding: 40px;

  #getVerifyCode {
    color: #838282;
  }
  #getVerifyCode:hover {
    color: #1989fa;
    cursor: pointer;
  }
  .el-button {
    width: 100%;
    background-color: #da4453;
    border-color: #da4453
  }
}
</style>