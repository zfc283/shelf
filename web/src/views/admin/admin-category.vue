<template>
  <a-layout>
    <a-layout>
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <p>
          <a-space size="small">
<!--            <a-button type="primary" @click="refresh">查询</a-button>-->
            <a-button type="primary" @click="add">New</a-button>
          </a-space>
        </p>
        <a-table
            :columns="columns"
            :row-key="record => record.id"
            :pagination="false"
            :loading="loading"
            :data-source="categoryList">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'action'">
              <a-space size="small">
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
    <a-form :model="category" labelAlign="left" :label-col="{ style: { width: '120px' } }" :wrapper-col="{ span: 18 }" :style="{paddingLeft: '15px'}">
      <a-form-item label="Name">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="Parent category">
        <a-select
            v-model:value="category.parent"
        >
          <a-select-option value="0">None</a-select-option>
          <a-select-option
              v-for="item in categoryList"
              :key="item.id"
              :value="item.id"
              :disabled="item.id === category.id"
          >
            {{item.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Display order">
        <a-input v-model:value="category.sort" />
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
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: 'Parent category',
    dataIndex: 'parent',
    key: 'parent',
  },
  {
    title: 'Display order',
    dataIndex: 'sort',
    key: 'sort',
  },
  {
    title: 'Action',
    key: 'action',
  },
];

export default defineComponent({
  name: 'AdminCategory',
  setup() {
    const categoryList = ref();
    const loading = ref(false);

    const handleQuery = () => {
      loading.value = true;
      axios.get("/category/list").then((resp) => {
        loading.value = false;
        const json = resp.data;
        if (json.success) {
          categoryList.value = Tool.arrayToTree3(json.content);
        } else {
          message.error(json.message);
        }
      })
    }

    const handleTableChange = () => {
      handleQuery()
    }

    const category = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    const add = () => {
      category.value = {};
      modalVisible.value = true;
    }

    const edit = (record: any) => {
      category.value = Tool.copy(record);
      modalVisible.value = true;
    };

    const del = (id: string) => {
      axios.delete("/category/" + id).then(resp => {
        const json = resp.data;
        if (json.success) {
          handleTableChange()
        } else {
          message.error(json.message);
        }
      })
    }
    const handleOk = () => {
      modalLoading.value = true;
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      if (category.value.id) {
        axios.put("/category", category.value).then(resp => {
          const json = resp.data;
          if (json.success) {
            modalVisible.value = false;
            modalLoading.value = false;
            handleTableChange();
          } else {
            modalLoading.value = false;
            message.error(json.message);
          }
        })
      } else {
        axios.post("/category", category.value).then(resp => {
          const json = resp.data;
          if (json.success) {
            modalVisible.value = false;
            modalLoading.value = false;
            handleTableChange();
          } else {
            modalLoading.value = false;
            message.error(json.message);
          }
        })
      }
    };

    const refresh = () => {
      handleQuery()
    }

    onMounted(() => {
      handleTableChange();
    })


    return {
      columns,
      categoryList,
      loading,
      handleTableChange,
      category,
      modalVisible,
      modalLoading,
      edit,
      add,
      handleOk,
      del,
      refresh
    };
  },
});
</script>