Vue.createApp({
  data() {
    return {
      type: "DEBIT",
      color: "SILVER",
      errorMessage: ""
    }
  },

  created(){
    
    window.onload = function(){
      let loader = document.querySelector("#loader").classList.toggle("loader2")  
      
      loader.style.visibility = "hidden"
      loader.style.opacity = "0"
    }
    
  },

  methods:{

    createCard(){
      Swal.fire({
        title: "Confirm card create",
        text: "Are you sure you want to create this card?",
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: '#2691d9',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes'
      })
      .then((results) =>{
        if (results.isConfirmed){
          axios.post('/api/clients/current/cards', `type=${this.type}&color=${this.color}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
          .then(() =>
          Swal.fire({
            icon: 'success',
            title: 'Card created successfully',
            showConfirmButton: false,
            timer: 1500
          })
          .then(() => window.location.href = "/web/cards.html")
          )
          .catch(error => {
            console.log(error.response.data)
            this.errorMessage = error.response.data
            Swal.fire({
              icon: 'error',
              title: this.errorMessage,
              confirmButtonColor: '#2691d9'
            })
          })
      } })
    },

    logOut(){
      axios.post('/api/logout')
      .then(() => window.location.href = "/web/index.html")
    }    

  }
  
}).mount('#app')