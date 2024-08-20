<template>
  <a-layout :style="{background: '#fff', overflow: 'hidden'}">
    <h3 v-if="!loading && docList.length === 0" :style="{marginLeft: '30px', marginTop: '18px', fontSize: '18px'}">Oops, no document is found for topic {{ebookID}} ...</h3>
    <a-row v-if="docList.length !== 0">
      <a-col :span="6" :style="{paddingRight: '30px'}">
        <a-tree
            v-if="docList.length"
            defaultExpandAll
            :tree-data="docList"
            block-node
            :field-names="{
                      children: 'children',
                      title:'name',
                      key:'id'
                    }"
            @select="handleDocItemClick"
            :selected-keys="selectedKeys"
        >
          <!--        <template #title="{ title, key }" @click="handleDocItemClick(key)">-->
          <!--          <template>{{ title }}</template>-->
          <!--        </template>-->
        </a-tree>
      </a-col>
      <a-col :span="18" :style="{paddingRight: '40px'}">
        <div>
          <h1 style="font-size: 24px">{{ doc.name }}</h1>
          <div>
            <span>Reads: {{ doc.viewCount }}</span> &nbsp; &nbsp;
            <span>Likes: {{ doc.voteCount }}</span>
          </div>
          <a-divider style="height: 2px; background-color: #9999cc"/>
        </div>
        <div class="wangeditor" :innerHTML="html"></div>
        <div class="vote-div">
          <a-button type="primary" shape="round" @click="handleLikeButtonClick">
            <template #icon>
              <LikeOutlined/>
            </template>
            Likes: {{ doc.voteCount }}
          </a-button>
        </div>
      </a-col>
    </a-row>
  </a-layout>
</template>

<style>
.wangeditor table {
  border-top: 1px solid #ccc !important;
  border-left: 1px solid #ccc !important;
}

.wangeditor table td,
.wangeditor table th {
  border-bottom: 1px solid #ccc !important;
  border-right: 1px solid #ccc !important;
  padding: 3px 5px !important;
}

.wangeditor table th {
  border-bottom: 2px solid #ccc !important;
  text-align: center !important;
}

/* blockquote 样式 */
.wangeditor blockquote {
  display: block !important;
  border-left: 8px solid #d0e5f2 !important;
  padding: 5px 10px !important;
  margin: 10px 0 !important;
  line-height: 1.4 !important;
  font-size: 100% !important;
  background-color: #f1f1f1 !important;
}

/* code 样式 */
.wangeditor code {
  display: inline-block !important;
  *display: inline !important;
  *zoom: 1 !important;
  background-color: #f1f1f1 !important;
  border-radius: 3px !important;
  padding: 3px 5px !important;
  margin: 0 3px !important;
}

.wangeditor pre code {
  display: block !important;
}

/* ul ol 样式 */
.wangeditor ul, .wangeditor ol {
  margin: 10px 0 10px 20px !important;
}

.vote-div {
  margin-top: 50px;
  padding: 15px;
  text-align: center;
}
</style>


<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message, Modal, TreeProps} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import E from 'wangeditor';


export default defineComponent({
  name: 'Doc',
  setup() {
    const route = useRoute();
    const ebookID = route.query.ebookId;
    const docList: any = ref([]);
    const loading = ref(true);
    const html = ref();
    const selectedKeys: any = ref([]);

    const handleQuery = () => {
      return axios.get("/doc/list?ebookId=" + route.query.ebookId).then((resp) => {
        const json = resp.data;
        if (json.success) {
          docList.value = Tool.arrayToTree3(json.content);
        } else {
          message.error(json.message);
        }
      })
    }

    const handleTableChange = () => {
      return handleQuery();
    }


    const getDocContent = (id: any) => {
      //loading.value = true;
      return axios.get("/doc/content?id=" + id)
          .then((resp) => {
            //loading.value = false;
            const json = resp.data;
            if (json.success) {
              if (json.content) {
                html.value = json.content.content;
              } else {
                html.value = '';
              }
            } else {
              message.error(json.message);
            }
          })
    }

    const doc: any = ref({})

    const handleDocItemClick = (keys: any, info: any) => {
      if (keys.length > 0) {
        selectedKeys.value = [];
        selectedKeys.value.push(keys[0]);
        doc.value = info.node;
        getDocContent(keys[0]);
      }
    }

    const handleLikeButtonClick = () => {
      axios.put("/doc/vote?id=" + doc.value.id)
          .then((resp) => {
            const json = resp.data;
            if (json.success) {
              doc.value.voteCount += 1;
            } else {
              message.error(json.message);
            }
          })
    }


    onMounted(() => {
      loading.value = true;
      handleTableChange().then(() => {
        if (Tool.isNotEmpty(docList.value)) {
          selectedKeys.value.push(docList.value[0].id);
          doc.value = docList.value[0];
          getDocContent(docList.value[0].id);
        } else {
          loading.value = false;
        }
      })
    })


    return {
      docList,
      loading,
      handleTableChange,
      handleDocItemClick,
      html,
      selectedKeys,
      doc,
      handleLikeButtonClick,
      ebookID
    };
  },
});
</script>