var ExpenseModel = Backbone.Model.extend({
    idAttribute: 'expenseId',
    defaults: function() {
        return {
            expenseDt: null, payInOutType: 10, payType: 0, expenseOption: 0, expenseGroup: 0,
            expenseAmount: 0, expenseNote: null,

        };
    },
});

var ExpenseListCollection = Backbone.Collection.extend({
    model: ExpenseModel,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/expenses';
    },

});