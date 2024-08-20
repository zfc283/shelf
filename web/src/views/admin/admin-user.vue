<template>
  <a-layout>
    <a-layout>
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <p>
          <a-space size="small">
            <a-input v-model:value="keyword" />
            <a-button type="primary" @click="searchByKeyword">Search by username</a-button>
            <a-button type="primary" @click="add">New</a-button>
          </a-space>
        </p>
        <a-table
            :columns="columns"
            :row-key="record => record.id"
            :pagination="pagination"
            :loading="loading"
            :data-source="userList"
            @change="handleTableChange">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'action'">
              <a-space size="small">
                <a-button type="primary" @click="() => { handleResetPasswordButtonClick(record.id) }">
                  Password reset
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
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="Username">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="Nickname">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item label="Password" v-show="!user.id">
        <a-input v-model:value="user.password" />
      </a-form-item>
    </a-form>
  </a-modal>
  <a-modal
      v-model:visible="resetPasswordModalVisible"
      title="Password reset"
      :confirm-loading="resetPasswordModalLoading"
      @ok="handleResetPasswordOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="New password">
        <a-input v-model:value="user.password" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>


<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import { Tool } from "@/util/tool";

declare let hexMd5: any;
declare let KEY: any;

const columns = [
  {
    title: 'Username',
    dataIndex: 'loginName',
    key: 'loginName',
  },
  {
    title: 'Nickname',
    dataIndex: 'name',
    key: 'name',
  },
  // {
  //   title: 'Password',
  //   key: 'password',
  //   dataIndex: 'password',
  // },
  {
    title: 'Action',
    key: 'action',
  },
];

export default defineComponent({
  name: 'AdminUser',
  setup() {
    const userList = ref();
    const pagination = ref({       // Table 组件使用 pagination 响应式变量进行分页
      current: 1,
      pageSize: 5,
      total: 0
    });
    const loading = ref(false);

    const handleQuery = (params: any) => {
      return axios.get("/user/list", {params}).then((resp) => {
        const json = resp.data;
        if (json.success) {
          userList.value = json.content.list;
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

    const user:any = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    const add = () => {
      user.value = {};
      modalVisible.value = true;
    }

    const edit = (record: any) => {
      user.value = Tool.copy(record);
      modalVisible.value = true;
    };

    const del = (id: string) => {
      axios.delete("/user/" + id).then(resp => {
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
    const handleOk = () => {
      modalLoading.value = true;

      user.value.password = hexMd5(user.value.password + KEY);

      if (user.value.id) {
        axios.put("/user", user.value).then(resp => {
          const json = resp.data;
          if (json.success) {
            modalVisible.value = false;
            modalLoading.value = false;
            handleTableChange(pagination.value);
          } else {
            modalLoading.value = false;
            message.error(json.message);
          }
        })
      } else {
        axios.post("/user", user.value).then(resp => {
          const json = resp.data;
          if (json.success) {
            modalVisible.value = false;
            modalLoading.value = false;
            pagination.value.total += 1;
            handleTableChange(pagination.value);
          } else {
            modalLoading.value = false;
            message.error(json.message);
          }
        })
      }
    };

    const keyword = ref("");
    const searchByKeyword = () => {
      handleQuery({
        current: 1,
        pageSize: pagination.value.pageSize,
        loginName: keyword.value
      })
    }


    const resetPasswordModalVisible = ref(false);
    const resetPasswordModalLoading = ref(false);

    const handleResetPasswordButtonClick = (id: any) => {
      user.value = {};
      user.value.id = id;
      user.value.password = null;
      resetPasswordModalVisible.value = true;
    }

    const handleResetPasswordOk = () => {
      resetPasswordModalLoading.value = true;

      user.value.password = hexMd5(user.value.password + KEY);

      axios.put("/user/password", user.value).then(resp => {
          const json = resp.data;
          if (json.success) {
            resetPasswordModalVisible.value = false;
            resetPasswordModalLoading.value = false;
            handleTableChange(pagination.value);
          } else {
            resetPasswordModalLoading.value = false;
            message.error(json.message);
          }
      })

    }






    onMounted(() => {
      loading.value = true;
      handleTableChange(pagination.value)
          .then(() => {
            loading.value = false;
          })
    })


    return {
      columns,
      userList,
      pagination,
      loading,
      handleTableChange,
      user,
      modalVisible,
      modalLoading,
      edit,
      add,
      handleOk,
      del,
      keyword,
      searchByKeyword,
      resetPasswordModalVisible,
      resetPasswordModalLoading,
      handleResetPasswordButtonClick,
      handleResetPasswordOk
    };
  },
});
</script>