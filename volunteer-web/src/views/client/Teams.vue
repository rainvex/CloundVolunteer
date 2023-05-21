<template>
  <el-container class="teams-container">
    <el-header class="teams-container-header">
      <div style="display:flex;margin-bottom: 20px;">
        <div class="header-title-div">所在地区：</div>
        <ul class="header-classic-ul">
          <li v-for="(item, index) in areaList" :key="index"  @click="teamByProvince(index)" :class="{'is-active-classic':index === currentProvinceIndex}">{{ item.label }}</li>
        </ul>
      </div>
      <div style="margin: 0 30px 20px 100px;background-color: #eaeaea;border-radius: 10px;padding: 10px;display: none;" id="teamByCity">
        <ul class="header-classic-ul">
          <li v-for="(item1, index) in areaList[currentProvinceIndex].children" @click="teamByCity(currentProvinceIndex, index)" :key="index" :class="{'is-active-classic':index === currentCityIndex}">{{ item1.label }}</li>
        </ul>
      </div>
      <div style="display:flex;margin-bottom: 20px;">
        <div class="header-title-div">团队分类：</div>
        <ul class="header-classic-ul">
          <li v-for="(item, index) in classicList" :key="index" @click="teamByClassic(index)"
              :class="{'is-active-classic':index === currentClassicIndex}">{{ item }}
          </li>
        </ul>
      </div>
      <div style="display:flex;margin-bottom: 20px;">
        <div class="header-title-div">服务对象：</div>
        <ul class="header-classic-ul">
          <li v-for="(item, index) in serviceObjList" :key="index" @click="teamByServiceObj(index)"
              :class="{'is-active-classic':index === currentServiceIndex}">{{ item }}
          </li>
        </ul>
      </div>
    </el-header>
    <el-main class="teams-container-main">
      <el-row>
        <el-col :span="4" v-if="data.teamList.length !== 0" v-for="list in data.teamList" :key="list">
          <el-card :body-style="{ padding: '0px' }" class="team-card" shadow="hover">
            <div class="card-top">
              <el-image :src="list.teamAvatar" fit="cover" alt="加载失败"/>
            </div>
            <div class="card-bottom">
              <span class="team-name" @click="viewTeamProfile(list.teamId)" :title="list.teamName">{{ list.teamName }}</span>
              <div class="team-members">
                志愿者总数：<span>{{ list.teamCount }}</span>人
              </div>
              <div class="team-hours">
                志愿总时长：<span>{{ list.teamHours }}</span>小时
              </div>
              <div class="team-place" :title="list.teamAddress">
                所在地：<span>{{ list.teamAddress }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="data.teamList.length === 0">
          <el-empty description="暂无相应数据"/>
        </el-col>
      </el-row>
      <el-pagination small v-if="data.teamList.length !== 0" background layout="prev, pager, next" :total="data.total" :current-page="currentPage" @current-change="handleCurrentChange" :page-size="12" style="margin-top: 20px;padding: 0"/>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref, watch } from 'vue';
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { provinceAndCityDataPlus } from "element-china-area-data";
import myAxios from "../../utils/axios";

const router = useRouter();
const store = useStore();
const currentPage = ref(1);
const currentClassicIndex = ref(0);
const currentServiceIndex = ref(0);
const currentProvinceIndex = ref(0);
const currentCityIndex = ref(0);
const areaList = provinceAndCityDataPlus;
const classicList = [
  '全部',
  '党政机关',
  '教育事业单位',
  '卫生事业单位',
  '科技事业单位',
  '文化事业单位',
  '社会福利事业单位',
  '居民/村民委员会',
  '社会团体',
  '社会服务机构',
  '基金会',
  '其他'
];
const serviceObjList = [
  '全部',
  '儿童',
  '妇女',
  '老年人',
  '青少年',
  '婴幼儿',
  '孤寡老人',
  '残障人士',
  '病患者',
  '优抚对象',
  '贫困家庭',
  '特殊群体',
  '农村居民',
  '城镇居民',
  '社会公众',
  '其他'
];
const queryList = reactive({
  province: '',
  city: '',
  classic: '',
  serviceObj: ''
})
//请求后端数据
const data = reactive({
  total: 0,
  teamList: []
})
watch(
    queryList,
    (newValue) => {
      currentPage.value = 1;
      const formData = new FormData();
      formData.append("province",newValue.province);
      formData.append("city",newValue.city);
      formData.append("classic",newValue.classic);
      formData.append("serviceObj",newValue.serviceObj);
      formData.append("currentPage",currentPage.value);
      myAxios.post('/team/queryByCondition',formData).then(response => {
        data.teamList = response.data.data.teamData.teamList;
        data.total = response.data.data.teamData.total;
      });
    },
    {
      deep:true, // 深度监听
      immediate:true // 立即执行
    }
);
const teamByProvince = (index) => {
  currentProvinceIndex.value = index;
  currentCityIndex.value = 0;
  index !== 0 ? document.querySelector('#teamByCity').style.cssText += 'display: block' : document.querySelector('#teamByCity').style.cssText += 'display: none';
  if (areaList[index].label === '全部') {
    queryList.province = '';
    queryList.city = '';
  } else {
    queryList.province = areaList[index].label;
  }
};
const teamByCity = (provinceIndex, index) => {
  currentCityIndex.value = index;
  areaList[provinceIndex].children[index].label === '全部' ?  queryList.city = '' : queryList.city = areaList[provinceIndex].children[index].label;
};
const teamByClassic = (index) => {
  currentClassicIndex.value = index;
  classicList[index] === '全部' ? queryList.classic = '' : queryList.classic = classicList[index];
};
const teamByServiceObj = (index) => {
  currentServiceIndex.value = index;
  serviceObjList[index] === '全部' ? queryList.serviceObj = '' : queryList.serviceObj = serviceObjList[index];
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  const formData = new FormData();
  formData.append("province",queryList.province);
  formData.append("city",queryList.city);
  formData.append("classic",queryList.classic);
  formData.append("serviceObj",queryList.serviceObj);
  formData.append("currentPage",currentPage.value);
  myAxios.post('/team/queryByCondition',formData).then(response => {
    data.teamList = response.data.data.teamData.teamList;
    data.total = response.data.data.teamData.total;
  });
};
const viewTeamProfile = (teamIndex) => {
  const detailKey = {userKey: teamIndex,userType: 1}
  store.commit("setDetail",detailKey);
  router.push('/user-detail/profile');
}
</script>

<style lang="scss" scoped>
.teams-container {
  width: 1250px;
  height: auto;
  margin: 30px auto;
}

:deep(.teams-container-header) {
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

:deep(.teams-container-main) {
  padding: 0;

  .el-card.is-always-shadow {
    box-shadow: none;
  }

  .team-card {
    margin: 0 5px 15px 5px;

    .el-card__body {
      display: flex;
      flex-direction: column;

      .card-top {
        .el-image {
          width: 100%;
          height: 150px;
        }
      }

      .card-bottom {
        display: flex;
        flex-direction: column;
        padding: 10px;
        text-align: left;

        .team-name {
          font-size: 16px;
          font-weight: bold;
          margin-bottom: 10px;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }

        .team-name:hover {
          color: #da4453;
          cursor: pointer;
        }

        .team-place, .team-members, .team-hours {
          font-size: 14px;
          color: #626060;
          line-height: 25px;
        }

        .team-place {
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }

      }
    }
  }
}
</style>