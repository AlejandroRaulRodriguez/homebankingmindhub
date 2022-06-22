Vue.createApp({
    data() {
      return {
        clientAccounts: [],
        loansTypes: [],
        loanSelected: [],
        nameLoanSelected: "Mortgage",
        paymentsLoanSelected: [],
        numberPayments: 12,
        loanAmount: null,
        maxLoanAmount: null,
        nameAccountDestination: "",
        amountForPayment: null,
        totalAmountToPay: null,
        interestRate : null,
        errorMessage : ""

      }
    },

    created(){

      axios.get('/api/clients/current')
        .then(data =>{
          this.clientAccounts = data.data.account.sort(function(a,b){return a.id - b.id})
          this.clientAccounts = this.clientAccounts.filter(cuenta => cuenta.disable == false)
        })

        .then(axios.get('/api/loans')    
        .then(loans =>{
          this.loansTypes = loans.data
          this.selectLoan();
      } ))
   
    },

    methods:{

      selectLoan(){
        this.loanSelected = this.loansTypes.filter(loan => loan.name == this.nameLoanSelected)

        this.paymentsLoanSelected = this.loanSelected[0].payments

        this.maxLoanAmount = (this.loanSelected[0].maxAmount).toLocaleString("de-DE")

        this.interestRate = this.loanSelected[0].interestRate;
      },

      calculateAmountForPayment(){
        this.totalAmountToPay =(((this.loanAmount * this.interestRate) / 100) + this.loanAmount).toLocaleString("de-DE")
        this.amountForPayment = (((this.loanAmount * this.interestRate) / 100 + this.loanAmount) / this.numberPayments).toLocaleString("de-DE")
      },

      requestLoan(){
          this.calculateAmountForPayment()
          Swal.fire({
              title: "Confirm loan",
              html: `<p>Loan amount: $${this.loanAmount.toLocaleString('de-DE')}</p>` +
                `<p>Payments: ${this.numberPayments} of $${this.amountForPayment}</p>` + 
                `<p>Total to pay: $ ${this.totalAmountToPay}</p>`,
              icon: "question",
              showCancelButton: true,
              confirmButtonColor: '#2691d9',
              cancelButtonColor: '#d33',
              confirmButtonText: 'Confirm'
          } )
          .then((results) =>{
          if (results.isConfirmed){
              axios.post('/api/loans',{id:`${this.loanSelected[0].id}`,amount:`${this.loanAmount}`,payments:`${this.numberPayments}`,accountDestination:`${this.nameAccountDestination}`,headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(() =>
                Swal.fire({
                  icon: 'success',
                  title: 'Loan requested successfully',
                  showConfirmButton: false,
                  timer: 1500
                })
                .then(() => window.location.href = "/web/accounts.html")
                )
                .catch(error => {
                  console.log(error.response.data)
                  this.errorMessage = error.response.data
                  Swal.fire({
                    icon: 'error',
                    title: this.errorMessage,
                    confirmButtonColor: '#2691d9'
                  })
                }
              )
        } } )  
      },

      logOut(){
          axios.post('/api/logout')
          .then(() => window.location.href = "/web/index.html")
      }
    },


}).mount('#app')