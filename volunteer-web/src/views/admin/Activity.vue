<template>
  <el-container style="width: 100%;height: 100%">
    <el-container v-if="activityData.activityList.length !== 0" style="flex-direction: column;padding: 40px 15px;">
      <div style="text-align: left;margin-bottom: 10px">
        <el-popover placement="right" :width="400" trigger="click">
          <template #reference>
            <el-button style="font-size: 12px" type="primary" plain :icon="Tools">自定义列显示</el-button>
          </template>
          <div>
            <div>选择显示列名</div>
            <div>
              <el-checkbox v-model="showColumn.activityClassic" label="活动分类"/>
              <el-checkbox v-model="showColumn.publisherUnitName" label="发布单位"/>
              <el-checkbox v-model="showColumn.activityPublic" label="发布时间"/>
              <el-checkbox v-model="showColumn.activityContinue" label="活动时间"/>
              <el-checkbox v-model="showColumn.activityPlace" label="活动地点"/>
              <el-checkbox v-model="showColumn.activitySimple" label="活动简介"/>
              <el-checkbox v-model="showColumn.activityContent" label="活动内容"/>
              <el-checkbox v-model="showColumn.activityRequire" label="报名要求"/>
              <el-checkbox v-model="showColumn.activityBanner" label="活动图片"/>
              <el-checkbox v-model="showColumn.activityCutoff" label="报名截止时间"/>
              <el-checkbox v-model="showColumn.activityContact" label="联系人"/>
              <el-checkbox v-model="showColumn.activityPhone" label="联系电话"/>
              <el-checkbox v-model="showColumn.activityLimit" label="人数限制"/>
              <el-checkbox v-model="showColumn.activityApplied" label="已招募人数"/>
              <el-checkbox v-model="showColumn.staredCount" label="被收藏数"/>
              <el-checkbox v-model="showColumn.expired" label="报名是否截止"/>
              <el-checkbox v-model="showColumn.activityShow" label="是否可见"/>
            </div>
          </div>
        </el-popover>
        <el-button type="danger" style="font-size: 12px;" plain :icon="Delete" @click="batchDeleteActivityRecord">批量删除活动记录</el-button>
      </div>
      <el-table :data="activityData.activityList" border stripe style="width: 100%" @selection-change="handleActivityTableSelect" table-layout="fixed">
        <el-table-column type="selection" width="40"/>
        <el-table-column fixed prop="activityTitle" label="活动标题" width="130" show-overflow-tooltip/>
        <el-table-column v-if="showColumn.activityClassic" prop="activityClassic" label="活动分类" width="110" align="center"
                         :filters="activityClassic" :filter-method="filterActivityClassic">
          <template #default="scope">
            <el-tag>{{ scope.row.activityClassic }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="showColumn.publisherUnitName" prop="publisherUnitName" sortable label="发布单位" width="180" align="center" show-overflow-tooltip/>
        <el-table-column v-if="showColumn.activityPublic" prop="activityPublic" sortable label="活动发布时间" width="160" align="center"/>
        <el-table-column v-if="showColumn.activityContinue" prop="activityContinue" sortable label="活动时间" width="180" align="center" show-overflow-tooltip/>
        <el-table-column v-if="showColumn.activityPlace" prop="activityPlace" label="活动地点" width="180" align="center" show-overflow-tooltip/>
        <el-table-column v-if="showColumn.activitySimple" prop="activitySimple" label="活动简介" width="180" align="center" show-overflow-tooltip/>
        <el-table-column v-if="showColumn.activityContent" prop="activityContent" label="活动内容" width="180" align="center" show-overflow-tooltip/>
        <el-table-column v-if="showColumn.activityRequire" prop="activityRequire" label="报名要求" width="180" align="center" show-overflow-tooltip/>
        <el-table-column v-if="showColumn.activityBanner" prop="activityBanner" label="活动图片" width="180" align="center">
          <template #default="scope">
            <el-image style="width: 110px;height: 70px" :src="scope.row.activityBanner" fit="cover">
              <template #error>
                <div class="image-slot" style="width: 100%;height: 100%;display:flex;justify-content: center;align-items: center;">
                  <el-icon :size="26"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column v-if="showColumn.activityCutoff" prop="activityCutoff" label="报名截止时间" width="160" align="center"/>
        <el-table-column v-if="showColumn.activityContact" prop="activityContact" label="联系人" width="150" align="center"/>
        <el-table-column v-if="showColumn.activityPhone" prop="activityPhone" label="联系电话" width="150" align="center"/>
        <el-table-column v-if="showColumn.activityLimit" prop="activityLimit" label="人数限制(人)" align="center" width="120"/>
        <el-table-column v-if="showColumn.activityApplied" prop="activityApplied" label="已招募人数" align="center" width="100"/>
        <el-table-column v-if="showColumn.staredCount" prop="staredCount" label="被收藏数" sortable align="center" width="110"/>
        <el-table-column v-if="showColumn.expired" prop="expired" label="报名是否截止" align="center" width="130"
                         :filters="[{text: '是',value: true},{text: '否',value: false}]" :filter-method="filterActivityExpired">
          <template #default="scope">
            {{ judgeActivityIsExpired(scope.row.expired) }}
          </template>
        </el-table-column>
        <el-table-column v-if="showColumn.activityShow" prop="activityShow" label="是否可见" align="center" width="100"
                         :filters="[{text: '是',value: 1},{text: '否',value: 0}]" :filter-method="filterActivityShow">
          <template #default="scope">
            <el-switch :model-value="scope.row.activityShow" @change="changeActivityShow(scope.row)" style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" inline-prompt active-text="是" inactive-text="否" :active-value="1" :inactive-value="0"/>
          </template>
        </el-table-column>
        <el-table-column fixed="right" prop="activityId" label="操作" width="140" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" :icon="View" @click="activityView(scope.row.activityId)">查看</el-button>
            <el-popconfirm title="确定要删除该条活动记录吗？" width="230px" confirm-button-text="确认" cancel-button-text="取消" :icon="InfoFilled" icon-color="#ff1f1f" @confirm="activityDelete(scope.row.activityId,scope.row.activityContinue)">
              <template #reference>
                <el-button link type="primary" :icon="Delete" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination small background layout="prev, pager, next" :total="activityData.activityList.length" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
    </el-container>
  </el-container>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { Delete,View,Picture,Tools,InfoFilled } from "@element-plus/icons";
import myAxios from "../../utils/axios";
import { useStore } from "vuex";
import {useRouter} from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const store = useStore();
const router = useRouter();
const selectedActivity = ref(null);
const currentPage = ref(1);
const activityData = reactive({
  activityList: []
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
const showColumn = reactive({
  activityClassic: true,
  publisherUnitName: true,
  activityPublic: true,
  activityContinue: true,
  activityPlace: true,
  activitySimple: true,
  activityContent: true,
  activityRequire: true,
  activityBanner: true,
  activityCutoff: true,
  activityContact: true,
  activityPhone: true,
  activityLimit: true,
  activityApplied: true,
  staredCount: true,
  expired: true,
  activityShow: true
});

const getActivityData = () => {
  let data = new FormData();
  data.append("userId",store.state.loginUser.userId);
  data.append("userType",store.state.loginUser.userType);
  data.append("currentPage",currentPage.value);
  myAxios.post('/activity/getActivityRecord',data).then((response) => {
    if (response.data.code === 200) {
      activityData.activityList = response.data.data.activityRecord.activityRecord;
    } else {
      ElMessage.error('请求出错');
    }
  })
};
onMounted(() => {
  getActivityData();
});
const handleActivityTableSelect = (values) => {
  if (values.length === 0) {
    selectedActivity.value = null;
  }else {
    selectedActivity.value = values;
  }
};
const filterActivityClassic = (value, row) => {
  return row.activityClassic === value;
};
const judgeActivityIsExpired = (isExpired) => {
  return isExpired ? '是' : '否';
};
const filterActivityExpired = (value, row) => {
  return row.expired === value;
};
const filterActivityShow = (value, row) => {
  return row.activityShow === value;
};
const changeActivityShow = (row) => {
  ElMessageBox.confirm(
      '确定要修改该活动记录的可见状态吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    let afterChangeShow = row.activityShow === 0 ? 1 : 0;
    let data = new FormData();
    data.append("activityId",row.activityId);
    data.append("changeShow",afterChangeShow);
    myAxios.post('/activity/changeActivityShow',data).then((response) => {
      if (response.data.code === 200) {
        ElMessage.success('修改该活动的可见状态成功');
        row.activityShow = afterChangeShow;
      } else {
        ElMessage.error('修改该活动的可见状态出错');
      }
    })
  }).catch(() => {
    ElMessage.info('已取消修改该活动的可见状态');
  });
};
const activityView = (index) => {
  store.commit('setActiveKey',index);
  router.push('/activity-detail');
};
const activityDelete = (index, continueTime) => {
  if (Date.now() < new Date(Date.parse(continueTime.substring(continueTime.lastIndexOf('至') + 2)))) {
    ElMessage.warning('活动结束之前还不能删除');
    return;
  }
  let willDelete = [];
  willDelete.push(index);
  let formData = new FormData();
  formData.append("activityIds",willDelete);
  myAxios.post('/activity/deleteActivity',formData).then((response) => {
    if (response.data.code === 200) {
      ElMessage.success('删除成功');
      getActivityData();
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
      myAxios.post('/activity/deleteActivity',formData).then((response) => {
        if (response.data.code === 200) {
          ElMessage.success('删除成功');
          getActivityData();
        } else {
          ElMessage.error('删除出错，请重试');
        }
      });
    }).catch(() => {
      ElMessage.info('已取消删除记录');
    })
  }
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getActivityData();
};
</script>

<style scoped>

</style>