<template>
  <el-container style="width: 100%;height: 100%">
    <el-container style="flex-direction: column;padding: 60px 40px;">
      <h3 style="text-align: left;margin-bottom: 10px;">资讯标题</h3>
      <el-input v-model="informInfo.informTitle" placeholder="请输入资讯标题" maxlength="60" show-word-limit style="width:50%;margin-bottom: 10px;"/>
      <h3 style="text-align: left;margin-bottom: 10px;">资讯来源</h3>
      <el-input v-model="informInfo.informFrom" placeholder="请输入资讯来源" maxlength="30" show-word-limit style="width:50%;margin-bottom: 10px;"/>
      <h3 style="text-align: left;margin-bottom: 10px;">资讯内容</h3>
      <Editor class="byteEditor" :value="informInfo.informContent" :plugins="plugins" :locale="zhHans" :uploadImages="uploadImage" placeholder="请输入资讯内容" @change="handleEditorChange"/>
      <p style="text-align: right"><el-button type="primary" style="width: 100px;margin-top: 15px" @click="publicInform">发布/修改</el-button></p>
    </el-container>
  </el-container>
</template>

<script setup>
import { Editor } from '@bytemd/vue-next';
import 'bytemd/dist/index.min.css';
import breaks from '@bytemd/plugin-breaks';
import frontmatter from '@bytemd/plugin-frontmatter';
import gemoji from '@bytemd/plugin-gemoji';
import gfm from '@bytemd/plugin-gfm';
import highlight from '@bytemd/plugin-highlight';
import highlightSSR from '@bytemd/plugin-highlight-ssr';
import zoom from '@bytemd/plugin-medium-zoom';
import mermaid from '@bytemd/plugin-mermaid';
import zhHans from "bytemd/locales/zh_Hans.json";
import gfmLocale from '@bytemd/plugin-gfm/locales/zh_Hans.json';
import mermaidLocale from '@bytemd/plugin-mermaid/locales/zh_Hans.json';
import {onMounted, reactive} from "vue";
import myAxios from "../../utils/axios";
import {ElMessage} from "element-plus";
import {useRoute, useRouter} from "vue-router";

const router = useRouter();
const route = useRoute();
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
const informInfo = reactive({
  informId: 0,
  informTitle: '',
  informPublic: '',
  informContent: '',
  informFrom: '',
  informView: 0,
  informStatus: 1
});

const handleEditorChange = (val) => {
  informInfo.informContent = val;
};
const validatePublicOrUpdate = () => {
  if (route.query.informId) {
    let data = new FormData();
    data.append("informId",route.query.informId);
    myAxios.post('/inform/getInform',data).then((response) => {
      if (response.data.code === 200) {
        informInfo.informId = response.data.data.informInfo.informId;
        informInfo.informTitle = response.data.data.informInfo.informTitle;
        informInfo.informPublic = response.data.data.informInfo.informPublic;
        informInfo.informContent = response.data.data.informInfo.informContent;
        informInfo.informFrom = response.data.data.informInfo.informFrom;
        informInfo.informView = response.data.data.informInfo.informView;
        informInfo.informStatus = response.data.data.informInfo.informStatus;
      } else {
        ElMessage.error('获取该资讯信息失败');
      }
    })
  }
};
onMounted(() => {
  validatePublicOrUpdate();
});
const uploadImage = (files) => {
  for (let i = 0; i < files.length; i++) {
    let formData = new FormData();
    formData.append('file',files[i]);
    myAxios.post('/base/image/upload',formData).then((response) => {
      if (response.data.code === 200) {
        let text = '\n\n' + '![image-' + new Date().getTime() + ']' + '(' +  response.data.data.filepath+ ')' + '\n\n';
        informInfo.informContent = informInfo.informContent + text;
      } else {
        ElMessage.error('上传图片出错');
      }
    })
  }
};
const publicInform = () => {
  myAxios.post('/inform/publicInform',informInfo).then((response) => {
    if (response.data.code === 200) {
      ElMessage.success(response.data.message);
      router.push('/admin/inform/informManage');
    } else {
      ElMessage.error(response.data.message);
    }
  })
};
</script>

<style lang="scss">
.byteEditor {
  .bytemd {
    text-align: left;
    height: 550px;
  }
}
</style>