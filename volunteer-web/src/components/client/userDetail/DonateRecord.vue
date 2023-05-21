<template>
  <el-card class="donate-record" style="border: 0;box-shadow: none;">
    <template #header>
      <h3 style="font-size: 20px;text-align: left;">捐赠记录</h3>
    </template>
    <div style="text-align: left;padding-left: 10px;">
      <el-button type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px;width: 80px;" @click="donateDialogVisible = true" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))">捐赠</el-button>
      <el-button type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="batchDeleteDonate" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType) && (donateRecord.total !== 0))">批量删除记录</el-button>
      <div v-if="donateRecord.total !== 0">
        <el-table :data="donateRecord.donateList" stripe style="width: 100%" @selection-change="handleDonateTableSelect">
          <el-table-column type="selection" width="40" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))"/>
          <el-table-column fixed prop="donateUserName" label="捐赠人" width="230" align="center" show-overflow-tooltip/>
          <el-table-column prop="donateAmount"  label="捐赠金额" width="180" align="center"/>
          <el-table-column prop="donateDate" sortable label="捐赠日期" align="center"/>
          <el-table-column prop="donateId" fixed="right" label="操作" width="170" align="center" v-if="((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType))">
            <template #default="scope">
              <el-popconfirm title="确定要删除该条记录吗？" confirm-button-text="确认" cancel-button-text="取消" @confirm="donateDelete(scope.row.donateId)">
                <template #reference>
                  <el-button link type="primary" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination small background layout="prev, pager, next" :total="donateRecord.total" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
      </div>
      <el-empty description="暂无捐赠记录" v-if="donateRecord.total === 0"/>
    </div>
    <el-dialog v-model="donateDialogVisible" title="捐赠金额填写" center draggable :before-close="cancelDonate" width="20%">
      <el-form :model="donateInfo" ref="donateInfoRef" label-width="150" style="padding-left: 40px;">
        <el-form-item label="捐赠金额" prop="money">
          <el-input-number v-model="donateInfo.money" :precision="2" :step="1" style="width: 80%;" controls-position="right"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDonate">取消</el-button>
          <el-button type="primary" @click="submitDonate">确认捐赠</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import { useStore } from "vuex";
import myAxios from "../../../utils/axios";

const store = useStore();
const selectedDonate = ref(null);
const donateInfoRef = ref();
const currentPage = ref(1);
const donateDialogVisible = ref(false);
const donateInfo = reactive({
  userId: store.state.loginUser.userId,
  userType: store.state.loginUser.userType,
  money: 0,
  out_trade_no: '',
  subject: '捐赠',
  total_amount: '',
  body: '捐赠'
});
const donateRecord = reactive({
  donateList: [],
  total: 0
});
const getDonateRecord = () => {
  let formData = new FormData();
  formData.append("userId",store.state.detail.userKey);
  formData.append("userType",store.state.detail.userType);
  formData.append("currentPage",currentPage.value);
  myAxios.post('/donate/getDonateRecord',formData).then(response => {
    if (response.data.code === 200) {
      donateRecord.donateList = response.data.data.donateRecord.donateRecord;
      donateRecord.total = response.data.data.donateRecord.total;
    } else {
      ElMessage.error('请求出错');
    }
  });
};
onMounted(() => {
  getDonateRecord()
});

const handleDonateTableSelect = (values) => {
  if (values.length === 0) {
    selectedDonate.value = null;
  }else {
    selectedDonate.value = values;
  }
};
const donateDelete = (index) => {
  let formData = new FormData();
  formData.append("donateId",index);
  myAxios.post('/donate/deleteSingle',formData).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('删除成功');
      getDonateRecord();
    } else {
      ElMessage.error('删除出错，请重试');
    }
  });
};
const batchDeleteDonate = () => {
  if (selectedDonate.value === null) {
    ElMessage.warning('请选择要删除的记录');
  }else {
    ElMessageBox.confirm(
        '确定要删除这些记录吗？',
        '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
        let willDelete = [];
        selectedDonate.value.forEach((selected) => {
          willDelete.push(selected.donateId);
        })
        let formData = new FormData();
        formData.append("donateIds",willDelete);
        myAxios.post('/donate/deleteBatch',formData).then(response => {
          if (response.data.code === 200) {
            ElMessage.success('删除成功');
            getDonateRecord();
          } else {
            ElMessage.error('删除出错，请重试');
          }
        });
    }).catch(() => {
      ElMessage.info('已取消删除记录');
    })
  }
};
const cancelDonate = () => {
  donateInfoRef.value.resetFields();
  donateDialogVisible.value = false;
}
const submitDonate = () => {
  if (donateInfo.money > 0) {
    donateInfo.out_trade_no = randomNumber();
    donateInfo.total_amount = donateInfo.money.toString();
    myAxios.post('/donate/initDonate',donateInfo).then(response => {
      if (response.data.code === 200) {
        document.write(response.data.data.alipayBody);
      } else {
        ElMessage.error('系统出错，请求错误');
      }
    })
  } else {
    ElMessage.error('捐赠金额不能小于等于0');
  }
}
const randomNumber = () => {
  const now = new Date()
  let month = now.getMonth() + 1
  let day = now.getDate()
  let hour = now.getHours()
  let minutes = now.getMinutes()
  let seconds = now.getSeconds()
  month = month < 10 ? "0" + month : month;
  day = day < 10 ? "0" + day : day;
  hour = hour < 10 ? "0" + hour : hour;
  minutes = minutes < 10 ? "0" + minutes : minutes;
  seconds = seconds < 10 ? "0" + seconds : seconds;
  return now.getFullYear().toString() + month.toString() + day + hour + minutes + seconds + (Math.round(Math.random() * 1000000)).toString();
}
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getDonateRecord();
}
</script>

<style scoped>

</style>