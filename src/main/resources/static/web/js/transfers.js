Vue.createApp({
    data() {
      return {
        clientAccounts: "",
        ownTransfer: true,
        originAccontSelected: [],
        originAccontSelectedNumber: "",
        destinationAccountSelectedNumber: "",
        othersAccounts: "",
        amountToTransfer: "",
        description: "",
        errorMessage: "",
        showBalance: false,
        balance: 0
      }
    },

    created(){
        axios.get('http://localhost:8080/api/clients/current')
        .then(data =>{
          this.clientAccounts = data.data.account.sort(function(a,b){return a.id - b.id})
          this.clientAccounts = this.clientAccounts.filter(account => account.disable == false)
           
        })
    },

    methods:{
      filterAccounts(){
        this.originAccontSelected = this.clientAccounts.filter(account => account.number == this.originAccontSelectedNumber)

        this.othersAccounts = this.clientAccounts.filter(account => account.number != this.originAccontSelectedNumber)

      },

      transfer(){
        Swal.fire({
          title: "Confirm transfer",
          html: `<p>Are you sure you want to confirm this transfer?</p>` +
           `<p>From: ${this.originAccontSelectedNumber} with a balance of $${this.originAccontSelected[0].balance.toLocaleString('de-DE')}  </p>` +
           `<p>To: ${this.destinationAccountSelectedNumber}</p>` +
           `<p>Amount to transfer: $${this.amountToTransfer.toLocaleString('de-DE')}</p>`,
          icon: "question",
          showCancelButton: true,
          confirmButtonColor: '#2691d9',
          cancelButtonColor: '#d33',
          confirmButtonText: 'Yes'
        })
        .then((results) =>{
          if (results.isConfirmed){
            axios.post('/api/clients/current/transactions', `amount=${this.amountToTransfer}&description=${this.description}&numberAccountOrigin=${this.originAccontSelectedNumber}&numberAccountTarget=${this.destinationAccountSelectedNumber}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(() =>
            Swal.fire({
              icon: 'success',
              title: 'Successful transfer',
              showConfirmButton: false,
              timer: 1500
            })
            .then(() => window.location.reload())
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
        }})  
      },

      logOut(){
        axios.post('/api/logout')
        .then(() => window.location.href = "http://localhost:8080/web/index.html")
      }
      
    }
} ).mount('#app')