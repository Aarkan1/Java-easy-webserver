export default {
  template: `
    <ul>
      <li v-for="user of users" :key="user.id">
        Name: {{ user.name }}
      </li>  
    </ul>
  `,
  computed: {
    users() {
      return this.$store.state.users
    }
  }
}