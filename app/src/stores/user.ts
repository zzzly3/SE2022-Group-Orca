import { defineStore } from 'pinia';
import {post} from 'boot/axios';
import {Notify} from 'quasar';

const demo_list = [
  {id: 1, name: 'test', type: 'admin'},
  {id: 2, name: 'test', type: 'admin'},
  {id: 3, name: 'test', type: 'admin'},
  {id: 4, name: 'test', type: 'admin'},
  {id: 5, name: 'test', type: 'admin'}
]

export interface UserInfo{
  id: number,
  name: string,
  type: string,
}

export const useUserStore = defineStore('user', {
  state: () => ({
    name: '[未登录]',
    login: false,
    force_chpwd: false,
    login_redirect: '',
    type: ''
  }),
  actions: {
    async load_user_info() {
      const r = await post('getinfo', {}, false)
      if (r !== false && r.login) {
        this.login = true
        this.name = r.user.name
        this.force_chpwd = Boolean(r.user.isFirst)
        if (r.user.isAdmin)
          this.type = 'admin'
        else if (Number(r.user.role) === 1)
          this.type = 'teacher'
        else if (Number(r.user.role) === 2)
          this.type = 'student'
      } else {
        this.login = false
      }
    },
    async load_user_list(start: number, count: number) {
      return {all: demo_list.length, data: demo_list.slice(start, count)}
    },
    async do_login(id: string, pwd: string) {
      if (await post('login', {number: id, password: pwd}) !== false) {
        await this.load_user_info()
        if (this.login === false) {
          Notify.create({type:'negative', message: '登录失败'})
          return false
        } else {
          return true
        }
      }
      return false
    },
    async do_logout() {
      return await post('logout', {}) !== false
    },
    async add_user({id, name, pid, phone, email, type}: {id: string, name: string, pid: string, phone: string, email: string, type: string}) {
      let role = 0
      if (type === 'teacher')
        role = 1
      else if (type === 'student')
        role = 2
      if (await post('register', {identifier: pid, phone, email, name, role, number: id}) !== false) {
        Notify.create({type:'positive', message:'添加成功'})
        return true
      }
      return false
    },
    async chpwd(oldpwd: string, newpwd: string) {
      if (await post('resetpw', {originPw: oldpwd, newPw: newpwd}) !== false) {
        Notify.create({type:'positive', message:'修改成功'})
        return true
      }
      return false
    }
  },
});
