Vue.createApp({
    data() {
      return {
        cardsClient : [],
        cardToDelete : {},
        cardNumber : "",
        idCardToDelete : "",
        cardDateExpirity : "",
        cardYearExpirity : "",
        cardMonthExpirity : ""
      }
    },

    created(){
        axios.get('/api/clients/current')
            .then(data =>{
                this.cardsClient = data.data.cards.sort(function(a,b){return a.id - b.id})
                document.querySelector("#loader").classList.toggle("loader2")    
                this.cardsClient = this.cardsClient.filter(card => card.disable == false)

                this.cardsClient.forEach(card => card.cardholder = card.cardholder.toUpperCase())
                this.cardsClient.forEach(card => card.expiry = "")

                this.getExpiryDate(this.cardsClient)

                this.cardNumber = this.cardsClient[0].number

                this.cardToDelete = this.cardsClient[0]

                this.idCardToDelete = this.cardToDelete.id
            }
        )
    },
        
    methods:{

        getExpiryDate(cards){

            for(let i = 0; i < cards.length; i++){
                this.cardDateExpirity = cards[i].thruDate.split("-")
                this.cardMonthExpirity = this.cardDateExpirity[1]
                this.cardYearExpirity = this.cardDateExpirity[0]
                this.cardYearExpirity = this.cardDateExpirity[0]%2000

                cards[i].expiry = `${this.cardMonthExpirity}/${this.cardYearExpirity}`            
            }
        },

        selectCardToDelete(){
            this.cardToDelete = this.cardsClient.filter(card => card.number == this.cardNumber)
            this.cardToDelete = this.cardToDelete[0]
            this.idCardToDelete = this.cardToDelete.id
        },

        deleteCard(){
            Swal.fire({
                title: "Confirm delete",
                text: "Are you sure you want to delete this card?",
                icon: "question",
                showCancelButton: true,
                confirmButtonColor: '#2691d9',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes'
            })
            .then((results) =>{
                if (results.isConfirmed){
                    axios.patch(`/api/clients/current/cards/${this.idCardToDelete}`)
                    .then(() =>
                    Swal.fire({
                        icon: 'success',
                        title: 'Card deleted successfully',
                        showConfirmButton: false,
                        timer: 1500
                    })
                    .then(() => window.location.href = "/web/cards.html")
                    )
                    .catch(error => {
                        console.log(error.response.data)
                        this.mensajeError = error.response.data
                        Swal.fire({
                          icon: 'error',
                          title: this.mensajeError,
                          confirmButtonColor: '#2691d9'
                        })
                    })
                }   }
            )    
        },

        logOut(){
            axios.post('/api/logout')
            .then(() => window.location.href = "/web/index.html")
        }     
    }
    
}).mount('#app')