<template>
  <el-card class="activity-record" style="border: 0;box-shadow: none;">
    <template #header>
      <h3 style="font-size: 20px;text-align: left;">我的活动</h3>
    </template>
    <div style="text-align: left;padding-left: 10px;" >
      <!--志愿单位的活动记录信息-->
      <el-button type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="activityPublic" v-if="((store.state.loginUser.userType === 2) && ((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType)))">发布活动</el-button>
      <el-button type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="batchDeleteActivityRecord" v-if="((activityData.total !== 0) && (store.state.loginUser.userType === 2) && ((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType)))">批量删除记录</el-button>
      <div v-if="activityData.total !== 0">
        <el-table :data="activityData.activityList" stripe style="width: 100%" @selection-change="handleActivityTableSelect" v-if="(store.state.detail.userType === 2)">
          <el-table-column type="selection" width="40" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))"/>
          <el-table-column fixed prop="activityTitle" label="活动标题" width="140" show-overflow-tooltip/>
          <el-table-column prop="activityClassic" label="活动分类" width="110" align="center"
            :filters="activityClassic" :filter-method="filterActivityClassic">
            <template #default="scope">
              <el-tag>{{ scope.row.activityClassic }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="activityPublic" sortable label="活动发布时间" width="160" align="center"/>
          <el-table-column prop="activityContinue" sortable label="活动时间" width="180" align="center" show-overflow-tooltip/>
          <el-table-column prop="activityPlace" label="活动地点" width="180" align="center" show-overflow-tooltip/>
          <el-table-column prop="activitySimple" label="活动简介" width="180" align="center" show-overflow-tooltip/>
          <el-table-column prop="activityCutoff" label="报名截止时间" width="160" align="center"/>
          <el-table-column prop="activityLimit" label="人数限制(人)" align="center" width="120"/>
          <el-table-column prop="activityApplied" label="已招募人数" align="center" width="100"/>
          <el-table-column prop="staredCount" label="被收藏数" align="center" width="100"/>
          <el-table-column fixed="right" prop="activityId" label="操作" width="150" align="center">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="activityView(scope.row.activityId)">查看</el-button>
              <el-button link type="primary" size="small" @click="viewActivityVolunteers(scope.row.activityId)" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))">志愿者</el-button>
              <el-popconfirm title="确定要删除该条活动记录吗？" confirm-button-text="确认" cancel-button-text="取消" @confirm="activityDelete(scope.row.activityId,scope.row.activityContinue)">
                <template #reference>
                  <el-button link type="primary" size="small" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <!--志愿者的活动记录信息-->
        <el-button type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="batchDeleteJoin" v-if="((store.state.loginUser.userType === 0) && ((store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType)))">批量删除记录</el-button>
        <el-table :data="activityData.activityList" stripe style="width: 100%" @selection-change="handleActivityTableSelect" v-if="(store.state.detail.userType === 0)">
          <el-table-column type="selection" width="40" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))"/>
          <el-table-column fixed prop="activityInfo.activityTitle" label="活动标题" width="130" show-overflow-tooltip/>
          <el-table-column prop="activityInfo.activityClassic" label="活动分类" width="110" align="center"
            :filters="activityClassic" :filter-method="filterActivityClassic">
            <template #default="scope">
              <el-tag>{{ scope.row.activityInfo.activityClassic }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="activityInfo.activityPublic" sortable label="活动发布时间" width="160" align="center"/>
          <el-table-column prop="activityInfo.activityContinue" sortable label="活动时间" width="180" align="center" show-overflow-tooltip/>
          <el-table-column prop="activityInfo.activityPlace" label="活动地点" width="180" align="center" show-overflow-tooltip/>
          <el-table-column prop="activityInfo.activitySimple" label="活动简介" width="180" align="center" show-overflow-tooltip/>
          <el-table-column prop="activityInfo.publisherUnitName" label="发布单位" width="180" align="center" show-overflow-tooltip/>
          <el-table-column prop="joinStatus" label="完成状态" width="100" align="center"
            :filters="[{text: '未完成',value: 0},{text: '已完成',value: 1}]" :filter-method="filterActivityCompleteStatus">
            <template #default="scope">
              <el-tag :type="completeStatusTag(scope.row.joinStatus)">{{ completeStatus(scope.row.joinStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column fixed="right" prop="activityInfo.activityId" label="操作" width="110" align="center">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="activityView(scope.row.activityInfo.activityId)">查看</el-button>
              <el-popconfirm title="确定要删除该条活动记录吗？" confirm-button-text="确认" cancel-button-text="取消" @confirm="deleteJoin(scope.row.joinId,scope.row.activityInfo.activityContinue)">
                <template #reference>
                  <el-button link type="primary" size="small" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination small background  layout="prev, pager, next" :total="activityData.total" :page-size="10" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
      </div>

      <el-empty description="暂无数据" v-if="activityData.total === 0"/>

      <el-dialog v-model="viewVolunteersDialogVisible" title="参与活动的志愿者" width="480px">
        <el-table :data="unitViewCurrentActivityVolunteer.volunteerList">
          <el-table-column property="volunteersInfo.volunteersUsername" label="用户名" width="120" show-overflow-tooltip align="center"/>
          <el-table-column property="volunteersInfo.volunteersAvatar" label="头像" width="100" align="center">
            <template #default="scope">
              <el-image :src="scope.row.volunteersInfo.volunteersAvatar" fit="cover" style="width: 40px;height: 40px;">
                <template #error>
                  <div class="image-slot" style="width: 100%;height: 100%;display:flex;justify-content: center;align-items: center;">
                    <el-icon :size="20"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column property="volunteersInfo.volunteersHours" label="志愿时长" width="100" align="center"/>
          <el-table-column property="joinStatus" label="完成状态" align="center" width="120">
            <template #default="scope">
              <el-switch :model-value="scope.row.joinStatus" :disabled="scope.row.joinStatus === 1" @change="completeVolunteerJoin(scope.row)" width="55px" style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" inline-prompt active-text="完成" inactive-text="未完成" :active-value="1" :inactive-value="0"/>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from "vue-router";
import { ElLoading, ElMessage, ElMessageBox } from "element-plus";
import { useStore } from "vuex";
import myAxios from "../../../utils/axios";

const router = useRouter();
const store = useStore();
const currentPage = ref(1);
const selectedActivity = ref(null);
const unitViewCurrentActivity = ref(0);
const unitViewCurrentActivityVolunteer = reactive({
  volunteerList: []
});
const viewVolunteersDialogVisible = ref(false);
const activityData = reactive({
  activityList: [],
  total: 0
});
const activityClassic = [
  {
    text: '治安防控',
    value: '治安防控'
  },
  {
    text: '青少年服务',
    value: '青少年服务'
  },
  {
    text: '文明实践',
    value: '文明实践'
  },
  {
    text: '扶贫帮困',
    value: '扶贫帮困'
  },
  {
    text: '敬老助残',
    value: '敬老助残'
  },
  {
    text: '公共文明',
    value: '公共文明'
  },
  {
    text: '志愿防疫',
    value: '志愿防疫'
  },
  {
    text: '环境保护',
    value: '环境保护'
  },
  {
    text: '企业服务',
    value: '企业服务'
  },
  {
    text: '便民服务',
    value: '便民服务'
  },
  {
    text: '青春情暖',
    value: '青春情暖'
  },
  {
    text: '其他',
    value: '其他'
  }
];
const getActivityRecord = () => {
  let data = new FormData();
  data.append("userId",store.state.detail.userKey);
  data.append("userType",store.state.detail.userType);
  data.append("currentPage",currentPage.value);
  const loading = ElLoading.service({lock: true, text: '正在获取数据...'});
  myAxios.post('/activity/getActivityRecord',data).then(response => {
    loading.close();
    if (response.data.code === 200) {
      activityData.activityList = response.data.data.activityRecord.activityRecord;
      activityData.total = response.data.data.activityRecord.total;
    } else {
      ElMessage.error('请求出错');
    }
  })
};
onMounted(() => {
  getActivityRecord();
})

const handleActivityTableSelect = (values) => {
  if (values.length === 0) {
    selectedActivity.value = null;
  }else {
    selectedActivity.value = values;
  }
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getActivityRecord();
};
const activityView = (index) => {
  store.commit('setActiveKey',index);
  router.push('/activity-detail');
};
const activityDelete = (index,continueTime) => {
  if (Date.now() < new Date(Date.parse(continueTime.substring(continueTime.lastIndexOf('至') + 2)))) {
    ElMessage.warning('活动结束之前还不能删除');
    return;
  }
  let willDelete = [];
  willDelete.push(index);
  let formData = new FormData();
  formData.append("activityIds",willDelete);
  myAxios.post('/activity/deleteActivity',formData).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('删除成功');
      setTimeout(() => {
        location.reload();
      },1000);
    } else {
      ElMessage.error('删除出错，请重试');
    }
  });
};
const batchDeleteActivityRecord = () => {
  if (selectedActivity.value === null) {
    ElMessage.warning('请选择要删除的记录');
  }else {
    ElMessageBox.confirm(
        '确定要删除这些活动记录吗？',
        '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      let willDelete = [];
      let couldDelete = true;
      selectedActivity.value.forEach((selected) => {
        willDelete.push(selected.activityId);
        if (Date.now() < new Date(Date.parse(selected.activityContinue.substring(selected.activityContinue.lastIndexOf('至') + 2)))) {
          couldDelete = false;
        }
      });
      if (!couldDelete) {
        ElMessage.warning('不能删除还未结束的活动');
        willDelete = [];
        return;
      }
      let formData = new FormData();
      formData.append("activityIds",willDelete);
      myAxios.post('/activity/deleteActivity',formData).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('删除成功');
          setTimeout(() => {
            location.reload();
          },1000);
        } else {
          ElMessage.error('删除出错，请重试');
        }
      });
    }).catch(() => {
      ElMessage.info('已取消删除记录');
    })
  }
};
const deleteJoin = (index,continueTime) => {
  if (Date.now() < new Date(Date.parse(continueTime.substring(continueTime.lastIndexOf('至') + 2)))) {
    ElMessage.warning('活动结束之前还不能删除');
    return;
  }
  let willDelete = [];
  willDelete.push(index);
  let formData = new FormData();
  formData.append("joinIds",willDelete);
  myAxios.post('/join/deleteJoin',formData).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('删除成功');
      setTimeout(() => {
        location.reload();
      },1000);
    } else {
      ElMessage.error('删除出错，请重试');
    }
  });
};
const batchDeleteJoin = () => {
  if (selectedActivity.value === null) {
    ElMessage.warning('请选择要删除的记录');
  }else {
    ElMessageBox.confirm(
        '确定要删除这些活动记录吗？',
        '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      let willDelete = [];
      let couldDelete = true;
      selectedActivity.value.forEach((selected) => {
        willDelete.push(selected.joinId);
        if (Date.now() < new Date(Date.parse(selected.activityInfo.activityContinue.substring(selected.activityInfo.activityContinue.lastIndexOf('至') + 2)))) {
          couldDelete = false;
        }
      });
      if (!couldDelete) {
        ElMessage.warning('不能删除还未结束的活动');
        willDelete = [];
        return;
      }
      let formData = new FormData();
      formData.append("joinIds",willDelete);
      myAxios.post('/join/deleteJoin',formData).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('删除成功');
          setTimeout(() => {
            location.reload();
          },1000);
        } else {
          ElMessage.error('删除出错，请重试');
        }
      });
    }).catch(() => {
      ElMessage.info('已取消删除记录');
    })
  }
};
const activityPublic = () => {
  router.push('/user-detail/ActivityPublic');
};
const completeStatus = (joinStatus) => {
  if (joinStatus === 0) {
    return '待完成';
  } else {
    return '已完成';
  }
};
const completeStatusTag = (joinStatus) => {
  if (joinStatus === 0) {
    return 'danger';
  } else {
    return 'success';
  }
};
const filterActivityClassic = (value, row) => {
  if (store.state.detail.userType === 0) {
    return row.activityInfo.activityClassic === value;
  } else if (store.state.detail.userType === 2) {
    return row.activityClassic === value;
  }
};
const filterActivityCompleteStatus = (value, row) => {
  return row.joinStatus === value;
};
const viewActivityVolunteers = (activityId) => {
  unitViewCurrentActivity.value = activityId;
  let data = new FormData();
  data.append("activityId",activityId);
  myAxios.post('/join/getVolunteersByActivityJoin',data).then((response) => {
    if (response.data.code === 200) {
      unitViewCurrentActivityVolunteer.volunteerList = response.data.data.joinList;
    } else {
      ElMessage.error('获取参与该志愿活动的志愿者列表失败');
    }
  })
  viewVolunteersDialogVisible.value = true;
};
const completeVolunteerJoin = (row) => {
  ElMessageBox.confirm(
      '确定该志愿者完成志愿活动了吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    let data = new FormData();
    data.append("activityId",unitViewCurrentActivity.value);
    data.append("userId",row.volunteersInfo.volunteersId);
    myAxios.post('/join/completeJoin',data).then((response) => {
      if (response.data.code === 200) {
        ElMessage.success('操作成功');
        row.joinStatus = 1;
      } else {
        ElMessage.error('操作失败');
      }
    })
  }).catch(() => {
    ElMessage.info('已取消');
  })
};
</script>

<style scoped>

</style>