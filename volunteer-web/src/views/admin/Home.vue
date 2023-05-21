<template>
  <el-container style="width: 100%;height: 100%">
    <el-container>
      <el-header height="260px" style="display: flex;padding: 50px;justify-content: space-around;">
        <div class="item" style="background-color: #5470c6;">
          <div class="item-title">已注册志愿者（人）</div>
          <div class="item-data" style="color: #75cc4b;">{{ homeData.volunteerCount }}</div>
        </div>
        <div class="item" style="background-color: #91cc75;">
          <div class="item-title">已注册志愿者团队（个）</div>
          <div class="item-data" style="color: #5a7fcc;">{{ homeData.teamCount }}</div>
        </div>
        <div class="item" style="background-color: #fac858;">
          <div class="item-title">已注册志愿单位（个）</div>
          <div class="item-data" style="color: #c54efa;">{{ homeData.unitCount }}</div>
        </div>
        <div class="item" style="background-color: #ee6666;">
          <div class="item-title">已发布志愿活动（个）</div>
          <div class="item-data" style="color: #5d5dee;">{{ homeData.activityCount }}</div>
        </div>
        <div class="item" style="background-color: #72bfdf;">
          <div class="item-title">志愿总时长（小时）</div>
          <div class="item-data" style="color: #df59b8;">{{ homeData.volunteerHours }}</div></div>
      </el-header>
      <el-container>
        <el-main>
          <div id="systemDataStatistics" style="width: 100%;height:500px;"></div>
        </el-main>
        <el-aside width="45%" style="padding: 20px;">
          <div id="userRatioStatistics" style="width: 100%;height: 50%"></div>
          <div id="activityClassicRatioStatistics" style="width: 100%;height: 50%"></div>
        </el-aside>
      </el-container>
    </el-container>
  </el-container>
</template>

<script setup>
import * as echarts from 'echarts';
import china from '../../utils/china.json';
import myAxios from "../../utils/axios";
import {onBeforeUnmount, onMounted, reactive} from "vue";
import {ElMessage} from "element-plus";

const homeData = reactive({
  volunteerCount: 0,
  teamCount: 0,
  unitCount: 0,
  activityCount: 0,
  volunteerHours: 0,
  activityClassicCountList: []
});
const provinceVolunteerOptions = reactive({
  title: {
    text: '系统已注册志愿者分布',
    x: "45%",
    y: '95%',
    textStyle: {
      fontSize: 20,
      color: "#333"
    },
  },
  series: [
    {
      name: '系统已注册志愿者分布',
      type: 'map',
      map: 'china',
      label: {
        show: true,
        color: "black"
      },
      emphasis: {
        label: {show: true}
      },
      top: '10%',
      left: '20%',
      data: []
    }
  ],
  tooltip: {
    trigger: 'item',
    backgroundColor: "white",
    showDelay: 0,
    transitionDuration: 0.2,
    //字符串模板(地图): {a}（系列名称），{b}（区域名称），{c}（合并数值）,{d}（无）
    formatter(data) {
      return  '地区：' + data.name + '<br/>' + '已注册志愿者数量：' + data.value;
    }
  },
  visualMap: {
    type: 'piecewise',
    top: '65%',
    left: '10%',
    pieces: [
      {max: 0, label: '0', color: '#ffffff'},
      {min: 1, max: 9, label: '1-9人', color: '#fff2cf'},
      {min: 10, max: 99, label: '10-99人', color: '#fdd2a0'},
      {min: 100, max: 499, label: '100-499人', color: '#ff8c71'},
      {min: 500, max: 999, label: '500-999人', color: '#e64b45'},
      {min: 1000, max: 9999, label: '1000-9999人', color: '#bd1316'},
      {min: 10000, label: '≥10000人', color: '#7f1100'},
    ]
  }
});
const userRatioOptions = reactive({
  title: {
    text: '用户类型比例统计',
    left: 'center'
  },
  series: [
    {
      name: '用户比例',
      type: 'pie',
      radius: '50%',
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      data: []
    }
  ],
  tooltip: {
    trigger: 'item',
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  }
});
const activityClassicRatioOptions = reactive({
  title: {
    text: '志愿活动分类比例统计',
    left: 'center'
  },
  series: [
    {
      name: '志愿活动分类比例',
      type: 'pie',
      radius: '50%',
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      data: []
    }
  ],
  tooltip: {
    trigger: 'item',
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  }
});
const renderHomeData = () => {
  myAxios.get('/admin/home').then((response) => {
    if (response.data.code === 200) {
      homeData.volunteerCount = response.data.data.homeData.volunteerCount;
      homeData.teamCount = response.data.data.homeData.teamCount;
      homeData.unitCount = response.data.data.homeData.unitCount;
      homeData.activityCount = response.data.data.homeData.activityCount;
      homeData.volunteerHours = response.data.data.homeData.volunteerHours;
      homeData.activityClassicCountList = response.data.data.homeData.activityClassicCountList;
      //渲染系统已注册志愿者所在省份
      let provinceVolunteerCountListData = [];
      provinceVolunteerCountListData.push()
      response.data.data.homeData.provinceVolunteerCountList.forEach(item => {
        provinceVolunteerCountListData.push({name: item.provinceName, value: item.volunteerCount});
      });
      provinceVolunteerOptions.series[0].data = provinceVolunteerCountListData;
      const systemDataStatisticsChart = echarts.init(document.querySelector('#systemDataStatistics'));
      echarts.registerMap('china',china);
      systemDataStatisticsChart.setOption(provinceVolunteerOptions);
      //渲染用户比例饼状图
      let userRatioData = [];
      userRatioData.push({name: '志愿者', value: response.data.data.homeData.volunteerCount});
      userRatioData.push({name: '志愿者团队', value: response.data.data.homeData.teamCount});
      userRatioData.push({name: '志愿单位', value: response.data.data.homeData.unitCount});
      userRatioOptions.series[0].data = userRatioData;
      const userRatioStatisticsChart = echarts.init(document.querySelector('#userRatioStatistics'));
      userRatioStatisticsChart.setOption(userRatioOptions);
      //渲染志愿活动分类比例饼状图
      let activityClassicRatioData = [];
      response.data.data.homeData.activityClassicCountList.forEach((activityClassic) => {
        activityClassicRatioData.push({name: activityClassic.activityClassic, value: activityClassic.classicCount});
      });
      activityClassicRatioOptions.series[0].data = activityClassicRatioData;
      const activityClassicStatisticsChart = echarts.init(document.querySelector('#activityClassicRatioStatistics'));
      activityClassicStatisticsChart.setOption(activityClassicRatioOptions);
    } else {
      ElMessage.error('获取数据出错');
    }
  })
};
onMounted(() => {
  renderHomeData();
});
onBeforeUnmount(() => {
  echarts.dispose(document.querySelector('#systemDataStatistics'));
  echarts.dispose(document.querySelector('#userRatioStatistics'));
  echarts.dispose(document.querySelector('#activityClassicRatioStatistics'));
});
</script>

<style scoped>
.item {
  width: 230px;
  height: 140px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.item-title {
 color: #ffffff;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
}
.item-data {
  font-size: 34px;
  font-weight: bold;
}
</style>