<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="240px">
        <el-menu
            :default-active="route.path"
            class="el-menu-vertical-demo"
            :router="true">
          <div style="padding: 5px 15px;height: 60px;">
            <div class="systemTitle">
              <router-link to="/admin/home">
                <img :src="logoUrl" alt="加载失败"/><span>云志愿后台管理</span>
              </router-link>
            </div>
          </div>
          <el-menu-item index="/admin/home">
            <el-icon :size="18"><HomeFilled /></el-icon>
            <span class="menu-name">首页</span>
          </el-menu-item>
          <el-sub-menu index="/admin/user">
            <template #title>
              <el-icon :size="18"><Avatar /></el-icon>
              <span class="menu-name">用户管理</span>
            </template>
            <el-menu-item index="/admin/user/volunteerManage">
              <el-icon :size="18"><UserFilled /></el-icon>
              <span class="menu-name">志愿者管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/user/teamManage">
              <el-icon :size="18"><UserFilled /></el-icon>
              <span class="menu-name">志愿团队管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/user/unitManage">
              <el-icon :size="18"><UserFilled /></el-icon>
              <span class="menu-name">志愿单位管理</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/admin/activity">
            <el-icon :size="18"><Promotion /></el-icon>
            <span class="menu-name">活动管理</span>
          </el-menu-item>
          <el-sub-menu index="/admin/inform">
            <template #title>
              <el-icon :size="18"><Share /></el-icon>
              <span class="menu-name">志愿资讯</span>
            </template>
            <el-menu-item index="/admin/inform/informManage">
              <el-icon :size="18"><Management /></el-icon>
              <span class="menu-name">资讯管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/inform/informEdit">
              <el-icon :size="18"><Edit /></el-icon>
              <span class="menu-name">资讯编辑</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="/admin/audit">
            <template #title>
              <el-icon :size="18"><Stamp /></el-icon>
              <span class="menu-name">审核管理</span>
            </template>
            <el-menu-item index="/admin/audit/registerAudit">
              <el-icon :size="18"><UserFilled /></el-icon>
              <span class="menu-name">注册审核</span>
            </el-menu-item>
            <el-menu-item index="/admin/audit/activityAudit">
              <el-icon :size="18"><Promotion /></el-icon>
              <span class="menu-name">活动审核</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/admin/message">
            <el-icon :size="18"><BellFilled /></el-icon>
            <span class="menu-name">消息管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/donate">
            <el-icon :size="18"><WalletFilled /></el-icon>
            <span class="menu-name">捐赠管理</span>
          </el-menu-item>
<!--          <el-menu-item index="/admin/setting">-->
<!--            <el-icon :size="18"><Tools /></el-icon>-->
<!--            <span class="menu-name">系统设置</span>-->
<!--          </el-menu-item>-->
        </el-menu>
      </el-aside>

      <el-container>
        <el-header style="padding: 0;">
          <div class="admin-header">
            <el-dropdown @command="handDropdown">
              <span class="el-dropdown-link">
                <el-avatar :size="45" :src="avatarUrl"/>
              </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="LoginOut">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
            </el-dropdown>
            <div v-if="token !== ''" class="messageBox">
              <el-icon :size="20" color="#868484" @click="toMyMessage" class="messageIcon"><BellFilled /></el-icon>
              <el-badge is-dot  type="primary" v-show="store.state.newMessageCount > 0"/>
            </div>
          </div>
        </el-header>

        <el-main style="padding: 0"><router-view/></el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import {onMounted, ref} from "vue";
import { useRoute,useRouter } from "vue-router";
import { HomeFilled,Avatar,Management,Share,Edit,UserFilled,Stamp,Promotion,WalletFilled,BellFilled,Tools } from "@element-plus/icons";
import myAxios from "../utils/axios";
import {useStore} from "vuex";
import jwt_decode from "jwt-decode";

const route = useRoute();
const router = useRouter();
const store = useStore();
const logoUrl = ref('https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/adminLogo.svg');
const avatarUrl = ref('');
const token = ref('');

const toMyMessage = () => {
  router.push('/admin/message');
};
const handDropdown = (command) => {
  if (command === 'LoginOut'){
    store.commit('setToken','');
    store.commit('setLoginUser','');
    store.commit('setDetail',{userKey: '',userType: ''});
    router.push('/admin');
  }
};
const getAvatarUrl = () => {
  token.value = store.state.token;
  if (token.value !== '') {
    avatarUrl.value = jwt_decode(token.value).userAvatar;
  }
};
const getMessageCount = () => {
  if (token.value !== '') {
    let data = new FormData();
    data.append("userId",store.state.loginUser.userId);
    data.append("userType",store.state.loginUser.userType);
    myAxios.post('/message/getMessageCount',data).then(response => {
      if (response.data.code === 200) {
        store.commit('setMessageCount',response.data.data.messageCount);
      }
    });
  }
};
onMounted(() => {
  getAvatarUrl();
  getMessageCount();
});
</script>

<style lang="scss" scoped>
.common-layout,.el-container {
  height: 100%;

  :deep(.el-aside) {
    .el-menu {
      background-color: #f4f4f4;
      width: 100%;
      height: 100%;

      .systemTitle {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-content: center;

        a {
          width: 100%;
          height: 100%;
          text-decoration: none;
          display: flex;
          vertical-align: top;
          justify-content: center;
          align-items: center;

          img {
            width: 60px;
            height: 50px;
            margin-top: 3px;
          }
          span {
            font-size: 19px;
            letter-spacing: 1px;
            font-weight: bold;
            color: #20adee;
          }
        }
      }
      .el-menu-item:hover {
        background-color: #e3e3e3;
      }
      .el-sub-menu__title:hover {
        background-color: #e3e3e3;
      }
      li,.el-sub-menu__title {
        color: #646464;
        font-size: 16px;
      }
      li.is-active {
        color: #1989fa !important;
      }
    }
  }
  :deep(.el-container) {
    .admin-header {
      width: 100%;
      height: 65px;
      background-color: #f4f4f4;
      border-bottom: 1px solid #e1dede;
      display: flex;
      align-items: center;
      flex-direction: row-reverse;

      .el-dropdown {
        margin: 0 70px 0 20px;
      }

      .messageBox {
        display: flex;
        position: relative;

        .el-badge {
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

  }
}
</style>