<template>
  <el-container class="activities-container">
    <el-header class="activities-container-header">
      <div style="display:flex;margin-bottom: 20px;">
        <div class="header-title-div">活动地区：</div>
        <ul class="header-classic-ul">
          <li v-for="(item, index) in areaList" :key="index"  @click="activityByProvince(index)" :class="{'is-active-classic':index === currentProvinceIndex}">{{ item.label }}</li>
        </ul>
      </div>
      <div style="margin: 0 30px 20px 100px;background-color: #eaeaea;border-radius: 10px;padding: 10px;display: none;" id="activityByCity">
        <ul class="header-classic-ul">
            <li v-for="(item1, index) in areaList[currentProvinceIndex].children" @click="activityByCity(currentProvinceIndex, index)" :key="index" :class="{'is-active-classic':index === currentCityIndex}">{{ item1.label }}</li>
        </ul>
      </div>
      <div style="display:flex;margin-bottom: 20px;">
        <div class="header-title-div">活动分类：</div>
        <ul class="header-classic-ul" >
          <li v-for="(item, index) in classicList" :key="index"  @click="activityByClassic(index)" :class="{'is-active-classic':index === currentClassicIndex}">{{ item }}</li>
        </ul>
      </div>
    </el-header>
    <el-main class="activities-container-main">
      <el-row>
        <el-col :span="24" v-if="data.activityList.length !== 0" v-for="list in data.activityList" :key="list">
          <el-card :body-style="{ padding: '0px' }" class="activity-card" shadow="hover">
            <div class="card-left">
              <el-image :src="list.activityBanner" fit="cover" alt="加载失败"/>
            </div>
            <div class="card-center">
              <div class="activity-title" @click="toSignUp(list.activityId)" :title="list.activityTitle">
                <span>{{ list.activityTitle }}</span>
              </div>
              <div class="activity-place">
                地点：<span>{{ list.activityPlace }}</span>
              </div>
              <div class="activity-publisher">
                发布者：<span @click="viewPublisher(list.fkActivityPublisher)">{{ list.publisherUnitName }}</span>
              </div>
              <div class="activity-publicTime">
                发布时间：<span>{{ list.activityPublic }}</span>
              </div>
            </div>
            <div class="card-right">
              <div class="activity-option-div">
                <el-icon :size="25" color="#eb6751"><Star /></el-icon>
                <div style="font-size: 14px;margin-top: 5px">收藏</div>
                <div style="font-size: 14px;margin-top: 5px">{{ list.staredCount }}</div>
              </div>
              <div class="activity-option-div" style="margin: 0 20px">
                <el-icon :size="25" color="#2386d0"><User /></el-icon>
                <div class="activity-option-des">招募人数</div>
                <div class="activity-option-des">{{ list.activityLimit }}</div>
              </div>
              <div class="activity-option-div">
                <el-icon :size="25" color="#2386d0"><UserFilled /></el-icon>
                <div class="activity-option-des">已招募</div>
                <div class="activity-option-des">{{ list.activityApplied }}</div>
              </div>
              <el-button type="primary" style="margin: 30px 30px 30px 100px;" @click="toSignUp(list.activityId)">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="data.activityList.length === 0">
          <el-empty description="暂无相应数据"/>
        </el-col>
      </el-row>
      <el-pagination small v-if="data.activityList.length !== 0" background layout="prev, pager, next" :total="data.total" :page-size="10" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px;padding: 0"/>
    </el-main>
  </el-container>
</template>

<script setup>
import { Star,User,UserFilled } from "@element-plus/icons";
import { reactive, ref, watch } from 'vue';
import { useRouter,useRoute } from "vue-router";
import { useStore } from "vuex";
import { provinceAndCityDataPlus } from 'element-china-area-data';
import myAxios from "../../utils/axios";

const router = useRouter();
const route = useRoute();
const store = useStore();
const currentPage = ref(1);
const currentProvinceIndex = ref(0);
const currentCityIndex = ref(0);
const currentClassicIndex = ref(0);
const areaList = provinceAndCityDataPlus;
const classicList = [
  "全部",
  "治安防控",
  "青少年服务",
  "文明实践",
  "公共文明",
  "志愿防疫",
  "环境保护",
  "扶贫帮困",
  "敬老助残",
  "企业服务",
  "便民服务",
  "青春情暖",
  "其他"
];
//查询条件对象
const queryList = reactive({
  province: '',
  city: '',
  classic: ''
})
const classicFromRoute = route.params.classic;
if (classicFromRoute !== undefined) {
  currentClassicIndex.value = classicList.indexOf(classicFromRoute);
  queryList.classic = classicFromRoute;
}
//后端数据
let data = reactive({
  activityList: [],
  total: 0
});
watch(
    queryList,
    (newValue) => {
      currentPage.value = 1;
      const formData = new FormData();
      formData.append("province",newValue.province);
      formData.append("city",newValue.city);
      formData.append("classic",newValue.classic);
      formData.append("currentPage",currentPage.value);
      myAxios.post('/activity/queryByCondition',formData).then(response => {
        data.activityList = response.data.data.activityData.activityList;
        data.total = response.data.data.activityData.total;
      });
    },
    {
      deep:true, // 深度监听
      immediate:true // 立即执行
    }
);

const activityByProvince = (index) => {
  currentProvinceIndex.value = index;
  currentCityIndex.value = 0;
  index !== 0 ? document.querySelector('#activityByCity').style.cssText += 'display: block' : document.querySelector('#activityByCity').style.cssText += 'display: none';
  if (areaList[index].label === '全部') {
    queryList.province = '';
    queryList.city = '';
  } else {
    queryList.province = areaList[index].label;
  }
};
const activityByCity = (provinceIndex, index) => {
  currentCityIndex.value = index;
  areaList[provinceIndex].children[index].label === '全部' ?  queryList.city = '' : queryList.city = areaList[provinceIndex].children[index].label;
};
const activityByClassic = (index) => {
  currentClassicIndex.value = index;
  classicList[index] === '全部' ? queryList.classic = '' : queryList.classic = classicList[index];
};
const toSignUp = (activityIndex) => {
  store.commit("setActiveKey",activityIndex);
  router.push('/activity-detail');
}
const viewPublisher = (publisherIndex) => {
  const detailKey = {userKey: publisherIndex, userType: 2}
  store.commit("setDetail",detailKey);
  router.push('/user-detail/profile');
}
const handleCurrentChange = (val) => {
  currentPage.value = val;
  const formData = new FormData();
  formData.append("province",queryList.province);
  formData.append("city",queryList.city);
  formData.append("classic",queryList.classic);
  formData.append("currentPage",currentPage.value);
  myAxios.post('/activity/queryByCondition',formData).then(response => {
    data.activityList = response.data.data.activityData.activityList;
    data.total = response.data.data.activityData.total;
  });
};
</script>

<style lang="scss" scoped>
.activities-container {
  width: 1250px;
  height: auto;
  margin: 30px auto;
}
:deep(.activities-container-header) {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: auto;

  .header-title-div {
    font-size: 18px;
    line-height: 30px;
    font-weight: bold;
    white-space: nowrap;
  }

  .header-classic-ul {
    display: flex;
    flex-wrap: wrap;

    li {
      list-style: none;
      display: inline-block;
      padding: 0 15px;
      height: 28px;
      font-size: 13px;
      border-radius: 20px 20px;
      line-height:28px;
      border: 1px solid transparent;
    }

    li:hover {
      background-color: #ffe0cd;
      border-color: #ffe0cd;
      cursor: pointer;
      color: #ed6a19;
    }

    li.is-active-classic {
      background-color: #ffe0cd;
      border-color: #ffe0cd;
      color: #ed6a19;
    }
  }

}
:deep(.activities-container-main) {
  padding: 0;

  .el-card.is-always-shadow {
    box-shadow: none;
  }

  .activity-card {
    margin-bottom: 15px;

    .el-card__body {
      display: flex;
      flex-direction: row;

      .card-left {
        padding: 10px 5px 10px 10px;

        .el-image {
          width: 180px;
          height: 120px;
        }
      }

      .card-center {
        display: flex;
        flex-direction: column;
        padding: 15px;
        text-align: left;
        width: 350px;

        .activity-title {
          font-size: 18px;
          font-weight: bold;
          margin-bottom: 10px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .activity-title:hover,.activity-publisher span:hover {
          color: #da4453;
          cursor: pointer;
        }

        .activity-place,.activity-publisher,.activity-publicTime {
          font-size: 14px;
          color: #626060;
          line-height: 25px;
        }
      }

      .card-right {
        display: flex;
        padding: 25px 25px 25px 70px;

        .activity-option-div {
          border: 1px solid #f2f1f1;
          width: 65px;
          height: 90px;

          .el-icon {
            margin-top: 10px;
          }

          .activity-option-des {
            font-size: 14px;
            margin-top: 5px;
          }
        }
      }
    }
  }
}
</style>