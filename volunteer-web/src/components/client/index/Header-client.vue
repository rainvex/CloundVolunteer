<template>
  <!--导航栏-->
  <el-container id="header-client">
    <div id="head-menu">
      <el-menu
          :default-active="route.path"
          class="el-menu-demo"
          mode="horizontal"
          :ellipsis="false"
          :router="true"
          background-color="#da4453"
      >
        <router-link to="/" id="logo"><el-image :src="logoUrl" style="width: 120px;height: 100%;margin: 0 15px;" /></router-link>
        <el-menu-item class="navOptions" index="/">首页</el-menu-item>
        <el-menu-item class="navOptions" index="/activities">志愿活动</el-menu-item>
        <el-menu-item class="navOptions" index="/informs">志愿资讯</el-menu-item>
        <el-menu-item class="navOptions" index="/volunteers">志愿者</el-menu-item>
        <el-menu-item class="navOptions" index="/teams">志愿者团队</el-menu-item>
        <el-menu-item class="navOptions" index="/units">志愿单位</el-menu-item>
      </el-menu>
    </div>
    <div id="head-user">
      <el-link :underline="false" class="navOptions" v-if="localToken === ''" @click="toLogin" id="login">登录</el-link>
      <el-link :underline="false" class="navOptions" v-if="localToken === ''" @click="toRegisterOption" id="register">注册</el-link>
      <div v-if="localToken !== '' && store.state.loginUser.userType !== 3" class="messageBox">
        <el-icon :size="20" color="#ffffff" @click="toMyMessage" class="messageIcon"><BellFilled /></el-icon>
        <el-badge is-dot  type="primary" v-show="store.state.newMessageCount > 0"/>
      </div>
      <el-dropdown @command="handDropdown" v-if="localToken !== '' && store.state.loginUser.userType !== 3" style="margin-left: 20px;">
        <span class="el-dropdown-link">
          <el-avatar :size="45" :src="userAvatar"/>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="UserDetail">个人信息</el-dropdown-item>
            <el-dropdown-item command="LoginOut">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-link :underline="false" class="navOptions" v-if="localToken !== '' && store.state.loginUser.userType === 3" @click="router.push('/admin/home')">返回后台</el-link>
    </div>
  </el-container>
</template>

<script setup>
import {onMounted, ref} from "vue";
import { useRoute,useRouter } from "vue-router";
import { useStore } from 'vuex';
import { BellFilled } from '@element-plus/icons';
import jwt_decode from 'jwt-decode';
import myAxios from "../../../utils/axios";

const router = useRouter();
const route = useRoute();
const store = useStore();
const logoUrl = ref('https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/logo.svg');
const localToken = store.state.token;
const userAvatar = localToken === '' ? '' : jwt_decode(localToken).userAvatar;

const getMessageCount = () => {
  if (localToken !== '') {
    let data = new FormData();
    data.append("userId",store.state.loginUser.userId);
    data.append("userType",store.state.loginUser.userType);
    myAxios.post('/message/getMessageCount',data).then(response => {
      if (response.data.code === 200) {
        store.commit('setMessageCount',response.data.data.messageCount);
      }
    });
  }
}
onMounted(() => {
  getMessageCount();
})
const handDropdown = (command) => {
  if (command === 'UserDetail'){
    let detailKey = {
      userKey: store.state.loginUser.userId,
      userType: store.state.loginUser.userType
    }
    store.commit('setDetail',detailKey);
    router.push('/user-detail/profile');
  } else if (command === 'LoginOut'){
    store.commit('setToken','');
    store.commit('setLoginUser','');
    store.commit('setDetail',{userKey: '',userType: ''});
    window.location.href = '/';
  }
};
const toMyMessage = () => {
  let detailKey = {
    userKey: store.state.loginUser.userId,
    userType: store.state.loginUser.userType
  }
  store.commit('setDetail',detailKey);
  router.push('/user-detail/messageManager');
}
const toLogin = () => {
  router.push('/login');
};
const toRegisterOption = () => {
  router.push('/register-option');
}
</script>

<style lang="scss" scoped>
#header-client {
  width: 100%;
  min-width: 1250px;
  height: auto;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  background-color: #da4453;

  #head-menu {
    width: 60%;
    :deep(.el-menu) {
      border-bottom: none;
      .el-menu-item.is-active {
        border-bottom: 3px solid #f8af3f;
        color: #ffffff !important;
      }
    }
  }

  #head-user {
    width: 20%;
    display: flex;
    justify-content: center;
    align-items: center;

    .messageBox {
      padding-top: 5px;
      display: flex;
      position: relative;

      :deep(.el-badge) {
        position: absolute;
        left: 14px;

        .el-badge__content {
          border: none;
        }
      }

      .messageIcon:hover {
        cursor: pointer;
      }
    }
  }

  .navOptions {
    font-size: 1rem;
    font-weight: bold;
    color: #ffffff !important;
  }
  #login,#register {
     padding-left: 10px;
     padding-right: 10px;
   }
}
</style>