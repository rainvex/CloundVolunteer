<template>
  <el-container class="volunteers-container">
    <div style="display:flex;margin-bottom: 20px;">
      <div class="header-title-div">活动地区：</div>
      <ul class="header-classic-ul">
        <li v-for="(item, index) in areaList" :key="index"  @click="volunteerByProvince(index)" :class="{'is-active-classic':index === currentProvinceIndex}">{{ item.label }}</li>
      </ul>
    </div>
    <div style="margin: 0 30px 20px 100px;background-color: #eaeaea;border-radius: 10px;padding: 10px;display: none;" id="volunteerByCity">
      <ul class="header-classic-ul">
        <li v-for="(item1, index) in areaList[currentProvinceIndex].children" @click="volunteerByCity(currentProvinceIndex, index)" :key="index" :class="{'is-active-classic':index === currentCityIndex}">{{ item1.label }}</li>
      </ul>
    </div>
    <el-main class="volunteers-container-main">
      <el-row>
        <el-col :span="6" v-if="data.volunteerList.length !== 0" v-for="list in data.volunteerList" :key="list">
          <el-card :body-style="{ padding: '0px' }" class="volunteer-card" shadow="hover" >
            <div class="card-left">
              <el-image :src="list.volunteersAvatar" fit="cover" alt="加载失败"/>
            </div>
            <div class="card-right">
              <span class="volunteer-name" @click="toVolunteerProfile(list.volunteersId)" :title="list.volunteersUsername">{{ list.volunteersUsername }}</span>
              <div class="volunteer-hours">
                志愿时长：<span>{{ list.volunteersHours }}</span>小时
              </div>
              <div class="volunteer-place" :title="list.volunteersAddress">
                所在地：<span>{{ list.volunteersAddress }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="data.volunteerList.length === 0">
          <el-empty description="暂无相应数据"/>
        </el-col>
      </el-row>
      <el-pagination small v-if="data.volunteerList.length !== 0" background layout="prev, pager, next" :total="data.total" :page-size="16" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px;padding: 0"/>
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from "vue-router";
import { reactive, ref, watch } from "vue";
import { useStore } from "vuex";
import myAxios from "../../utils/axios";
import { provinceAndCityDataPlus } from "element-china-area-data";

const router = useRouter();
const store = useStore();
const currentPage = ref(1);
const currentProvinceIndex = ref(0);
const currentCityIndex = ref(0);
const areaList = provinceAndCityDataPlus;
const queryList = reactive({
  province: '',
  city: '',
})
//后端数据
let data = reactive({
  total: 0,
  volunteerList: []
})
watch(
    queryList,
    (newValue) => {
      currentPage.value = 1;
      const formData = new FormData();
      formData.append("province",newValue.province);
      formData.append("city",newValue.city);
      formData.append("currentPage",currentPage.value);
      myAxios.post('/volunteer/queryByCondition',formData).then(response => {
        data.volunteerList = response.data.data.volunteerData.volunteerList;
        data.total = response.data.data.volunteerData.total;
      })
    },
    {
      deep:true, // 深度监听
      immediate:true // 立即执行
    }
);
const volunteerByProvince = (index) => {
  currentProvinceIndex.value = index;
  currentCityIndex.value = 0;
  index !== 0 ? document.querySelector('#volunteerByCity').style.cssText += 'display: block' : document.querySelector('#volunteerByCity').style.cssText += 'display: none';
  if (areaList[index].label === '全部') {
    queryList.province = '';
    queryList.city = '';
  } else {
    queryList.province = areaList[index].label;
  }
};
const volunteerByCity = (provinceIndex, index) => {
  currentCityIndex.value = index;
  areaList[provinceIndex].children[index].label === '全部' ?  queryList.city = '' : queryList.city = areaList[provinceIndex].children[index].label;
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  const formData = new FormData();
  formData.append("province",queryList.province);
  formData.append("city",queryList.city);
  formData.append("currentPage",currentPage.value);
  myAxios.post('/volunteer/queryByCondition',formData).then(response => {
    data.volunteerList = response.data.data.volunteerData.volunteerList;
    data.total = response.data.data.volunteerData.total;
  })
};
const toVolunteerProfile = (volunteerIndex) => {
  const detailKey = {userKey: volunteerIndex,userType:0};
  store.commit('setDetail',detailKey);
  router.push('/user-detail/profile');
}
</script>

<style lang="scss" scoped>
.volunteers-container {
  width: 1250px;
  height: auto;
  margin: 30px auto;
  flex-direction: column;

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
:deep(.volunteers-container-main) {
  padding: 0;
  margin-top: 20px;

  .el-card.is-always-shadow {
    box-shadow: none;
  }

  .volunteer-card {
    margin: 0 5px 15px 5px;

    .el-card__body {
      display: flex;

      .card-left {
        .el-image {
          width: 100px;
          height: 100px;
        }
      }

      .card-right {
        display: flex;
        flex-direction: column;
        padding: 10px;
        text-align: left;

        .volunteer-name {
          font-size: 16px;
          font-weight: bold;
          margin-bottom: 5px;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }

        .volunteer-name:hover {
          color: #da4453;
          cursor: pointer;
        }

        .volunteer-place,.volunteer-hours {
          font-size: 14px;
          color: #626060;
          line-height: 25px;
        }

        .volunteer-place {
          width: 190px;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }

      }
    }
  }
}
</style>