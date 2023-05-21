<template>
  <el-container style="width: 100%;height: 100%;">
    <el-container v-if="auditInfoData.total !== 0" style="flex-direction: column;padding: 40px 15px;">
      <div style="text-align: left;margin-bottom: 10px">
        <el-button style="font-size: 12px" type="danger" plain :icon="Delete" @click="batchDeleteAudit">批量删除记录</el-button>
      </div>
      <el-table :data="auditInfoData.auditList" stripe style="width: 100%" @selection-change="handleAuditTableSelect" table-layout="fixed">
        <el-table-column type="selection"/>
        <el-table-column fixed prop="applyTitle" label="申请标题" show-overflow-tooltip align="center"/>
        <el-table-column prop="applyDescription"  label="申请内容" show-overflow-tooltip align="center"/>
        <el-table-column prop="applierObject.teamName" label="申请人" align="center" show-overflow-tooltip>
          <template #default="scope">{{ getApplierName(scope.row) }}</template>
        </el-table-column>
        <el-table-column prop="applyType" label="审核类型" align="center">
          <template #default="scope"><el-tag>{{ auditTypeFormatter(scope.row.applyType) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="applyStatus" label="审核状态" align="center"
                         :filters="[{ text: '待审核', value: 0 },{ text: '审核通过', value: 1 },{text: '审核未通过', value: 2}]"
                         :filter-method="filterStatus" filter-placement="bottom-end">
          <template #default="scope"><el-tag :type="judgeAuditStatusTag(scope.row.applyStatus)">{{ auditStatusFormatter(scope.row.applyStatus) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="appliedActive.activityTitle" v-if="applyType === 2" label="活动名" align="center" show-overflow-tooltip/>
        <el-table-column prop="applyTime" sortable label="申请日期" align="center"/>
        <el-table-column prop="applyId" fixed="right" label="操作" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" :icon="View" @click="auditView(scope.row.applyId)">查看</el-button>
            <el-popconfirm title="确定要删除该条记录吗？" cancel-button-text="取消" confirm-button-text="确认" @confirm="auditDelete(scope.row.applyId,scope.row.applyStatus)">
              <template #reference>
                <el-button link type="primary" :icon="Delete" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination small background layout="prev, pager, next" :total="auditInfoData.total" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
    </el-container>

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
        <span @click="viewApplier(auditInfoData.applyById.applierObject)" style="padding-right: 15px;font-size: 14px;color: #1989fa;cursor: pointer;">
          {{ getApplierName(auditInfoData.applyById) }}
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
  </el-container>
</template>

<script setup>
import { reactive, ref, watch} from "vue";
import { useStore } from "vuex";
import {ElMessage, ElMessageBox} from "element-plus";
import myAxios from "../../utils/axios";
import {useRouter} from "vue-router";
import { Delete,View } from "@element-plus/icons";

const router = useRouter();
const store = useStore();
const selectedAudit = ref(null);
const currentPage = ref(1);
const auditDrawerVisible = ref(false);
const applyType = ref(0);
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
  data.append("applyType",applyType.value);
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
watch(() => router.currentRoute.value.path,(newValue)=> {
  if (newValue === '/admin/audit/registerAudit') {
    applyType.value = 5;
    currentPage.value = 1;
    getAuditData();
  } else if (newValue === '/admin/audit/activityAudit') {
    applyType.value = 2;
    currentPage.value = 1;
    getAuditData();
  }
},{ immediate: true });
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getAuditData();
};
const auditTypeFormatter = (applyType) => {
  if (applyType === 2) {
    return '发布活动';
  } else if (applyType === 3) {
    return  '志愿者团队注册';
  } else if (applyType === 4) {
    return  '志愿单位注册';
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
const auditStatusFormatter = (applyStatus) => {
  if (applyStatus === 0) {
    return '待审核';
  } else if (applyStatus === 1) {
    return '审核通过';
  } else if (applyStatus === 2) {
    return '审核未通过';
  }
};
const filterStatus = (value, row) => {
  return row.applyStatus === value;
};
const getApplierName = (applyObject) => {
  if (applyObject.applyType === 2 || applyObject.applyType === 4) {
    return applyObject.applierObject.unitName;
  } else if (applyObject.applyType === 3) {
    return applyObject.applierObject.teamName;
  }
}
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
const auditApply = (index, auditCommand) => {
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
const auditDelete = (index, applyStatus) => {
  if (applyStatus === 0) {
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
      getAuditData();
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
          getAuditData();
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
};
const viewApplier = (applier) => {
  let detail = {userKey: '',userType: ''};
  if (applier.teamId && applier.teamType) {
     detail = {userKey: applier.teamId, userType: applier.teamType};
  } else if (applier.unitId && applier.unitType) {
    detail = {userKey: applier.unitId, userType: applier.unitType};
  }
  store.commit('setDetail',detail);
  router.push('/user-detail/profile');
};
</script>

<style scoped>

</style>