<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleCategoryMenuClick"
      >
        <a-menu-item key="welcome">
<!--          <MailOutlined />-->
          <span>Welcome</span>
        </a-menu-item>
        <a-sub-menu v-for="item in categoryList" :key="item.id">
          <template #title>{{item.name}}</template>
          <a-menu-item v-for="childItem in item.children" :key="childItem.id">
            {{childItem.name}}
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <div v-show="showWelcome">
          <TheWelcome />
        </div>
        <a-list item-layout="vertical" :grid="{ gutter: 20, column: 4 }" size="large" :data-source="ebookList" v-show="!showWelcome">
          <template #renderItem="{ item }">
              <a-list-item key="item.name">
                  <template #actions>
                    <span>
                      <FileOutlined style="margin-right: 8px"/>
                      {{item.docCount}}
                    </span>
                    <span>
                      <UserOutlined style="margin-right: 8px"/>
                      {{item.viewCount}}
                    </span>
                    <span>
                      <LikeOutlined style="margin-right: 8px"/>
                      {{item.voteCount}}
                    </span>
                  </template>
                  <a-list-item-meta :description="item.description">
                      <template #title>
                          <a @click="$router.push('/doc?ebookId=' + item.id)">{{ item.name }}</a>
                      </template>
                      <template #avatar><a-avatar :src="item.cover" /></template>
                  </a-list-item-meta>
              </a-list-item>
          </template>
        </a-list>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, reactive, toRef } from 'vue';
import axios from "axios";
import {Tool} from "@/util/tool";
import {message} from "ant-design-vue";
import TheWelcome from "@/components/the-welcome.vue";



export default defineComponent({
  name: 'Home',
  components: {
    TheWelcome
  },
  setup() {     // setup() 把 Vue2 的 data, methods, 生命周期函数全部包括了
      const ebookList = ref();       // 响应式数据 (JS修改这个数据后, 数据需要实时地反馈到页面上 (类似 React 中的 useState))
      //const ebookList2 = reactive({books: []});         // reactive 里面一般放一个对象


      const categoryList:any = ref();
      const handleQueryCategory = () => {
        return axios.get("/category/list").then((resp) => {
          const json = resp.data;
          if (json.success) {
            categoryList.value = Tool.arrayToTree(json.content);
          } else {
            message.error(json.message);
          }
        })
      }

      const showWelcome = ref(true);

      const handleQueryBook = (categoryId: string) => {
        axios.get("/ebook/list", {
          params: {
            current: 1,
            pageSize: 1000,
            categoryId: categoryId
          }
        }).then((resp) => {
          const json = resp.data;
          ebookList.value = json.content.list;      // 使用 ref.value 给 ref 赋值
          showWelcome.value = false;
          //ebookList2.books = json.content;
        })
      }

      const handleCategoryMenuClick = (value:any) => {
        if (value.key === "welcome") {
          showWelcome.value = true;
        } else {
          handleQueryBook(value.key);
        }
      }



      onMounted(() => {
          // console.log("onMounted")
          // Promise.all([axios.get("/ebook/list", {
          //   params: {
          //     current: 1,
          //     pageSize: 1000
          //   }
          // }).then((resp) => {
          //     const json = resp.data;
          //     ebookList.value = json.content.list;      // 使用 ref.value 给 ref 赋值
          //     //ebookList2.books = json.content;
          // }), handleQueryCategory()])

        handleQueryCategory();
      })

      return {           // HTML代码要拿到响应式变量和自定义方法, 需要在 setup 最后 return
        ebookList,
        //books: toRef(ebookList2, "books")
        categoryList,
        showWelcome,
        handleCategoryMenuClick
      }
  }
});
</script>

<!-- scoped 表示样式只在当前组件起作用 -->
<style scoped>
  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>
