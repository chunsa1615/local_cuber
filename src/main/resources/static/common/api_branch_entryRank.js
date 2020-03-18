

var EntryRankModel = Backbone.Model.extend({
    idAttribute: 'entryRAnkId',
    defaults: function() {
        return {
//            startDt: '', name: '', gender: '', birthDt: '',
//            tel: '', email: '', cmpRoute: '', school: '',

        };
    },
});

var EntryRankListCollection = Backbone.Collection.extend({
    model: EntryRankModel,
    branchId: null,
    sDate: null,
    url: function() {
    	return ctxRoot + 'e/api/v1/' + this.branchId + '/entryRank' + '?sDate=' + this.sDate;

    },
});
