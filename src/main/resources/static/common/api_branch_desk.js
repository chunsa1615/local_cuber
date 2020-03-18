var DeskModel = Backbone.Model.extend({
    idAttribute: 'deskId',
    defaults: function() {
        return {
            name: '', roomId: null, deskMax: '1', deskType: '10',
            t: 30, l: 30, h: 50, w: 90, deskNote: '',

        };
    },
});

var DeskListCollection = Backbone.Collection.extend({
    model: DeskModel,
    branchId: null,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/desks';
    },
});
