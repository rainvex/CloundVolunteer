<template>
  <el-container style="width: 1100px;height: auto;margin: 30px auto;flex-direction: column;">
    <el-card style="margin-top: 20px;border: 0;box-shadow: none;width: 100%;min-height: 300px;">
      <h3 style="text-align: left;margin-bottom: 20px">志愿资讯<el-icon :size="25" color="#ff890c" style="vertical-align: bottom;"><Share /></el-icon></h3>
      <ul style="text-align: left;padding-left: 10px;font-weight: bold;font-size: 15px">
        <li v-for="(inform, index) in informData.informList" :key="index" style="line-height: 45px;display: flex;justify-content: space-between;">
          <span @click="viewInformDetail(inform.informId)" style="max-width: 850px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">{{ inform.informTitle }}</span>
          <span>{{ inform.informPublic }}</span>
        </li>
      </ul>
    </el-card>
    <el-pagination small v-if="informData.informList.length !== 0" background layout="prev, pager, next" :total="informData.informList.length" :page-size="15" :current-page="currentPage" @current-change="handleCurrentChange" style="margin-top: 20px;padding: 0"/>
  </el-container>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import { Share } from "@element-plus/icons";
import myAxios from "../../utils/axios";
import {ElMessage} from "element-plus";
import {useStore} from "vuex";
import {useRouter} from "vue-router";

const store = useStore();
const router = useRouter();
const currentPage = ref(1);
const informData = reactive({
  informList: []
});

const getInformData = () => {
  let data = new FormData();
  data.append("currentPage",currentPage.value);
  myAxios.post('/inform/getAllInform',data).then((response) => {
    if (response.data.code === 200) {
      informData.informList = response.data.data.informList;
    } else {
      ElMessage.error('获取数据失败');
    }
  })
};
onMounted(() => {
  getInformData();
})
const viewInformDetail = (informId) => {
  store.commit('setInformKey',informId);
  router.push('/inform-detail');
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getInformData();
}
</script>

<style scoped>
ul li::before {
  content: '';
  position: absolute;
  border-radius: 50%;
  margin-top: 20px;
  margin-left: -15px;
  width: 5px;
  height: 5px;
  background: #d41e1e;
}

ul li:hover {
  color: #f21e1e;
  cursor: pointer;
}
</style>