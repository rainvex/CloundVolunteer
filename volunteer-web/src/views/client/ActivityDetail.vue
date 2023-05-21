<template>
  <!--活动详情-->
  <el-container class="activity-detail-container">
    <el-container>
      <el-header class="activity-detail-header">
        <h3 class="activity-detail-title">{{ activityData.activity.activityTitle }}</h3>
        <div style="display: flex;font-size: 13px">
          <span>分类：<span>{{ activityData.activity.activityClassic }}</span></span>
          <span style="margin: 0 30px">发布时间：<span>{{ activityData.activity.activityPublic }}</span></span>
          <span>发布单位：<span>{{ activityData.activity.publisherUnitName }}</span></span>
          <span style="margin-left: 30px">志愿活动分享：<el-link :underline="false" type="primary"><el-icon :size="20"><Share /></el-icon></el-link></span>
        </div>
      </el-header>
      <el-main style="background-color: #ffffff">
        <div style="padding: 0 150px">
          <el-image :src="activityData.activity.activityBanner" alt="加载失败" >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        <div class="activity-detail-main-description">
          <div class="activity-detail-description-div">【活动时间】：<span>{{ activityData.activity.activityContinue }}</span></div>
          <div class="activity-detail-description-div">【活动地点】：<span>{{ activityData.activity.activityPlace }}</span></div>
          <div class="activity-detail-description-div">【活动简介】：<span>{{ activityData.activity.activitySimple }}</span></div>
          <div class="activity-detail-description-div">【活动内容】：<span>{{ activityData.activity.activityContent }}</span></div>
          <div class="activity-detail-description-div">【报名要求】：<span>{{ activityData.activity.activityRequire }}</span></div>
          <div class="activity-detail-description-div">【活动负责人】：<span>{{ activityData.activity.activityContact }}</span></div>
          <div class="activity-detail-description-div">【联系电话】：<span>{{ activityData.activity.activityPhone }}</span></div>
          <div class="activity-detail-description-div">【报名截止时间】：<span>{{ activityData.activity.activityCutoff }}</span></div>
        </div>
      </el-main>
    </el-container>
    <el-aside class="activity-detail-aside">
      <div class="activity-detail-aside-top">
        <div class="activity-detail-aside-top-option">
          <div class="activity-detail-aside-top-option-div">
            <el-icon :size="25" color="#eb6751" v-if="!activityData.isStared"><Star /></el-icon>
            <el-icon :size="25" color="#eb6751" v-if="activityData.isStared"><StarFilled/></el-icon>
            <div style="font-size: 14px;margin-top: 5px">收藏</div>
            <div style="font-size: 14px;margin-top: 5px">{{ activityData.activity.staredCount }}</div>
          </div>
          <div class="activity-detail-aside-top-option-div" style="margin: 0 20px">
            <el-icon :size="25" color="#2386d0"><User /></el-icon>
            <div class="activity-detail-aside-top-option-des">招募人数</div>
            <div class="activity-detail-aside-top-option-des">{{ activityData.activity.activityLimit }}</div>
          </div>
          <div class="activity-detail-aside-top-option-div">
            <el-icon :size="25" color="#2386d0"><UserFilled /></el-icon>
            <div class="activity-detail-aside-top-option-des">已招募</div>
            <div class="activity-detail-aside-top-option-des">{{ activityData.activity.activityApplied }}</div>
          </div>
        </div>
        <div v-if="store.state.loginUser.userType === 0 && activityData.activity.activityShow === 1" style="margin-bottom: 30px;">
          <el-button type="primary" round @click="starActivity" style="background-color: #ff8814;border-color: #ff8814" v-if="!activityData.isStared">收藏活动</el-button>
          <el-button type="primary" disabled round style="background-color: #ff8814;border-color: #ff8814" v-if="activityData.isStared">已收藏</el-button>
          <el-button type="primary" @click="applySignUp" round style="background-color: #ff8814;border-color: #ff8814" v-if="!activityData.isApplied">我要报名</el-button>
          <el-button type="primary" disabled round style="background-color: #ff8814;border-color: #ff8814" v-if="activityData.isApplied">已报名</el-button>
        </div>
        <div v-if="activityData.activity.activityShow === 0" style="margin-bottom: 30px;">该志愿活动当前属于不可见状态</div>
      </div>
      <p style="text-align: left;font-weight: bold;font-size: 15px;margin-bottom: 10px">
        <el-icon :size="20" color="#da4453" style="vertical-align: bottom"><Medal /></el-icon>已招募的志愿者
      </p>
      <div class="activity-detail-aside-bottom">
        <div style="display: flex;margin-bottom: 10px;" v-if="activityData.passVolunteers.length !== 0" v-for="list in activityData.passVolunteers">
          <el-avatar shape="square" :size="50" :src="list.volunteersAvatar" />
          <div style="text-align: left;margin-left: 15px;font-size: 15px;line-height: 25px">
            <p class="passVolunteerName" @click="viewVolunteerProfile(list.volunteersId)">{{ list.volunteersUsername }}</p>
            <p style="color: #959393">服务时长：<span>{{ list.volunteersHours }}</span>小时</p>
          </div>
        </div>
        <p style="margin-top: 50%;" v-if="activityData.passVolunteers.length === 0">暂无志愿者报名</p>
      </div>
    </el-aside>
    <el-dialog v-model="applyInfoDialogVisible" center draggable :before-close="cancelSubmit" title="申请信息填写" width="30%">
      <el-form :model="applyInfo" ref="applyInfoRef" :rules="applyInfoRules" label-width="150">
        <el-form-item label="申请标题" prop="applyTitle">
          <el-input v-model="applyInfo.applyTitle" placeholder="请输入申请标题" maxlength="50" show-word-limit/>
        </el-form-item>
        <el-form-item label="申请描述" prop="applyDescription">
          <el-input v-model="applyInfo.applyDescription" type="textarea" :autosize="{minRows: 3}" placeholder="请描述你想申请的原因及自身优势（最多300字）" show-word-limit maxlength="300"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelSubmit">取消</el-button>
          <el-button type="primary" @click="submitApplyInfo">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { Share,Picture,Star,StarFilled,User,UserFilled,Medal } from "@element-plus/icons";
import { ref, reactive, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import myAxios from "../../utils/axios";

const store = useStore();
const router = useRouter();
const applyInfoDialogVisible = ref(false);
const applyInfoRef = ref();
const applyInfoRules = reactive({
  applyTitle: [
    {required: true, message: '请输入申请标题', trigger: 'blur'}
  ],
  applyDescription: [
    {required: true, message: '请输入申请信息', trigger: 'blur'}
  ]
});
const activityData = reactive({
  activity: '',
  passVolunteers: [],
  isStared: false,
  isApplied: false,
  isExpired: false
});
const applyInfo = reactive({
  applyTitle: '',
  applyDescription: '',
  applyType: 0,
  fkApplicantId: store.state.loginUser.userId,
  fkApplicantType: store.state.loginUser.userType,
  fkAuditorType: 2,
  fkAppliedActivity: store.state.activeKey
});

const getActivityData = () => {
  let data = new FormData();
  data.append("activeId",store.state.activeKey);
  myAxios.post('/activity/getActivityById',data).then(response => {
    if (response.data.code === 200) {
      activityData.activity = response.data.data.activity;
      activityData.isExpired = response.data.data.activity.expired;
      activityData.passVolunteers = response.data.data.activity.volunteersInfoList;
    } else {
      ElMessage.error('系统出错，查询失败');
    }
  })
};
const getIsStared = () => {
  //如果查看该志愿活动的角色是志愿者，则判断其是否已经收藏该活动
  if (store.state.loginUser.userType === 0) {
    let data = new FormData();
    data.append("userId",store.state.loginUser.userId);
    data.append("activeId",store.state.activeKey);
    myAxios.post('/star/getIsStared',data).then(response => {
      activityData.isStared  = response.data.data.isStared;
    })
  }
};
const getIsApplied = () => {
  //如果查看该志愿活动的角色是志愿者，则判断其是否已经申请参加该活动
  if (store.state.loginUser.userType === 0) {
    let data = new FormData();
    data.append("userId",store.state.loginUser.userId);
    data.append("activeId",store.state.activeKey)
    myAxios.post('/apply/getIsApplied',data).then(response => {
      activityData.isApplied = response.data.data.isApplied;
    })
  }
};
onMounted(() => {
  getActivityData();
  getIsStared();
  getIsApplied();
})
const starActivity = () => {
  let data = new FormData();
  data.append("userId",store.state.loginUser.userId);
  data.append("activeId",store.state.activeKey);
  myAxios.post('/star/starActivity',data).then(response => {
    if (response.data.code === 200) {
      activityData.isStared = true;
      activityData.activity = response.data.data.newActivity;
      ElMessage.success('收藏成功');
    } else {
      ElMessage.error('系统错误，收藏失败');
    }
  })
};
const applySignUp = () => {
  if (activityData.isExpired) {
    ElMessage.warning('已超过该志愿活动报名截止时间');
    return;
  }
  if (activityData.activity.activityLimit <= activityData.activity.activityApplied) {
    ElMessage.warning('抱歉，本次活动已达招募人数限制');
    return;
  }
  applyInfoDialogVisible.value = true;
}
const cancelSubmit = () => {
  applyInfoRef.value.resetFields();
  applyInfoDialogVisible.value = false;
};
const submitApplyInfo = () => {
  applyInfoRef.value.validate((valid) => {
    if (valid) {
      myAxios.post('/apply/insertApply',applyInfo).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('申请参加活动成功，请等待审核');
          activityData.isApplied = true;
          cancelSubmit();
        } else {
          ElMessage.error('系统错误，申请失败');
        }
      })
    } else {
      ElMessage.warning('请输入申请参加活动的必要信息');
    }
  })
};
const viewVolunteerProfile = (volunteerId) => {
  const detailKey = {userKey: volunteerId,userType: 0};
  store.commit('setDetail',detailKey);
  router.push('/user-detail/profile');
}
</script>

<style scoped>
.activity-detail-container {
  width: 1250px;
  margin: 0 auto;
  height: auto;
  padding-bottom: 50px;
}
.activity-detail-header {
  margin: 50px auto 20px
}
.activity-detail-title {
  font-size: 22px;
  margin-bottom: 20px
}
.activity-detail-main-description {
  text-align: left;
  padding: 30px;
  font-weight: bold;
  font-size: 15px;
  line-height: 30px;
}
.activity-detail-description-div {
  margin-top: 20px;
}
.activity-detail-description-div span {
  font-weight: normal;
}
.activity-detail-aside {
  margin-left: 20px;
  margin-top: 130px;
}
.activity-detail-aside-top {
  background-color: #ffffff;
  height: auto;
  margin-bottom: 20px;
  border: 1px solid #ffffff;
}
.activity-detail-aside-bottom {
  background-color: #ffffff;
  height: auto;
  padding: 20px;
  min-height: 300px;
}
.activity-detail-aside-top-option {
  display: flex;
  margin: 30px auto;
  justify-content: center;
  align-items: center;
}
.activity-detail-aside-top-option-div {
  border: 1px solid #f2f1f1;
  width: 65px;
  height: 90px;
}
.activity-detail-aside-top-option-div:hover {
  background-color: #f0f0f0;
}
.activity-detail-aside-top-option .el-icon {
  margin-top: 10px;
}
.activity-detail-aside-top-option-des {
  font-size: 14px;
  margin-top: 5px;
}
.passVolunteerName:hover {
  cursor: pointer;
  color: #da4453;
}
</style>