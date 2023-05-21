<template>
  <el-card class="activity-public" style="border: 0;box-shadow: none;">
    <template #header>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/user-detail/activityRecord' }">活动记录</el-breadcrumb-item>
        <el-breadcrumb-item>发布活动</el-breadcrumb-item>
      </el-breadcrumb>
    </template>
    <div style="text-align: left;padding-left: 10px;">
      <el-form ref="activityFormRef" :rules="activityPublicRules" :model="activity" label-width="auto" style="width: 50%;margin: 20px auto;">
        <el-form-item label="活动标题" prop="activityTitle">
          <el-input v-model="activity.activityTitle" placeholder="请输入活动标题，最多30字" maxlength="30" show-word-limit/>
        </el-form-item>
        <el-form-item label="活动时间" prop="activityDuration">
          <el-date-picker
              v-model="activity.activityDuration"
              type="datetimerange"
              range-separator="至"
              start-placeholder="活动开始时间"
              end-placeholder="活动结束时间"
              @change="dateTimeChange"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动简介" prop="activitySimple">
          <el-input v-model="activity.activitySimple" maxlength="50" type="textarea" placeholder="请输入活动简介，最多50字" :autosize="{minRows: 2}" show-word-limit/>
        </el-form-item>
        <el-form-item label="活动内容" prop="activityContent">
          <el-input v-model="activity.activityContent" type="textarea" placeholder="请输入活动具体内容" :autosize="{minRows: 4}"/>
        </el-form-item>
        <el-form-item label="活动地点" prop="activityPlaceCode">
          <el-cascader
              v-model="activity.activityPlaceCode"
              :options="options"
              :props="{expandTrigger: 'hover'}"
              @change="chooseRegisterAddress"
              placeholder="请选择活动地点"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item prop="activityPlaceDetail">
          <el-input :disabled="activity.activityPlaceCode === ''" v-model="activity.activityPlaceDetail" placeholder="请填写具体地点" @change="addressDetailChange"/>
        </el-form-item>
        <el-form-item label="报名要求" prop="activityRequire">
          <el-input v-model="activity.activityRequire" type="textarea" maxlength="300" placeholder="请输入活动报名要求，最多300字" :autosize="{minRows: 3}" show-word-limit />
        </el-form-item>
        <el-form-item label="联系人员" prop="activityContact">
          <el-input v-model="activity.activityContact" placeholder="请输入活动主要联系人，若有多个请以逗号分开"/>
        </el-form-item>
        <el-form-item label="联系电话" prop="activityPhone">
          <el-input v-model="activity.activityPhone" placeholder="请输入活动联系人的联系电话" />
        </el-form-item>
        <el-form-item label="人数限制" prop="activityLimit">
          <el-input-number
              v-model="activity.activityLimit"
              class="mx-4"
              :min="1"
              :max="100"
              controls-position="right"
              style="width: 220px"
              placeholder="请输入报名人数限制"
          />
        </el-form-item>
        <el-form-item label="活动分类" prop="activityClassic">
          <el-select v-model="activity.activityClassic" class="m-2" placeholder="请选择活动分类" style="width: 220px">
            <el-option
                v-for="item in activeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间" prop="activityCutoff">
          <el-date-picker
              v-model="activity.activityCutoff"
              type="datetime"
              placeholder="请选择活动报名截止时间"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动图片" prop="activityBanner">
          <el-upload class="banner-uploader"
                     accept="image/jpeg,image/jpg,image/png"
                     ref="uploadRef"
                     :file-list="activityFile"
                     action="http://localhost:8081/base/image/upload"
                     list-type="picture" :limit="1"
                     :before-upload="handleUploadBefore"
                     :on-success="bannerSuccess"
                     :on-exceed="bannerExceed"
                     :on-remove="bannerRemove">
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传大小小于2M，格式为jpg或png的照片
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitActivityForm" style="margin-left: 150px">发布活动</el-button>
          <el-button type="primary" @click="resetActivityForm">重置表单</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script setup>
import { ref,reactive } from "vue";
import { useRouter } from "vue-router";
import {ElLoading, ElMessage} from "element-plus";
import myAxios from "../../../utils/axios";
import { useStore } from "vuex";
import { regionData,CodeToText } from 'element-china-area-data';

const router = useRouter();
const store = useStore();
const activityFormRef = ref();
const uploadRef = ref();
const activityFile = ref([]);
const options = regionData;
const activeOptions = [
      {
        value: "治安防控",
        label: "治安防控"
      },
      {
        value: "青少年服务",
        label: "青少年服务"
      },
      {
        value: "文明实践",
        label: "文明实践"
      },
      {
        value: "扶贫帮困",
        label: "扶贫帮困"
      },
      {
        value: "敬老助残",
        label: "敬老助残"
      },
      {
        value: "公共文明",
        label: "公共文明"
      },
      {
        value: "志愿防疫",
        label: "志愿防疫"
      },
      {
        value: "环境保护",
        label: "环境保护"
      },
      {
        value: "企业服务",
        label: "企业服务"
      },
      {
        value: "便民服务",
        label: "便民服务"
      },
      {
        value: "青春情暖",
        label: "青春情暖"
      },
      {
        value: "其他",
        label: "其他"
      },
    ];
const activity = reactive({
  activityTitle: '',
  activityDuration: [],
  activityContinue: '',
  activitySimple: '',
  activityContent: '',
  activityPlaceCode: '',
  activityPlaceDetail: '',
  activityPlace: '',
  activityRequire: '',
  activityContact: '',
  activityPhone: '',
  activityLimit: 0,
  activityClassic: '',
  activityCutoff: '',
  activityBanner: '',
  fkActivityPublisher: store.state.loginUser.userId
});
const activityPublicRules = reactive({
  activityTitle: [
    {required: true, message: '活动标题不能为空', trigger: 'blur'}
  ],
  activityDuration: [
    {required: true, message: '活动时间不能为空', trigger: 'change'}
  ],
  activityCutoff: [
    {required: true, message: '报名截止时间不能为空', trigger: 'blur'}
  ],
  activitySimple: [
    {required: true, message: '活动简介不能为空', trigger: 'blur'}
  ],
  activityContent: [
    {required: true, message: '活动内容不能为空', trigger: 'blur'}
  ],
 activityPlaceCode: [
    {required: true, message: '活动地点不能为空', trigger: 'blur'}
  ],
  activityRequire: [
    {required: true, message: '活动要求不能为空', trigger: 'blur'}
  ],
  activityLimit:[
    {required: true, message: '人数限制不能为空', trigger: 'change'}
  ],
  activityContact: [
    {required: true, message: '联系人员不能为空', trigger: 'blur'}
  ],
  activityPhone: [
    {required: true, message: '联系电话不能为空', trigger: 'blur'}
  ],
  activityClassic: [
    {required: true, message: '活动分类不能为空', trigger: 'blur'}
  ]
})

const dateTimeChange = (value) => {
  activity.activityContinue = value[0] + " 至 " + value[1];
};
const chooseRegisterAddress = (value) => {
  let address = '';
  for (let i = 0; i < value.length; i++) {
    address += CodeToText[value[i]];//码转汉字
  }
  activity.activityPlace = address;
};
const addressDetailChange = (value) => {
  activity.activityPlace += value;
};
const handleUploadBefore = (file) => {
  const isLimit2M = file.size / 1024 / 1024 < 2;
  const isJpgOrPng = (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png');
  if (!isLimit2M) {
    ElMessage.warning('上传图片大小不能超过 2MB!');
  }
  if (!isJpgOrPng) {
    ElMessage.warning('上传图片格式必须为jpg或png');
  }
  return isLimit2M && isJpgOrPng;
};
const bannerSuccess = (result) => {
  activity.activityBanner = result.data.filepath;
  activityFile.value.push({name: result.data.filepath,url: result.data.filepath});
};
const bannerExceed = () => {
  ElMessage.warning('只能上传一张图片，若想上传新的图片请先将旧的图片移除');
};
const bannerRemove = () => {
  let formData = new FormData();
  formData.append("pictureUrl",activity.activityBanner);
  myAxios.post('/base/image/remove',formData).then(response => {
    if (response.data.code === 200) {
      activity.activityBanner = '';
      activityFile.value = [];
    } else {
      ElMessage.error("系统错误，图片删除失败，请重试");
    }
  });
};
const submitActivityForm = () => {
  if (Date.parse(activity.activityCutoff) > Date.parse(activity.activityDuration[0])) {
    ElMessage.warning('报名截止日期不能超过活动开始时间');
    return;
  }
  activityFormRef.value.validate((valid) => {
    if (valid) {
      const loading = ElLoading.service({lock: true, text: '正在发布志愿活动...'});
      myAxios.post('/activity/public',activity).then(response => {
        loading.close();
        if (response.data.code === 200) {
          ElMessage.success('活动发布成功，请等待管理员审核');
          let detail = {userKey: store.state.loginUser.userId,userType: store.state.loginUser.userType};
          store.commit('setDetail',detail);
          router.push('/user-detail/myApply');
        } else {
          ElMessage.error('系统出错，发布活动失败');
        }
      });
    } else {
      ElMessage.warning('请完善活动信息');
    }
  });
}
const resetActivityForm = () => {
  bannerRemove();
  uploadRef.value.clearFiles();
  activityFormRef.value.resetFields();
};
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