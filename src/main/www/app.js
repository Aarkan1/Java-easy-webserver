export default {
  template: `
    <div id="app">
      <nav>
        <router-link to="/">Home</router-link>
        <router-link to="/about">About</router-link>
      </nav>

      <main>
        <router-view />
      </main>
    </div>
  `,
  async created() {
    this.$store.dispatch('whoami')
    this.$store.dispatch('fetchUsers')
  },
  mounted() {
    console.log(M);
    M.AutoInit()
  }
}