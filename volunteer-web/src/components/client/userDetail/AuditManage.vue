<template>
  <div>
    <el-card class="audit-manager" style="border: 0;box-shadow: none;">
      <template #header>
        <h3 style="font-size: 20px;text-align: left;">审核管理</h3>
      </template>
      <div style="text-align: left;padding-left: 10px;">
        <el-button type="primary" v-if="auditInfoData.total !== 0" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="batchDeleteAudit">批量删除记录</el-button>
        <el-table :data="auditInfoData.auditList" v-if="auditInfoData.total !== 0" stripe style="width: 100%" @selection-change="handleAuditTableSelect">
          <el-table-column type="selection" width="40"/>
          <el-table-column prop="applyTitle" label="申请标题" width="120" show-overflow-tooltip/>
          <el-table-column prop="applyDescription" label="申请内容" width="180" align="center" show-overflow-tooltip/>
          <el-table-column prop="applyType" label="审核类型" width="120" align="center">
            <template #default="scope">
              <el-tag>{{ auditTypeFormatter(scope.row.applyType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applyStatus" label="审核状态" width="120" align="center"
            :filters="[{ text: '待审核', value: 0 },{ text: '审核通过', value: 1 },{text: '审核未通过', value: 2}]"
            :filter-method="filterStatus">
            <template #default="scope">
              <el-tag :type="judgeAuditStatusTag(scope.row.applyStatus)">{{ auditStatusFormatter(scope.row.applyStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applierObject.volunteersUsername" label="申请人" width="120" align="center" show-overflow-tooltip/>
          <el-table-column prop="applyTime" sortable label="申请日期" width="180" align="center"/>
          <el-table-column prop="applyId" label="操作" width="110" align="center">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="auditView(scope.row.applyId)">查看</el-button>
              <el-popconfirm title="确定要删除该条记录吗？" cancel-button-text="取消" confirm-button-text="确认" @confirm="auditDelete(scope.row.applyId,scope.row)">
                <template #reference>
                  <el-button link type="primary" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-if="auditInfoData.total !== 0" small background layout="prev, pager, next" :total="auditInfoData.total" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
        <el-empty description="暂无相应数据" v-if="auditInfoData.total === 0"/>
      </div>
    </el-card>

    <el-drawer v-model="auditDrawerVisible" direction="rtl">
      <h3>{{ auditInfoData.applyById.applyTitle }}</h3>
      <h4 style="text-align: left;margin: 15px 0">审核内容：</h4>
      <p style="text-indent: 2em;text-align: left;line-height: 25px;">{{ auditInfoData.applyById.applyDescription }}</p>
      <p style="margin-top: 20px;font-size: 15px;text-align: left;">申请时间：<span>{{ auditInfoData.applyById.applyTime }}</span></p>
      <p style="margin-top: 10px;font-size: 15px;text-align: left;" v-if="auditInfoData.applyById.appliedActive !== null">来自活动：
        <span @click="viewActivity(auditInfoData.applyById.appliedActive.activityId)" style="padding-right: 15px;font-size: 14px;color: #1989fa;cursor: pointer;">
          {{ auditInfoData.applyById.appliedActive.activityTitle }}
        </span>
      </p>
      <p style="margin-top: 10px;font-size: 15px;text-align: left;">申请人：
        <span @click="viewApplier(auditInfoData.applyById.applierObject.volunteersId,auditInfoData.applyById.applierObject.volunteersType)" style="padding-right: 15px;font-size: 14px;color: #1989fa;cursor: pointer;">
          {{ auditInfoData.applyById.applierObject.volunteersUsername }}
        </span>
      </p>
      <div style="margin-top: 50px;text-align: right;padding-right: 15px;" v-if="auditInfoData.applyById.applyStatus === 0">
        <el-button type="primary" size="small" @click="auditApply(auditInfoData.applyById.applyId,'审核通过')">审核通过</el-button>
        <el-button type="primary" size="small" @click="auditApply(auditInfoData.applyById.applyId,'审核未通过')">审核不通过</el-button>
      </div>
      <div style="margin-top: 50px;text-align: right;padding-right: 15px;" v-if="auditInfoData.applyById.applyStatus !== 0">
        已经审核过啦！
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import { useStore } from "vuex";
import {ElMessage, ElMessageBox} from "element-plus";
import myAxios from "../../../utils/axios";
import {useRouter} from "vue-router";

const router = useRouter();
const store = useStore();
const selectedAudit = ref(null);
const currentPage = ref(1);
const auditDrawerVisible = ref(false);
const auditInfoData = reactive({
  auditList: [],
  total: 0,
  applyById: {
    applierObject: '',
    appliedActive: ''
  }
});

const getAuditData = () => {
  let data = new FormData();
  data.append("userId",store.state.loginUser.userId);
  data.append("userType",store.state.loginUser.userType);
  data.append("currentPage",currentPage.value);
  myAxios.post('/apply/getAuditInfo',data).then(response => {
    if (response.data.code === 200) {
      auditInfoData.auditList = response.data.data.auditData.auditList;
      auditInfoData.total = response.data.data.auditData.total;
    } else {
      ElMessage.error('请求出错');
    }
  })
};
onMounted(() => {
  getAuditData();
});
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getAuditData();
};
const auditTypeFormatter = (applyType) => {
  if (applyType === 0) {
    return '活动参与申请';
  } else if (applyType === 1) {
    return  '入队申请';
  }
};
const auditStatusFormatter = (applyStatus) => {
  if (applyStatus === 0) {
    return '待审核';
  } else if (applyStatus === 1) {
    return '审核通过';
  } else if (applyStatus === 2) {
    return '审核未通过';
  }
};
const judgeAuditStatusTag = (status) => {
  if (status === 1){
    return 'success';
  }else if (status === 2) {
    return 'warning';
  } else {
    return 'danger';
  }
};
const filterStatus = (value, row) => {
  return row.applyStatus === value;
};
const handleAuditTableSelect = (values) => {
  if (values.length === 0) {
    selectedAudit.value = null;
  }else {
    selectedAudit.value = values;
  }
};
const auditView = (index) => {
  myAxios.get('/apply/queryApplyById/' + index).then(response => {
    if (response.data.code === 200) {
      auditInfoData.applyById = response.data.data.applyById;
    } else {
      ElMessage.error('请求出错');
    }
  });
  auditDrawerVisible.value = true;
};
const auditApply = (index,auditCommand) => {
  let data = new FormData();
  data.append("applyId",index);
  data.append("auditCommand",auditCommand);
  myAxios.post('/apply/auditApply',data).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('审核成功');
      setTimeout(() => {
        location.reload();
      },1000);
    } else {
      ElMessage.error('请求出错，审核失败');
    }
  });
};
const auditDelete = (index, row) => {
  if (row.applyStatus === 0) {
    ElMessage.warning('不能删除还未审核的记录');
    return;
  }
  let willDelete = [];
  willDelete.push(index);
  let data = new FormData();
  data.append("applyIds",willDelete);
  myAxios.post('/apply/deleteApply',data).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('删除记录成功');
      setTimeout(() => {
        location.reload();
      },1000);
    } else {
      ElMessage.error('请求错误，删除失败');
    }
  })
};
const batchDeleteAudit = () => {
  if (selectedAudit.value === null) {
    ElMessage({
      message: '请选择要删除的记录',
      type: 'warning'
    })
  } else {
    ElMessageBox.confirm(
        '确定要删除这些记录吗？这将不能恢复',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      let willDelete = [];
      let couldDelete = true;
      selectedAudit.value.forEach((selected) => {
        if (selected.applyStatus === 0) couldDelete = false;
        willDelete.push(selected.applyId);
      });
      if (!couldDelete) {
        ElMessage.warning('不能删除还未审核的记录');
        return;
      }
      let data = new FormData();
      data.append("applyIds",willDelete);
      myAxios.post('/apply/deleteApply',data).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('删除记录成功');
          setTimeout(() => {
            location.reload();
          },1000);
        } else {
          ElMessage.error('请求错误，删除失败');
        }
      });
    }).catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消删除',
      })
    })
  }
};
const viewActivity = (activityId) => {
  store.commit('setActiveKey',activityId);
  router.push('/activity-detail');
}
const viewApplier = (voId,voType) => {
  let detail = {userKey: voId, userType: voType};
  store.commit('setDetail',detail);
  router.push('/user-detail/profile');
}
</script>

<style scoped>

</style>