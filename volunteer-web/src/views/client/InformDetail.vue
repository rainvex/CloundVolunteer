<template>
  <el-container style="width: 1250px;height: auto;margin: 0 auto;padding-bottom: 50px">
    <el-container >
      <el-header style="margin: 50px auto 20px;">
        <h3 style="font-size: 22px;margin-bottom: 20px;">{{ informData.informInfo.informTitle }}</h3>
        <div style="display: flex;font-size: 13px">
          <span style="margin: 0 30px">发布时间：<span>{{ informData.informInfo.informPublic }}</span></span>
          <span>资讯来源：<span>{{ informData.informInfo.informFrom }}</span></span>
          <span style="margin-left: 30px">志愿资讯分享：<el-link :underline="false" type="primary"><el-icon :size="20"><Share /></el-icon></el-link></span>
        </div>
      </el-header>
      <el-main style="background-color: #ffffff">
        <Viewer class="informViewer" :value="informData.informInfo.informContent" :plugins="plugins"/>
      </el-main>
    </el-container>
    <el-aside style="margin-left: 20px;margin-top: 130px;">
      <el-card style="border: 0;box-shadow: none;">
        <h4 style="text-align: left;margin-bottom: 20px">热点资讯<el-icon :size="20" color="#ff890c" style="vertical-align: bottom;"><Share /></el-icon></h4>
        <ul style="text-align: left;padding-left: 10px;font-weight: bold;font-size: 15px">
          <li v-for="(inform, index) in informData.hotInformList" :key="index" style="line-height: 40px;display: flex;">
            <span @click="viewInformDetail(inform.informId)" style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">{{ inform.informTitle }}</span>
          </li>
        </ul>
      </el-card>
    </el-aside>
  </el-container>
</template>

<script setup>
import { Viewer } from '@bytemd/vue-next';
import 'bytemd/dist/index.min.css';
import breaks from '@bytemd/plugin-breaks';
import frontmatter from '@bytemd/plugin-frontmatter';
import gemoji from '@bytemd/plugin-gemoji';
import gfm from '@bytemd/plugin-gfm';
import highlight from '@bytemd/plugin-highlight';
import highlightSSR from '@bytemd/plugin-highlight-ssr';
import zoom from '@bytemd/plugin-medium-zoom';
import mermaid from '@bytemd/plugin-mermaid';
import gfmLocale from '@bytemd/plugin-gfm/locales/zh_Hans.json';
import mermaidLocale from '@bytemd/plugin-mermaid/locales/zh_Hans.json';
import {onMounted, reactive, watch} from "vue";
import {useStore} from "vuex";
import {useRouter} from "vue-router";
import myAxios from "../../utils/axios";
import {ElMessage} from "element-plus";
import { Share } from "@element-plus/icons";

const store = useStore();
const router = useRouter();
const plugins = [
  breaks(),
  frontmatter(),
  gemoji(),
  gfm({locale: gfmLocale}),
  highlight(),
  highlightSSR(),
  zoom(),
  mermaid({locale: mermaidLocale})
];
const informData = reactive({
  informInfo: '',
  hotInformList: []
});
const getInformById = () => {
  let data = new FormData();
  data.append("informId",store.state.informKey);
  myAxios.post('/inform/getInform',data).then((response) => {
    if (response.data.code === 200) {
      informData.informInfo = response.data.data.informInfo;
    } else {
      ElMessage.error('获取资讯详情出错');
    }
  })
};
const getHotInformList = () => {
  myAxios.get('/inform/getInformHighView').then((response) => {
    if (response.data.code === 200) {
      informData.hotInformList = response.data.data.highViewInform;
    }
  })
};
onMounted(() => {
  getInformById();
  getHotInformList();
});
const viewInformDetail = (informId) => {
  store.commit('setInformKey',informId);
  location.reload();
};
</script>

<style lang="scss" scoped>
:deep(.informViewer) {
  text-align:left;
  font-size: 16px;
  line-height: 32px;
  text-indent: 2em;

  p {
    margin-bottom: 12px;

    img {
      display: block;
      margin: auto;
    }
  }
}
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