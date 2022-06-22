const urlParams = new URLSearchParams (window.location.search)
const paramId = urlParams.get('id')
const id = paramId


Vue.createApp({
    data() {
      return {
        clientAccounts : [],
        selectedAccount : {},
        transactions: [],
        idAccountToDelete: "",
        balance : 0
      }
    },

    created(){
        axios.get('/api/clients/current')
            .then(data => {

              this.clientAccounts = data.data.account.sort(function(a,b){return a.id - b.id})
              this.clientAccounts = this.clientAccounts.filter(account => account.disable == false)

              this.selectedAccount =  this.clientAccounts.filter(cuenta => cuenta.id == id )
              this.selectedAccount = this.selectedAccount[0]

              this.balance = this.selectedAccount.balance.toLocaleString("de-DE")
              
              this.idAccountToDelete = this.selectedAccount.id

              this.transactions =  this.selectedAccount.transaction.sort(function(a,b){return b.id - a.id})
            })
    },

    methods:{
      deleteAccount(){
        Swal.fire({
          title: "Confirm delete",
          text: "Are you sure you want to delete this account?",
          icon: "question",
          showCancelButton: true,
          confirmButtonColor: '#2691d9',
          cancelButtonColor: '#d33',
          confirmButtonText: 'Yes'
      })
      .then((results) =>{
          if (results.isConfirmed){
            axios.patch(`/api/clients/current/accounts/${this.idAccountToDelete}`)
              .then(() =>
              Swal.fire({
                  icon: 'success',
                  title: 'Account deleted successfully',
                  showConfirmButton: false,
                  timer: 1500
              })
              .then(() => window.location.href = "/web/accounts.html")
              )
              .catch(error => {
                  console.log(error.response.data)
                  this.mensajeError = error.response.data
                  Swal.fire({
                    icon: 'error',
                    title: this.mensajeError,
                    confirmButtonColor: '#2691d9'
                  })
              } )
          } }
        )      
      },

      logOut(){
        axios.post('/api/logout')
          .then(() => window.location.href = "/web/index.html")
      }
    }
  }).mount('#app')