<template>
  <HeaderClient v-if="route.meta.clientNavShow"/>
  <router-view/>
</template>

<script setup>
import HeaderClient from '../components/client/index/Header-client.vue';
import { useRoute } from "vue-router";

const route = useRoute();
</script>

<style scoped>

</style>