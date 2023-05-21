<template>
  <div class="epidemic-map">
    <h1 style="color: #da4453;margin-bottom: 20px">全国疫情实况</h1>
    <el-row style="margin-top: 80px">
      <el-col :span="16">
        <div ref="chinaMap" style="width: 100%; height: 500px"></div>
      </el-col>
      <el-col :span="8">
        <div style="margin: 0 auto; padding: 20px; box-shadow: 0 5px 15px -5px rgba(0, 0, 0, 0.34); border-radius: 10px;">
          <div style="text-align: center; margin-bottom: 20px; font-size: 18px">
            <b>全国疫情实时数据</b>
          </div>
          <div style="display: flex; flex-wrap:wrap; justify-content: flex-start;" >
            <div class="item" style="background-color: lightblue">
              <div class="item_title">境外输入</div>
              <div class="item_num" style="color: RoyalBlue">{{ epidemicData.total.input ? epidemicData.total.input : '未更新' }}</div>
              <div class="item_num item_num_plus">较昨日 <span style="color: RoyalBlue">+{{ epidemicData.today.input }}</span></div>
            </div>
            <div class="item" style="background-color: aliceblue">
              <div class="item_title">无症状感染者</div>
              <div class="item_num" style="color: LightCoral">{{ epidemicData.extData.noSymptom ? epidemicData.extData.noSymptom : '未更新' }}</div>
              <div class="item_num item_num_plus">较昨日 <span style="color: LightCoral">+{{ epidemicData.extData.incrNoSymptom }}</span></div>
            </div>
            <div class="item" style="background-color: lightgoldenrodyellow">
              <div class="item_title">现有确诊</div>
              <div class="item_num" style="color: OrangeRed">{{ ( epidemicData.total.confirm - epidemicData.total.dead - epidemicData.total.heal ) ? ( epidemicData.total.confirm - epidemicData.total.dead - epidemicData.total.heal ) : '未更新' }}</div>
              <div class="item_num item_num_plus">较昨日 <span style="color: OrangeRed">+{{ epidemicData.today.confirm - epidemicData.today.dead - epidemicData.today.heal ? epidemicData.today.confirm - epidemicData.today.dead - epidemicData.today.heal : '' }}</span></div>
            </div>
            <div class="item" style="background-color: lightgray">
              <div class="item_title">累计确诊</div>
              <div class="item_num" style="color: Brown">{{ epidemicData.total.confirm ? epidemicData.total.confirm : '未更新' }}</div>
              <div class="item_num item_num_plus">较昨日 <span style="color: Brown">+{{ epidemicData.today.confirm }}</span></div>
            </div>
            <div class="item" style="background-color: lightpink">
              <div class="item_title">累计死亡</div>
              <div class="item_num" style="color: #333">{{ epidemicData.total.dead ? epidemicData.total.dead : '未更新' }}</div>
              <div class="item_num item_num_plus">较昨日 <span style="color: #333">+{{ epidemicData.today.dead }}</span></div>
            </div>
            <div class="item" style="background-color: lightgreen">
              <div class="item_title">累计治愈</div>
              <div class="item_num" style="color: green">{{ epidemicData.total.heal ? epidemicData.total.heal : '未更新' }}</div>
              <div class="item_num item_num_plus">较昨日 <span style="color: green">+{{ epidemicData.today.heal }}</span></div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import * as echarts from 'echarts';
import china from '../../../utils/china.json';
import myAxios from "../../../utils/axios";
import { onMounted, reactive, ref} from "vue";

const chinaMap = ref(null);
const epidemicData = reactive({
  total: '',
  today: '',
  extData: ''
});
const options = reactive({
  //标题样式
  title: {
    text: '中国疫情地图',
    x: "40%",
    y: '85%',
    textStyle: {
      fontSize: 20,
      color: "#333"
    },
  },
  series: [
    {
      name: '中国疫情地图',
      type: 'map',
      map: 'china',
      label: {
        show: true, //是否显示标签
        color: "black"
      },
      emphasis: { //是图形在高亮状态下的样式,比如在鼠标悬浮或者图例联动高亮时
        label: {show: true}
      },
      top: '0',
      left: '20%',
      data: []
    }
  ],
  //这里设置提示框 (鼠标悬浮效果)
  tooltip: {
    //数据项图形触发
    trigger: 'item',
    //提示框浮层的背景颜色。 (鼠标悬浮后的提示框背景颜色)
    backgroundColor: "white",
    showDelay: 0,
    transitionDuration: 0.2,
    //字符串模板(地图): {a}（系列名称），{b}（区域名称），{c}（合并数值）,{d}（无）
    formatter(data) {
      return  '地区：' + data.name + '<br/>' + '现有确诊病例：' + data.value;
    }
  },
  //视觉映射组件
  visualMap: {
    type: 'piecewise',
    top: '55%',
    left: '10%',
    // 数据的范围
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
onMounted(() => {
  // 获取数据
  myAxios.get("/echarts/epidemic").then(response => {
    epidemicData.total = response.data.data.chinaTotal.total;
    epidemicData.today = response.data.data.chinaTotal.today;
    epidemicData.extData = response.data.data.chinaTotal.extData;
    let data = [{name: '南海诸岛', value: 0}];
    response.data.data.areaTree[2].children.forEach(item => {
      data.push({name: item.name, value: (item.total.confirm - item.total.dead - item.total.heal)});
    });
    options.series[0].data = data;
    const myCharts = echarts.init(chinaMap.value);
    echarts.registerMap('china',china);
    myCharts.setOption(options);
  });
})
</script>

<style lang="scss" scoped>
.epidemic-map {
  width: 1250px;
  height: auto;
  margin: 40px auto;

  .item {
    text-align: center;
    width: 120px;
    height: 120px;
    margin-right: 5px;
    margin-bottom: 5px;

    .item_title {
      font-size: 15px;
      font-weight: bold;
      margin-top: 15px;
    }
    .item_num {
      margin-top: 10px;
      font-size: 24px;
      font-weight: bold;
    }
    .item_num_plus {
      font-size: 15px;
      color: #666;
    }
  }
  .item:nth-child(3n) {
    margin-right: 0;
  }
}
</style>