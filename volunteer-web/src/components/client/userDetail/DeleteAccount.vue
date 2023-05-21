<template>
  <div>
    <el-card class="account-setting" style="border: 0;box-shadow: none;">
      <template #header>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/user-detail/account' }">账号设置</el-breadcrumb-item>
          <el-breadcrumb-item>注销账号</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
      <div style="text-align: left;padding-left: 10px;line-height: 30px;">
        <h4>账号注销则视为您主动放弃以下资产和权益，且同意以下规则：</h4>
        <p>1.账号注销后，您将无法登录、使用该账号，且不支持找回任何个人资料。</p>
        <p>2.注销该账号并不代表您注销前的账号行为和相关责任得到豁免或减轻。</p>
      </div>
      <el-popconfirm
          confirm-button-text="确认"
          cancel-button-text="取消"
          title="你确认要注销该账号吗?"
          @confirm="confirmDeleteAccount">
        <template #reference>
          <el-button round color="#da4453" style="margin-top: 20px">已知悉，确认注销该账号</el-button>
        </template>
      </el-popconfirm>

    </el-card>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import myAxios from "../../../utils/axios";
import {ElMessage} from "element-plus";

const store = useStore();

const confirmDeleteAccount = () => {
  let data = new FormData();
  data.append("userId",store.state.loginUser.userId);
  data.append("userType",store.state.loginUser.userType);
  myAxios.post('/base/deleteAccount',data).then(response => {
    if (response.data.code === 200) {
      store.commit('setToken','');
      store.commit('setLoginUser','');
      store.commit('setDetail',{userKey: '',userType: ''});
      window.location.href = '/';
      ElMessage.success('注销账号成功，祝你生活愉快。江湖再见！');
    } else {
      ElMessage.error('发生错误，注销账号失败');
    }
  })
  console.log('已注销该账号');
};
</script>

<style scoped>

</style>