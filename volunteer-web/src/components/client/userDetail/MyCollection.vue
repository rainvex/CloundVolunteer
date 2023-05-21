<template>
  <el-card class="my-collection" style="border: 0;box-shadow: none;">
    <template #header>
      <h3 style="font-size: 20px;text-align: left;">我的收藏</h3>
    </template>
    <div style="text-align: left;padding-left: 10px;">
      <el-row :gutter="10">
        <el-col :span="4" v-if="starData.total !== 0" v-for="list in starData.starList">
          <el-card :body-style="{ padding: '0px' }" class="activity-card">
            <el-image :src="list.activityInfo.activityBanner" class="active-banner" fit="cover" alt="加载失败"/>
            <div class="card-bottom">
              <div class="active-title" @click="toSignUp(list.activityInfo.activityId)" :title="list.activityInfo.activityTitle">
                <span>{{ list.activityInfo.activityTitle }}</span>
              </div>
              <div class="active-place">
                <span>{{ list.activityInfo.activityPlace }}</span>
              </div>
              <div class="active-time">
                <time>{{ list.activityInfo.activityPublic }}</time>
              </div>
              <el-button type="primary" @click="cancelStar(list.starId)" v-if="store.state.loginUser.userId === store.state.detail.userKey && store.state.loginUser.userType === store.state.detail.userType" link style="margin-top: 5px;font-size: 12px;">取消收藏</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="starData.total === 0">
          <el-empty description="该用户暂无收藏记录" />
        </el-col>
      </el-row>
      <el-pagination small v-if="starData.total !== 0" background layout="prev, pager, next" :total="starData.total" :page-size="12" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px;padding: 0"/>
    </div>
  </el-card>
</template>

<script setup>
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import myAxios from "../../../utils/axios";
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";

const store = useStore();
const router = useRouter();
const currentPage = ref(1);
const starData = reactive({
  starList: [],
  total: 0
});
const getAllStar = () => {
  let data = new FormData();
  data.append("userId",store.state.detail.userKey);
  data.append("currentPage",currentPage.value);
  myAxios.post('/star/getAllStarByVolunteer',data).then(response => {
    if (response.data.code === 200) {
      starData.starList = response.data.data.allStarByVolunteer.starList;
      starData.total = response.data.data.allStarByVolunteer.total;
    }else {
      ElMessage.error('系统错误，获取收藏记录失败');
    }
  })
};
onMounted(() => {
  getAllStar()
});
const handleCurrentChange = (val) => {
  currentPage.value = val;
  let data = new FormData();
  data.append("userId",store.state.detail.userKey);
  data.append("currentPage",currentPage.value);
  myAxios.post('/star/getAllStarByVolunteer',data).then(response => {
    if (response.data.code === 200) {
      starData.starList = response.data.data.allStarByVolunteer.starList;
      starData.total = response.data.data.allStarByVolunteer.total;
    }else {
      ElMessage.error('系统错误，获取收藏记录失败');
    }
  })
}
const cancelStar = (starIndex) => {
  myAxios.get('/star/cancelStar/' + starIndex).then(response => {
    if (response.data.code === 200) {
      ElMessage.success('取消收藏成功');
      setTimeout(function (){
        location.reload();
      },1000);
    } else {
      ElMessage.error('系统错误，取消收藏失败');
    }
  })
}
const toSignUp = (activityIndex) => {
  store.commit("setActiveKey",activityIndex);
  router.push('/activity-detail');
}
</script>

<style scoped>
.el-image {
  width: 100%;
  height: 100%;
}
.active-banner {
  height: 120px;
}
.active-title:hover {
  color: #da4453;
  cursor: pointer;
}
.card-bottom {
  text-align: left;
  padding: 8px;
  background-color: #ededed;
}
.card-bottom .active-title {
  font-size: 14px;
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.card-bottom .active-place {
  font-size: 11px;
  color: #959393;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  margin-top: 8px;
  margin-bottom: 2px;
}
.card-bottom .active-time {
  font-size: 11px;
  color: #959393;
}
.el-button+.el-button {
  margin-left: 25px;
}
</style>
<style lang="scss" scoped>
:deep(.activity-card) {
  .el-card__body {
    display: flex;
    flex-direction: column;
  }
}
</style>