<template>
  <div class="about-page">
    <div class="container">
      <div>Author: Zhifeng Cui</div>
      <div>Email: zhifengcui283@gmail.com</div>
      <div>Github: https://github.com/zfc283</div>
      <div>Phone: (+1) 647-518-2960</div>
<!--      <div>Resume: <a @click="downloadFile" style="font-size: 14px">Zhifeng Cui Resume <vertical-align-bottom-outlined /> </a></div>-->
    </div>
  </div>
</template>

<style scoped>
.about-page {
  overflow: hidden;

  .container {
    margin-left: 60px;
    margin-top: 40px;
    font-size: 17px;
    line-height: 45px;
  }
}
</style>
<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: 'About',
  setup() {
    const downloadFile = () => {
      // Specify the path to the file
      // eslint-disable-next-line @typescript-eslint/no-var-requires
      const filePath = require('@/assets/logo.png');

      // Create a temporary anchor element
      const link = document.createElement('a');
      link.href = filePath;
      link.setAttribute('download', 'logo.png'); // Optional: specify a filename for the download

      // Append to the DOM, click, and remove it after click
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
    return {
      downloadFile
    }
  }


});
</script>