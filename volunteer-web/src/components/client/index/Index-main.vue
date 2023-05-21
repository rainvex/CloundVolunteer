<template>
  <!--客户端主页主要内容-->
  <el-container class="data-card">
    <el-row style="flex-direction: column">
      <!--主页志愿活动部分-->
      <el-tabs model-value="活动推荐" class="demo-tabs" @tab-change="tabChange">
        <el-tab-pane label="活动推荐" name="活动推荐">
          <el-row>
            <el-card :body-style="{ padding: '0px' }" class="card-body" shadow="hover" v-for="(item,index) in data.indexData.activityInfoList" :key="index">
              <div class="card-top">
                <el-image :src="item.activityBanner" fit="cover" alt="加载失败"/>
              </div>
              <div class="card-bottom">
                <span class="card-name" @click="toActivityDetail(item.activityId)" :title="item.activityTitle">{{ item.activityTitle }}</span>
                <div class="card-publisher" :title="item.publisherUnitName">
                  <time>{{ item.publisherUnitName }}</time>
                </div>
                <div class="card-time" :title="item.activityPublic">
                  <time>{{ item.activityPublic }}</time>
                </div>
              </div>
            </el-card>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="团队推荐" name="团队推荐">
          <el-row>
            <el-card :body-style="{ padding: '0px' }" class="card-body" shadow="hover" v-for="(item,index) in data.indexData.teamInfoList" :key="index">
              <div class="card-top">
                <el-image :src="item.teamAvatar" fit="cover" alt="加载失败"/>
              </div>
              <div class="card-bottom">
                <span class="card-name" @click="toUserDetail(item.teamId,item.teamType)" :title="item.teamName">{{ item.teamName }}</span>
                <div class="card-members">
                  志愿者总数：<span>{{ item.teamCount }}</span>人
                </div>
                <div class="card-hours">
                  志愿总时长：<span>{{ item.teamHours }}</span>小时
                </div>
                <div class="card-place" :title="item.teamAddress">
                  <span>{{ item.teamAddress }}</span>
                </div>
              </div>
            </el-card>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="单位推荐" name="单位推荐">
          <el-row>
            <el-card :body-style="{ padding: '0px' }" class="card-body" shadow="hover" v-for="(item,index) in data.indexData.unitInfoList" :key="index">
              <div class="card-top">
                <el-image :src="item.unitAvatar" fit="cover" alt="加载失败"/>
              </div>
              <div class="card-bottom">
                <span class="card-name" @click="toUserDetail(item.unitId,item.unitType)" :title="item.unitName">{{ item.unitName }}</span>
                <div class="card-activeNum">
                  发布志愿活动数：<span>{{ item.unitPubliccount }}</span>个
                </div>
                <div class="card-place" :title="item.unitAddress">
                  <span>{{ item.unitAddress }}</span>
                </div>
              </div>
            </el-card>
          </el-row>
        </el-tab-pane>
      </el-tabs>
      <div style="text-align: right"><span id="goToMore" @click="goToMore">更多>></span></div>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <h3 style="text-align: left;margin-top: 20px;color: #da4453">热点志愿资讯<el-icon :size="25" color="#ff890c" style="vertical-align: bottom;"><Share /></el-icon></h3>
        <el-card style="margin-top: 20px;border: 0;box-shadow: none;">
          <ul style="text-align: left;padding-left: 10px;font-weight: bold;font-size: 17px">
            <li v-for="(inform, index) in data.indexData.informList" :key="index" style="line-height: 40px;display: flex;justify-content: space-between;">
              <span @click="viewInformDetail(inform.informId)" style="max-width: 450px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">{{ inform.informTitle }}</span>
              <span>{{ inform.informPublic }}</span>
            </li>
          </ul>
          <p class="moreInform" @click="toMoreInform" style="font-size: 14px;text-align: right;margin-top: 8px;color: darkgray;">查看更多>></p>
        </el-card>
      </el-col>
      <el-col :span="12">
        <h3 style="text-align: left;margin-top: 20px;color: #da4453">志愿时长排行<el-icon :size="25" color="#ff890c" style="vertical-align: bottom;"><Flag /></el-icon></h3>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-card class="rank-card" style="margin-top: 20px;border: 0;box-shadow: none;">
              <template #header>
                <div class="rank-title">
                  <span>志愿者排行榜</span>
                </div>
              </template>
              <div v-for="(item,index) in data.indexData.mostFiveVolunteerByHours" :key="index" class="rank-list">
                <div style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;max-width: 150px">
                  <span class="rank-number"><i>{{ index + 1 }}</i></span>
                  <span class="rank-volunteer" @click="toUserDetail(item.volunteersId,item.volunteersType)">{{ item.volunteersUsername }}</span>
                </div>
                <div class="rank-hour">{{ item.volunteersHours + '小时' }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="rank-card" style="margin-top: 20px;border: 0;box-shadow: none;">
              <template #header>
                <div class="rank-title">
                  <span>志愿者团队排行榜</span>
                </div>
              </template>
              <div v-for="(item,index) in data.indexData.mostFiveTeamByHours" :key="index" class="rank-list">
                <div style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;max-width: 150px">
                  <span class="rank-number"><i>{{ index + 1 }}</i></span>
                  <span class="rank-volunteer" @click="toUserDetail(item.teamId,item.teamType)">{{ item.teamName }}</span>
                </div>
                <div class="rank-hour">{{ item.teamHours + '小时' }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-backtop :right="100" :bottom="100" />
  </el-container>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Flag, Share } from "@element-plus/icons";
import { useStore } from 'vuex';

const router = useRouter();
const store = useStore();
const isActiveTab = ref('');

const tabChange = (name) => {
  isActiveTab.value = name;
};
const goToMore = () => {
  if(isActiveTab.value === '' || isActiveTab.value === '活动推荐') {
    router.push('/activities');
  } else if(isActiveTab.value === '团队推荐') {
    router.push('/teams');
  } else if(isActiveTab.value === '单位推荐') {
    router.push('/units');
  }
};
const data = defineProps({
  indexData: {
    type: Object,
    default: null
  }
});
const toActivityDetail = (activityId) => {
  store.commit('setActiveKey',activityId);
  router.push('/activity-detail');
};
const toUserDetail = (userId,userType) => {
  store.commit('setDetail',{userKey: userId,userType: userType});
  router.push('/user-detail/profile');
};
const viewInformDetail = (informId) => {
  store.commit('setInformKey',informId);
  router.push('/inform-detail');
};
const toMoreInform = () => {
  router.push('/informs');
};
</script>

<style lang="scss" scoped>
.data-card {
  width: 1250px;
  height: auto;
  margin: 20px auto;
  flex-direction: column;

  ul li::before {
    content: '';
    position: absolute;
    border-radius: 50%;
    margin-top: 15px;
    margin-left: -15px;
    width: 5px;
    height: 5px;
    background: #d41e1e;
  }

  ul li:hover {
    color: red;
    cursor: pointer;
  }

  .moreInform:hover {
    cursor: pointer;
  }
}
:deep(.el-tabs) {
  .el-tabs__item {
    font-weight: bold;
    font-size: 15px;
  }

  .card-body {
    width: 196px;
    margin: 0 5px 15px 5px;

    .el-card__body {
      display: flex;
      flex-direction: column;

      .card-top {
        .el-image {
          width: 100%;
          height: 170px;
        }
      }
      .card-bottom {
          display: flex;
          flex-direction: column;
          padding: 10px;
          text-align: left;

          .card-name {
            font-size: 17px;
            font-weight: bold;
            margin-bottom: 10px;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
          }

          .card-name:hover {
            color: #da4453;
            cursor: pointer;
          }

          .card-members,.card-hours,.card-place,.card-time,.card-activeNum,.card-publisher {
            font-size: 14px;
            color: #626060;
            line-height: 25px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .card-place {
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
          }
      }
    }
  }
}
#goToMore {
  font-size: 14px;
  color: #959393;
}
#goToMore:hover {
  color: #da4453;
  cursor: pointer;
}
:deep(.rank-card) {

  .rank-title {
    font-weight: bold;
    font-size: 18px;
  }

  .el-card__body {

    .rank-list {
      display: flex;
      justify-content: space-between;
      font-weight: bold;
      padding: 6px 0;
    }
    .rank-list:nth-child(1),.rank-list:nth-child(1) span {
      color: #e03e4b;
    }
    .rank-list:nth-child(2),.rank-list:nth-child(2) span {
      color: #ff5c6c;
    }
    .rank-list:nth-child(3),.rank-list:nth-child(3) span {
      color: #ff8a3d;
    }
    .rank-list:hover {
      background-color: #ebeaea;
    }
    .rank-number,.rank-volunteer,.rank-hour {
      font-size: 16px;
      color: #959393;
    }
    .rank-volunteer {
      margin-left: 20px;
    }
    .rank-volunteer:hover {
      cursor: pointer;
    }
  }

}
</style>