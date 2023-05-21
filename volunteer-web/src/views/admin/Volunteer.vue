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
              <el-checkbox v-model="userTableShowColumn.volunteersPhone" label="联系电话"/>
              <el-checkbox v-model="userTableShowColumn.volunteersEmail" label="邮箱"/>
              <el-checkbox v-model="userTableShowColumn.volunteersPolitical" label="政治面貌"/>
              <el-checkbox v-model="userTableShowColumn.volunteersProfessional" label="从业情况"/>
              <el-checkbox v-model="userTableShowColumn.volunteersDegree" label="学历"/>
              <el-checkbox v-model="userTableShowColumn.volunteersAddress" label="联系地址"/>
              <el-checkbox v-model="userTableShowColumn.volunteersAvatar" label="用户头像"/>
              <el-checkbox v-model="userTableShowColumn.volunteersRegister" label="注册时间"/>
              <el-checkbox v-model="userTableShowColumn.belongTeam" label="所属团队"/>
              <el-checkbox v-model="userTableShowColumn.volunteersSimple" label="个人简介"/>
              <el-checkbox v-model="userTableShowColumn.volunteersName" label="姓名"/>
              <el-checkbox v-model="userTableShowColumn.volunteersGender" label="性别"/>
              <el-checkbox v-model="userTableShowColumn.volunteersBirth" label="出生日期"/>
              <el-checkbox v-model="userTableShowColumn.volunteersNational" label="民族"/>
              <el-checkbox v-model="userTableShowColumn.volunteersNative" label="籍贯"/>
              <el-checkbox v-model="userTableShowColumn.volunteersCard" label="证件号码"/>
              <el-checkbox v-model="userTableShowColumn.volunteersHours" label="志愿时长"/>
              <el-checkbox v-model="userTableShowColumn.volunteersActivecount" label="活动参与数"/>
              <el-checkbox v-model="userTableShowColumn.volunteersBreak" label="违约次数"/>
              <el-checkbox v-model="userTableShowColumn.volunteersStatus" label="帐号状态"/>
            </div>
          </div>
        </el-popover>
        <el-button type="danger" style="font-size: 12px;" plain :icon="Delete" @click="batchDeleteUser">批量删除用户</el-button>
      </div>
      <el-table :data="userData.userList" border stripe style="width: 100%" @selection-change="handleUserTableSelect" table-layout="fixed">
        <el-table-column type="selection" width="40"/>
        <el-table-column fixed prop="volunteersUsername" align="center" label="用户名" width="120" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.volunteersEmail" prop="volunteersEmail" align="center" label="邮箱" width="180" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.volunteersPhone" prop="volunteersPhone" align="center" label="联系电话" width="130" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.volunteersPolitical" prop="volunteersPolitical" align="center" label="政治面貌" width="110" show-overflow-tooltip :filters="filterPolitical" :filter-method="filterUserPolitical"/>
        <el-table-column v-if="userTableShowColumn.volunteersProfessional" prop="volunteersProfessional" align="center" label="从业情况" width="120" show-overflow-tooltip :filters="filterProfessional" :filter-method="filterUserProfessional"/>
        <el-table-column v-if="userTableShowColumn.volunteersDegree" prop="volunteersDegree" align="center" label="学历" width="110" show-overflow-tooltip :filters="filterDegree" :filter-method="filterUserDegree"/>
        <el-table-column v-if="userTableShowColumn.volunteersAddress" prop="volunteersAddress" align="center" label="联系地址" width="200" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.volunteersAvatar" prop="volunteersAvatar" align="center" label="用户头像" width="130" show-overflow-tooltip>
          <template #default="scope">
            <el-image style="width: 40px;height: 40px;margin-top: 5px;" :src="scope.row.volunteersAvatar" fit="cover">
              <template #error>
                <div class="image-slot" style="width: 100%;height: 100%;display:flex;justify-content: center;align-items: center;">
                  <el-icon :size="26"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersRegister" prop="volunteersRegister" align="center" sortable label="注册时间" width="110" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.belongTeam" prop="teamInfo.teamName" align="center" label="所属团队" width="200" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeVolunteerTeam(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersSimple" prop="volunteersSimple" align="center" label="个人简介" width="200" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeUserSimple(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersName" prop="volunteersName" align="center" label="姓名" width="100" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeVolunteerAuth(scope.row.volunteersName) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersGender" prop="volunteersGender" align="center" label="性别" width="100" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeVolunteerAuth(scope.row.volunteersGender) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersBirth" prop="volunteersBirth" align="center" label="出生日期" width="120" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeVolunteerAuth(scope.row.volunteersBirth) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersNational" prop="volunteersNational" align="center" label="民族" width="100" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeVolunteerAuth(scope.row.volunteersNational) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersNative" prop="volunteersNative" align="center" label="籍贯" width="180" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeVolunteerAuth(scope.row.volunteersNative) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersCard" prop="volunteersCard" align="center" label="证件号码" width="130" show-overflow-tooltip>
          <template #default="scope">
            {{ judgeVolunteerAuth(scope.row.volunteersCard) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userTableShowColumn.volunteersHours" prop="volunteersHours" align="center" sortable label="志愿时长(小时)" width="150" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.volunteersActivecount" prop="volunteersActivecount" align="center" sortable label="活动参与数" width="120" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.volunteersBreak" prop="volunteersBreak" align="center" sortable label="违约次数" width="110" show-overflow-tooltip/>
        <el-table-column v-if="userTableShowColumn.volunteersStatus" prop="volunteersStatus" align="center" label="帐号状态" width="130" show-overflow-tooltip>
          <template #default="scope">
            <el-switch :model-value="scope.row.volunteersStatus" width="45px" @change="changeUserStatus(scope.row)" style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" inline-prompt active-text="启用" inactive-text="禁用" :active-value="1" :inactive-value="0"/>
          </template>
        </el-table-column>
        <el-table-column fixed="right" prop="volunteersId" label="操作" width="140" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="viewUserDetail(scope.row.volunteersId)" :icon="View">查看</el-button>
            <el-popconfirm title="确定要删除该用户吗？" width="230px" @click="deleteUser(scope.row.volunteersId)" confirm-button-text="确认" cancel-button-text="取消" :icon="InfoFilled" icon-color="#ff1f1f">
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
const currentUserType = ref(0);
const selectedUserList = ref(null);
const currentPage = ref(1);
const userData = reactive({
  userList: []
});
const userTableShowColumn = reactive({
  volunteersEmail: true,
  volunteersPhone: true,
  volunteersPolitical: true,
  volunteersProfessional: true,
  volunteersDegree: true,
  volunteersAddress: true,
  volunteersAvatar: true,
  volunteersRegister: true,
  belongTeam: true,
  volunteersSimple: true,
  volunteersName: true,
  volunteersGender: true,
  volunteersBirth: true,
  volunteersNational: true,
  volunteersNative: true,
  volunteersCard: true,
  volunteersHours: true,
  volunteersActivecount: true,
  volunteersBreak: true,
  volunteersStatus: true
});
const filterPolitical = [
  {
    text: '中共党员',
    value: '中共党员'
  },
  {
    text: '民主党派',
    value: '民主党派'
  },
  {
    text: '共青团员',
    value: '共青团员'
  },
  {
    text: '群众',
    value: '群众'
  },
  {
    text: '其他',
    value: '其他'
  },
];
const filterProfessional = [
  {
    text: '国家公务员',
    value: '国家公务员'
  },
  {
    text: '专业技术人员',
    value: '专业技术人员'
  },
  {
    text: '职员',
    value: '职员'
  },
  {
    text: '企业管理人员',
    value: '企业管理人员'
  },
  {
    text: '工人',
    value: '工人'
  },
  {
    text: '农民',
    value: '农民'
  },
  {
    text: '学生',
    value: '学生'
  },
  {
    text: '现役军人',
    value: '现役军人'
  },
  {
    text:'自由职业者',
    value: '自由职业者'
  },
  {
    text: '个体经营者',
    value: '个体经营者'
  },
  {
    text: '无业人员',
    value: '无业人员'
  },
  {
    text: '退（离）休人员',
    value: '退（离）休人员'
  },
  {
    text: '其他',
    value: '其他'
  }
];
const filterDegree = [
  {
    text: '小学',
    value: '小学'
  },
  {
    text: '初中',
    value: '初中'
  },{
    text: '高中（职高/中专/技校）',
    value: '高中（职高/中专/技校）'
  },
  {
    text: '大专',
    value: '大专'
  },
  {
    text: '本科',
    value: '本科'
  },
  {
    text: '第二学士学位',
    value: '第二学士学位'
  },
  {
    text: '硕士研究生',
    value: '硕士研究生'
  },
  {
    text: '博士研究生',
    value: '博士研究生'
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
const filterUserPolitical = (value, row) => {
  return row.volunteersPolitical === value;
};
const filterUserProfessional = (value, row) => {
  return row.volunteersProfessional === value;
};
const filterUserDegree = (value, row) => {
  return row.volunteersDegree === value;
};
const judgeVolunteerTeam = (row) => {
  return row.teamInfo === null ? '暂未加入任何团队' : row.teamInfo.teamName;
};
const judgeVolunteerAuth = (authInfo) => {
  return authInfo === null ? '暂未进行实名认证' : authInfo;
};
const judgeUserSimple = (row) => {
  return row.volunteersSimple === null ? '暂无任何简介' : row.volunteersSimple;
};
const changeUserStatus = (row) => {
  let changedUserId = row.volunteersId;
  let afterChangeStatus = row.volunteersStatus === 0 ? 1 : 0;
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
        row.volunteersStatus = afterChangeStatus;
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
        willDelete.push(selected.volunteersId);
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