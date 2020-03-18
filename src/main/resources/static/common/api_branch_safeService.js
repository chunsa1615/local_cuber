
var SafeServiceModel = Backbone.Model.extend({
    idAttribute: 'id',
    defaults: function() {
        return {
//            startDt: '', name: '', gender: '', birthDt: '',
//            tel: '', email: '', cmpRoute: '', school: '',

        };
    },
});


var SafeServiceListCollection = Backbone.Collection.extend({
    model: SafeServiceModel,
    branchId: null,
    url: function() {
    	
    	return ctxRoot + 'api/v1/' + this.branchId + '/safeServiceList';
    	
    },
});