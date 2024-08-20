<template>
  <a-layout id="components-layout-demo-top-side-2" :style="{height: '100vh', display: 'flex', flexDirection: 'column'}">
    <the-header />
    <div :style="{flexGrow: 1, background: '#fff'}">
      <router-view />
    </div>
    <the-footer />
  </a-layout>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import TheHeader from "@/components/the-header.vue";
import TheFooter from "@/components/the-footer.vue";

export default defineComponent({
  components: {
    TheHeader,
    TheFooter
  }
})


</script>



<style>
#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 130px;
  margin: 16px 24px 16px 0;
  //background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

.site-layout-background {
  background: #fff;
}

.ant-table-thead .ant-table-cell {
  text-align: center;
}
.ant-table-tbody .ant-table-cell:not(.ant-table-cell-with-append) {
  text-align: center;
}
</style>
