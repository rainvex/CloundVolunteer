<template>
  <div>
    <el-card class="message-manager" style="border: 0;box-shadow: none;">
      <template #header>
        <h3 style="font-size: 20px;text-align: left;">消息管理</h3>
      </template>
      <div style="text-align: left;padding-left: 10px;">
        <div v-if="messageData.messageList.length !== 0">
          <el-button type="primary" style="font-size: 12px;height: 28px;margin-bottom: 5px" @click="batchDeleteMessage">批量删除记录</el-button>
          <el-table :data="messageData.messageList" stripe style="width: 100%" @selection-change="handleMessageTableSelect">
            <el-table-column type="selection" width="40"/>
            <el-table-column fixed prop="messageTitle" label="消息标题" width="160" show-overflow-tooltip/>
            <el-table-column prop="messageContent" label="消息内容" show-overflow-tooltip align="center"/>
            <el-table-column prop="messageType" label="消息类型" width="120" align="center">
              <template #default="scope">
                <el-tag :type="judgeMessageTypeTag(scope.row.messageType)">{{ judgeMessageType(scope.row.messageType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="messageTime" sortable label="发送日期" width="170" align="center"/>
            <el-table-column prop="messageStatus" label="消息状态" width="100" align="center"
              :filters="[{text: '未读',value: 0},{text: '已读',value: 1}]" :filter-method="filterMessageStatus">
              <template #default="scope">
                <el-tag :type="judgeMessageStatusTag(scope.row.messageStatus)">{{ judgeMessageStatus(scope.row.messageStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column fixed="right" prop="messageId" label="操作" width="130" align="center">
              <template #default="scope">
                <el-button link type="primary" size="small" @click="messageView(scope.row.messageId)">查看</el-button>
                <el-popconfirm title="确定要删除该条消息吗？" cancel-button-text="取消" confirm-button-text="确认" @confirm="messageDelete(scope.row.messageId)">
                  <template #reference>
                    <el-button link type="primary" size="small">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination small background layout="prev, pager, next" :total="messageData.messageList.length" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
        </div>

        <el-empty description="暂无消息" v-if="messageData.messageList.length === 0"/>
      </div>
    </el-card>

    <el-drawer v-model="messageDrawerVisible" direction="rtl" @closed="getMessageData">
      <h3>{{ messageData.messageById.messageTitle }}</h3>
      <p style="text-indent: 2em;text-align: left;line-height: 25px;margin-top: 20px">{{ messageData.messageById.messageContent }}</p>
      <p style="margin-top: 30px;font-size: 15px;color: #626060;text-align: right;padding-right: 15px">{{ messageData.messageById.messageSender }}</p>
      <p style="margin-top: 10px;font-size: 14px;color: #626060;text-align: right;padding-right: 15px">{{ messageData.messageById.messageTime }}</p>
    </el-drawer>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import { useStore } from "vuex";
import myAxios from "../../../utils/axios";

const store = useStore();
const selectedMessage = ref(null);
const messageDrawerVisible = ref(false);
const currentPage = ref(1);
const messageData = reactive({
  messageList: [],
  messageById: ''
});

const getMessageData = () => {
  let data = new FormData();
  data.append("userId",store.state.loginUser.userId);
  data.append("userType",store.state.loginUser.userType);
  data.append("currentPage",currentPage.value);
  myAxios.post('/message/getMessageByUser',data).then(response => {
    if (response.data.code === 200) {
      messageData.messageList = response.data.data.messageList;
    } else {
      ElMessage.error('请求出错');
    }
  })
};
onMounted(() => {
  getMessageData();
})
const judgeMessageType = (messageType) => {
  if (messageType === 0) {
    return '审核通知';
  } else if (messageType === 1) {
    return '审核结果通知';
  } else {
    return '系统消息';
  }
}
const judgeMessageTypeTag = (messageType) => {
  if (messageType === 0 || messageType === 1) {
    return '';
  } else {
    return 'danger';
  }
}
const judgeMessageStatus = (status) => {
  if (status === 0){
    return '未读';
  }else if (status === 1) {
    return '已读';
  }
};
const judgeMessageStatusTag = (status) => {
  if (status === 0){
    return 'danger';
  }else if (status === 1) {
    return 'success';
  }
};
const handleMessageTableSelect = (values) => {
  if (values.length === 0) {
    selectedMessage.value = null;
  }else {
    selectedMessage.value = values;
  }
};
const messageDelete = (index) => {
  let willDelete = [];
  willDelete.push(index);
  let data = new FormData();
  data.append("messageIds",data);
  myAxios.post('/message/deleteMessage',data).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('删除消息成功');
      setTimeout(() => {
        location.reload();
      },1000);
    } else {
      ElMessage.error('请求错误，删除失败');
    }
  });
}
const batchDeleteMessage = () => {
  if (selectedMessage.value === null) {
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
      selectedMessage.value.forEach((selected) => {
        willDelete.push(selected.messageId);
      });
      let data = new FormData();
      data.append("messageIds",willDelete);
      myAxios.post('/message/deleteMessage',data).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('删除消息成功');
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
        message: '已取消删除消息',
      })
    });
  }
};
const messageView = (index) => {
  myAxios.get('/message/getSingleMessage/' + index).then(response => {
    if (response.data.code === 200) {
      messageData.messageById = response.data.data.messageById.messageInfo;
      store.commit('setMessageCount',response.data.data.messageById.messageCount);
    } else {
      ElMessage.error('请求出错');
    }
  })
  messageDrawerVisible.value = true;
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getMessageData();
}
const filterMessageStatus = (value, row) => {
  return row.messageStatus === value;
}
</script>

<style scoped>

</style>