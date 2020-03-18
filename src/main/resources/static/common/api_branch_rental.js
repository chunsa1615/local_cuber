var RentalModel = Backbone.Model.extend({
    idAttribute: 'id',
    defaults: function() {
        return {
          id:null, rentalId: null, goodsId:null, goodsIds:null, rentalType: null, memberId: null, rentalNote: null, is_returned: false, rentalDt: null,

        };
    },
});

var RentalListCollection = Backbone.Collection.extend({
    model: RentalModel,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/rentals';
    },
    getStats: function() {
        var total = 0;

       return {total: total};

    },
});
