<template>
  <el-container style="width: 100%;height: 100%">
    <el-container v-if="donateData.donateList.length !== 0" style="flex-direction: column;padding: 40px 15px;">
      <div style="text-align: left;margin-bottom: 10px">
        <el-button type="danger" style="font-size: 12px;" plain :icon="Delete" @click="batchDeleteDonate">批量删除记录</el-button>
      </div>
      <el-table :data="donateData.donateList" stripe style="width: 100%" @selection-change="handleDonateTableSelect" table-layout="fixed">
        <el-table-column type="selection"/>
        <el-table-column prop="donateUserName" label="捐赠人" show-overflow-tooltip align="center"/>
        <el-table-column prop="fkDonateUsertype" label="捐赠人类型" show-overflow-tooltip align="center"
          :filters="[{text: '志愿者',value: 0},{text: '志愿者团队',value: 1},{text: '志愿单位',value: 2}]" :filter-method="filterHandler">
          <template #default="scope">
            <el-tag>{{ judgeDonorType(scope.row.fkDonateUsertype) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="donateAmount" label="捐赠金额" align="center"/>
        <el-table-column prop="donateDate" sortable label="捐赠日期" align="center"/>
        <el-table-column prop="donateId" label="操作" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="donorView(scope.row.fkDonateUser, scope.row.fkDonateUsertype)" :icon="View">查看捐赠人</el-button>
            <el-popconfirm title="确定要删除该条捐赠记录吗？" cancel-button-text="取消" confirm-button-text="确认" @confirm="donateDelete(scope.row.donateId)">
              <template #reference>
                <el-button link type="primary" size="small" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination small background layout="prev, pager, next" :total="donateData.donateList.length" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
    </el-container>
  </el-container>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { Delete,View } from "@element-plus/icons";
import myAxios from "../../utils/axios";
import { ElMessage, ElMessageBox } from "element-plus";
import { useStore } from "vuex";
import {useRouter} from "vue-router";

const store = useStore();
const router = useRouter();
const selectedDonate = ref(null);
const currentPage = ref(1);
const donateData = reactive({
  donateList: []
});
const getDonateData = () => {
  let data = new FormData();
  data.append("userId",store.state.loginUser.userId);
  data.append("userType",store.state.loginUser.userType);
  data.append("currentPage",currentPage.value);
  myAxios.post('/donate/getDonateRecord',data).then(response => {
    if (response.data.code === 200) {
      donateData.donateList = response.data.data.donateRecord.donateRecord;
    } else {
      ElMessage.error('请求出错');
    }
  })
};
onMounted(() => {
  getDonateData();
});
const judgeDonorType = (fkDonateUsertype) => {
  if (fkDonateUsertype === 0) {
    return '志愿者';
  } else if (fkDonateUsertype === 1) {
    return '志愿者团队';
  } else if (fkDonateUsertype === 2) {
    return '志愿单位';
  }
}
const handleDonateTableSelect = (values) => {
  if (values.length === 0) {
    selectedDonate.value = null;
  }else {
    selectedDonate.value = values;
  }
};
const donateDelete = (index) => {
  let willDelete = [];
  willDelete.push(index);
  let data = new FormData();
  data.append("donateIds",data);
  myAxios.post('/donate/deleteDonate',data).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('删除捐赠记录成功');
      getDonateData();
    } else {
      ElMessage.error('请求错误，删除失败');
    }
  });
};
const batchDeleteDonate = () => {
  if (selectedDonate.value === null) {
    ElMessage({
      message: '请选择要删除的记录',
      type: 'warning'
    })
  }else {
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
      selectedDonate.value.forEach((selected) => {
        willDelete.push(selected.donateId);
      });
      let data = new FormData();
      data.append("donateIds",willDelete);
      myAxios.post('/donate/deleteDonate',data).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('删除捐赠记录成功');
          getDonateData();
        } else {
          ElMessage.error('请求错误，删除失败');
        }
      });
    }).catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消删除捐赠记录',
      })
    });
  }
};
const donorView = (fkDonorId, fkDonorType) => {
  let detail = {userKey: fkDonorId,userType: fkDonorType};
  store.commit('setDetail',detail);
  router.push('/user-detail/profile');
};
const filterHandler = (value, row) => {
  return row.fkDonateUsertype === value;
}
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getDonateData();
};
</script>

<style scoped>

</style>