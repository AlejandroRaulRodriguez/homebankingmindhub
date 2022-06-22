Vue.createApp({
    data() {
      return {
        email: "",
        password: "",
        nameReg: "",
        lastNameReg: "",
        showLogIn: true,
        errorMessage: "",
      }
    },

    created(){

    },

    methods: {

      logIn(){
        axios.post('/api/login' , `email=${this.email}&password=${this.password}` , {headers:{'content-type':'application/x-www-form-urlencoded'}})
        .then(() => axios.patch("/api/clients/current/cards"))
        .then(() => window.location.href = "/web/accounts.html")
        .catch(error => {
          console.log(error.response.data.error)
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Wrong email or password!',
            confirmButtonColor: '#2691d9'
          })
        })
      },

      register(){
        axios.post('/api/clients', `name=${this.nameReg}&lastName=${this.lastNameReg}&email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
        .then(() => 
          Swal.fire({
            icon: 'success',
            title: 'You have registered successfully',
            showConfirmButton: false,
            timer: 1500
          })
        )
        .then(() => this.logIn())
        .catch(error => {
          console.log(error.response.data)
          this.errorMessage = error.response.data
          Swal.fire({
            icon: 'error',
            title: this.errorMessage,
            confirmButtonColor: '#2691d9'
          })
        })
      },

    }
  }).mount('#app')