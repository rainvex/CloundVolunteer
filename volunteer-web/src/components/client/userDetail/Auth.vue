<template>
  <!--实名认证页面组件-->
  <el-card class="auth-setting" style="border: 0;box-shadow: none;">
    <template #header>
      <h3 style="font-size: 20px;text-align: left;">实名认证</h3>
    </template>
    <div>
      <div v-if="!isAuth">
        <p style="font-size: 15px;">请上传身份证正面图</p>
        <el-upload class="auth-uploader"
                   accept="image/jpg,image/jpeg"
                   :file-list="fileList"
                   list-type="picture" :limit="1"
                   :auto-upload="false"
                   :on-change="handleChange"
                   :on-remove="authRemove"
                   :on-exceed="authExceed">
          <el-button type="primary" style="margin-top: 15px">点击上传</el-button>
          <template #tip>
            <div class="el-upload__tip">
              请上传不超过2MB，格式为jpg的照片
            </div>
          </template>
        </el-upload>
        <el-button type="primary" @click="auth">确认认证</el-button>
      </div>
      <div v-if="isAuth">
        你已完成实名认证
      </div>
    </div>
  </el-card>
</template>

<script setup>
import {ElLoading, ElMessage, ElMessageBox} from "element-plus";
import {onMounted, ref} from "vue";
import { useStore } from "vuex";
import jwt_decode from "jwt-decode";
import myAxios from "../../../utils/axios";

const store = useStore();
const isAuth = ref(false);
const fileList= ref([]);

const getIsAuth = () => {
  if (jwt_decode(store.state.token).userGender) {
    isAuth.value = true;
  }
}
onMounted(() => {
  getIsAuth();
})
const handleChange = (file) => {
  const isLimit2M = file.raw.size / 1024 / 1024 < 2;
  const isJpg = (file.raw.type === 'image/jpg' || file.raw.type === 'image/jpeg');
  if (!isLimit2M) {
    ElMessage.warning('上传图片大小不能超过 2MB!');
  }
  if (!isJpg) {
    ElMessage.warning('上传图片格式必须为jpg');
  }
  if (isLimit2M && isJpg) {
    fileList.value.push(file);
  }
};
const authExceed = () => {
  ElMessage.warning('只能上传一张图片，若想上传新的图片请先将旧的图片移除');
};
const authRemove = () => {
  fileList.value = [];
};
const auth = () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请上传符合条件的身份证正面照');
    return;
  }
  ElMessageBox.confirm(
      '确定现在要实名认证吗？认证后将不能更改',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    let data = new FormData();
    data.append("file",fileList.value[0].raw);
    data.append("userId",store.state.loginUser.userId);
    const loading = ElLoading.service({lock: true, text: '正在认证，请稍后'});
    myAxios.post('/volunteer/auth',data).then(response => {
      loading.close();
      if (response.data.code === 200) {
        store.commit('setToken',response.data.data.newToken);
        store.commit('setLoginUser',response.data.data.newToken);
        location.reload();
      } else {
        ElMessage.warning('请确保上传正确的身份证正面照');
      }
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消进行实名认证',
    })
  })
}
</script>

<style lang="scss" scoped>
:deep(.el-upload-list--picture){
  margin: 10px 0;

  .el-upload-list__item {
      width: 350px;
      margin: 0 auto;
      padding: 20px;
  }
  .el-upload-list__item-thumbnail {
    width: 300px;
    height: auto;
  }
}
</style>