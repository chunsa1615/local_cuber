var OrderModel = Backbone.Model.extend({
    idAttribute: 'orderId',
    defaults: function() {
        return {
            memberId: null,
            orderNote: null,
            orderStatus: -1,
        };
    },
    initialize: function() {


    },

});

var OrderListCollection = Backbone.Collection.extend({
    model: OrderModel,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/orders';
    }
});