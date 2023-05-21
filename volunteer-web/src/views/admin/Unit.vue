<template>
  <el-container style="width: 100%;height: 100%">
    <el-container v-if="userData.userList.length !== 0" style="flex-direction: column;padding: 40px 15px;">
      <div style="text-align: left;margin-bottom: 10px">
        <el-popover placement="right" :width="400" trigger="click">
          <template #reference>
            <el-button style="font-size: 12px" type="primary" plain :icon="Tools">自定义列显示</el-button>
          </template>
          <div>
            <div>选择显示列名</div>
            <div>
              <el-checkbox v-model="userTableShowColumn.unitEmail" label="单位邮箱"/>
              <el-checkbox v-model="userTableShowColumn.unitPhone" label="单位电话"/>
              <el-checkbox v-model="userTableShowColumn.unitAddress" label="单位地址"/>
              <el-checkbox v-model="userTableShowColumn.unitSimple" label="单位简介"/>
              <el-checkbox v-model="userTableShowColumn.unitAvatar" label="单位头像"/>
              <el-checkbox v-model="userTableShowColumn.unitProve" label="单位证明"/>
              <el-checkbox v-model="userTableShowColumn.unitClassic" label="单位类别"/>
              <el-checkbox v-model="userTableShowColumn.unitHeadName" label="负责人姓名"/>
              <el-checkbox v-model="userTableShowColumn.unitHeadPhone" label="负责人电话"/>
              <el-checkbox v-model="userTableShowColumn.unitHeadEmail" label="负责人邮箱"/>
              <el-checkbox v-model="userTableShowColumn.unitHeadIdentity" label="负责人证件号码"/>
              <el-checkbox v-model="userTableShowColumn.unitHeadPicture" label="负责人证件照"/>
              <el-checkbox v-model="userTableShowColumn.unitRegister" label="注册时间"/>
              <el-checkbox v-model="userTableShowColumn.unitPubliccount" label="发布活动数"/>
              <el-checkbox v-model="userTableShowColumn.unitStatus" label="账号状态"/>
            </div>
          </div>
        </el-popover>
        <el-button type="danger" style="font-size: 12px;" plain :icon="Delete" @click="batchDeleteUser">批量删除用户</el-button>
      </div>
      <el-table :data="userData.userList" border stripe style="width: 100%" @selection-change="handleUserTableSelect" table-layout="fixed">
        <el-table-column type="selection" width="40"/>
        <el-table-column fixed prop="unitName" align="center" label="团队名称" width="130" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitEmail" prop="unitEmail" align="center" label="团队邮箱" width="180" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitPhone" prop="unitPhone" align="center" label="团队电话" width="130" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitAddress" prop="unitAddress" align="center" label="团队地址" width="200" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitSimple" prop="unitSimple" align="center" label="团队简介" width="200" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeUserSimple(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.unitAvatar" prop="unitAvatar" align="center" label="团队头像" width="130" show-overflow-tooltip>
          <template #default="scope">
            <el-image style="width: 40px;height: 40px;margin-top: 5px;" :src="scope.row.unitAvatar" fit="cover">
              <template #error>
                <div class="image-slot" style="width: 100%;height: 100%;display:flex;justify-content: center;align-items: center;">
                  <el-icon :size="26"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.unitProve" prop="unitProve" align="center" label="团队证明" width="180" show-overflow-tooltip>
          <template #default="scope">
            <el-image style="width: 110px;height: 70px;margin-top: 5px;" :src="scope.row.unitProve" fit="cover">
              <template #error>
                <div class="image-slot" style="width: 100%;height: 100%;display:flex;justify-content: center;align-items: center;">
                  <el-icon :size="26"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.unitClassic" prop="unitClassic" align="center" label="团队类别" width="110" show-overflow-tooltip :filters="filterOrganizationType" :filter-method="filterUserOrganizationType"/>
        <el-table-column v-if="userTableShowColumn.unitRegister" prop="unitRegister" align="center" sortable label="注册时间" width="110" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitHeadName" prop="unitHeadName" align="center" label="负责人姓名" width="100" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitHeadEmail" prop="unitHeadEmail" align="center" label="负责人邮箱" width="100" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitHeadPhone" prop="unitHeadPhone" align="center" label="负责人电话" width="120" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitHeadIdentity" prop="unitHeadIdentity" align="center" label="负责人证件号码" width="100" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitHeadPicture" prop="unitHeadPicture" align="center" label="负责人证件照" width="130" show-overflow-tooltip>
          <template #default="scope">
            <el-image style="width: 110px;height: 70px;margin-top: 5px;" :src="scope.row.unitHeadPicture" fit="cover">
              <template #error>
                <div class="image-slot" style="width: 100%;height: 100%;display:flex;justify-content: center;align-items: center;">
                  <el-icon :size="26"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.unitPubliccount" prop="unitPubliccount" align="center" sortable label="活动参与总数" width="120" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.unitStatus" prop="unitStatus" align="center" label="帐号状态" width="130" show-overflow-tooltip>
          <template #default="scope">
            <el-switch :model-value="scope.row.unitStatus" width="45px" @change="changeUserStatus(scope.row)" style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" inline-prompt active-text="启用" inactive-text="禁用" :active-value="1" :inactive-value="0"/>
          </template>
        </el-table-column>
        <el-table-column fixed="right" prop="unitId" label="操作" width="140" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="viewUserDetail(scope.row.unitId)" :icon="View">查看</el-button>
            <el-popconfirm title="确定要删除该用户吗？" width="230px" @click="deleteUser(scope.row.unitId)" confirm-button-text="确认" cancel-button-text="取消" :icon="InfoFilled" icon-color="#ff1f1f">
              <template #reference>
                <el-button link type="primary" :icon="Delete" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination small background layout="prev, pager, next" :total="userData.userList.length" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
    </el-container>
  </el-container>
</template>

<script setup>
import {onMounted, reactive, ref, watch} from "vue";
import { Delete,View,Picture,Tools,InfoFilled } from "@element-plus/icons";
import myAxios from "../../utils/axios";
import { useStore } from "vuex";
import {useRouter} from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const store = useStore();
const router = useRouter();
const currentUserType = ref(2);
const selectedUserList = ref(null);
const currentPage = ref(1);
const userData = reactive({
  userList: []
});
const userTableShowColumn = reactive({
  unitEmail: true,
  unitPhone: true,
  unitAddress: true,
  unitSimple: true,
  unitAvatar: true,
  unitProve: true,
  unitClassic: true,
  unitHeadName: true,
  unitHeadPhone: true,
  unitHeadEmail: true,
  unitHeadIdentity: true,
  unitHeadPicture: true,
  unitRegister: true,
  unitPubliccount: true,
  unitStatus: true
});

const filterOrganizationType = [
  {
    text: '党政机关',
    value: '党政机关'
  },
  {
    text: '教育事业单位',
    value: '教育事业单位'
  },
  {
    text: '卫生事业单位',
    value: '卫生事业单位'
  },
  {
    text: '科技事业单位',
    value: '科技事业单位'
  },
  {
    text: '文化事业单位',
    value: '文化事业单位'
  },
  {
    text: '社会福利事业单位',
    value: '社会福利事业单位'
  },
  {
    text: '居民/村民委员会',
    value: '居民/村民委员会'
  },
  {
    text: '社会团体',
    value: '社会团体'
  },
  {
    text: '社会服务机构',
    value: '社会服务机构'
  },
  {
    text: '基金会',
    value: '基金会'
  },
  {
    text: '其他',
    value: '其他'
  }
];

const getUserData = () => {
  let data = new FormData();
  data.append("userType",currentUserType.value);
  data.append("currentPage",currentPage.value);
  myAxios.post('/admin/getUserList',data).then((response) => {
    if (response.data.code === 200) {
      userData.userList = response.data.data.userList;
    } else {
      ElMessage.error('请求出错');
    }
  })
};
onMounted(() => {
  getUserData();
});
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getUserData();
};
const handleUserTableSelect = (values) => {
  if (values.length === 0) {
    selectedUserList.value = null;
  }else {
    selectedUserList.value = values;
  }
};
const filterUserOrganizationType = (value, row) => {
  return row.unitClassic === value;
};
const judgeUserSimple = (row) => {
  return row.unitSimple === null ? '暂无任何简介' : row.unitSimple;
};
const changeUserStatus = (row) => {
  let changedUserId = row.unitId;
  let afterChangeStatus = row.unitStatus === 0 ? 1 : 0;
  let text = afterChangeStatus === 0 ? '禁用' : "启用";
  ElMessageBox.confirm(
      '确定要' + text+ '该用户账号吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    let data = new FormData();
    data.append("userId",changedUserId);
    data.append("userType",currentUserType.value);
    data.append("changeStatus",afterChangeStatus);
    myAxios.post('/base/changeUserStatus',data).then((response) => {
      if (response.data.code === 200) {
        ElMessage.success(text + '该用户账号成功');
        row.unitStatus = afterChangeStatus;
      } else {
        ElMessage.error(text + '该用户账号出错');
      }
    })
  }).catch(() => {
    ElMessage.info('已取消' + text + '该用户账号');
  });
};
const viewUserDetail = (userId) => {
  let detail = {userKey: userId, userType: currentUserType.value};
  store.commit('setDetail',detail);
  router.push('/user-detail/profile');
};
const deleteUser = (userId) => {
  let willDelete = [];
  willDelete.push(userId);
  let data = new FormData();
  data.append("userIds",data);
  data.append("userType",currentUserType.value);
  myAxios.post('/base/deleteAccount',data).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('删除用户成功');
      getUserData();
    } else {
      ElMessage.error('请求错误，删除失败');
    }
  });
};
const batchDeleteUser = () => {
  if (selectedUserList.value === null) {
    ElMessage({
      message: '请选择要删除的用户',
      type: 'warning'
    })
  }else {
    ElMessageBox.confirm(
        '确定要删除这些用户吗？这将不能恢复',
        '提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      let willDelete = [];
      selectedUserList.value.forEach((selected) => {
        willDelete.push(selected.unitId);
      });
      let data = new FormData();
      data.append("userIds",willDelete);
      data.append("userType",currentUserType.value);
      myAxios.post('/base/deleteAccount',data).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('删除用户成功');
          getUserData()
        } else {
          ElMessage.error('请求错误，删除失败');
        }
      });
    }).catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消删除用户',
      })
    });
  }
};
</script>

<style scoped>

</style>