Vue.createApp({
    
  data() {
    return {
      clientData : [],
      clientAccounts : [],
      clientLoans : [],
      accountsTransactions : [],
      errorMessage : "",
      typeAccount : "",
      firstDate : new Date().toISOString().split("T")[0],
      secondDate : new Date().toISOString().split("T")[0],
      maxDate : new Date().toISOString().split("T")[0],
      showFilter : false,
      activeFilter : false,
      filteredTransactions : [],
    }
  },

  created(){

    axios.get('/api/clients/current')
    .then(data =>{
      this.clientData = data.data
              
      this.clientAccounts = data.data.account.sort(function(a,b){return a.id - b.id})
      this.clientAccounts = this.clientAccounts.filter(account => account.disable == false)

      this.clientLoans = data.data.loans.sort(function(a,b){return b.id - a.id})

      this.getAllTransactions()

    })
  },

  methods:{
    logOut(){
      Swal.fire({
        title: "Log Out",
        text: "Are you sure you want to go out?",
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: '#2691d9',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes'
      })
      .then((results) =>{
        if (results.isConfirmed){
          axios.post('/api/logout')
          .then(response => window.location.href = "http://localhost:8080/web/index.html")
        }
      })
    },

    selectAccountType(){
      Swal.fire({
        title: "Select the account type",
        input: "select",
        inputOptions: {
          SAVINGS: "Savings",
          CURRENT: "Current",
          SALARY: "Salary",
        },
        showCancelButton: true,
        inputValidator: (value) => {
          this.typeAccount = value
        }
      })
      .then((results) =>{
        if (results.isConfirmed){
        this.createAccount()
      }})  
    },

    createAccount(){
      Swal.fire({
        title: "Create new account",
        text: "Are you sure you want a new account?",
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: '#2691d9',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes'
      })
      .then((results) =>{
        if (results.isConfirmed){
          axios.post('/api/clients/current/accounts',`type=${this.typeAccount}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
          .then(() => 
          Swal.fire({
            icon: 'success',
            title: 'Account created successfully',
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
              title: this.errorMessage + '!',
              confirmButtonColor: '#2691d9'
            })
          })
        }
      }) 
    },

    getAllTransactions(){
      for(let i = 0; i < this.clientAccounts.length; i++){
        this.clientAccounts[i].transaction.forEach(account => {
          this.accountsTransactions.push(account)
        })
      };

      this.accountsTransactions.sort(function(a,b){return b.id - a.id})
    },

    filterTransactions(){

      if (this.firstDate > this.secondDate) {
        Swal.fire({
          icon: 'error',
          title: 'Error applying filters',
          text: 'Make sure the SINCE date is before or equal to the UNTIL date.',
          confirmButtonColor: '#2691d9'
        })
      }
      else{
        this.filteredTransactions = this.accountsTransactions.filter(transaction => transaction.localDate >= this.firstDate && transaction.localDate <= this.secondDate)
        
        if (this.filteredTransactions.length > 0){
          Swal.fire({
            position: 'top-end',
            icon: 'success',
            title: 'Filter applied successfully',
            showConfirmButton: false,
            timer: 1000
          })
          this.activeFilter = true
        } else {
          Swal.fire({
            icon: 'info',
            title: 'You have no transactions in that time period'
          })
        }
      }
    }
  }

}).mount('#app')