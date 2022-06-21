Vue.createApp({
    data() {
        return {
        clients : [],
        jsonClients : [],
        nombreReg: "",
        apellidoReg: "",
        email: "",
        password: 1234,
        loanName: "",
        loanMaxAmount: "",
        loanInterestRate: "",
        loanPayments: [],


        clienteModal: {},
        nombreModal: "",
        apellidoModal: "",
        emailModal: "",

      }
    },

    created(){
        axios.get('http://localhost:8080/api/clients')
            .then(datos =>{
                this.jsonClients = datos.data
                this.clients = datos.data
            }
        )  
    },

    methods:{
        agregarCliente(){
            
            axios.post('/api/clients', `nombre=${this.nombreReg}&apellido=${this.apellidoReg}&email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(() => window.location.reload())
        },
        
        pasarClienteAlModal(cliente){
            this.clienteModal = cliente
        },

        editarCliente(clienteModal){
            let urlCliente = clienteModal._links.client.href
            axios.put(urlCliente,{

                nombre : this.nombreModal,
                apellido: this.apellidoModal,
                email: this.emailModal,
            })
            location.reload()
        },

        borrarCliente(cliente){
            let urlCliente = cliente._links.client.href
            axios.delete(urlCliente)
            location.reload()
        },

        createLoan(){
            Swal.fire({
                title: "Create loan?",
                showCancelButton: true,
            })
            .then((results) => {
                if (results.isConfirmed){
                    axios.post('/api/loans/createLoan', `name=${this.loanName}&maxAmount=${this.loanMaxAmount}&interestRate=${this.loanInterestRate}&payments=${this.loanPayments}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})

                .then(() => 
                Swal.fire({
                    title: "Loan Created"
                })
                .then(() => window.location.reload())
                )
                .catch(error => {
                    console.log(error.response.data)
                    this.mensajeError = error.response.data
                    Swal.fire({
                      icon: 'error',
                      title: this.mensajeError,
                      confirmButtonColor: '#2691d9'
                    })
                  }
                )
            }})
        },
    },

  }).mount('#app')