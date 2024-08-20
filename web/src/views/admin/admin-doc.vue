<template>
  <a-layout :style="{background: '#fff', overflow: 'hidden'}">
    <a-row>
      <a-col :span="8">
        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
          <p>
            <a-space size="small">
<!--              <a-button type="primary" @click="refresh">查询</a-button>-->
              <a-button type="primary" @click="add">New</a-button>
            </a-space>
          </p>
          <a-table
              :columns="columns"
              :row-key="record => record.id"
              :pagination="false"
              :loading="loading"
              :data-source="docList">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'action'">
                <a-space size="small">
                  <a-button type="primary" size="small" @click="() => { edit(record) }">
                    Edit
                  </a-button>
                  <a-popconfirm
                      title="Deletion cannot be undone. Do you want to proceed?"
                      ok-text="OK"
                      cancel-text="Cancel"
                      @confirm="() => { handleDeleteButtonClick(record.id) }"
                  >
                    <a-button size="small" type="danger">
                      Delete
                    </a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-layout-content>
      </a-col>
      <a-col :span="16">
        <a-button
            type="primary"
            :style="{ margin: '24px 0 24px 72px' }"
            @click="handleSaveButtonClick"
        >
          Save
        </a-button>
        <a-form :model="doc"
                labelAlign="left"
                :label-col="{ style: { width: '120px' } }"
                :wrapper-col="{ span: 19 }"
                :style="{margin: '0 0 0 72px'}"
        >
          <a-form-item label="Title">
            <a-input v-model:value="doc.name"/>
          </a-form-item>
          <a-form-item label="Parent doc">
            <a-tree-select
                v-model:value="doc.parent"
                :tree-data="treeSelectData"
                tree-default-expand-all
                :field-names="{
                      children: 'children',
                      label: 'name',
                      value: 'id',
                    }"
            >
            </a-tree-select>
          </a-form-item>
          <a-form-item label="Display order">
            <a-input v-model:value="doc.sort"/>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handlePreviewButtonClick">
              <template #icon>
                <EyeOutlined/>
              </template>
              Preview
            </a-button>
          </a-form-item>
          <a-form-item label="Content">
            <div id="content" style="font-size: 16px"></div>
          </a-form-item>
        </a-form>
      </a-col>
    </a-row>
  </a-layout>
  <a-modal v-model:visible="confirmDeleteModalVisible"
           title="Important note"
           @ok="() => {del(doc.id)}">
    <p>
      The following documents will be deleted: [{{ deleteItemsInfo.deleteItemNames.toString() }}]. This operation cannot be undone. Do you want to proceed?
    </p>
  </a-modal>
  <a-drawer
      :visible="drawerVisible"
      placement="right"
      :width="1000"
      @close="closeDrawer"
  >
    <div class="wangeditor" :innerHTML="html"></div>
  </a-drawer>
</template>

<style>
font[size="6"] {
  font-size: 28px;
}
font[size="7"] {
  font-size: 32px;
}

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
</style>


<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import E from 'wangeditor';
import i18next from 'i18next';
import store from "@/store";

const columns = [
  {
    title: 'Title',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: 'Order',
    dataIndex: 'sort',
    key: 'sort',
  },
  {
    title: 'Action',
    key: 'action',
  },
];

export default defineComponent({
  name: 'AdminDoc',
  setup() {
    const route = useRoute();
    const docList: any = ref([]);
    const loading = ref(false);
    const editor = ref();
    const drawerVisible = ref(false);
    const html = ref();

    const handleQuery = () => {
      loading.value = true;
      return axios.get("/doc/list?ebookId=" + route.query.ebookId).then((resp) => {
        loading.value = false;
        const json = resp.data;
        if (json.success) {
          docList.value = Tool.arrayToTree3(json.content);
        } else {
          message.error(json.message);
        }
      }).then(() => {
        updateTreeSelectData();
      })
    }

    const handleTableChange = () => {
      return handleQuery();
    }

    const doc = ref();
    doc.value = {};
    //const modalVisible = ref(false);
    //const modalLoading = ref(false);


    const getDocContent = (id: string) => {
      loading.value = true;
      axios.get("/doc/content?id=" + id)
          .then((resp) => {
            loading.value = false;
            const json = resp.data;
            if (json.success) {
              if (json.content) {
                editor.value.txt.html(json.content.content);
              } else {
                editor.value.txt.html('');
              }
            } else {
              message.error(json.message);
            }
          })
    }

    const add = () => {
      doc.value = {};                       // 清空
      editor.value.txt.html('');
      //doc.value.ebookId = route.query.ebookId;
      updateTreeSelectData();
    }

    const edit = (record: any) => {
      doc.value = Tool.copy(record);
      updateTreeSelectData();
      getDocContent(doc.value.id);
    };

    const confirmDeleteModalVisible = ref(false);

    const deleteItemsInfo: any = ref({
      deleteIds: [],
      deleteItemNames: []
    })

    const getDeleteItemsInfo = (array: Array<any>, id: any) => {
      deleteItemsInfo.value.deleteIds = [];
      deleteItemsInfo.value.deleteItemNames = [];

      const findNodeQueue: any[] = [];
      array.forEach(item => {
        findNodeQueue.push(item);
      })
      let node = null;
      while (findNodeQueue.length > 0) {
        const item = findNodeQueue.shift();
        if (item.id === id) {
          node = item;
          break;
        } else {
          if (item.children) {
            item.children.forEach((childItem: any) => {
              findNodeQueue.push(childItem);
            })
          }
        }
      }

      const queue2: any[] = [];
      queue2.push(node);
      while (queue2.length > 0) {
        const head = queue2.shift();
        deleteItemsInfo.value.deleteIds.push(head.id);
        deleteItemsInfo.value.deleteItemNames.push(head.name);

        if (head.children) {
          head.children.forEach((childItem: any) => {
            queue2.push(childItem);
          })
        }
      }
    }


    const del = (id: string) => {
      const deleteIdsStr = deleteItemsInfo.value.deleteIds.join(',');
      return axios.delete("/doc/" + deleteIdsStr).then(resp => {
        const json = resp.data;
        confirmDeleteModalVisible.value = false;
        if (json.success) {
          doc.value = {};                     // 清空
          editor.value.txt.html('');
          handleTableChange();
        } else {
          message.error(json.message);
        }
      })
    }


    const handleDeleteButtonClick = (id: string) => {
      doc.value.id = id;
      getDeleteItemsInfo(docList.value, id);
      confirmDeleteModalVisible.value = true;
    }


    const handleSaveButtonClick = () => {
      doc.value.ebookId = route.query.ebookId;        // 因为我们只能编辑同一 ebookId 下的所有电子书, 所以不管是新增(add), 还是编辑(edit), 都在这里统一设置 doc.value.ebookId = route.query.ebookId
      doc.value.content = editor.value.txt.html();
      if (doc.value.id) {
        axios.put("/doc", doc.value).then(resp => {
          const json = resp.data;
          if (json.success) {
            message.success("Update successful");
            handleTableChange();
          } else {
            message.error(json.message);
          }
        })
      } else {
        axios.post("/doc", doc.value).then(resp => {
          const json = resp.data;
          if (json.success) {
            message.success("Creation successful");
            doc.value = {};
            editor.value.txt.html('');
            handleTableChange();
          } else {
            message.error(json.message);
          }
        })
      }
    };

    const refresh = () => {
      handleQuery()
    }

    const disableTreeNodes = (array: Array<any>, id: any) => {
      const findNodeQueue: any[] = [];
      array.forEach(item => {
        findNodeQueue.push(item);
      })
      let node = null;
      while (findNodeQueue.length > 0) {
        const item = findNodeQueue.shift();
        if (item.id === id) {
          node = item;
          break;
        } else {
          if (item.children) {
            item.children.forEach((childItem: any) => {
              findNodeQueue.push(childItem);
            })
          }
        }
      }

      const disableNodeQueue: any[] = [];
      disableNodeQueue.push(node);
      while (disableNodeQueue.length > 0) {
        const head = disableNodeQueue.shift();
        head.disabled = true;
        if (head.children) {
          head.children.forEach((childItem: any) => {
            disableNodeQueue.push(childItem);
          })
        }
      }
    }


    const treeSelectData: any = ref([]);

    // const treeSelectData = computed(() => {
    //   const newList = Tool.copy(docList.value);
    //   if (doc.value.id) {
    //     disableTreeNodes(newList, doc.value.id);
    //   }
    //   newList.unshift({
    //     id: "0",
    //     name: "None",
    //   });
    //   return newList;
    // })

    const updateTreeSelectData = () => {
      if (docList.value.length > 0) {      // Tool.copy 无法处理 docList.value 为空数组的情况，因此我们需要手动判断
        treeSelectData.value = Tool.copy(docList.value);
        if (doc.value.id) {
          disableTreeNodes(treeSelectData.value, doc.value.id);
        }
      }
      treeSelectData.value.unshift({
        id: "0",
        name: "None",
      });
    }

    const handlePreviewButtonClick = () => {
      html.value = editor.value.txt.html();
      drawerVisible.value = true;
    }

    const closeDrawer = () => {
      drawerVisible.value = false;
    }


    onMounted(() => {
      handleTableChange();
      editor.value = new E('#content');
      editor.value.config.zIndex = 0;
      // 选择语言
      editor.value.config.lang = 'en';
      // 引入 i18next 插件
      editor.value.i18next = i18next;
      editor.value.config.uploadImgServer = "http://localhost:8080/api/img/upload";
      editor.value.config.uploadFileName = 'img';
      editor.value.config.uploadImgHeaders = {'token': store.state.user.token};
      editor.value.config.uploadImgMaxSize = 8 * 1024 * 1024; // 8M
      editor.value.config.excludeMenus = [
        'video'
      ];
      editor.value.config.uploadImgHooks = {
        customInsert: function(insertImgFn:any, result:any) {
          // const height = result.height;
          // const width = result.width;
          // const fixedWidth = 500;
          // const ratio = fixedWidth / width;
          // const updatedHeight = height * ratio;
          // console.log("updatedHeight: ", updatedHeight);

          // editor.value.cmd.do(
          //     'insertHtml',
          //     '<img src="' + result.data[0] + '" style="width: 100px; height: 100px; text-align:center;"/>'
          // )
          // editor.value.cmd.do(
          //     'insertHtml',
          //     '<img src="' + result.data[0] + '"' + ' style="' + `width: ${fixedWidth}px; height: ${updatedHeight}px; margin: auto; display: block;` + '"/>'
          // )
          editor.value.cmd.do(
              'insertHtml',
              '<img src="' + result.data[0] + '"' + ' style="' + `margin: auto; display: block;` + '"/>'
          )
        }
      }
      editor.value.config.fontSizes = {
        'x-small': { name: '10px', value: '1' },
        'small': { name: '13px', value: '2' },
        'normal': { name: '16px', value: '3' },
        'large': { name: '18px', value: '4' },
        'x-large': { name: '24px', value: '5' },
        'xx-large': { name: '28px', value: '6' },
        'xxx-large': { name: '32px', value: '7' },
      }
      editor.value.create();
    })


    return {
      columns,
      docList,
      loading,
      handleTableChange,
      doc,
      edit,
      add,
      del,
      refresh,
      treeSelectData,
      handleDeleteButtonClick,
      confirmDeleteModalVisible,
      deleteItemsInfo,
      handleSaveButtonClick,
      drawerVisible,
      html,
      handlePreviewButtonClick,
      closeDrawer
    };
  },
});
</script>