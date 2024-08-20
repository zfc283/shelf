<template>
  <a-layout>
    <a-layout>
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <p>
          <a-space size="small">
            <a-input v-model:value="keyword" />
            <a-button type="primary" @click="searchByKeyword">Search</a-button>
            <a-button type="primary" @click="add">New</a-button>
          </a-space>
        </p>
        <a-table
            :columns="columns"
            :row-key="record => record.id"
            :pagination="pagination"
            :loading="loading"
            :data-source="ebookList"
            @change="handleTableChange">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'cover'">
              <img :src="record.cover" alt="avatar" style="width: 50px; height: 50px; border-radius: 8%;" />
            </template>
            <template v-else-if="column.key === 'category'">
              {{categoryIdNameMap[record.category1Id]}} / {{categoryIdNameMap[record.category2Id]}}
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space size="small">
                <a-button type="primary" @click="$router.push('/admin/doc?ebookId=' + record.id)">
                  Docs admin
                </a-button>
                <a-button type="primary" @click="() => { edit(record) }">
                  Edit
                </a-button>
                <a-popconfirm
                    title="Deletion cannot be undone. Do you want to proceed?"
                    ok-text="OK"
                    cancel-text="Cancel"
                    @confirm="() => {del(record.id)}"
                >
                  <a-button type="danger">
                    Delete
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-layout-content>
    </a-layout>
  </a-layout>
  <a-modal
      v-model:visible="modalVisible"
      title="Edit"
      :confirm-loading="modalLoading"
      @ok="handleOk"
  >
    <a-form :model="ebook" labelAlign="left" :label-col="{ style: { width: '100px' } }" :wrapper-col="{ span: 18 }" :style="{paddingLeft: '20px'}">
      <a-form-item label="Cover">
<!--        <a-input v-model:value="ebook.cover" />-->
        <input type="file" @change="handleInputImage" ref="imageInput">
      </a-form-item>
      <a-form-item label="Name">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="Category">
        <!-- 一般组件库都会提供一个选项修改映射参数的名字, 如 :field-names="{ label: 'name', value: 'id', children: 'children' }"-->
        <a-cascader
            v-model:value="ebookCategorys"
            :field-names="{ label: 'name', value: 'id', children: 'children' }"
            :options="categoryList"
            :allow-clear="false"/>
      </a-form-item>
      <a-form-item label="Description">
        <a-input v-model:value="ebook.description" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>



<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import { Tool } from "@/util/tool";

const columns = [
  {
    title: 'Cover',
    dataIndex: 'cover',
    key: 'cover',
  },
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
  },
  // {
  //   title: '分类一',
  //   dataIndex: 'category1Id',
  //   key: 'category1Id',
  // },
  // {
  //   title: '分类二',
  //   key: 'category2Id',
  //   dataIndex: 'category2Id',
  // },
  {
    title: 'Category',
    key: 'category'
  },
  {
    title: 'Documents',
    key: 'docCount',
    dataIndex: 'docCount',
  },
  {
    title: 'Read count',
    key: 'viewCount',
    dataIndex: 'viewCount',
  },
  {
    title: 'Likes',
    key: 'voteCount',
    dataIndex: 'voteCount',
  },
  {
    title: 'Action',
    key: 'action',
  },
];

export default defineComponent({
  name: 'AdminEbook',
  setup() {
    const ebookList = ref();
    const pagination = ref({       // Table 组件使用 pagination 响应式变量进行分页
      current: 1,
      pageSize: 5,
      total: 0
    });
    const loading = ref(false);

    const handleQuery = (params: any) => {
      return axios.get("/ebook/list", {params}).then((resp) => {
        const json = resp.data;
        if (json.success) {
          ebookList.value = json.content.list;
          pagination.value.current = params.current;
          pagination.value.total = json.content.total;
        } else {
          message.error(json.message);
        }
      })
    }

    const handleTableChange = (params: any) => {      // 点击分页按钮后, 会调用 handleTableChange 方法, 并把当前的 pagination.value 作为参数传入
      return handleQuery({
        current: params.current,
        pageSize: params.pageSize
      })
    }

    const ebook:any = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    const imageInput:any = ref(null);

    const add = () => {
      ebook.value = {};
      ebookCategorys.value = [];
      ebookImageChange.value = false;
      if (imageInput.value) {
        imageInput.value.value = '';
      }
      modalVisible.value = true;
    }

    const edit = (record: any) => {
      ebook.value = Tool.copy(record);
      ebookCategorys.value[0] = ebook.value.category1Id;
      ebookCategorys.value[1] = ebook.value.category2Id;
      ebookImageChange.value = false;
      if (imageInput.value) {
        imageInput.value.value = '';
      }
      modalVisible.value = true;
    };

    const del = (id: string) => {
      axios.delete("/ebook/" + id).then(resp => {
        const json = resp.data;
        if (json.success) {
          const pageValue = pagination.value;
          pageValue.total -= 1;
          if ((pageValue.current > 1) && (pageValue.current - 1) * pageValue.pageSize === pageValue.total) {
            pageValue.current -= 1;
          }
          handleTableChange(pageValue);
        } else {
          message.error(json.message);
        }
      })
    }

    const ebookImageChange = ref(false);

    const ebookImage:any = ref(null);

    const handleInputImage = (event:any) => {
      const file = event.target.files[0];    // Store the file
      if (!file) {
        return;      // No file was selected
      }
      if (!file.type.match('image.*')) {     // Not an image file
        message.error('Please upload an image file.');
        event.target.value = '';
        return;
      }

      const maxFileSize = 5 * 1024 * 1024;     // 5MB
      if (file.size > maxFileSize) {
        message.error('File size exceeds the maximum limit of 5MB.');
        event.target.value = '';
        return;
      }
      ebookImage.value = file;
      ebookImageChange.value = true;
    }

    const uploadImage = () => {
      if (ebookImageChange.value) {         // Only upload an image if ebook cover image is changed
        let formData = new FormData();
        formData.append('img', ebookImage.value);

        const config = {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        };

        return axios.post("/img/upload", formData, config)
            .then((resp) => {
              const json = resp.data;
              if (!json.errno) {
                console.log("Cover image uploaded successfully");
                ebook.value.cover = json.data[0];
              } else {
                modalLoading.value = false;
                message.error(json.message);
                throw new Error(json.message);
              }
            })
      }
      return Promise.resolve();

    }





    // const handleOk = () => {
    //   modalLoading.value = true;
    //   ebook.value.category1Id = ebookCategorys.value[0];
    //   ebook.value.category2Id = ebookCategorys.value[1];
    //
    //   if (ebook.value.id) {
    //     axios.put("/ebook", ebook.value).then(resp => {
    //       const json = resp.data;
    //       if (json.success) {
    //         modalVisible.value = false;
    //         modalLoading.value = false;
    //         ebookCategorys.value = [];
    //         handleTableChange(pagination.value);
    //       } else {
    //         modalLoading.value = false;
    //         message.error(json.message);
    //       }
    //     })
    //   } else {
    //     axios.post("/ebook", ebook.value).then(resp => {
    //       const json = resp.data;
    //       if (json.success) {
    //         modalVisible.value = false;
    //         modalLoading.value = false;
    //         pagination.value.total += 1;
    //         ebookCategorys.value = [];
    //         handleTableChange(pagination.value);
    //       } else {
    //         modalLoading.value = false;
    //         message.error(json.message);
    //       }
    //     })
    //   }
    // };


    const handleOk = () => {
      modalLoading.value = true;
      ebook.value.category1Id = ebookCategorys.value[0];
      ebook.value.category2Id = ebookCategorys.value[1];

      uploadImage().then(() => {
        if (ebook.value.id) {
          axios.put("/ebook", ebook.value).then(resp => {
            const json = resp.data;
            if (json.success) {
              modalVisible.value = false;
              modalLoading.value = false;
              ebookCategorys.value = [];
              handleTableChange(pagination.value);
            } else {
              modalLoading.value = false;
              message.error(json.message);
            }
          })
        } else {
          axios.post("/ebook", ebook.value).then(resp => {
            const json = resp.data;
            if (json.success) {
              modalVisible.value = false;
              modalLoading.value = false;
              pagination.value.total += 1;
              ebookCategorys.value = [];
              handleTableChange(pagination.value);
            } else {
              modalLoading.value = false;
              message.error(json.message);
            }
          })
        }
      })
    };

    const keyword = ref("");
    const searchByKeyword = () => {
      handleQuery({
        current: 1,
        pageSize: pagination.value.pageSize,
        name: keyword.value
      })
    }

    const categoryList:any = ref();
    const getCategoryList = () => {
      return axios.get("/category/list").then((resp) => {
        const json = resp.data;
        if (json.success) {
          categoryList.value = Tool.arrayToTree(json.content);
        } else {
          message.error(json.message);
        }
      })
    }
    const categoryIdNameMap:any = {};
    const getCategoryIdNameMap = () => {
      categoryList.value.forEach((item: { id: any; name: any; children: any[]; }) => {
        categoryIdNameMap[item.id] = item.name;
        item.children.forEach(childItem => {
          categoryIdNameMap[childItem.id] = childItem.name;
        })
      })
    }

    const ebookCategorys:any = ref([]);

    onMounted(() => {
      loading.value = true;
      Promise.all([handleTableChange(pagination.value), getCategoryList().then(() => {
        getCategoryIdNameMap();
      })])
          .then(() => {
            loading.value = false;
          })
    })


    return {
      columns,
      ebookList,
      pagination,
      loading,
      handleTableChange,
      ebook,
      modalVisible,
      modalLoading,
      edit,
      add,
      handleOk,
      del,
      keyword,
      searchByKeyword,
      categoryList,
      ebookCategorys,
      categoryIdNameMap,
      handleInputImage,
      imageInput
    };
  },
});
</script>