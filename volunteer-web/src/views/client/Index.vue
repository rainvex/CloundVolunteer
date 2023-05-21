<template>
  <!--客户端主页-->
  <div class="index-container">
    <!--嵌套客户端主页上半部分内容-->
    <IndexHead :indexData="indexData"/>
    <!--嵌套客户端主页中间部分内容-->
    <IndexMain :indexData="indexData"/>
    <!--显示全国疫情地图信息-->
    <EpidemicMap/>
  </div>
</template>

<script setup>
import IndexHead from '../../components/client/index/Index-head.vue'
import IndexMain from '../../components/client/index/Index-main.vue'
import EpidemicMap from '../../components/client/index/EpidemicMap.vue'
import {onMounted, reactive} from "vue";
import myAxios from "../../utils/axios";
import { ElMessage } from "element-plus";

const indexData = reactive({
  volunteerCount: '',
  totalHours: '',
  teamCount: '',
  unitCount: '',
  activityInfoList: [],
  teamInfoList: [],
  unitInfoList: [],
  mostFiveVolunteerByHours: [],
  mostFiveTeamByHours: [],
  informList: []
})
const getIndexData = () => {
  myAxios.get('/base/index').then(response => {
    if (response.data.code === 200) {
      const data = response.data.data.indexData;
      indexData.volunteerCount = data.volunteerCount;
      indexData.totalHours = data.totalHours;
      indexData.mostFiveVolunteerByHours = data.mostFiveVolunteerByHours;
      indexData.teamCount = data.teamCount;
      indexData.mostFiveTeamByHours = data.mostFiveTeamByHours;
      indexData.unitCount = data.unitCount;
      indexData.activityInfoList = data.activityInfoList;
      indexData.teamInfoList = data.teamInfoList;
      indexData.unitInfoList = data.unitInfoList;
      indexData.informList = data.informList;
    } else {
      ElMessage.error('请求出错');
    }
  })
};
onMounted(() => {
  getIndexData()
})
</script>

<style scoped>

</style>