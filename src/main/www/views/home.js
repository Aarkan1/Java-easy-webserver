import usersList from '../components/usersList.js'
import loginForm from '../components/loginForm.js'

export default {
  template: `
    <div>
      <h2 v-if="user">Logged in user: {{ user.username }}</h2>
      <h2 v-else>Not logged in</h2>
      <loginForm />
      <h3>Users from database:</h3>
      <usersList />
    </div>
  `,
   components: {
    usersList,
    loginForm
  },
  computed: {
    user() {
      return this.$store.state.user
    }
  }
}