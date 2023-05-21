<template>
  <div class="team-manager">
    <el-card style="border: 0;box-shadow: none;">
      <template #header>
        <h3 style="font-size: 20px;text-align: left;">团队信息</h3>
      </template>
      <div style="text-align: left;padding-left: 10px;">
        <div v-if="(teamData.teamInfo !== '')">
          <el-row class="demo-avatar demo-basic" style="justify-content: flex-start;align-items: center">
            <el-avatar :size="70" :src="teamData.teamInfo.teamAvatar" style="margin: 15px;"/>
            <div style="text-align: left">
              <h3 style="margin-bottom: 5px;">{{ teamData.teamInfo.teamName }}</h3>
              <div style="display: flex;">
                <div class="teamInfoShow">志愿者人数：<span>{{ teamData.teamInfo.teamCount }}</span></div>
                <div class="teamInfoShow">志愿总时长：<span>{{ teamData.teamInfo.teamHours }}</span></div>
                <div class="teamInfoShow">成员参与活动总数：<span>{{ teamData.teamInfo.teamActivecount }}</span></div>
              </div>
            </div>
          </el-row>
          <div style="margin-top: 10px;">
            <el-button size="small" type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="toTeamAudit" v-if="(store.state.loginUser.userType === 1 && ((store.state.loginUser.userId === store.state.detail.userKey) && (store.state.loginUser.userType === store.state.detail.userType)))">入团审核</el-button>
            <el-button size="small" type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="judgeCouldJoinTeam" v-if="(store.state.loginUser.userType === 0 && !teamData.isMember && !teamData.isApplyCurrent)">加入团队</el-button>
            <el-button size="small" type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" disabled v-if="(store.state.loginUser.userType === 0 && teamData.isApplyCurrent)">正在申请加入</el-button>
            <el-button size="small" type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="exitTeam" v-if="(store.state.loginUser.userType === 0 && teamData.isMember)">退出团队</el-button>
          </div>
        </div>
        <el-empty description="没有加入任何志愿者团队" v-if="(teamData.teamInfo === '')"/>
      </div>

    </el-card>
    <el-card style="border: 0;box-shadow: none;margin-top: 10px;">
      <template #header>
        <h3 style="font-size: 20px;text-align: left;">团队成员</h3>
      </template>
      <div style="text-align: left;padding-left: 10px;">
        <div v-if="teamData.memberList.length !== 0">
          <el-button type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="batchKickOut" v-if="store.state.loginUser.userType === 1 && (store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType)">批量踢出</el-button>
          <el-table :data="teamData.memberList" stripe style="width: 100%" @selection-change="handleMembersTableSelect">
            <el-table-column type="selection" width="40" v-if="store.state.loginUser.userType === 1 && (store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType)"/>
            <el-table-column fixed prop="volunteersUsername" label="成员用户名" width="130" align="center" show-overflow-tooltip/>
            <el-table-column prop="volunteersEmail" label="成员邮箱" align="center" width="150" show-overflow-tooltip/>
            <el-table-column prop="volunteersPhone" label="联系方式" width="130" align="center"/>
            <el-table-column prop="volunteersPolitical" label="政治面貌" width="100" align="center"/>
            <el-table-column prop="volunteersDegree" label="最高学历" width="100" align="center"/>
            <el-table-column prop="volunteersHours" label="志愿时长(小时)" width="120" align="center"/>
            <el-table-column prop="volunteersActivecount" label="参与活动数(个)" width="120" align="center"/>
            <el-table-column prop="volunteersRegister" sortable label="注册时间" width="120" align="center"/>
            <el-table-column fixed="right" prop="volunteersId" label="操作" width="120" align="center">
              <template #default="scope">
                <el-button link type="primary" size="small" @click="memberDetail(scope.row.volunteersId)">查看</el-button>
                <el-popconfirm title="确定要删除该条记录吗？" confirm-button-text="确认" cancel-button-text="取消" @confirm="memberKickOut(scope.row.volunteersId)">
                  <template #reference>
                    <el-button link type="primary" size="small" v-if="store.state.loginUser.userType === 1 && (store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType)">踢出</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination small background layout="prev, pager, next" :total="teamData.memberList.length" :page-size="15" :current-page="currentPage" @current-change="handleCurrentPage" style="margin-top: 20px"/>
        </div>
        <el-empty description="暂无成员数据" v-if="teamData.memberList.length === 0"/>
      </div>
    </el-card>
    <el-dialog v-model="applyInfoDialogVisible" center draggable :before-close="cancelSubmit" title="申请信息填写" width="30%">
      <el-form :model="applyInfo" ref="applyInfoRef" :rules="applyInfoRules" label-width="150">
        <el-form-item label="申请标题" prop="applyTitle">
          <el-input v-model="applyInfo.applyTitle" placeholder="请输入申请标题" maxlength="50" show-word-limit/>
        </el-form-item>
        <el-form-item label="申请描述" prop="applyDescription">
          <el-input v-model="applyInfo.applyDescription" type="textarea" :autosize="{minRows: 3}" placeholder="请描述你想申请的原因及自身优势（最多300字）" show-word-limit maxlength="300"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelSubmit">取消</el-button>
          <el-button type="primary" @click="submitApplyInfo">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import { useRouter } from "vue-router";
import {ElMessage, ElMessageBox} from "element-plus";
import { useStore } from "vuex";
import myAxios from "../../../utils/axios";

const router = useRouter();
const store = useStore();
const selectedMembers = ref(null);
const currentPage = ref(1);
const applyInfoDialogVisible = ref(false);
const applyInfoRef = ref(null);
const applyInfoRules = reactive({
  applyTitle: [
    {required: true, message: '请输入申请标题', trigger: 'blur'}
  ],
  applyDescription: [
    {required: true, message: '请输入申请信息', trigger: 'blur'}
  ]
});
const applyInfo = reactive({
  applyTitle: '',
  applyDescription: '',
  applyType: 1,
  fkApplicantId: store.state.loginUser.userId,
  fkApplicantType: store.state.loginUser.userType,
  fkAuditorId: store.state.detail.userKey,
  fkAuditorType: store.state.detail.userType
});
const teamData = reactive({
  //用于判断志愿者是否正在申请加入一个志愿者团队
  isApply: false,
  //用于判断志愿者是否正在申请加入当前志愿者团队
  isApplyCurrent: false,
  //用于判断志愿者是否已经是当前团队的成员
  isMember: false,
  //用于判断志愿者是否已经加入一个志愿者团队
  hasTeam: false,
  memberList: [],
  teamInfo: '',
})

const getTeamInfo = () => {
  let data = new FormData();
  data.append("userId",store.state.detail.userKey);
  data.append("userType",store.state.detail.userType);
  data.append("currentPage",currentPage.value);
  myAxios.post('/team/getTeamInfo',data).then(response => {
    if (response.data.code === 200) {
      teamData.memberList = response.data.data.teamData.memberList;
      teamData.teamInfo = response.data.data.teamData.teamInfo;
    } else if (response.data.code === 440) {
      teamData.teamInfo = '';
      teamData.memberList = [];
    } else {
      ElMessage.error('请求数据出错');
    }
  })
};
const getIsAppliedOrMember = () => {
  //如果当前登录用户是志愿者，判断其加入志愿者团队的状态（1、正在申请加入团队 2、正在申请加入其他团队 3、已是其他团队成员 4、已经是该团队的成员）
  //当一个志愿者正在申请加入某个志愿者团队时，不能是一个团队的成员;当他是一个团队的成员时不能正在申请加入某个团队;当他不是一个团队的成员时可以不申请加入任何团队
  if (store.state.loginUser.userType === 0) {
    let formData = new FormData();
    formData.append("userId",store.state.loginUser.userId);
    formData.append("detailId",store.state.detail.userKey);
    formData.append("detailType",store.state.detail.userType);
    myAxios.post('/volunteer/judgeTeam',formData).then(response => {
      teamData.isApply = response.data.data.judgeTeam.isApply;
      teamData.isApplyCurrent = response.data.data.judgeTeam.isApplyCurrent;
      teamData.isMember = response.data.data.judgeTeam.isMember;
      teamData.hasTeam = response.data.data.judgeTeam.hasTeam;
    })
  }
};
onMounted(() => {
  getTeamInfo();
  getIsAppliedOrMember();
})
const toTeamAudit = () => {
  router.push('/user-detail/auditManager');
};
const judgeCouldJoinTeam = () => {
  //判断该志愿者是否满足申请加入当前团队的条件
  //当一个志愿者正在申请加入某个志愿者团队时，不能是一个团队的成员;当他是一个团队的成员时不能正在申请加入某个团队;当他不是一个团队的成员时可以不申请加入任何团队
  //如果该志愿者已经有团队
  if (teamData.hasTeam === true) {
    ElMessage.warning('操作失败，只能加入一个志愿者团队');
  }
  //如果该志愿者没有团队但正在申请加入某个团队
  else if (teamData.isApply === true) {
    ElMessage.warning('操作失败，只能同时申请加入一个志愿者团队');
  }
  //如果该志愿者没有团队，也没有正在申请加入某个团队
  else if (teamData.hasTeam === false && teamData.isApply === false) {
    applyInfoDialogVisible.value = true;
  }
}
const exitTeam = () => {
  ElMessageBox.confirm(
      '确定要退出团队吗？',
      '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    let willDelete = [];
    willDelete.push(store.state.loginUser.userId);
    let data = new FormData();
    data.append("volunteerIds",willDelete);
    data.append("teamId",store.state.detail.userKey);
    myAxios.post('/team/memberKickOrExit',data).then(response => {
      if (response.data.code === 200) {
        ElMessage.success('退出团队成功');
        setTimeout(() => {
          location.reload();
        },1000);
      } else {
        ElMessage.error('请求出错，退出失败');
      }
    });
  }).catch(() => {
    ElMessage.info('已取消退出团队');
  })
};
const handleMembersTableSelect = (values) => {
  if (values.length === 0) {
    selectedMembers.value = null;
  }else {
    selectedMembers.value = values;
  }
};
const handleCurrentPage = (val) => {
  currentPage.value = val;
  getTeamInfo();
}
const memberDetail = (index) => {
  let detail = {userKey: index,userType: 0};
  store.commit('setDetail',detail);
  router.push('/user-detail/profile');
};
const memberKickOut = (index) => {
  let willDelete = [];
  willDelete.push(index);
  let data = new FormData();
  data.append("volunteerIds",willDelete);
  data.append("teamId",store.state.loginUser.userId);
  myAxios.post('/team/memberKickOrExit',data).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('踢出成员成功');
      setTimeout(() => {
        location.reload();
      },1000);
    } else {
      ElMessage.error('请求出错，踢出失败');
    }
  });
};
const batchKickOut = () => {
  if (selectedMembers.value === null) {
    ElMessage({
      message: '请选择要踢出的团队成员',
      type: 'warning'
    });
  }else {
    ElMessageBox.confirm(
        '确定要删除踢出这些成员吗？',
        '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      let willDelete = [];
      selectedMembers.value.forEach((selected) => {
        willDelete.push(selected.volunteersId);
      })
      let data = new FormData();
      data.append("volunteerIds",willDelete);
      data.append("teamId",store.state.loginUser.userId);
      myAxios.post('/team/memberKickOrExit',data).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('踢出成员成功');
          setTimeout(() => {
            location.reload();
          },1000);
        } else {
          ElMessage.error('请求出错，踢出失败');
        }
      });
    }).catch(() => {
      ElMessage.info('已取消踢出这些成员');
    })
  }
};
const cancelSubmit = () => {
  applyInfoRef.value.resetFields();
  applyInfoDialogVisible.value = false;
};
const submitApplyInfo = () => {
  applyInfoRef.value.validate((valid) => {
    if (valid) {
      myAxios.post('/apply/insertApply',applyInfo).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('申请加入团队成功，请等待审核');
          cancelSubmit();
          setTimeout(() => {
            location.reload();
          },1000);
        } else {
          ElMessage.error('系统错误，申请失败');
        }
      })
    } else {
      ElMessage.warning('请输入申请参加活动的必要信息');
    }
  })
};
</script>

<style scoped>
.teamInfoShow {
  font-size: 14px;
  color: #959393;
  margin-right: 30px;
}
</style>