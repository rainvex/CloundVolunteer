<template>
  <el-container style="width: 100%;height: 100%;">
    <el-container v-if="messageData.messageList.length !== 0" style="flex-direction: column;padding: 40px 15px;">
      <div style="text-align: left;margin-bottom: 10px">
        <el-button type="primary" style="font-size: 12px;" plain :icon="Plus" @click="messageInfoDialogVisible = true">发布消息</el-button>
        <el-button type="danger" style="font-size: 12px;" plain :icon="Delete" @click="batchDeleteMessage">批量删除记录</el-button>
      </div>
      <el-table :data="messageData.messageList" stripe style="width: 100%" @selection-change="handleMessageTableSelect" table-layout="fixed">
        <el-table-column type="selection"/>
        <el-table-column fixed prop="messageTitle" label="消息标题" show-overflow-tooltip align="center"/>
        <el-table-column prop="messageContent" label="消息内容" show-overflow-tooltip align="center"/>
        <el-table-column prop="messageType" label="消息类型" align="center" :filters="[{text: '审核通知',value: 0},{text: '系统消息',value: 2}]" :filter-method="filterMessageType">
          <template #default="scope">
            <el-tag :type="judgeMessageTypeTag(scope.row.messageType)">{{ judgeMessageType(scope.row.messageType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="messageTime" sortable label="发送日期" align="center"/>
        <el-table-column prop="messageStatus" label="消息状态" align="center" :filters="[{text: '未读',value: 0},{text: '已读',value: 1}]" :filter-method="filterMessageStatus">
          <template #default="scope">
            <el-tag :type="judgeMessageStatusTag(scope.row.messageStatus)">{{ judgeMessageStatus(scope.row.messageStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" prop="messageId" label="操作" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="messageView(scope.row.messageId)" :icon="View">查看</el-button>
            <el-popconfirm title="确定要删除该条消息吗？" cancel-button-text="取消" confirm-button-text="确认" @confirm="messageDelete(scope.row.messageId)">
              <template #reference>
                <el-button link type="primary" size="small" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination small background layout="prev, pager, next" :total="messageData.messageList.length" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px"/>
    </el-container>

    <el-drawer v-model="messageDrawerVisible" direction="rtl" @closed="getMessageData">
      <h3>{{ messageData.messageById.messageTitle }}</h3>
      <p style="text-indent: 2em;text-align: left;line-height: 25px;margin-top: 20px">{{ messageData.messageById.messageContent }}</p>
      <p style="margin-top: 30px;font-size: 15px;color: #626060;text-align: right;padding-right: 15px">{{ messageData.messageById.messageSender }}</p>
      <p style="margin-top: 10px;font-size: 14px;color: #626060;text-align: right;padding-right: 15px">{{ messageData.messageById.messageTime }}</p>
    </el-drawer>

    <el-dialog v-model="messageInfoDialogVisible" center draggable :before-close="cancelSubmit" title="发布系统消息" width="30%">
      <el-form :model="messageInfo" ref="messageInfoRef" :rules="messageInfoRules" label-width="150">
        <el-form-item label="申请标题" prop="messageTitle">
          <el-input v-model="messageInfo.messageTitle" placeholder="请输入消息标题" maxlength="50" show-word-limit/>
        </el-form-item>
        <el-form-item label="申请描述" prop="messageContent">
          <el-input v-model="messageInfo.messageContent" type="textarea" :autosize="{minRows: 3}" placeholder="请输入消息内容"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelSubmit">取消</el-button>
          <el-button type="primary" @click="submitMessageInfo">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import { useStore } from "vuex";
import myAxios from "../../utils/axios";
import { Delete,View,Plus } from "@element-plus/icons";

const store = useStore();
const selectedMessage = ref(null);
const messageDrawerVisible = ref(false);
const messageInfoDialogVisible = ref(false);
const messageInfoRef = ref();
const currentPage = ref(1);
const messageData = reactive({
  messageList: [],
  messageById: ''
});
const messageInfo = reactive({
  messageTitle: '',
  messageContent: '',
  fkMessageSender: store.state.loginUser.userId,
  fkMessageSendertype: store.state.loginUser.userType
});
const messageInfoRules = reactive({
  messageTitle: [
    {required: true, message: '请输入消息标题', trigger: 'blur'}
  ],
  messageContent: [
    {required: true, message: '请输入消息内容', trigger: 'blur'}
  ]
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
};
const judgeMessageTypeTag = (messageType) => {
  if (messageType === 0 || messageType === 1) {
    return '';
  } else {
    return 'danger';
  }
};
const filterMessageType = (value, row) => {
  return row.messageType === value;
};
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
const filterMessageStatus = (value, row) => {
  return row.messageStatus === value;
}
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
      getMessageData();
    } else {
      ElMessage.error('请求错误，删除失败');
    }
  });
};
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
          getMessageData();
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
};
const cancelSubmit = () => {
  messageInfoRef.value.resetFields();
  messageInfoDialogVisible.value = false;
};
const submitMessageInfo = () => {
  messageInfoRef.value.validate((valid) => {
    if (valid) {
      myAxios.post('/message/publicMessage',messageInfo).then(response => {
        if (response.data.code === 200) {
          ElMessage.success('发布系统消息成功');
          cancelSubmit();
          location.reload();
        } else {
          ElMessage.error('系统错误，发布失败');
        }
      })
    } else {
      ElMessage.warning('请输入消息发布的必要信息');
    }
  })
};
</script>

<style scoped>

</style>