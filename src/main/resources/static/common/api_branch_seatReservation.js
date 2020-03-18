
var SeatReservationModel = Backbone.Model.extend({
    idAttribute: 'applicationId',
    defaults: function() {
        return {
//            startDt: '', name: '', gender: '', birthDt: '',
//            tel: '', email: '', cmpRoute: '', school: '',

        };
    },
});


var SeatReservationListCollection = Backbone.Collection.extend({
    model: SeatReservationModel,
    branchId: null,
    url: function() {
    	
    	return ctxRoot + 'api/v1/' + this.branchId + '/seatReservationList';
    	
    },
});