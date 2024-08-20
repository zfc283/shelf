<template>
  <a-layout-header class="header">
<!--    <div class="logo" />-->
    <img :src="logoPath" class="logo">
    <a class="login-menu" v-show="!user.id" @click="handleLoginButtonClick">
      <span>Admin login</span>
    </a>
    <a class="login-menu" v-show="user.id" :style="{paddingLeft: '20px'}" @click="handleLogoutButtonClick">
      <span>Log out</span>
    </a>
    <span class="login-menu" v-show="user.id">
      {{user.name}}
    </span>
    <a-menu
        v-model:selectedKeys="selectedKeys1"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="home">
        <router-link to="/">Home</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/topic" v-if="user.id">
        <router-link to="/admin/topic">Topic admin</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/category" v-if="user.id">
        <router-link to="/admin/category">Category admin</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/user" v-if="user.id">
        <router-link to="/admin/user">Admin accounts</router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">About</router-link>
      </a-menu-item>
    </a-menu>
    <a-modal
        v-model:visible="modalVisible"
        title="Login"
        :confirm-loading="modalLoading"
        @ok="handleOk"
    >
      <a-form :model="params" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="Login name">
          <a-input v-model:value="params.loginName" />
        </a-form-item>
        <a-form-item label="Password">
          <a-input v-model:value="params.password" type="password"/>
        </a-form-item>
      </a-form>
    </a-modal>
    <a-modal
        v-model:visible="logoutModalVisible"
        :confirm-loading="logoutModalLoading"
        ok-text="OK"
        cancel-text="Cancel"
        @ok="handleLogoutOk"
    >
      <p>Are you sure you want to log out?</p>
    </a-modal>
  </a-layout-header>
</template>

<style scoped>
.login-menu {
  float: right;
  color: #ffffffa6;
}
</style>


<script lang="ts">
import {computed, defineComponent, ref } from 'vue';
import axios from "axios";
import { message } from 'ant-design-vue';
import store from '@/store';


declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'the-header',
  setup() {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const logoutModalVisible = ref(false);
    const logoutModalLoading = ref(false);

    const user = computed(() => {
      return store.state.user;
    })


    const params:any = ref({
      loginName: '',
      password: ''
    });

    const handleLoginButtonClick = () => {
      modalVisible.value = true;
    }

    const handleOk = () => {
      modalLoading.value = true;

      params.value.password = hexMd5(params.value.password + KEY);

      axios.post("/user/login", params.value)
          .then((resp) => {
            const json = resp.data;
            if (json.success) {
              params.value = {
                loginName: '',
                password: ''
              };
              //user.value = json.content;
              store.commit("setUser", json.content);
              modalLoading.value = false;
              modalVisible.value = false;
              message.success("Login successful");
            } else {
              modalLoading.value = false;
              message.error(json.message);
            }
          })
    }

    const handleLogoutButtonClick = () => {
      logoutModalVisible.value = true;
    }

    const handleLogoutOk = () => {
      logoutModalLoading.value = true;
      axios.post("/user/logout", user.value.token)
          .then((resp) => {
            logoutModalLoading.value = false;

            const json = resp.data;
            if (json.success) {
              store.commit("setUser", {});
              logoutModalVisible.value = false;
              message.success("Logout successful");
            } else {
              logoutModalVisible.value = false;
              message.error(json.message);
            }
          })
    }

    // eslint-disable-next-line @typescript-eslint/no-var-requires
    const logoPath = require('@/assets/logo1.png');

    return {
      modalVisible,
      modalLoading,
      handleLoginButtonClick,
      user,
      params,
      handleOk,
      logoutModalVisible,
      logoutModalLoading,
      handleLogoutButtonClick,
      handleLogoutOk,
      logoPath
    }
  }
});
</script>