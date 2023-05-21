<template>
  <el-container class="layout-container-demo" style="height: auto;">
    <el-aside width="160px">
      <el-menu
          :default-active="route.path"
          class="el-menu-vertical-demo"
          @select="handSelectMenu"
          style="border-right: 0;text-align: center">
        <el-menu-item class="user-detail-options" index="/user-detail/profile">
          <span>个人资料</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/account" v-if="(store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType)">
          <span>账号设置</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/activityRecord" v-if="(store.state.detail.userType === 0 || store.state.detail.userType === 2)">
          <span>我的活动</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/myApply" v-if="(store.state.detail.userType === 0 || store.state.detail.userType === 2)">
          <span>我的申请</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/myCollection" v-if="(store.state.detail.userType === 0)">
          <span>我的收藏</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/auth" v-if="((store.state.detail.userType === 0) && ((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType)))">
          <span>实名认证</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/teamInfo" v-if="(store.state.detail.userType === 0 || store.state.detail.userType === 1)">
          <span>团队信息</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/auditManage" v-if="((store.state.detail.userType === 1 || store.state.detail.userType === 2) && ((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType)))">
          <span>审核管理</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/messageManage" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))">
          <span>消息管理</span>
        </el-menu-item>
        <el-menu-item class="user-detail-options" index="/user-detail/donateRecord">
          <span>捐赠记录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-main style="padding-top: 0;">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute,useRouter } from "vue-router";
import { useStore } from "vuex";

const route = useRoute();
const router = useRouter();
const store = useStore();

const handSelectMenu = (index) => {
  router.push(index);
}
</script>

<style scoped>
.layout-container-demo {
  margin: 30px auto;
  width: 1250px;
}
.user-detail-options {
  justify-content: center;
  align-items: center;
  font-size: 15px;
}
</style>