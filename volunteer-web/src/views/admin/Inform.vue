<template>
  <el-container style="width: 100%;height: 100%;">
    <el-container v-if="informData.informList.length !== 0" style="flex-direction: column;padding: 40px 15px;">
      <div style="text-align: left;margin-bottom: 10px">
        <el-button type="danger" style="font-size: 12px;" plain :icon="Delete" @click="batchDeleteInform">批量删除资讯</el-button>
      </div>
      <el-table :data="informData.informList" stripe style="width: 100%" @selection-change="handleInformTableSelect" table-layout="fixed">
        <el-table-column type="selection" width="40"/>
        <el-table-column prop="informTitle" label="资讯标题" width="200" show-overflow-tooltip align="center"/>
        <el-table-column prop="informContent" label="资讯内容" show-overflow-tooltip align="center"/>
        <el-table-column prop="informPublic" sortable label="发布时间" width="160" show-overflow-tooltip  align="center"/>
        <el-table-column prop="informFrom" label="资讯来源" width="160" show-overflow-tooltip align="center"/>
        <el-table-column prop="informView" sortable label="资讯热度" width="130" show-overflow-tooltip align="center"/>
        <el-table-column prop="informStatus" label="资讯状态" width="130" align="center" :filters="[{text: '显示',value: 1},{text: '不显示',value: 0}]" :filter-method="filterInformStatus">
          <template #default="scope">
            <el-switch :model-value="scope.row.informStatus" @change="changeInformStatus(scope.row)" width="60px" style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" inline-prompt active-text="显示" inactive-text="不显示" :active-value="1" :inactive-value="0"/>
          </template>
        </el-table-column>
        <el-table-column prop="informId" label="操作" width="190" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="viewInform(scope.row.informId)" :icon="View">查看</el-button>
            <el-button link type="primary" size="small" @click="editInform(scope.row.informId)" :icon="Edit">编辑</el-button>
            <el-popconfirm title="确定要删除该条资讯吗？" width="200px" cancel-button-text="取消" confirm-button-text="确认" @confirm="informDelete(scope.row.informId)">
              <template #reference>
                <el-button link type="primary" size="small" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination small background layout="prev, pager, next" :total="informData.informList.length" :page-size="12" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
    </el-container>
  </el-container>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import myAxios from "../../utils/axios";
import { Delete,View,Edit } from "@element-plus/icons";
import {ElMessage, ElMessageBox} from "element-plus";
import {useStore} from "vuex";
import {useRouter} from "vue-router";

const store = useStore();
const router = useRouter();
const selectedInform = ref(null);
const currentPage = ref(1);
const informData = reactive({
  informList: []
});

const getInformData = () => {
  let data = new FormData();
  data.append("userType",store.state.loginUser.userType);
  data.append("currentPage",currentPage.value);
  myAxios.post('/inform/getAllInform',data).then(response => {
    if (response.data.code === 200) {
      informData.informList = response.data.data.informList;
    } else {
      ElMessage.error('请求出错');
    }
  })
};
onMounted(() => {
  getInformData();
});
const filterInformStatus = (value, row) => {
  return row.informStatus === value;
}
const handleInformTableSelect = (values) => {
  if (values.length === 0) {
    selectedInform.value = null;
  }else {
    selectedInform.value = values;
  }
};
const changeInformStatus = (row) => {
  ElMessageBox.confirm(
      '确定要修改该资讯的可见状态吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    let afterChangeStatus = row.informStatus === 0 ? 1 : 0;
    let data = new FormData();
    data.append("informId",row.informId);
    data.append("changedStatus",afterChangeStatus);
    myAxios.post('/inform/changeInformStatus',data).then((response) => {
      if (response.data.code === 200) {
        ElMessage.success('修改该资讯的可见状态成功');
        row.informStatus = afterChangeStatus;
      } else {
        ElMessage.error('修改该资讯的可见状态出错');
      }
    })
  }).catch(() => {
    ElMessage.info('已取消修改该资讯的可见状态');
  });
};
const informDelete = (index) => {
  let willDelete = [];
  willDelete.push(index);
  let data = new FormData();
  data.append("informIds",data);
  myAxios.post('/inform/deleteInform',data).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('删除资讯成功');
      getInformData();
    } else {
      ElMessage.error('请求错误，删除失败');
    }
  });
};
const batchDeleteInform = () => {
  if (selectedInform.value === null) {
    ElMessage.warning('请选择要删除的资讯')
  }else {
    ElMessageBox.confirm(
        '确定要删除这些资讯吗？这将不能恢复',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      let willDelete = [];
      selectedInform.value.forEach((selected) => {
        willDelete.push(selected.informId);
      });
      let data = new FormData();
      data.append("informIds",willDelete);
      myAxios.post('/inform/deleteInform',data).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('删除资讯成功');
          getInformData();
        } else {
          ElMessage.error('请求错误，删除失败');
        }
      });
    }).catch(() => {
      ElMessage.info('已取消删除资讯');
    });
  }
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getInformData();
};
const viewInform = (informId) => {
  store.commit('setInformKey',informId);
  router.push('/inform-detail');
};
const editInform = (informId) => {
  router.push({
    path: '/admin/inform/informEdit',
    query: {
      informId: informId
    }
  });
}
</script>

<style scoped>

</style>