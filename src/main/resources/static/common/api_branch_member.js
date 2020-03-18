var MemberModel = Backbone.Model.extend({
    idAttribute: 'memberId',
    defaults: function() {
        return {
            regDt:'', no: '', name: '',
            tel: '', telParent: '', telEtc: '', email: '',
            school: '', schoolGrade: -1,
            postcode:'', birthDt: '', gender: -1, address1: '', address2: '', addressDetail: '', membershipNo: null, enterexitYes : 0, smsYes : 0, promotionYes : 0
            , memberNote :'', cabinet :'', checkoutYn : 0, examType : -1, jobType : 0, interestType : 0, personalYn : 1, utilYn : 1, timeYn : ''
        };
    },
});

var MemberListCollection = Backbone.Collection.extend({
    model: MemberModel,
    branchId: null,
    url: function() {
        return ctxRoot + 'api/v1/branch/' + this.branchId + '/members';
    },
});