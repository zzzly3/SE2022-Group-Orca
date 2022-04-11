<template>
  <q-layout view="hhh lpR fff">
    <q-header class="text-basic bg-white q-mx-xl" style="z-index:100">
      <q-toolbar class="q-mx-xl">
        <q-input outlined v-model="search" label="Search (TODO)" dense style="margin-top: 1px" rounded disable>
          <template v-slot:append>
            <q-btn color="primary" icon="search" round flat></q-btn>
          </template>
        </q-input>
        <q-btn color="positive" class="q-ml-lg" icon="add" outline @click="adder_bind=undefined;show_adder=true">添加用户</q-btn>
        <q-btn color="primary" class="q-ml-lg" icon="upload" outline @click="show_upload=true">导入用户</q-btn>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <q-page class="column items-center">
        <div style="width: 80%; ">
          <q-markup-table flat class="text-center">
            <thead class="text-basic">
              <tr>
                <th>学/工号</th>
                <th>
                  <span>
                    类型
                  </span>
                  <span class="text-info" style="text-decoration: underline">
                    (?)
                    <q-tooltip>删除线表示用户已离校，不能使用系统。</q-tooltip>
                  </span>
                </th>
                <th>姓名</th>
                <th>手机</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="i in rows" :key="i.id">
                <td>{{i.id}}</td>
                <td>
                  <span v-if="i.leave" style="text-decoration: teal line-through">
                    {{i.type}}
                  </span>
                  <span v-else>
                    {{i.type}}
                  </span>
                </td>
                <td>{{i.name}}</td>
                <td>{{i.phone}}</td>
                <td>
                  <q-btn color="primary" icon="visibility" round flat size="sm"
                         @click="adder_bind=i;show_adder=true;">
                    <q-tooltip>查看信息</q-tooltip>
                  </q-btn>
                  <q-btn color="negative" icon="update" round flat size="sm" v-if="i.type !== 'admin'"
                         @click="chpwd_uid=i.id;show_chpwd=true;">
                    <q-tooltip>修改密码</q-tooltip>
                  </q-btn>
                </td>
              </tr>
            </tbody>
          </q-markup-table>
        </div>
        <q-pagination
          class="q-pa-sm"
          input
          v-model="current"
          :max="pcnt"
        />
        <q-inner-loading
          :showing="loading"
          color="teal"
        />
      </q-page>
    </q-page-container>

  </q-layout>

  <UserAdder v-model="show_adder" :old="adder_bind" @user_update="load"/>
  <PasswordUpdater v-model="show_chpwd" :admin="chpwd_uid"/>

  <q-dialog v-model="show_upload" :persistent="uploading">
    <q-card style="width: 500px">
      <q-card-section>
        <div class="text-subtitle1 row items-center">
          <q-icon name="upload" size="sm" color="primary"/>
          <span>导入用户</span>
        </div>
      </q-card-section>

      <q-card-section>
        <div class="q-px-md q-text-body2 text-basic">
          &nbsp;&nbsp;&nbsp;&nbsp;系统支持以csv格式批量导入用户。csv文件中的每一行代表一个用户，格式为
          <span class="bg-grey-3 text-weight-bold">学/工号,类型(stduent/teacher),姓名,身份证号,学院id,专业id,手机号,邮箱</span>
          ，最后四项可以留空，留空不能省略逗号分隔符。学院id和专业id可以在院系管理页面中查看。<span class="text-weight-bold">单次最多导入10000条数据。</span>
        </div>
      </q-card-section>

      <q-card-section>
        <q-form class="q-px-md" style="width: 350px">
          <div class="row">
            <p class="col-auto q-pr-md" style="line-height: 40px">
              选择csv文件
            </p>
            <q-file
              class="col"
              v-model="file"
              label="用户列表"
              clearable
              outlined
              dense
              accept=".csv"
              color="primary"
            />
          </div>
        </q-form>
      </q-card-section>

      <q-card-actions align="right" class="q-pb-md q-px-md">
        <q-btn flat label="取消" color="negative" @click="file=null" v-close-popup :disable="uploading" />
        <q-btn flat label="确定" color="primary" @click="upload_csv" :disable="!file" :loading="uploading" />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <q-uploader url="/api/user/import" class="hidden" ref="uploader"
              field-name="file"
              @finish="uploading=false"
              @failed="$q.notify({color:'negative',message:'网络异常'})"
              @uploaded="onuploaded" />
</template>

<script lang="ts">
import {defineComponent, ref, watch} from 'vue';
import {UserInfo, useUserStore} from 'stores/user';
import UserAdder from 'components/UserAdder.vue';
import PasswordUpdater from 'components/PasswordUpdater.vue';
import {QUploader, useQuasar} from 'quasar';

export default defineComponent({
  name: 'UserList',
  components: {PasswordUpdater, UserAdder},
  setup() {
    const $q = useQuasar()
    const user = useUserStore()
    const rows = ref([] as UserInfo[])
    const search = ref('')
    const show_adder = ref(false)
    const adder_bind = ref<UserInfo | undefined>(undefined)
    const show_chpwd = ref(false)
    const chpwd_uid = ref('')
    const current = ref(1)
    const pcnt = ref(1)
    const loading = ref(false)
    const show_upload = ref(false)
    const file = ref<File|null>(null)
    const uploader = ref<QUploader|null>(null)
    const uploading = ref(false)

    const load = async () => {
      loading.value = true
      const r = await user.load_user_list((current.value - 1) * 10, 10)
      if (r === false) {
        loading.value = false
      } else {
        pcnt.value = Math.floor(r.total / 10) + 1
        rows.value = r.list
        loading.value = false
      }
    }
    load()
    watch(current, load)

    return {
      load, current, pcnt, loading,
      show_adder, adder_bind, show_upload,
      show_chpwd, chpwd_uid,
      rows,
      search,
      file,
      uploader, uploading,
      upload_csv() {
        if (uploader.value === null) return
        uploading.value = true
        uploader.value.reset()
        uploader.value.addFiles([file.value])
        uploader.value.upload()
      },
      onuploaded({xhr}: {xhr: XMLHttpRequest}) {
        console.log(xhr)
        if (xhr.status === 200) {
          try {
            const r = JSON.parse(xhr.responseText)
            if (Number(r.code) === 200) {
              show_upload.value=false;file.value=null;
              $q.dialog({
                title: r.data.indexOf('成功') > -1 ? '导入成功' : '导入中断',
                message: r.data,
                color: r.data.indexOf('成功') > -1 ? 'positive' : 'negative'
              })
              load()
            } else {
              $q.notify({color:'negative',message:r.msg})
            }
          } catch (e) {
            $q.notify({color:'negative',message:'网络异常'})
          }
        } else {
          $q.notify({color:'negative',message:'网络异常'})
        }
      }
    };
  }
});
</script>
