var MembershipCardModel = Backbone.Model.extend({
    idAttribute: 'id',
    defaults: function() {
        return {
            
        };
    },
});

var MembershipCardListCollection = Backbone.Collection.extend({
    model: EntryModel,
    branchId: null,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/members/membership';
    }
});