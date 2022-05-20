import {defineStore} from 'pinia';
import {post} from 'boot/axios';
import {Notify} from 'quasar';

export interface UserInfo{
  id: string,
  name: string,
  type: string,
  email: string,
  pid: string,
  phone: string,
  leave: number,
  college: number,
  major: number
}

export const useUserStore = defineStore('user', {
  state: () => ({
    name: '[未登录]',
    id: '',
    login: false,
    force_chpwd: false,
    login_redirect: '',
    type: '',
    email: '',
    pid: '',
    phone: '',
    leave: 0,
    college: {
      id: 0,
      name: ''
    },
    major: {
      id: 0,
      name: '',
      college: 0
    },
    colleges: [] as {id: number, name: string}[],
    majors: [] as {id: number, name: string, college: number}[],
    init: false
  }),
  actions: {
    async load_user_info() {
      const r = await post('user/getinfo', {}, false)
      if (r !== false && r.login) {
        this.login = true
        this.id = String(r.user.number)
        this.name = r.user.name
        this.pid = r.user.identifier
        this.email = r.user.email
        this.phone = r.user.phone
        this.force_chpwd = Boolean(r.user.isFirst)
        this.college = r.college
        this.major = r.major
        if (r.user.isAdmin)
          this.type = 'admin'
        else if (Number(r.user.role) === 1)
          this.type = 'teacher'
        else if (Number(r.user.role) === 2)
          this.type = 'student'
      } else {
        this.login = false
      }
      if (this.login && !this.init) {
        if (this.type === 'admin')
          await Promise.all([this.load_college(), this.load_major()]);
        else {
          if (this.college)
            this.colleges = [this.college]
          if (this.major)
            this.majors = [this.major]
        }
        this.init = true
      }
    },
    async load_user_list(start: number, count: number) {
      const r = await post('user/list', {start, count})
      if (r !== false) {
        const rr = {
          total: r.count,
          list: [] as UserInfo[]
        }
        for (let i = 0; i < r.list.length; i++) {
          rr.list.push({
            id: String(r.list[i].number),
            name: r.list[i].name,
            type: r.list[i].isAdmin ? 'admin' : (Number(r.list[i].role) === 1 ? 'teacher' : 'student'),
            email: r.list[i].email,
            pid: r.list[i].identifier,
            phone: r.list[i].phone,
            leave: r.list[i].isLeave,
            college: r.list[i].college,
            major: r.list[i].major
          })
        }
        return rr;
      } else {
        return false;
      }
    },
    async do_login(id: string, pwd: string) {
      if (await post('user/login', {number: id, password: pwd}) !== false) {
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
      if (await post('user/logout', {}) !== false) {
        Notify.create({type:'info', message:'用户已退出'})
        return true
      }
      return false
    },
    async add_user({id, name, pid, phone, email, type, update, leave, college, major}: {id: string, name: string, pid: string, phone: string, email: string, type: string, update: boolean, leave: number, college: number, major: number}) {
      let role = 1
      if (type === 'teacher')
        role = 1
      else if (type === 'student')
        role = 2
      if (update) {
        if (await post('user/update', {identifier: pid, phone, email, name, role, number: id, isLeave: leave, college, major}) !== false) {
          Notify.create({type:'positive', message:'修改成功'})
          if (id === this.id)
            await this.load_user_info()
          return true
        }
      } else {
        if (await post('user/register', {identifier: pid, phone, email, name, role, number: id, isLeave: leave, college, major}) !== false) {
          Notify.create({type:'positive', message:'添加成功'})
          return true
        }
      }
      return false
    },
    async chpwd(uid: string, oldpwd: string, newpwd: string) {
      if (await post('user/resetpw', {originPw: oldpwd, newPw: newpwd, uid: uid ? Number(uid) : null}) !== false) {
        Notify.create({type:'positive', message:'修改成功'})
        return true
      }
      return false
    },
    async load_college() {
      const r = await post('user/colleges')
      if (r !== false) {
        this.colleges = r
        return true
      }
      return false
    },
    async load_major() {
      const r = await post('user/majors')
      if (r !== false) {
        this.majors = r
        return true
      }
    },
    async update_college(cid: number, name: string) {
      if (await post('user/update_college', {id: cid, name: name}) !== false) {
        Notify.create({type:'positive', message:'修改成功'})
        return true
      }
      return false
    },
    async update_major(mid: number, name: string) {
      if (await post('user/update_major', {id: mid, name: name}) !== false) {
        Notify.create({type:'positive', message:'修改成功'})
        return true
      }
      return false
    },
    async add_college(name: string) {
      if (await post('user/add_college', {name: name}) !== false) {
        Notify.create({type:'positive', message:'添加成功'})
        return true
      }
      return false
    },
    async add_major(cid: number, name: string) {
      if (await post('user/add_major', {college: cid, name: name}) !== false) {
        Notify.create({type:'positive', message:'添加成功'})
        return true
      }
      return false
    },
    async delete_college(cid: number) {
      if (await post('user/delete_college', {id: cid}) !== false) {
        Notify.create({type:'positive', message:'删除成功'})
        return true
      }
      return false
    },
    async delete_major(mid: number) {
      if (await post('user/delete_major', {id: mid}) !== false) {
        Notify.create({type:'positive', message:'删除成功'})
        return true
      }
      return false
    },
    get_college_name(cid: number) {
      let r = '未分配'
      for (const c of this.colleges) {
        if (c.id === cid) {
          r = c.name
          break
        }
      }
      return r
    },
    get_major_name(mid: number) {
      let r = '未分配'
      for (const m of this.majors) {
        if (m.id === mid) {
          r = m.name
          break
        }
      }
      return r
    },
    get_type_name(type: string) {
      if (type === 'admin')
        return '管理员'
      else if (type === 'teacher')
        return '教师'
      else if (type === 'student')
        return '学生'
      else
        return '未知'
    }
  },
});
