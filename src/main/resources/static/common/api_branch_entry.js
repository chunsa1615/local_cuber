var EntryModel = Backbone.Model.extend({
    idAttribute: 'id',
    defaults: function() {
        return {
            deskId: null,
            entryDt: '', entryType: -1, memberId: null,
        };
    },
});

var EntryListCollection = Backbone.Collection.extend({
    model: EntryModel,
    branchId: null,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/entries';
    },
    getStats: function() {
        var totalUsage = 0;
        var previousEntryModel = null;

        _.each(this.models, function(model){
            if(previousEntryModel) {
                if(model.get('entryType') == 2 && previousEntryModel.get('entryType') == 1) {
                    var diff = moment(model.get('entryDt')).diff(previousEntryModel.get('entryDt'), 'minutes');
                    if(diff > 0) {
                        totalUsage += diff;

                    }

                }

            }

            previousEntryModel = model;

        });

        return {totalUsageHour: Math.floor(totalUsage / 60), totalUsageMinute: totalUsage % 60};
    },
});