import { boot } from 'quasar/wrappers';
import axios, { AxiosInstance } from 'axios';
import {Notify} from 'quasar';

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $axios: AxiosInstance;
  }
}

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({ baseURL: 'http://127.0.0.1:8888' });

export default boot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api;
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API
});

// false means fail
async function post(url: string, data: object, notify = true) {
  try {
    const r = await api.post(url, data)
    if (Number(r.data.code) === 200) {
      return r.data.data
    }
    if (notify)
      Notify.create({
        type: 'negative',
        message: r.data.msg
      })
    return false
  }
  catch (e) {
    if (notify)
      Notify.create({
        type: 'negative',
        message: '网络异常'
      })
    return false
  }
}

export { api, post };
