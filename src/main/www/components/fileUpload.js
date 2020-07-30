export default {
  template: `
    <div>
      <input 
        type="file" 
        accept="image/*" 
        multiple 
        @change="readFiles"
      >
      <button @click="uploadFiles">Upload</button>
    </div>
  `,
  data() {
    return {
      files: [],
      images: []
    }
  },
  methods: {
    async uploadFiles() {
      let res = await fetch('/api/upload-files', {
        method: 'POST',
        body: this.files
      });

      console.log(await res.json());

      this.files = null
      this.images = []
    },

    readFiles(e) {
      let formData = new FormData()

      formData.append('name', 'Some random name')

      for(let file of e.target.files) {
        formData.append('files', file, file.name)
      }

      this.files = formData
    },

  }

}