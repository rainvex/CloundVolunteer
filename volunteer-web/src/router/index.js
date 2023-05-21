import { createRouter, createWebHashHistory } from 'vue-router';
import store from '../store/index'
import {ElMessage} from 'element-plus';

const routes = [
  //TODO 2022/7/23 Rainvex 前台客户端路由
  {
    path: '/',
    name: 'ClientLayout',
    component: () => import('../layout/ClientLayout.vue'),
    meta: {clientNavShow: true,isAuth: false},
    children: [
      {
        path: '',
        name: 'Index',
        component: () => import('../views/client/Index.vue')
      },
      {
        path: 'activities',
        name: 'Activities',
        component: () => import('../views/client/Activities.vue')
      },
      {
        path: 'informs',
        name: 'Informs',
        component: () => import('../views/client/Informs.vue')
      },
      {
        path: 'volunteers',
        name: 'Volunteers',
        component: () => import('../views/client/Volunteers.vue')
      },
      {
        path: 'teams',
        name: 'Teams',
        component: () => import('../views/client/Teams.vue')
      },
      {
        path: 'units',
        name: 'Units',
        component: () => import('../views/client/Units.vue')
      },
      {
        path: 'activity-detail',
        name: 'Activity-Detail',
        component: () => import('../views/client/ActivityDetail.vue')
      },
      {
        path: 'inform-detail',
        name: 'Inform-Detail',
        component: () => import('../views/client/InformDetail.vue')
      },
      {
        path: 'login',
        name: 'Login',
        component: () => import('../views/client/Login.vue'),
        meta: {clientNavShow: false,isAuth: false}
      },
      {
        path: 'register-option',
        name: 'RegisterOption',
        component: () => import('../views/client/RegisterOption.vue')
      },
      {
        path: 'register/:registerType',
        name: 'Register',
        component: () => import('../views/client/Register.vue'),
        meta: {clientNavShow: false,isAuth: false}
      },
      {
        path: 'user-detail',
        name: 'User-Detail',
        component: () => import('../views/client/UserDetail.vue'),
        meta: {clientNavShow: true,isAuth: true}, //这儿加了，访问子路由一定会经由这儿，后面的子路由都是同样是设置（如果有不一样的就需要给子路由单独加上，如上）
        children: [
          {
            //个人资料（通用）
            path: 'profile',
            name: 'Profile',
            component: () => import('../components/client/userDetail/Profile.vue')
          },
          {
            //账号设置（通用）
            path: 'account',
            name: 'Account',
            component: () => import('../components/client/userDetail/Account.vue')
          },
          {
            //账号设置下的修改密码（通用）
            path: 'account/modifyPassword',
            name: 'ModifyPassword',
            component: () => import('../components/client/userDetail/ModifyPassword.vue')
          },
          {
            //账号设置下的注销账号（通用）
            path: 'account/deleteAccount',
            name: 'DeleteAccount',
            component: () => import('../components/client/userDetail/DeleteAccount.vue')
          },
          {
            //活动参加记录（志愿者和志愿单位）
            path: 'activityRecord',
            name: 'ActivityRecord',
            component: () => import('../components/client/userDetail/ActivityRecord.vue')
          },
          {
            //活动发布（志愿单位）
            path: 'activityPublic',
            name: 'ActivityPublic',
            component: () => import('../components/client/userDetail/ActivityPublic.vue')
          },
          {
            //活动申请记录（志愿者和志愿单位）
            path: 'myApply',
            name: 'MyApply',
            component: () => import('../components/client/userDetail/MyApply.vue')
          },
          {
            //活动收藏记录（志愿者）
            path: 'myCollection',
            name: 'MyCollection',
            component: () => import('../components/client/userDetail/MyCollection.vue')
          },
          {
            //实名认证（志愿者）
            path: 'auth',
            name: 'Auth',
            component: () => import('../components/client/userDetail/Auth.vue')
          },
          {
            //团队管理（志愿者和志愿者团队）
            path: 'teamInfo',
            name: 'TeamInfo',
            component: () => import('../components/client/userDetail/TeamInfo.vue')
          },
          {
            //审核管理（志愿者团队和志愿单位）
            path: 'auditManage',
            name: 'AuditManage',
            component: () => import('../components/client/userDetail/AuditManage.vue')
          },
          {
            //消息记录管理（通用）
            path: 'messageManage',
            name: 'MessageManage',
            component: () => import('../components/client/userDetail/MessageManage.vue')
          },
          {
            //捐赠记录（通用）
            path: 'donateRecord',
            name: 'DonateRecord',
            component: () => import('../components/client/userDetail/DonateRecord.vue')
          }
        ]
      }
    ]
  },
  //TODO 2022/7/23 Rainvex 后台管理路由
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/AdminView.vue'),
    redirect: '/admin/aLogin',
    children: [
      {
        path: 'aLogin',
        name: 'ALogin',
        component: () => import('../views/admin/Login.vue'),
        meta: {isAdmin: false}
      },
      {
        path: '',
        name: 'AdminLayout',
        component: () => import('../layout/AdminLayout.vue'),
        meta: {isAdmin: true},  //这儿加了，访问子路由一定会经由这儿，后面的子路由都是同样是设置（如果有不一样的就需要给子路由单独加上，如上）
        children: [
          {
            path: 'home',
            name: 'Home',
            component: () => import('../views/admin/Home.vue')
          },
          {
            path: 'user',
            name: 'User',
            children: [
              {
                path: 'volunteerManage',
                name: 'VolunteerManage',
                component: () => import('../views/admin/Volunteer.vue')
              },
              {
                path: 'teamManage',
                name: 'TeamManage',
                component: () => import('../views/admin/Team.vue')
              },
              {
                path: 'unitManage',
                name: 'UnitManage',
                component: () => import('../views/admin/Unit.vue')
              }
            ]
          },
          {
            path: 'activity',
            name: 'Activity',
            component: () => import('../views/admin/Activity.vue')
          },
          {
            path: 'inform',
            name: 'Inform',
            children: [
              {
                path: 'informManage',
                name: 'InformManage',
                component: () => import('../views/admin/Inform.vue')
              },
              {
                path: 'informEdit',
                name: 'InformEdit',
                component: () => import('../views/admin/InformEdit.vue')
              }
            ]
          },
          {
            path: 'audit',
            name: 'Audit',
            children: [
              {
                path: 'registerAudit',
                name: 'RegisterAudit',
                component: () => import('../views/admin/Audit.vue')
              },
              {
                path: 'activityAudit',
                name: 'ActivityAudit',
                component: () => import('../views/admin/Audit.vue')
              }
            ]
          },
          {
            path: 'message',
            name: 'Message',
            component: () => import('../views/admin/Message.vue')
          },
          {
            path: 'donate',
            name: 'Donate',
            component: () => import('../views/admin/Donate.vue')
          },
          {
            path: 'setting',
            name: 'Setting',
            component: () => import('../views/admin/Setting.vue')
          }
        ]
      }
    ]
  },
  {
    path: '/error',
    name: 'Error',
    component: () => import('../views/error.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

//利用router全局前置守卫配置路由守卫
router.beforeEach((to, from, next) => {
  //如果即将跳转的路由需要用户身份认证
  if (to.meta.isAuth) {
    //如果需要用户登录，而用户并没有登录时，则跳转到登录页
    if (store.state.token === '') {
      ElMessage.error('请先登录');
      next({name: 'Login'});
    } else {
      next();
    }
  } else if (to.meta.isAdmin) {
    //对进入后台管理相关页面的限制
    if (store.state.token === '') {
      ElMessage.error('请先登录');
      next({name: 'ALogin'});
    } else if (store.state.token !== '' && store.state.loginUser.userType !== 3) {
      next({name: 'Error'});
    } else {
      next();
    }
  } else {
    next();
  }
})

export default router
