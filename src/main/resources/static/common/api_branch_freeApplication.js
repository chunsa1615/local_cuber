

var FreeApplicationModel = Backbone.Model.extend({
    idAttribute: 'freeApplicationId',
    defaults: function() {
        return {
//            startDt: '', name: '', gender: '', birthDt: '',
//            tel: '', email: '', cmpRoute: '', school: '',

        };
    },
});

var FreeApplicationListCollection = Backbone.Collection.extend({
    model: FreeApplicationModel,
    branchId: null,
    url: function() {
    	return ctxRoot + 'e/api/v1/' + this.branchId + '/try';
    	//return ctxRoot + 'api/v1/branch/7ebf50c6-3b2e-41b0-9272-55c5bb80a839/free_applications';    	
    	//return ctxRoot + 't/' + this.branchId + '/free_applications' ;
    },
});
