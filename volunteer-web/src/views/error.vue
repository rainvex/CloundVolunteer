<template>
  <el-container style="width: 100%;height: 100%;justify-content: center;align-items: center">
    <el-result
        icon="error"
        title="错误"
        sub-title="你所访问的路径不存在"
    >
      <template #extra>
        <el-button type="primary" @click="router.push('/')">返回首页</el-button>
      </template>
    </el-result>
  </el-container>
</template>

<script setup>
import {useRouter} from "vue-router";

const router = useRouter();
</script>

<style scoped>

</style>