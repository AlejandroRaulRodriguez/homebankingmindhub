Vue.createApp({
  data() {
    return {
      clientCards : [],
      cardsDebit : [],
      cardsCredit : [],
      expirationMonth : "",
      expirationYear : "",
      dateComplete : "",
      cardNumber : [],
      type: "",
      color: "",
    }
  },


  created(){
    axios.get('/api/clients/current')
    .then(data =>{
      window.onload = function(){
        let loader = document.querySelector("#loader").classList.toggle("loader2")  
        
        loader.style.visibility = "hidden"
        loader.style.opacity = "0"
      }
      this.clientCards = data.data.cards
      this.clientCards = this.clientCards.filter(card => card.disable == false)
      this.clientCards.forEach(card => card.cardholder = card.cardholder.toUpperCase())
      this.clientCards.forEach(card => card.expiry = "")
      this.clientCards.forEach(card => card.last4Digits = "")

      this.getExpirationDate(this.clientCards)
      this.gateLast4Digits(this.clientCards)
                
      this.cardsDebit = this.clientCards.filter(cards => cards.type == "DEBIT").sort(function(a,b){return a.id - b.id})

      this.cardsCredit = this.clientCards.filter(cards => cards.type == "CREDIT").sort(function(a,b){return a.id - b.id})
    })
  },

  methods:{

    getExpirationDate(cards){
      for(let i = 0; i < cards.length; i++){
        this.dateComplete = cards[i].thruDate.split("-")
        this.expirationMonth = this.dateComplete[1]
        this.expirationYear = this.dateComplete[0]
        this.expirationYear = this.dateComplete[0]%2000
        cards[i].expiry = `${this.expirationMonth}/${this.expirationYear}`
      }
          
      
    },
    
    gateLast4Digits(cards){
      for(let i = 0; i < cards.length; i++){
        this.cardNumber = cards[i].number.split("-")

        cards[i].last4Digits = this.cardNumber[3]
      }
    },

    logOut(){
      axios.post('/api/logout')
      .then(() => window.location.href = "/web/index.html")
    },

  },

}).mount('#app')