import { defineStore } from 'pinia';

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
    name: '咕咕咕',
    login: true,
    force_chpwd: true
  }),
  actions: {
    async load_user_list(start: number, count: number) {
      return {all: demo_list.length, data: demo_list.slice(start, count)}
    }
  },
});
