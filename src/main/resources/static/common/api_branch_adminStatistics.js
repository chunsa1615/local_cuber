

var AdminStatisticsModel = Backbone.Model.extend({
    idAttribute: 'id',
    defaults: function() {
        return {
//            startDt: '', name: '', gender: '', birthDt: '',
//            tel: '', email: '', cmpRoute: '', school: '',

        };
    },
});

var AdminStatisticsCollection = Backbone.Collection.extend({
    model: AdminStatisticsModel,
    branchId: null,
    sDate: null,
    url: function() {
    	return ctxRoot + 'api/v1/branch/' + this.branchId + '/adminStatistics' + '?sDate=' + this.sDate;

    },
});
