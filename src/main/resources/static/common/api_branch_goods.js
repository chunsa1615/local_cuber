var GoodsModel = Backbone.Model.extend({
    idAttribute: 'goodsId',
    defaults: function() {
        return {
           rentalType : null, goodsNote : null,

        };
    },
});

var GoodsListCollection = Backbone.Collection.extend({
    model: GoodsModel,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/goods';
    },

});
