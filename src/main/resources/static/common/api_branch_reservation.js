var ReservationModel = Backbone.Model.extend({
    idAttribute: 'reservationId',
    defaults: function() {
        return {
            memberId: null, deskId: null, reservationNum: 1,
            deskStartDt: null, deskEndDt: null,
            reservationNote: null, checkoutYn: 0,

        };
    },
});

var ReservationListCollection = Backbone.Collection.extend({
    model: ReservationModel,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/reservations';
    }
});