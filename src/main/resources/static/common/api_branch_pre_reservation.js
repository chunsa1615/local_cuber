var PreReservationModel = Backbone.Model.extend({
    idAttribute: 'preReservationId',
    defaults: function() {
        return {
          
        };
    },
});

var PreReservationListCollection = Backbone.Collection.extend({
    model: PreReservationModel,
    branchId : null,
    url: function() {
        return ctxRoot + 'e/api/v1/'+ this.branchId + '/pre';
    }
});