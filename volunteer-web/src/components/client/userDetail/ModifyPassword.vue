<template>
  <div>
    <el-card class="account-setting-password" style="border: 0;box-shadow: none;">
      <template #header>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/user-detail/account' }">账号设置</el-breadcrumb-item>
          <el-breadcrumb-item>修改密码</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
      <div>
        <el-form :model="modifyPassword" label-width="120px">
          <el-form-item label="输入旧密码">
            <el-input
                v-model="modifyPassword.oldPassword"
                type="password"
                placeholder="请输入旧密码"
                show-password
            />
          </el-form-item>
          <el-form-item label="输入新密码">
            <el-input
                v-model="modifyPassword.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
            />
          </el-form-item>
          <el-form-item label="确认新密码">
            <el-input
                v-model="modifyPassword.confirmPwd"
                type="password"
                placeholder="请再次输入新密码"
                show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitModifyPwd" :disabled="!(modifyPassword.oldPassword !== '' && modifyPassword.newPassword !== '' && modifyPassword.confirmPwd !== '')">确认修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { ElMessage } from 'element-plus';
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import myAxios from "../../../utils/axios";


const store = useStore();
const router = useRouter();
const modifyPassword = reactive({
  userId: store.state.loginUser.userId,
  userType: store.state.loginUser.userType,
  oldPassword: '',
  newPassword: '',
  confirmPwd: ''
});

const submitModifyPwd = () => {
  if (modifyPassword.newPassword !== modifyPassword.confirmPwd){
    ElMessage.error('两次输入的新密码不一致');
  }else {
    myAxios.post('/base/modifyPassword',modifyPassword).then(response => {
      if (response.data.code === 200) {
        ElMessage.success('密码修改成功，请重新登录');
        store.commit('setToken','');
        store.commit('setLoginUser','');
        router.push('/login');
      } else {
        ElMessage.error(response.data.message);
      }
    })
  }
};
</script>

<style scoped>

</style>