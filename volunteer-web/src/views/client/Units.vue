<template>
  <el-container class="units-container">
    <el-header class="units-container-header">
      <div style="display:flex;margin-bottom: 20px;">
        <div class="header-title-div">活动地区：</div>
        <ul class="header-classic-ul">
          <li v-for="(item, index) in areaList" :key="index"  @click="unitByProvince(index)" :class="{'is-active-classic':index === currentProvinceIndex}">{{ item.label }}</li>
        </ul>
      </div>
      <div style="margin: 0 30px 20px 100px;background-color: #eaeaea;border-radius: 10px;padding: 10px;display: none;" id="unitByCity">
        <ul class="header-classic-ul">
          <li v-for="(item1, index) in areaList[currentProvinceIndex].children" @click="unitByCity(currentProvinceIndex, index)" :key="index" :class="{'is-active-classic':index === currentCityIndex}">{{ item1.label }}</li>
        </ul>
      </div>
      <div style="display:flex;margin-bottom: 20px;">
        <div class="header-title-div">志愿单位分类：</div>
        <ul class="header-classic-ul">
          <li v-for="(item, index) in classicList" :key="index" @click="unitByClassic(index)"
              :class="{'is-active-classic':index === currentClassicIndex}">{{ item }}
          </li>
        </ul>
      </div>
    </el-header>
    <el-main class="units-container-main">
      <el-row>
        <el-col :span="4" v-if="data.unitList.length !== 0" v-for="list in data.unitList">
          <el-card :body-style="{ padding: '0px' }" class="unit-card" shadow="hover">
            <div class="card-top">
              <el-image :src="list.unitAvatar" fit="cover" alt="加载失败"/>
            </div>
            <div class="card-bottom">
              <span class="unit-name" @click="viewUnitProfile(list.unitId)" :title="list.unitName">{{ list.unitName }}</span>
              <div class="unit-activeNum">
                发布志愿活动数：<span>{{ list.unitPubliccount }}</span>个
              </div>
              <div class="unit-place" :title="list.unitAddress">
                所在地：<span>{{ list.unitAddress }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="data.unitList.length === 0">
          <el-empty description="暂无相应数据"/>
        </el-col>
      </el-row>
      <el-pagination small v-if="data.unitList.length !== 0" background layout="prev, pager, next" :total="data.total" :page-size="12" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px;padding: 0"/>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, reactive, watch } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { provinceAndCityDataPlus } from "element-china-area-data";
import myAxios from "../../utils/axios";

const router = useRouter();
const store = useStore();
const currentPage = ref(1);
const currentClassicIndex = ref(0);
const currentProvinceIndex = ref(0);
const currentCityIndex = ref(0);
//地区列表
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
const queryList = reactive({
  province: '',
  city: '',
  classic: ''
});
//获取后端数据
let data = reactive({
  total: 0,
  unitList: []
})
watch(
    queryList,
    (newValue) => {
      currentPage.value = 1;
      const formData = new FormData();
      formData.append("province",newValue.province);
      formData.append("city",newValue.city);
      formData.append("classic",newValue.classic);
      formData.append("currentPage",currentPage.value);
      myAxios.post('/unit/queryByCondition',formData).then(response => {
        data.unitList = response.data.data.unitData.unitList;
        data.total = response.data.data.unitData.total;
      });
    },
    {
      deep:true, // 深度监听
      immediate:true // 立即执行
    }
);
const unitByProvince = (index) => {
  currentProvinceIndex.value = index;
  currentCityIndex.value = 0;
  index !== 0 ? document.querySelector('#unitByCity').style.cssText += 'display: block' : document.querySelector('#unitByCity').style.cssText += 'display: none';
  if (areaList[index].label === '全部') {
    queryList.province = '';
    queryList.city = '';
  } else {
    queryList.province = areaList[index].label;
  }
};
const unitByCity = (provinceIndex, index) => {
  currentCityIndex.value = index;
  areaList[provinceIndex].children[index].label === '全部' ?  queryList.city = '' : queryList.city = areaList[provinceIndex].children[index].label;
};
const unitByClassic = (index) => {
  currentClassicIndex.value = index;
  classicList[index] === '全部' ? queryList.classic = '' : queryList.classic = classicList[index];
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  const formData = new FormData();
  formData.append("province",queryList.province);
  formData.append("city",queryList.city);
  formData.append("classic",queryList.classic);
  formData.append("currentPage",currentPage.value);
  myAxios.post('/unit/queryByCondition',formData).then(response => {
    data.unitList = response.data.data.unitData.unitList;
    data.total = response.data.data.unitData.total;
  });
};
const viewUnitProfile = (unitIndex) => {
  const detailKey = {userKey: unitIndex,userType: 2}
  store.commit("setDetail",detailKey);
  router.push('/user-detail/profile');
}
</script>

<style lang="scss" scoped>
.units-container {
  width: 1250px;
  height: auto;
  margin: 30px auto;
}

:deep(.units-container-header) {
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

:deep(.units-container-main) {
  padding: 0;

  .el-card.is-always-shadow {
    box-shadow: none;
  }

  .unit-card {
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

        .unit-name {
          font-size: 17px;
          font-weight: bold;
          margin-bottom: 10px;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }

        .unit-name:hover {
          color: #da4453;
          cursor: pointer;
        }

        .unit-place, .unit-activeNum {
          font-size: 14px;
          color: #626060;
          line-height: 25px;
        }

        .unit-place {
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }
      }
    }
  }
}
</style>