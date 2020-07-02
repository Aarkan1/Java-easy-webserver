export default {
  template: `
    <div>
      <input placeholder="Enter name" type="text" v-model="name">
      <br>
      <input placeholder="Enter username" type="text" v-model="username">
      <br>
      <input @keyup.enter="login" placeholder="Enter password" type="password" v-model="password">
      <br>
      <button @click="login">Login</button>
      <button @click="register">Register</button>
      <button @click="logout">Logout</button>
    </div>
  `,
  data() {
    return {
      name: '',
      username: '',
      password: ''
    }
  },
  methods: {
    async logout() {
      this.$store.dispatch('logout')
    },
    async login() {
      this.$store.dispatch('login', { 
        username: this.username, 
        password: this.password
      })

      this.username = ''
      this.password = ''
    },
    register() {
      this.$store.dispatch('register', { 
        name: this.name, 
        username: this.username, 
        password: this.password
      })

      this.name = ''
      this.username = ''
      this.password = ''
    }
  }
}