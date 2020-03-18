var NotificationModel = Backbone.Model.extend({
    idAttribute: 'id',
    defaults: function() {
        return {
        	smsDt : null, memberId : null, msg : null, resultCode : null,
        };
    },
});

var NotificationListCollection = Backbone.Collection.extend({
    model: NotificationModel,
    branchId: null,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/notifications';
    },
//    getStats: function() {
//    	var totalNotification = 0;
//    	
//    	_.each(this.models, function(model){
//    		totalNotification += 1;
//    	});
//
//        return {totalNotification: totalNotification};
//    },
});    