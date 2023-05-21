<template>
  <el-card class="my-apply" style="border: 0;box-shadow: none;">
    <template #header>
      <h3 style="font-size: 20px;text-align: left;">我的申请</h3>
    </template>
    <div style="text-align: left;padding-left: 10px;">
      <el-row :gutter="10">
        <el-col :span="4" v-if="applyData.applyList.length !== 0" v-for="list in applyData.applyList" :key="list">
          <el-card :body-style="{ padding: '0px' }" class="activity-card">
            <el-image :src="list.appliedActive.activityBanner" class="active-banner" fit="cover" alt="加载失败"/>
            <div class="card-bottom">
              <div class="active-title" @click="toSignUp(list.appliedActive.activityId)" :title="list.appliedActive.activityTitle">
                <span>{{ list.appliedActive.activityTitle }}</span>
              </div>
              <div class="active-place" :title="list.appliedActive.activityPlace">
                <span>{{ list.appliedActive.activityPlace }}</span>
              </div>
              <div class="active-time">
                <time>{{ list.appliedActive.activityPublic }}</time>
              </div>
              <el-button type="primary" link style="margin-top: 5px;font-size: 12px;" @click="viewApplyProgress(list.applyStatus,list.applyTime,list.applyAudittime)" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))">申请进度</el-button>
              <el-button type="primary" link style="margin-top: 5px;font-size: 12px;" @click="cancelApply(list.appliedActive.activityId)" v-if="((!list.appliedActive.expired) && (list.applyStatus === 0) && ((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType)))">取消申请</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="applyData.applyList.length === 0">
          <el-empty description="暂无相应数据"/>
        </el-col>
      </el-row>
      <el-pagination small v-if="applyData.applyList.length !== 0" background layout="prev, pager, next" :total="applyData.total" :page-size="12" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px;padding: 0"/>
    </div>
  </el-card>
  <el-dialog v-model="progressDialogVisible" title="活动申请进度" width="30%" center>
    <el-steps :active="auditData.auditStatus === 0 ? 1 : 3" align-center>
      <el-step title="提交申请" :icon="Document" :description="auditData.applyTime"/>
      <el-step title="待审核" :icon="Clock"/>
      <el-step title="审核结果" v-if="auditData.auditStatus === 0" :icon="Compass"/>
      <el-step title="审核通过" v-if="auditData.auditStatus === 1" :icon="CircleCheck" :description="auditData.applyAudittime"/>
      <el-step title="审核未通过" v-if="auditData.auditStatus === 2" :icon="CircleClose" :description="auditData.applyAudittime"/>
    </el-steps>
    <template #footer>
          <span class="dialog-footer">
            <el-button type="primary" @click="progressDialogVisible = false">确认</el-button>
          </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import myAxios from "../../../utils/axios";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Document, Clock, CircleCheck, CircleClose, Compass  } from "@element-plus/icons";

const store = useStore();
const router = useRouter();
const currentPage = ref(1);
const progressDialogVisible = ref(false);
const applyData = reactive({
  applyList: [],
  total: 0
});
const auditData = reactive({
  auditStatus: 0,
  applyTime: '',
  applyAudittime: ''
})
const getApplyRecord = () => {
  let data = new FormData();
  data.append("userId",store.state.detail.userKey);
  data.append("userType",store.state.detail.userType);
  data.append("currentPage",currentPage.value);
  myAxios.post('/apply/activityApply',data).then(response => {
    if (response.data.code === 200) {
      applyData.applyList = response.data.data.activityApplyData.applyList;
      applyData.total = response.data.data.activityApplyData.total;
    } else {
      ElMessage.error('请求发生错误');
    }
  })
};
onMounted(() => {
  getApplyRecord();
});
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getApplyRecord();
};
const toSignUp = (activityIndex) => {
  store.commit("setActiveKey",activityIndex);
  router.push('/activity-detail');
};
const viewApplyProgress = (applyStatus,applyTime,applyAudittime) => {
  auditData.auditStatus = applyStatus;
  auditData.applyTime = applyTime;
  if (applyAudittime !== null) auditData.applyAudittime = applyAudittime;
  progressDialogVisible.value = true;
};
const cancelApply = (activityIndex) => {
  if (store.state.loginUser.userType === 0) {
    ElMessageBox.confirm(
        '确定要取消申请参加这次活动吗？这将增加你的违约次数！',
        '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      let data = new FormData();
      data.append("userId",store.state.loginUser.userId);
      data.append("userType",store.state.loginUser.userType);
      data.append("activeId",activityIndex);
      myAxios.post('/apply/cancelApplyActivity',data).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('取消申请成功');
          setTimeout(function () {
            location.reload();
          },1000);
        } else {
          ElMessage.error('系统出错，取消申请失败');
        }
      })
    }).catch(() => {});
  } else if (store.state.loginUser.userType === 2) {
    ElMessageBox.confirm(
        '确定要取消申请发布该活动吗？',
        '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      let data = new FormData();
      data.append("userId",store.state.loginUser.userId);
      data.append("userType",store.state.loginUser.userType);
      data.append("activeId",activityIndex);
      myAxios.post('/apply/cancelApplyActivity',data).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('取消申请成功');
          setTimeout(function () {
            location.reload();
          },1000);
        } else {
          ElMessage.error('系统出错，取消申请失败');
        }
      })
    }).catch(() => {});
  }
}
</script>

<style scoped>
.el-image {
  width: 100%;
  height: 100%;
}
.active-banner {
  height: 120px;
}
.card-bottom {
  text-align: left;
  padding: 8px;
  background-color: #ededed;
}
.card-bottom .active-title {
  font-size: 14px;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.active-title:hover {
  color: #da4453;
  cursor: pointer;
}
.card-bottom .active-place {
  font-size: 11px;
  color: #959393;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  margin-top: 8px;
  margin-bottom: 2px;
}
.card-bottom .active-time {
  font-size: 11px;
  color: #959393;
}
.el-button+.el-button {
  margin-left: 25px;
}
</style>
<style lang="scss" scoped>
:deep(.activity-card) {
  .el-card__body {
    display: flex;
    flex-direction: column;
  }
}
:deep(.el-step) {
  .el-step__description {
    font-size: 14px;
    margin-top: 10px;
  }
}
</style>