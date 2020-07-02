import Vue from 'https://cdn.jsdelivr.net/npm/vue@2.6.11/dist/vue.esm.browser.js'
import Vuex from 'https://cdn.jsdelivr.net/npm/vuex@3.1.2/dist/vuex.esm.browser.js'
Vue.use(Vuex)

export const store = new Vuex.Store({
  state: {
    user: null,
    users: []
  },
  mutations: {
    setUser(state, user) {
      state.user = user
    },
    setUsers(state, users) {
      state.users = users
    },
    appendUsers(state, user) {
      state.users.push(user)
    }
  },
  actions: {
    async fetchUsers(store) {
      let users = await fetch('/rest/users')
      users = await users.json()
      store.commit('setUsers', users)
    },
    async whoami(store) {
      let user = await fetch('/api/login')
      try {
        user = await user.json()
        console.log(user);
        store.commit('setUser', user)
      } catch {
        console.log("Not logged in");
      }
    },
    async login(store, data) {
      let user = await fetch('/api/login', {
        method: 'POST',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify({ 
          username: data.username, 
          password: data.password
        })
      })
      user = await user.json()
      console.log(user);

      store.commit('setUser', user)
    },
    logout(store) {
      fetch('/api/logout')  
      store.commit('setUser', null)
    },
    async register(store, data) {
      let user = await fetch('/api/register', {
        method: 'POST',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify({ 
          name: data.name, 
          username: data.username, 
          password: data.password
        })
      })
      user = await user.json()
      console.log(user);

      store.commit('setUser', user)
      store.commit('appendUsers', user)
    }
  }
})