<template>
  <el-card class="account-setting" style="border: 0;box-shadow: none;">
    <template #header>
      <h3 style="font-size: 20px;text-align: left;">账号设置</h3>
    </template>
    <div style="text-align: left;padding-left: 10px;">
      <div style="margin-bottom: 25px;font-size: 15px">密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码<span style="margin-left: 40px;font-size: 14px;"><router-link style="text-decoration: none;color: #1989fa;" to="/user-detail/account/modifyPassword">修改密码</router-link></span></div>
      <div style="font-size: 15px">注销账号<span style="margin-left: 40px;font-size: 14px;"><router-link style="text-decoration: none;color: #1989fa;" to="/user-detail/account/deleteAccount">立即注销</router-link></span></div>
    </div>
  </el-card>
</template>

<script setup>

</script>

<style scoped>

</style>