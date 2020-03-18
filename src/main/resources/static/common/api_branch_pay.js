var PayModel = Backbone.Model.extend({
    idAttribute: 'payId',
    defaults: function() {
        return {
            payDt: null, payType: 0, payAmount: 0,
            memberId: null, payNote: null,

        };
    },
});

var PayListCollection = Backbone.Collection.extend({
    model: PayModel,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/pays';
    },
    getStats: function() {
        var total = 0;

        _.each(this.models, function(model){
            total += model.get('payAmount');

        });

        return {total: total};

    },
});