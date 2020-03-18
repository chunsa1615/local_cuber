var RoomModel = Backbone.Model.extend({
    idAttribute: 'roomId',
    defaults: function() {
        return {
            name: '', roomNote: '', roomType: 10,
            t: 30, l: 30, h: 100, w: 300, 
        };
    },
});

var RoomListCollection = Backbone.Collection.extend({
    model: RoomModel,
    branchId: null,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/rooms';
    },
});