<template>
  <el-container style="width: 100%;min-width: 1250px;height: auto;margin: 0 auto;flex-direction: column;">
    <!--客户端主页上半部分内容-->
    <!--轮播图-->
    <div class="block text-center">
      <el-carousel height="350px">
        <el-carousel-item v-for="item in carouselArray" :key="item">
          <el-image class="small justify-center" :src="item" fit="cover"></el-image>
        </el-carousel-item>
      </el-carousel>
    </div>
    <!--志愿活动分类部分-->
    <div class="classic-data">
      <!--左边-->
      <div class="classic-data-left">
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[0]" fit="cover" />
          <span class="classic-title">文明实践</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[1]" fit="cover" />
          <span class="classic-title">青少年服务</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[2]" fit="cover" />
          <span class="classic-title">治安防控</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[3]" fit="cover" />
          <span class="classic-title">敬老助残</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[4]" fit="cover" />
          <span class="classic-title">扶贫帮困</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[5]" fit="cover" />
          <span class="classic-title">公共文明</span>
        </div>
      </div>
      <!--中间-->
      <div class="classic-data-center">
        <div id="volunteer-num">
          <span class="statistics-title">已注册志愿者数量（人）</span>
          <span class="statistics-number">{{ data.indexData.volunteerCount }}</span>
        </div>
        <div id="volunteer-hours">
          <span class="statistics-title">全站志愿时长（小时）</span>
          <span class="statistics-number">{{ data.indexData.totalHours }}</span>
        </div>
        <div id="volunteer-team-num">
          <span class="statistics-title">已注册志愿者团队数量（个）</span>
          <span class="statistics-number">{{ data.indexData.teamCount }}</span>
        </div>
        <div id="volunteer-unit-num">
          <span class="statistics-title">已注册志愿单位数量（个）</span>
          <span class="statistics-number">{{ data.indexData.unitCount }}</span>
        </div>
      </div>
      <!--右边-->
      <div class="classic-data-right">
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[6]" fit="cover" />
          <span class="classic-title">志愿防疫</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[7]" fit="cover" />
          <span class="classic-title">环境保护</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[8]" fit="cover" />
          <span class="classic-title">企业服务</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[9]" fit="cover" />
          <span class="classic-title">便民服务</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[10]" fit="cover" />
          <span class="classic-title">青春情暖</span>
        </div>
        <div class="classic-box" @click="goToActivityByClassic">
          <el-image class="classic-image" :src="logoArray[11]" fit="cover" />
          <span class="classic-title">其他</span>
        </div>
      </div>
    </div>
  </el-container>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();
const carouselArray = [
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/carousel1.jpg',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/carousel2.jpg',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/carousel3.jpg',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/carousel4.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/carousel5.jpg',
];
const logoArray = [
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/pratice.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/teenagers.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/security.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/respect.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/proverty.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/public.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/epidemic.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/environment.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/enterprise.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/convenience.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/youth.png',
  'https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/other.png'

];
const data = defineProps({
  indexData: {
    type: Object,
    default: null
  }
});

const goToActivityByClassic = (event) => {
  router.push({
    name: 'Activities',
    params: {
      classic: event.currentTarget.lastChild.innerText
    }
  })
}
</script>

<style scoped>
.el-carousel__item,.el-image,.el-image__inner {
  width: 100%;
  height: 100%;
}
.classic-data {
  width: 100%;
  background-color: #ededed;
  height: 350px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.classic-data-left {
  display: flex;
  flex-direction: row-reverse;
  flex-wrap: wrap;
  width: 400px;
}
.classic-data-right {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  width: 400px;
}
.classic-box {
  display: flex;
  flex-direction: column;
  margin: 30px;
}
.classic-box:hover {
  cursor: pointer;
}
.classic-image {
  width: 60px;
  height: 60px;
}
.classic-title {
  font-size: 13px;
}
.classic-data-center {
  height: 100%;
  width: 300px;
  background-color: #da4453;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #ffffff;
  font-size: 14px;
}
.classic-data-center #volunteer-num {
  margin-top: 30px;
}
.classic-data-center #volunteer-unit-num {
  margin-bottom: 40px;
}
.statistics-title {
  display: block;
  margin-bottom: 10px;
}
.statistics-number {
  margin-top: 10px;
  font-size: 26px;
  font-weight: bold;
  background-color: #f8af3f;
  padding: 3px;
}
</style>