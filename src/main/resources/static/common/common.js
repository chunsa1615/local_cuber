// underscore template - thymeleaf
// http://qiita.com/nenokido2000/items/b93f79232445461eeba1
_.templateSettings = {
    evaluate:    /\{\{(.+?)\}\}/g,
    interpolate: /\{\{=(.+?)\}\}/g,
    escape:      /\{\{-(.+?)\}\}/g
};

var datepickerFormat = 'yyyy년 m월 d일';
var momentDatepickerFormat = 'YYYY년 M월 D일';
var momentClockpickerFormat = 'HH:mm';

// jQuery ajax
// http://api.jquery.com/jQuery.ajax/
$.ajaxSetup({
    global: true,
    beforeSend: function() {
        showLoadingOverlay();
    },
    complete: function(jqXHR, textStatus){
        hideLoadingOverlay();
        /*
        if(jqXHR.responseText.startsWith('<!DOCTYPE HTML>')) {
            location.assign(ctxRoot);

        }
        */

    },

});

function showLoadingOverlay() {
    $('body').append("<div class='loading-overlay'><i class='fa fa-refresh fa-spin'></i></div>");
}

function hideLoadingOverlay() {
    $('body .loading-overlay').remove();

}

function momentDateFormat(v) {
    if (!_.isNumber(v)) {
        return moment(Number(v)).format('YYYY년 M월 D일');

    } else {
        return moment(v).format('YYYY년 M월 D일');

    }

}


function momentTimeFormat(v) {
    //return moment(v, 'HH:mm:ss').format('H시 m분');
    return moment(v, 'HH:mm:ss').format('HH:mm');

}

//
// http://stackoverflow.com/a/2117523/3614964
function guid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
    return v.toString(16);
  });
/*
  // http://stackoverflow.com/a/105074/3614964
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
*/
}

function getMembersNo(branchId, successHandler) {
    $.ajax(ctxRoot + 'api/v1/branch/' + branchId + '/members/No', {
        success: successHandler
    });
}

function getMembersNextNo(branchId, successHandler) {
    $.ajax(ctxRoot + 'api/v1/branch/' + branchId + '/members/nextNo', {
        success: successHandler
    });
}

function getPaysNextNo(branchId, successHandler) {
    $.ajax(ctxRoot + 'api/v1/branch/' + branchId + '/pays/nextNo', {
        success: successHandler
    });
}

function getPayListforReservationDelete(branchId, orderId, successHandler) {
    $.ajax(ctxRoot + 'api/v1/branch/' + branchId + '/orders/' + orderId + '/pays/list', {
        success: successHandler
    });
}

function getRoomsName(branchId, successHandler) {
    $.ajax(ctxRoot + 'api/v1/branch/' + branchId + '/design/room/name', {
        success: successHandler
    });
}

function getDesksName(branchId, successHandler) {
    $.ajax(ctxRoot + 'api/v1/branch/' + branchId + '/design/desk/name', {
        success: successHandler
    });
}

function getPostcode(oncomplete) {
    // Daum 우편번호 서비스 : http://postcode.map.daum.net/guide
    daum.postcode.load(function(){
        new daum.Postcode({
            oncomplete: oncomplete
        }).open();
    });
}

function resizeContent(target) {
    var $content = target ? $(target) : $('#content');
    $content.height($(window).height() - $content.offset().top - $('footer').height() - 40);

}

/*
$element.on('touchstart mousedown', (e) => {
  e.preventDefault();

  return false;
});
*/

// https://codepen.io/cRckls/pen/mcGCL
function scalePages(target) {
    var $container = $(window);

    var maxWidth = $container.width() * 0.8;
    var maxHeight = Math.min(maxWidth / 2, $container.height());

    var basePage = {
        width: 800,
        height: 600,
        scale: 1,
        scaleX: 1,
        scaleY: 1
    };

    var scaleX = 1, scaleY = 1;
    scaleX = maxWidth / basePage.width;
    scaleY = maxHeight / basePage.height;
    basePage.scaleX = scaleX;
    basePage.scaleY = scaleY;
    basePage.scale = (scaleX > scaleY) ? scaleY : scaleX;

//    var newLeftPos = Math.abs(Math.floor(((basePage.width * basePage.scale) - maxWidth)/2));
//    var newTopPos = Math.abs(Math.floor(((basePage.height * basePage.scale) - maxHeight)/2));
//
//    page.attr('style', '-webkit-transform:scale(' + basePage.scale + ');left:' + newLeftPos + 'px;top:' + newTopPos + 'px;');

    target.attr('style', '-webkit-transform:scale(' + basePage.scale + ');left:0px;top:0px;');

}

//입력받은 날짜 형식 'YYYY-MM-DD' 만드는 함수                                           
function makeDateFormat(inputDate) {
	var reservationDeskDt = inputDate;
	reservationDeskDt = reservationDeskDt.replace(/ /g, '');
	reservationDeskDt = reservationDeskDt.replace('년','-');
	reservationDeskDt = reservationDeskDt.replace('월','-');
	reservationDeskDt = reservationDeskDt.replace('일','');

	//month가 한 자리수인 경우 강제로 0을 붙여준다
	var temp = reservationDeskDt.substring(0,7);
	var results = temp.match(/-/g);
	if (results != null) {
		if (results.length == 2) { // '-' 가 2개면 month는 한자리
			var splitTemp = reservationDeskDt.split('-');
			reservationDeskDt = splitTemp[0] + '-0' + splitTemp[1] + '-' + splitTemp[2];
		}
		else if (results.length == 1) { // '-' 가 1개면 month는 두자리
			
		}
		
		//전체 자리수로 day의 자리수 확인
		if(reservationDeskDt.length == 9) { //day 가 한자리
			var splitTemp = reservationDeskDt.split('-');
			reservationDeskDt = splitTemp[0] + '-' + splitTemp[1] + '-0' + splitTemp[2];
		}
		else if(reservationDeskDt.length == 10) { //day 가 두자리
			
		}
	}
	return reservationDeskDt;
}

//페이징
function paging(e) {
	var page = $(e.target).text();
    var url = location.href;
    url = url.replace('action=newReservation&', '');
    var newUrl = "";
    var index = url.indexOf("page");
    var indexSearch = url.indexOf("?");
    
    if(indexSearch == -1) {
    	if(index == -1)
       		newUrl = url + "?page=" + page;
       	else {
       		newUrl = url.substring(0, index) + "page=" + page;           		
       	}	
    }
    else {
    	if(index == -1)
       		newUrl = url + "&page=" + page;
       		
       	else {
       		newUrl = url.substring(0, index) + "page=" + page;           		
       	}
    }
    
	location.assign(newUrl);
	
}
function alimtalkSend(Alimtalk, branchId) {
	jQuery.ajax({
        method : "POST",
        url : "/api/v1/branch/" + branchId + "/alimTalk",        
        contentType: "application/json; charset=UTF-8",
        data : JSON.stringify(Alimtalk),
        dataType : "JSON" ,
        success : function() {
           //alert("Suceeded!");         
        },
        error : function() {
           //alert("Failed!");
        }
    });

}
function prevPaging(e, page) {
	//var page = [[${pageMaker.startPage -1}]];
    var url = location.href;
    var newUrl = "";
    var index = url.indexOf("page");
    var indexSearch = url.indexOf("?");
    
    if(indexSearch == -1){
    	if(index == -1)
       		newUrl = url + "?page=" + page;
       		
       	else {
       		newUrl = url.substring(0, index) + "page=" + page;           		
       	}	
    }
    else {
    	if(index == -1)
       		newUrl = url+ "&page=" + page;
       		
       	else {
       		newUrl = url.substring(0, index) + "page=" + page;           		
       	}
    }
    
	location.assign(newUrl);    
}

function nextPaging(e, page) {
	//var page = [[${pageMaker.endPage +1}]];
    var url = location.href;
    var newUrl = "";
    var index = url.indexOf("page");
    var indexSearch = url.indexOf("?");
    
    if(indexSearch == -1){
    	if(index == -1)
       		newUrl = url + "?page=" + page;
       		
       	else {
       		newUrl = url.substring(0, index) + "page=" + page;           		
       	}	
    }
    else {
    	if(index == -1)
       		newUrl = url+ "&page=" + page;
       		
       	else {
       		newUrl = url.substring(0, index) + "page=" + page;           		
       	}
    }
    
	location.assign(newUrl);        	
}


function AddComma(data_value) {
	var temp = ''+data_value+'';
	data_value = temp.replace(/,/,'');
	return Number(data_value).toLocaleString('en');
}

function formReservation(model, ReservationList, MemberList, DeskList, RoomList, PayList, payTypes, flag, branchType, branchId, branchTel) {
	var ReservationOfOrderView = Backbone.View.extend({
        tagName: 'li',
        className: 'reservationOfOrder list-group-item',
        template: _.template($('#reservation-of-order-template').html()),
        events: {
            'click .btn-form-change-reservation': 'formReservationOfOrder',
            'click .btn-deleteTest-reservation': 'deleteReservationOne',
            'click .btn-add-cost' : 'formAddPay',
            'scannerDetected' : 'scannerDetected'
            
        },
        initialize: function() {
            this.listenTo(this.model, 'change', this.change);
            this.listenTo(this.model, 'destroy', this.remove);
            var monthFlag = 0;

        },
        change: function() {
            this.render();

        },
        render: function() {
        	//수정 버튼 눌렀을 때
        	var data = this.model.toJSON();                 	
        	
	        	data['member'] = MemberList.get(this.model.get('memberId')).toJSON();
	        	data['branchType'] = branchType;
	        	
	            var modelDesk = DeskList.get(this.model.get('deskId'));
	
	            if(data['deskId'] == null || data['deskId'] == '') {
	            	data['desk'] = "";
	            	data['room'] = "";
	            }
	            else {
		            data['desk'] = modelDesk.toJSON();
		            data['room'] = RoomList.get(modelDesk.get('roomId')).toJSON();
	            }
	            
	            this.el.id = data.reservationId;
	           
	            //style=
	            //현재시간 기준 과거등록 데이터
	    		var now = moment();
				var strDeskStartDtTm = moment(this.model.get('deskStartDt')).format("YYYY-MM-DD") + this.model.get('deskStartTm');
				var strDeskEndDtTm = moment(this.model.get('deskEndDt')).format("YYYY-MM-DD") + this.model.get('deskEndTm');
				var strDeskEndDt = moment(this.model.get('deskEndDt')).format("YYYY-MM-DD");
				
				var deskStartDtTm = new moment(strDeskStartDtTm, 'YYYY-MM-DD HH:mm:ss');
				var deskEndDtTm = new moment(strDeskEndDtTm, 'YYYY-MM-DD HH:mm:ss');   
	            var deskEndTm = new moment(this.model.get('deskEndTm'), 'HH:mm:ss');
	            
	            var today = new Date();
	            var year = today.getFullYear();            
	            var month = (today.getMonth() + 1);
	            if (month < 10) {
	            	month = '0' + month; 
	            }
	            var day = today.getDate();
	            if (day < 10) {
	            	day = '0' + day;
	            }
	            
	            var todayDate = year + '-' + month + '-' + day;
	            if (todayDate == strDeskEndDt) {
			            if (now.isAfter(deskEndDtTm)) {
							data['pastData'] = 1;
							
							var compareTime = Math.abs(deskEndTm.diff(now, 'minutes'));
							//시간 계산
							var hour = parseInt(compareTime / 60);
							var minute = parseInt(compareTime % 60);
							//추가비용 계산 (20분당 500원)
							var addCost = parseInt(compareTime / 20);
							var addCostRemainder = parseInt(compareTime % 20);
			
							var totalAddCost;
							if (addCostRemainder != 0) { //0이 아닐경우 500원 추가 발생
								totalAddCost = (addCost * 500) + 500;
							}
							else {
								totalAddCost = (addCost * 500);
							}
							
							data['excessTime'] = hour + '시간 ' + minute + '분 초과';
							data['addCost'] = AddComma(totalAddCost);
							this.model.set({addCost: data['addCost']});
							this.model.set({payNote: data['excessTime']});
						}
						else {
							data['pastData'] = 0;
						}            	
	            }
	            else {
	            	data['pastData'] = 0;
	            }
	            
	            this.$el.html(this.template(data));
	            

	            if(data.reservationStatus != 20) { 
	            	this.$el.find('span').addClass('text-muted text-strike'); 
	            	this.el.style = "display:none";
	            }
	            
	            return this;
            

        },
        scannerDetected: function() {
        	var memberNo = this.$('#membership').val();
        	var member = MemberList.findWhere({no: memberNo});
        	var memberId = member.get('memberId');

        	this.$('#membership').val('');
        	
        	this.$('#memberId').val(memberId).trigger('chosen:updated');

        },
        formReservationOfOrder: function(e) {
        	this.model.collection.trigger('formReservationOfOrder', this.model);

        },
        deleteReservationOne: function(e) {
        	this.model.collection.trigger('deleteReservationOne', this.model);

        },
        formAddPay : function() {
        	this.model.collection.trigger('formAddPay', this.model);
        },        
        formAddRental : function() {
        	this.model.collection.trigger('formAddRental', this.model);
        }, 
    });



	var PayOfOrderView = Backbone.View.extend({
        tagName: 'li',
        className: 'payOfOrder list-group-item',
        template: _.template($('#pay-of-order-template').html()),
        //template: _.template($('#pay-form-template').html()),
        events: {
        	'click .btn-form-cancel-pay' : 'cancelPayOfOrder',
        	'click .btn-save-pay' : 'savePay',
        	//'click #btn-add-pay' : 'test', //test
        },
        initialize: function() {
            this.listenTo(this.model, 'change', this.change);
            this.listenTo(this.model, 'destroy', this.remove);

        },
        change: function() {
            this.render();

        },
        destroy: function() {
        	this.render();
        },
        render: function() {
            var data = this.model.toJSON();
            data['member'] = MemberList.get(this.model.get('memberId')).toJSON();
            data['members'] = MemberList.toJSON();
			data['payTypes'] = payTypes;
			data['payAmount'] = AddComma(data['payAmount']);
            this.el.id = data.payId;

            this.$el.html(this.template(data));
            
            if(data.payStateType == 0){ 
            	this.$el.find('span').addClass('text-muted text-strike');
	            //this.el.style = "display:none";	                        
            }
            
            return this;

        },        
        
        cancelPayOfOrder: function() {
        	this.model.collection.trigger('cancelPayOfOrder', this.model);
        	
        },
        savePay: function() {
        	this.model.collection.trigger('savePay', this.model);
        },

    });

	var flagClick = "";
	var EditReservation_flag = false;
	this.currentReservationModel = model;
    var ModalOrderFormView = Backbone.View.extend({
        el: $('#modal-order-form'),
        currentReservationModel: model,
        currentReservationOfOrderModel: null,
        currentPayOfOrderModel: null,
        currentRentalOfOrderModel:null,
        templateForReservationForm: _.template($('#reservation-form-template').html()),        
        templateForPayForm: _.template($('#pay-form-template').html()),
        templateForAddReservationForm: _.template($('#addReservation-form-template').html()),
        template: _.template($('#reservation-of-order-template').html()),
        events: {
            'click .changeReservation .btn-save-reservation': 'saveChangeReservationOfOrder',
            'click .addReservation .btn-save-reservation': 'saveAddReservationOfOrder',
            'click .changeReservation .btn-do-not-save-reservation': 'doNotSaveChangeReservationOfOrder',
            'click .addReservation .btn-do-not-save-reservation': 'doNotSaveAddReservationOfOrder',
            'click .btn-form-add-reservation': 'formAddReservation',
			'click .btn-form-add-pay': 'formAddPay',						
			'click .addPay .btn-add-pay' : 'addPay',
			'click .addPay .btn-refund' : 'refundPay',
			'click .btn-form-add-rental': 'formAddRental',						
			'click .addPay .btn-add-rental' : 'addRental',		
			'click .addPay .btn-do-not-add-pay' : 'doNotAddPay',
			'click .addPay .btn-do-not-add-rental' : 'doNotAddRental',
			'click .btn-form-checkout' : 'checkout',
            'click .btn-set-time' : 'setReservationTime',            
            'click #btn-search-membership' : 'changeMemberShip',
        	'changeMemberShip' : 'changeMemberShip',
        	'keyup #payAmount' : 'keyup',
        	'click #btn-delete-reservation' : 'deleteReservation',
        	'click #btn-checkout': 'checkoutReservation',
        	'click .btn-form-history' : 'formHistory',
        	'click .btn-form-clear' : 'formClear',
        	'click .orderForm .reservationOfOrder' : 'selectReservation',

        },
        selectReservation: function(e) {
        	//debugger;
        	//if(flagRoom != $(e.target).text()) {
        	
        	if(flagClick != e.currentTarget.getAttribute('id')) {
        		if (flagClick != "" && flagClick != null) {
	        		document.querySelector('.reservationOfOrder.active #btn-form-add-pay').style.color = "#0275d8";
	        		document.querySelector('.reservationOfOrder.active #btn-form-edit-reg').style.color = "#0275d8";
	        		
	         		var payTemp = PayList.filter({ reservationId : flagClick });

	         		if (payTemp.length > 0) {
	                	for (var i=0; i < payTemp.length; i++){
	                		this.$el.find('.payOfOrder' + '#' + payTemp[i].attributes.payId).removeClass('active');
	                	}
	               
	                }
        		}
        		var targetId = e.currentTarget.getAttribute('id');
        		
        		this.$el.find('.reservationOfOrder' + '#' + targetId).addClass('active');

         		//pay부분

         		
	            this.$el.find('.reservationOfOrder' + '#' + targetId).siblings()
                .removeClass('active');
	            
         		//pay부분
	            var payTemp = PayList.filter({ reservationId : targetId });
	            
         		if (payTemp.length > 0) {
                	for (var i=0; i < payTemp.length; i++){
                		this.$el.find('.payOfOrder' + '#' + payTemp[i].attributes.payId).addClass('active');
                	}
               
                }
	            
	            
	            if (flagClick != "" && flagClick != null) {
	            	e.currentTarget.querySelector('#btn-form-add-pay').style.color = "#0275d8";
	            	e.currentTarget.querySelector('#btn-form-edit-reg').style.color = "#0275d8";
	            }

	            
	            document.querySelector('.reservationOfOrder.active #btn-form-add-pay').style.color = "#fff";
         		document.querySelector('.reservationOfOrder.active #btn-form-edit-reg').style.color = "#fff";
        		
	            

         		
         		flagClick = e.currentTarget.getAttribute('id');
	            
	            
        	}
        	else {        		
        		flagClick = e.currentTarget.getAttribute('id');
        	}
        },

        change: function(e) {
            var keys = _.keys(this.model.changedAttributes());
            if (_.without(keys, 'w', 'h', 't', 'l', 'designChanged').length == 0){

            } else {
                this.render();

            }

        },
        formHistory: function() {
        	
        	model.reservationsOfOrder.forEach(function(item) {
        		 
        		if (item.attributes.reservationStatus != 20) {
        			
        			$('#'+item.attributes.reservationId).show();

        			pay = model.paysOfOrder.findWhere({ reservationId : item.attributes.reservationId });
        			if (pay.attributes.payStateType == 0) {
        				$('#'+pay.attributes.payId).show();
        			}
        		}

        			           
        	});

	            
        },
        
        formClear: function() {
        	
        	var branchId = model.attributes.branchId;
        	var memberId = model.attributes.memberId;
        	
        	jQuery.ajax({
                method : "POST",       
                url : "/api/v1/branch/" + branchId + "/members/" + memberId + "/expireYn",              
                //data : JSON.stringify(reservationCheckout),
                contentType: "application/json;charset=utf-8",
                dataType : 'json',
                success : function(id) {
                   alert("배석해제 성공");
                   location.reload();
                },
                error : function(id) {
                   alert("배석해제 실패");
                }
            });
        	
        },
        
    	checkoutReservation: function() {
    	
    	reservationModels = model.reservationsOfOrder;


    	var $modal = $('#modal-order-form');
    	if(confirm('퇴실처리 하시겠습니까?')) {
    		var reservationCheckout = new Array();
    		
        	reservationModels.forEach(function(item) {
        		reservationCheckout.push(item);  
        	});            	
	        
    	}
    	var id = reservationCheckout[0].attributes.branchId;
    			// Checkout	        		
        		if (id != '') {
                	
                	jQuery.ajax({
                        method : "POST",       
                        url : "/api/v1/branch/" + id + "/reservations/checkout",              
                        data : JSON.stringify(reservationCheckout),
                        contentType: "application/json;charset=utf-8",
                        dataType : 'json',
                        success : function(id) {
                           //alert("Suceeded!");         
                        },
                        error : function(id) {
                           //alert("Failed!");
                        }
                    });
                	
                }
                else {
                	alert('알 수 없는 오류');            	
               	}
	        	

    	location.reload();
        	

        	
        },
        
        initialize: function() {
            this.listenTo(model.reservationsOfOrder, 'add', this.addOneReservationOfOrder);
            this.listenTo(model.paysOfOrder, 'add', this.addOnePayOfOrder);      
            
            this.listenTo(model.paysOfOrder, 'destroy', this.changeReservation);

            this.listenTo(model.reservationsOfOrder, 'reset', this.resetReservationsOfOrder);
            this.listenTo(model.paysOfOrder, 'reset', this.resetPaysOfOrder);

            this.listenTo(model.reservationsOfOrder, 'formReservationOfOrder', this.formReservationOfOrder);
            this.listenTo(model.reservationsOfOrder, 'deleteReservationOne', this.deleteReservationOne);
            
            this.listenTo(model.reservationsOfOrder, 'formAddPay', this.formAddPay);            
            this.listenTo(model.paysOfOrder, 'cancelPayOfOrder', this.cancelPayOfOrder);
            this.listenTo(model.paysOfOrder, 'savePay', this.savePay);
            
            this.listenTo(model.reservationsOfOrder, 'formAddRental', this.formAddRental);
            this.listenTo(model.paysOfOrder, 'saveRental', this.saveRental);

        },
        changeReservation: function() {
        	//수정 버튼 이벤트 발생
        	$('.orderSection .deskReservationList .btn-form-change-reservation').trigger('click');        	
        },
        deleteReservation: function() {
        	//console.log(model.paysOfOrder);
        	//console.log(model.reservationsOfOrder);
        	console.log(model);
        	reservationModels = model.reservationsOfOrder;
        	payModels = model.paysOfOrder;

        	var $modal = $('#modal-order-form');
        	if(confirm('등록 삭제할 경우 모든 등록 내역과 결제 내역까지 삭제됩니다. 정말로 삭제하시겠습니까?')) {
	        	reservationModels.forEach(function(item) {
	        		currentReservationModel = item;
		        	this.currentReservationModel.destroy({
	                wait: true,
	                success: function (model, response) {
	                
		        	},
		        	
		        	});
	        	});            	
		        	payModels.forEach(function(item) {
	                	currentPayOfOrderModel = item;		                	
			        	this.currentPayOfOrderModel.destroy({
				        	headers: {
				        		'flag': false
				        	},
				        	wait: true,
			                success: function (model, response) {
			                		                    
			
			                },
		
			        	});
		        	});
		        
		        //$modal.modal('hide');
		        location.reload();
        	}
        	
        	
        	
        	
//	        if(confirm('정말로 삭제하시겠습니까?')) {
//	        	this.currentReservationModel.destroy({
//	                wait: true,
//	                success: function (model, response) {
//	                    $modal.modal('hide');
//	
//	                },
//	
//	            });
//	
//	        }
        	
        	//currentReservationModel = this.model;
        	
//        	var $modal = $('#modal-order-form');
//        	
//	        if(confirm('정말로 삭제하시겠습니까?')) {
//	            this.currentReservationModel.destroy({
//	                wait: true,
//	                success: function (model, response) {
//	                    $modal.modal('hide');
//	
//	                },
//	
//	            });
//	
//	        }
        },
        
        savePay: function(model) {
//        	currentPayOfOrderModel = model;
//        	var payId = currentPayOfOrderModel.get('payId');
//        	var payId2 = '#'+payId;        	
//
//        	var payNote = this.$(''+payId2+'').find('#payNote').val();
//        	currentPayOfOrderModel.set({payNote: payNote});
//        	
//        	currentPayOfOrderModel.save({
//                wait: true,
//                success: function (model, response) {
//                	formReservationOfOrder();
//                },
//
//            });            

        	
        	var payId = model.get('payId');
        	var payId2 = '#'+payId;        	

        	var payNote = this.$(''+payId2+'').find('#payNote').val();
        	model.set({payNote: payNote});
        	
        	model.save({
                wait: true,
                success: function (model, response) {
                	formReservationOfOrder();
                },

            });                    	
        	
        	
        },
        saveRental: function(model) {
        	currentRentalOfOrderModel = model;
        	var rentalId = currentRentalOfOrderModel.get('rentalId');
        	var rentalId2 = '#'+rentalId;        	

        	var rentalNote = this.$(''+rentalId2+'').find('#rentalNote').val();
        	currentRentalOfOrderModel.set({rentalNote: rentalNote});
        	
        	currentRentalOfOrderModel.save({
                wait: true,
                success: function (model, response) {
                	formReservationOfOrder();
                },

            });            

        },
        
        keyup : function(e) {
        	if (e.keyCode >= 96 && e.keyCode <= 105) {
	        	var num = this.$('#payAmount').val();        	
	        	num = num.replace(/[^0-9]/g,'');
	        	
	        	num = AddComma(num);
	        	this.$('#payAmount').val(num);
        	}
        },       
        cancelPayOfOrder: function(model) {        	
        	currentPayOfOrderModel = model;

            if(confirm('정말로 취소하시겠습니까?')) {
            	currentPayOfOrderModel.destroy({
            		headers: {
            			'flag': false
            		},
            		wait: true,
                    success: function (model, response) {
                        
                    },

                });            	
            }
            
        },        
        changeMemberShip: function() {
            
        	var memberNo = this.$('#membership').val();
        	var member = MemberList.findWhere({no: memberNo});
        	var memberId = member.get('memberId');

            this.$('#membership').val('');

			var reservation = ReservationList.findWhere({memberId: memberId});
        	var reservationId = reservation.get('reservationId');
        			        	
        	$('#'+reservationId+'').trigger('click');

        },
        formAddReservation: function() {
        	var answer = confirm('좌석을 배정받아 사용중인 회원입니다.\n 추가 좌석을 지정하시겠습니까?');
        	if (answer)
        	{
        	  //'Yes'
        		monthFlag = 0;
            	// 자리 선택후 등록 내역 아래의 등록 추가
            	this.currentReservationOfOrderModel = model;
                
                this.$('.addReservation').show();
                this.$('.changeReservation').hide();

                var data = model.toJSON();
                var currentDate = sysDate.clone();

                data['members'] = MemberList.toJSON();
                data['desks'] = DeskList.toJSON();
                data['rooms'] = RoomList.toJSON();

                data['memberId'] = model.get("memberId");        
                var temp = MemberList.filter({ memberId : data['memberId']});
                data['memberName'] = temp[0].get('name');
                data['no'] = temp[0].get('no');
                //data['deskId'] = null;
                data['deskId'] = model.get('deskId');
    			data['payTypes'] = payTypes;
    			data['pay'] = new PayModel().toJSON();
    			data['payAmount'] = 0;
                data['payType'] = 0;
    			data['reservationDt'] = currentDate.valueOf();
                data['deskStartDt'] = currentDate.valueOf();
                data['deskEndDt'] = currentDate.valueOf();
                data['deskStartTm'] = currentDate.format('HH:mm:ss');
                data['deskEndTm'] = currentDate.endOf('day').format('HH:mm:ss');
                
                console.log(data);
                
                
                //var formHtml = this.templateForReservationForm(data);
                
                var formHtml = this.templateForAddReservationForm(data);

                var $f = this.$('.addReservationForm');
                $f.html(formHtml);

                $f.find('.datepicker, .input-daterange').datepicker({
                    language: 'ko', autoclose: true, todayHighlight: true, format: datepickerFormat,
                }).on('changeDate', function(e) {
                    var orgInputName = $(e.target).attr('data-org-input-name');
                    if (!(_.isEmpty(orgInputName))) {
                        var $orgInput = $f.find('[name=' + orgInputName + ']');
                        $orgInput.val(e.date.valueOf());
                    }
                    
                    if ( orgInputName == "deskStartDt") {
                        d = new Date();
                        //선택한 날짜와 현재 날짜가 같으면
                        if ( (e.date.getYear().valueOf() == d.getYear().valueOf()) && (e.date.getDate().valueOf() == d.getDate().valueOf()) && (e.date.getDay().valueOf() == d.getDay().valueOf())) {
                        	
                        } else {
                        	$f.find('#deskStartTm').val("08:00");
                        	var $deskStart = $f.find('[name=deskStartTm]');
                            $deskStart.val("08:00:00");
                        	
                        }
                    }
                    
                    //총 일수
                    var start = $f.find('#deskStartDt').datepicker('getDate');
                    var end   = $f.find('#deskEndDt').datepicker('getDate');
                    
                    var days = (end - start)/1000/60/60/24;
                    days = parseInt(days) + 1;
                    $f.find('#dateCount').text('총 ' + days + '일');

                });

                $f.find('#deskStartTm').clockpicker({
                    placement: 'bottom',
                    align: 'left',
                    autoclose: true,
                    donetext: '완료',
                	'default': 'now',
                	
                }).on('change', function(e){
                	if($f.find('#deskStartDt').val() == $f.find('#deskEndDt').val()) {
                    	if ($f.find('#deskStartTm').val() > $f.find('#deskEndTm').val()) {
                    		$f.find('#deskEndTm').val($f.find('#deskStartTm').val());
                    	}
                	}
                	var deskStartName = $(e.target).attr('time-org-input-name');
                    if (!(_.isEmpty(deskStartName))) {
                        var $deskStart = $f.find('[name=' + deskStartName + ']');
                        $deskStart.val($('#deskStartTm').val() + ":00");

                    }
                });

                $f.find('#deskEndTm').clockpicker({
                    placement: 'bottom',
                    align: 'left',
                    autoclose: true,
                	donetext: '완료',
                	'default': 'now',
                	
                }).on('change', function(e){ 
                	if($f.find('#deskStartDt').val() == $f.find('#deskEndDt').val()) {
                    	if ($f.find('#deskEndTm').val() < $f.find('#deskStartTm').val()){
                    		$f.find('#deskStartTm').val($f.find('#deskEndTm').val());
                    	}                    	
                	}
                	
                	var deskEndName = $(e.target).attr('time-org-input-name');
                    if (!(_.isEmpty(deskEndName))) {
                        var $deskEnd = $f.find('[name=' + deskEndName + ']');
                        $deskEnd.val($('#deskEndTm').val() + ":59");

                    }
                });
                
                $f.find('.chosen').chosen({width: "100%"});
                
                $.validator.addMethod(
                		'chkNumeric', function (value, element) {
                			value = value.replace(/,/g, '');
    		                return this.optional(element) || /^[0-9]+$/.test(value);
    		                
    		                	}, '숫자만 사용 가능합니다'
                );
            	
                $f.find('form').validate({
                	ignore: "not:hidden",
                	rules: {
            			memberId: {            			
            				required: true,
            			},
            			payType:{                				
            				required: true,
            				number : true,
            			},
            			payAmount: {
            				//min: 1000,
            				chkNumeric : true,
            			}
//             			deskId: {
//             				required: true,
//             			}
            			
            		},
            		messages: {
            			memberId: {            	
            	    		required: "회원을 선택하세요",
            			},
            			payType:{
            				required: "결제 구분을 선택하세요",
            				number : "결제 구분을 선택하세요",
            			},
            			payAmount: {
            				
            			}
//             			deskId: {
//             				required: "좌석을 선택하세요",
//             			}
            			
            		}		
                });
        	}
        	else
        	{
        	  //'Cancel'
        	}
        	
        	


            /*$f.find('form').on('submit', function(e) {
                e.preventDefault();

            });*/
        	
             
            
        },
        addOneReservationOfOrder: function(reservationOfOrder) {
        	
	            
            
	        	var view = new ReservationOfOrderView({ model: reservationOfOrder });
	
	            this.$('.reservationsOfOrder').append(view.render().el);
	            
//	            if ( reservationOfOrder.attributes.reservationStatus == 20) {
//	            	var tempId = "'#" + reservationOfOrder.attributes.reservationId + "'";
//	            	
//	            	
//	            }    
	            

        },
        addOnePayOfOrder: function(payOfOrder) {
        	
            var view = new PayOfOrderView({ model: payOfOrder });
            this.$('.paysOfOrder').append(view.render().el);
            

        },
        resetReservationsOfOrder: function(reservationsOfOrder) {
            this.$('.reservationsOfOrder').empty();

            reservationsOfOrder.forEach(this.addOneReservationOfOrder);
        },
        resetPaysOfOrder: function(paysOfOrder) {
            this.$('.paysOfOrder').empty();

            paysOfOrder.forEach(this.addOnePayOfOrder);
            

        },
        formReservationOfOrder: function(model) {
        	monthFlag = 0;
        	//예약 수정 뿌려지는 부분
            this.currentReservationOfOrderModel = model;
        	
            this.$('.changeReservation').show();
            this.$('.addReservation').hide();

            var data = model.toJSON();
            var currentDate = sysDate.clone();
            
            data['members'] = MemberList.toJSON();
       
            data['desks'] = DeskList.toJSON();
            data['rooms'] = RoomList.toJSON();
            data['memberId'] = model.get("memberId");
            
            
            var temp = MemberList.filter({ memberId : data['memberId']});
            
            
            data['memberName'] = temp[0].get('name');
            data['no'] = temp[0].get('no');
            //  data['memberNo'] = temp[0].get('no');
            
            data['deskId'] = model.get("deskId");
            data['reservationNum'] = model.get("reservationNum");
			data['payTypes'] = payTypes;
			data['pay'] = new PayModel().toJSON();
            data['reservationDt'] = currentDate.valueOf();
            data['timeYn'] = model.get("timeYn");
            
//            data['deskStartDt'] = currentDate.valueOf();
//            data['deskEndDt'] = currentDate.valueOf();

//            data['deskStartTm'] = model.get("deskStartTm");
//            data['deskEndTm'] = model.get("deskEndTm");
            
            var formHtml = this.templateForReservationForm(data);

            var $f = this.$('.changeReservationForm');
            $f.html(formHtml);

            
            
            
            $f.find('.datepicker, .input-daterange').datepicker({
                language: 'ko', autoclose: true, todayHighlight: true, format: datepickerFormat,
            }).on('changeDate', function(e) {                                         	
            	var orgInputName = $(e.target).attr('data-org-input-name');
                if (!(_.isEmpty(orgInputName))) {
                    var $orgInput = $f.find('[name=' + orgInputName + ']');
                    $orgInput.val(e.date.valueOf());
                }
                
                if ( orgInputName == "deskStartDt") {
                    d = new Date();
                    //선택한 날짜와 현재 날짜가 같으면
                    if ( (e.date.getYear().valueOf() == d.getYear().valueOf()) && (e.date.getDate().valueOf() == d.getDate().valueOf()) && (e.date.getDay().valueOf() == d.getDay().valueOf())) {
                    	
                    } else {
                    	$f.find('#deskStartTm').val("08:00");
                    	var $deskStart = $f.find('[name=deskStartTm]');
                        $deskStart.val("08:00:00");
                    	
                    }
                }
                
                //총 일수
                var start = $f.find('#deskStartDt').datepicker('getDate');
                var end   = $f.find('#deskEndDt').datepicker('getDate');
                
                var days = (end - start)/1000/60/60/24;
                days = parseInt(days) + 1;
                $f.find('#dateCount').text('총 ' + days + '일');

            });
            
            $f.find('#deskStartTm').clockpicker({
                placement: 'bottom',
                align: 'left',
                autoclose: true,
                donetext: '완료',
            	'default': 'now',
            	
            }).on('change', function(e){
            	if($f.find('#deskStartDt').val() == $f.find('#deskEndDt').val()) {
                	if ($f.find('#deskStartTm').val() > $f.find('#deskEndTm').val()) {
                		$f.find('#deskEndTm').val($f.find('#deskStartTm').val());
                	}
            	}
            	var deskStartName = $(e.target).attr('time-org-input-name');
                if (!(_.isEmpty(deskStartName))) {
                    var $deskStart = $f.find('[name=' + deskStartName + ']');
                    $deskStart.val($('#deskStartTm').val() + ":00");

                }             
            });

            $f.find('#deskEndTm').clockpicker({
                placement: 'bottom',
                align: 'left',
                autoclose: true,
            	donetext: '완료',
            	'default': 'now',
            	
            }).on('change', function(e){                
            	if($f.find('#deskStartDt').val() == $f.find('#deskEndDt').val()) {
                	if ($f.find('#deskEndTm').val() < $f.find('#deskStartTm').val()){
                		$f.find('#deskStartTm').val($f.find('#deskEndTm').val());
                	}
            	}
            	var deskEndName = $(e.target).attr('time-org-input-name');
                if (!(_.isEmpty(deskEndName))) {
                    var $deskEnd = $f.find('[name=' + deskEndName + ']');
                    $deskEnd.val($f.find('#deskEndTm').val() + ":00");

                }
            });
            
                       
            $f.find('.chosen').chosen({width: "100%"});

            $f.find('form').validate({
            	
            		ignore: "not:hidden",
            		rules: {
            			memberId: {            			
            				required: true,
            			},
//             			deskId: {
//             				required: true,
//             			}
            			
            		},
            		messages: {
            			memberId: {            	
            	    		required: "회원을 선택하세요",
            			},
//             			deskId: {
//             				required: "좌석을 선택하세요",
//             			}
            		}		
            });
            /* $f.find('form').on('submit', function(e) {
                e.preventDefault();

            }); */

            //총 일수
            var start = $f.find('#deskStartDt').datepicker('getDate');
            var end   = $f.find('#deskEndDt').datepicker('getDate');
            
            var days = (end - start)/1000/60/60/24;
            days = parseInt(days) + 1;
            $f.find('#dateCount').text('총 ' + days + '일');
            
        },
        saveChangeReservationOfOrder: function(e) {
        	var currentReservation = this.currentReservationOfOrderModel;
        	var values = {};
            var $view = this.$('.changeReservation');
            var $form = $view.find('form');
            var $modal = $('#modal-order-form');
            
            if($form.valid()) {
            //$('#reservationFormTemplate').validate({
            	//submitHandler: function(form) {                    
                var $inputs = $form.find(':input');
                $inputs.each(function(i, element) {
                    var $element = $(element);
                    if ($element.is('[type=radio]')) {
                        if ($element.is(':checked')) {
                            var name = $element.attr('name');
                            if (!(_.isEmpty(name))) {
                                values[name] = $element.val();
								
                            }
                        }

                    } else {
                        var name = $element.attr('name');
                        if (!(_.isEmpty(name))) {
                            values[name] = $element.val();									
                        }
                        else {
                        	
                        }

                    }
                });
				

                var startTimeStamp;
                var timeStamp;
                ReservationList.comparator = function(model) {
                    return model.get('deskStartDt');
                }
                ReservationList.sort();
                
                if (ReservationList.length > 1) {
                	startTimeStamp = ReservationList.models[0].attributes["deskStartDt"]
                	timeStamp = ReservationList.models[ReservationList.length-1].attributes["deskEndDt"]
                } else {
                	startTimeStamp = values['deskStartDt'];
                	timeStamp = values['deskEndDt'];
                }

    			
    			startTimeStamp = parseInt(startTimeStamp);
                //var timestamp=new Date('02/10/2016').getTime();
                var todate=new Date(startTimeStamp).getDate();
                var tomonth=new Date(startTimeStamp).getMonth()+1;
                var toyear=new Date(startTimeStamp).getFullYear();
                var original_date=tomonth+'/'+todate+'/'+toyear;
                var timestamp_formation = new Date(original_date).getTime();
                var IntStartTimeStamp = timestamp_formation.toString().substring(0,10);
                IntStartTimeStamp  = parseInt(IntStartTimeStamp)+ 32400;
                
                values['deskStartDtTimeStamp'] = IntStartTimeStamp;
                
                
                timeStamp = parseInt(timeStamp);
                //var timestamp=new Date('02/10/2016').getTime();
                var todate=new Date(timeStamp).getDate();
                var tomonth=new Date(timeStamp).getMonth()+1;
                var toyear=new Date(timeStamp).getFullYear();
                var original_date=tomonth+'/'+todate+'/'+toyear;
                var timestamp_formation = new Date(original_date).getTime();
                var IntTimeStamp = timestamp_formation.toString().substring(0,10);
                IntTimeStamp  = parseInt(IntTimeStamp)+ 32400;
                
                values['deskEndDtTimeStamp'] = IntTimeStamp;
                var member = MemberList.filter({ memberId: values['memberId'] });
                values['memberNo'] = member[0].get('no');
              
                if (values['deskId'] == "") { //좌석지정을 안할 경우
                   	 //중복데이터 구분
   	 	            var reservations = ReservationList.filter({ memberId: values['memberId'], deskId: "" });
   	    			
   	 	           	//예약이 있는 경우
   	                if (reservations.length > 0) {
   	      				//var reservationDeskStartDt = makeDateFormat($('#deskStartDt').val()); //함수호출
   	      				//입력된 StartDt	      				
   	      				var inpuStartDt = $modal.find('[name=deskStartDt]').val();
   	      				var dateStartDt = new Date(inpuStartDt*1);
   	      				var reservationDeskStartDt = moment(dateStartDt).format('YYYY-MM-DD');

   	 					//입력된 EndDt
   	      				var inpuEndDt = $modal.find('[name=deskEndDt]').val();
   	      				var dateEndDt = new Date(inpuEndDt*1);
   	      				var reservationDeskEndDt = moment(dateEndDt).format('YYYY-MM-DD');
   						
   		    			var valuesDeskStartDtTm = new moment(reservationDeskStartDt + values['deskStartTm'], 'YYYY-MM-DD HH:mm:ss');
   		    			var valuesDeskEndDtTm = new moment(reservationDeskEndDt + values['deskEndTm'], 'YYYY-MM-DD HH:mm:ss');
   		    			
   	                	var now = moment();
   	                	var reservationId = []; //겹치는 날짜의 reservationId가 들어가는 변수
   						reservations.forEach(function(item) {
   							var reservationTemp;
   							var reservation = item;
   							
   			    			var strDeskStartDtTm = moment(reservation.get('deskStartDt')).format("YYYY-MM-DD") + reservation.get('deskStartTm');
   			    			var strDeskEndDtTm = moment(reservation.get('deskEndDt')).format("YYYY-MM-DD") + reservation.get('deskEndTm');
   	
   			    			var deskStartDtTm = new moment(strDeskStartDtTm, 'YYYY-MM-DD HH:mm:ss');
   			    			var deskEndDtTm = new moment(strDeskEndDtTm, 'YYYY-MM-DD HH:mm:ss');
   			    			
   			    			if (deskStartDtTm.isSame(valuesDeskStartDtTm)) {	    				
   			    				reservationId.push(reservation.get('reservationId'));
   			    				return false;
   			    			}
   	
   			    			else if (deskStartDtTm.isSame(valuesDeskEndDtTm)) {    				
   			    				reservationId.push(reservation.get('reservationId'));
   			    				return false;
   			    			}
   	
   			    			else if (deskEndDtTm.isSame(valuesDeskStartDtTm)) {
   			    				reservationId.push(reservation.get('reservationId'));
   			    				return false;
   			    			}
   			    			
   			    			else if (deskEndDtTm.isSame(valuesDeskEndDtTm)) {
   			    				reservationId.push(reservation.get('reservationId'));
   			    				return false;
   			    			}
   			    			
   			    			else if (deskStartDtTm.isBetween(valuesDeskStartDtTm, valuesDeskEndDtTm)) { 
   			    				reservationId.push(reservation.get('reservationId'));
   			    				return false;
   	 		    			}
   	 		    			else if (deskEndDtTm.isBetween(valuesDeskStartDtTm, valuesDeskEndDtTm)) { 
   	 		    				reservationId.push(reservation.get('reservationId'));
   	 		    				return false;
   			    			}
   	
   	 		    			else if (valuesDeskStartDtTm.isBetween(deskStartDtTm, deskEndDtTm)) { 
   	 		    				reservationId.push(reservation.get('reservationId'));
   	 		    				return false;
   			    			}
   			    			
   	 		    			else if (valuesDeskEndDtTm.isBetween(deskStartDtTm, deskEndDtTm)) { 
   	 		    				reservationId.push(reservation.get('reservationId'));
   	 		    				return false;
   			    			}
   	 		    			else { //안겹침
   	 		    			}
   							
   						});
   						
   						
   	                	if (reservationId.length > 0) { //존재한다면 겹치는 등록이 있음
   	                		alert('이 회원은 해당 시간에 이미 지정되지 않은 좌석에 등록되어 있습니다');
   	                	}
   	                	else {   	                		
   	   	                	this.currentReservationOfOrderModel.save(values, {
   	   	                        wait: true,
   	   	                        success: function (model, response) {
   	   	                            $view.hide();
   	   	                            model.collection.fetch();	                                
   	   	                            EditReservation_flag = true;
   	   	                        },

   	   	                    });
   	                	}
   	                }
   	                else {
   	                	this.currentReservationOfOrderModel.save(values, {
   	                        wait: true,
   	                        success: function (model, response) {
   	                            $view.hide();
   	                            model.collection.fetch();
   	                            EditReservation_flag = true;
   	                        },

   	                    });
   	                }

                }
                else { //좌석지정을 할 경우
	                //중복데이터 구분
	 	            var reservations = ReservationList.filter({ deskId: values['deskId'] });
	 	           	
	 	            //예약이 있는 경우
	                if (reservations.length > 0) {
	      				//입력된 StartDt	      				
	      				var inpuStartDt = $modal.find('[name=deskStartDt]').val();
	      				var dateStartDt = new Date(inpuStartDt*1);
	      				var reservationDeskStartDt = moment(dateStartDt).format('YYYY-MM-DD');

	 					//입력된 EndDt
	      				var inpuEndDt = $modal.find('[name=deskEndDt]').val();
	      				var dateEndDt = new Date(inpuEndDt*1);
	      				var reservationDeskEndDt = moment(dateEndDt).format('YYYY-MM-DD');	
						
		    			var valuesDeskStartDtTm = new moment(reservationDeskStartDt + values['deskStartTm'], 'YYYY-MM-DD HH:mm:ss');
		    			var valuesDeskEndDtTm = new moment(reservationDeskEndDt + values['deskEndTm'], 'YYYY-MM-DD HH:mm:ss');
		    			
	                	var now = moment();
	                	var reservationId = []; //겹치는 날짜의 reservationId가 들어가는 변수
						reservations.forEach(function(item) {
							var reservationTemp;
							var reservation = item;
							
							if (reservation.get('reservationId') == currentReservation.get('reservationId')) { //본인의 reservation은 제외
								
							}
							else {
				    			var strDeskStartDtTm = moment(reservation.get('deskStartDt')).format("YYYY-MM-DD") + reservation.get('deskStartTm');
				    			var strDeskEndDtTm = moment(reservation.get('deskEndDt')).format("YYYY-MM-DD") + reservation.get('deskEndTm');
		
				    			var deskStartDtTm = new moment(strDeskStartDtTm, 'YYYY-MM-DD HH:mm:ss');
				    			var deskEndDtTm = new moment(strDeskEndDtTm, 'YYYY-MM-DD HH:mm:ss');
				    			
				    			if (deskStartDtTm.isSame(valuesDeskStartDtTm)) {	    				
				    				reservationId.push(reservation.get('reservationId'));
				    			}
		
				    			else if (deskStartDtTm.isSame(valuesDeskEndDtTm)) {    				
				    				reservationId.push(reservation.get('reservationId'));
				    			}
		
				    			else if (deskEndDtTm.isSame(valuesDeskStartDtTm)) {
				    				reservationId.push(reservation.get('reservationId'));
				    			}
				    			
				    			else if (deskEndDtTm.isSame(valuesDeskEndDtTm)) {
				    				reservationId.push(reservation.get('reservationId'));
				    			}
				    			
				    			else if (deskStartDtTm.isBetween(valuesDeskStartDtTm, valuesDeskEndDtTm)) { //날짜가 겹치면
				    				reservationId.push(reservation.get('reservationId'));
		 		    			}
		 		    			else if (deskEndDtTm.isBetween(valuesDeskStartDtTm, valuesDeskEndDtTm)) { //시작날짜가 겹치지 않으면 end날짜와 다시한번 조회
		 		    				reservationId.push(reservation.get('reservationId')); 		    					
				    			}
		
		 		    			else if (valuesDeskStartDtTm.isBetween(deskStartDtTm, deskEndDtTm)) { 
		 		    				reservationId.push(reservation.get('reservationId')); 		    					
				    			}
				    			
		 		    			else if (valuesDeskEndDtTm.isBetween(deskStartDtTm, deskEndDtTm)) { 
		 		    				reservationId.push(reservation.get('reservationId')); 		    					
				    			}
		 		    			else { //안겹침
		 		    			}
							}
						});
						
	                	var temp = 0;
						reservationId.forEach(function(item) {
						var reservationTemp = ReservationList.filter({ reservationId: item });
	                	temp++;
	                	
	                	});					
	                	//좌석의 deskMax(최대수용인원) 체크
	     	            var deskList = DeskList.filter({ deskId: values['deskId']});
	     	            var deskMax = deskList[0].get('deskMax');
	     	           	if (temp >= deskMax) {
	     	           		alert('해당 시간에는 이미 좌석이 등록되어 있습니다');
	     	           	}
	     	           	else {
	                        this.currentReservationOfOrderModel.save(values, {
	                            wait: true,
	                            success: function (model, response) {
	                                $view.hide();
	                                model.collection.fetch();	                                
	                                EditReservation_flag = true;
	                            },
	
	                        });
	     	           	}
	                }
	 	            
	                else { //예약이 없는 경우
	                    this.currentReservationOfOrderModel.save(values, {
	                        wait: true,
	                        success: function (model, response) {
	                            $view.hide();
	                            model.collection.fetch();	                                
	                            EditReservation_flag = true;
	                        },
	
	                    });
	                }
	            	//},
                }
            	
                
            };
            
            //$form.submit();
            
                            
        },
        saveAddReservationOfOrder: function(e) {
	    	//ModalOrderFormApp.undelegateEvents();
	    	
	    	//ModalOrderFormApp.stopListening();
	    	
            var currentDate = sysDate.clone();
        	var $modal = $('#modal-order-form .addReservation');                	
        	var values = {};
            var $view = this.$('.addReservation');
            
            var $form = $view.find('form');
            //$form.validate({});

            if($form.valid()) {
                var $inputs = $form.find(':input');
                $inputs.each(function(i, element) {
                    var $element = $(element);
                    if ($element.is('[type=radio]')) {
                        if ($element.is(':checked')) {
                            var name = $element.attr('name');
                            if (!(_.isEmpty(name))) {
                                values[name] = $element.val();

                            }
                        }

                    } else {
                        var name = $element.attr('name');
                        if (!(_.isEmpty(name))) {
                            values[name] = $element.val();

                        }

                    }
                });

                
                var startTimeStamp;
                var timeStamp;
                ReservationList.comparator = function(model) {
                    return model.get('deskStartDt');
                }
                ReservationList.sort();
                
                if (ReservationList.length > 1) {
                	startTimeStamp = ReservationList.models[0].attributes["deskStartDt"]
                	timeStamp = values["deskEndDt"]
                } else {
                	startTimeStamp = values['deskStartDt'];
                	timeStamp = values['deskEndDt'];
                }

    			startTimeStamp = parseInt(startTimeStamp);
                //var timestamp=new Date('02/10/2016').getTime();
                var todate=new Date(startTimeStamp).getDate();
                var tomonth=new Date(startTimeStamp).getMonth()+1;
                var toyear=new Date(startTimeStamp).getFullYear();
                var original_date=tomonth+'/'+todate+'/'+toyear;
                var timestamp_formation = new Date(original_date).getTime();
                var IntStartTimeStamp = timestamp_formation.toString().substring(0,10);
                IntStartTimeStamp  = parseInt(IntStartTimeStamp)+ 32400;
                
                                               
                timeStamp = parseInt(timeStamp);
                //var timestamp=new Date('02/10/2016').getTime();
                var todate=new Date(timeStamp).getDate();
                var tomonth=new Date(timeStamp).getMonth()+1;
                var toyear=new Date(timeStamp).getFullYear();
                var original_date=tomonth+'/'+todate+'/'+toyear;
                var timestamp_formation = new Date(original_date).getTime();
                var IntTimeStamp = timestamp_formation.toString().substring(0,10);
                IntTimeStamp  = parseInt(IntTimeStamp)+ 32400;
                
                values['deskEndDtTimeStamp'] = IntTimeStamp;
                
                
                
              values['payInOutType'] = 20;
              
            	//알림톡 정보
	            var roomName;
	            var deskName;
	            var memberName;
	            var phone_number;
	            var school;
	            var registration;
	            var amount;
	            
	            amount = values['payAmount'];
	            
 	           	var memberList = MemberList.filter({ memberId: values['memberId']});
	            if (memberList[0] != null) {	     	           	
	            	if (memberList[0].get('name') != null) {
	            		memberName = memberList[0].get('name');
	            	}
	           		if (memberList[0].get('telParent') != null) {
	            		phone_number = memberList[0].get('telParent');	//고객용
	           		}
	           		if (memberList[0].get('school') != null || memberList[0].get('school') != '') {
	           			school = memberList[0].get('school');
	           		}
	           		else {
	           			school = '미입력';
	           		}
	            }
	           registration = '연장';
              
	           var timeStamp = values['deskEndDt'];
               
               timeStamp = parseInt(timeStamp);
               //var timestamp=new Date('02/10/2016').getTime();
               var todate=new Date(timeStamp).getDate();
               var tomonth=new Date(timeStamp).getMonth()+1;
               var toyear=new Date(timeStamp).getFullYear();
               var original_date=tomonth+'/'+todate+'/'+toyear;
               var timestamp_formation = new Date(original_date).getTime();
               var IntTimeStamp = timestamp_formation.toString().substring(0,10);
               IntTimeStamp  = parseInt(IntTimeStamp)+ 32400;
                       
               values['deskEndDtTimeStamp'] = IntTimeStamp;
               var member = MemberList.filter({ memberId: values['memberId'] });
               values['memberNo'] = member[0].get('no');
	           
               
              if (values['deskId'] == "") { //좌석지정을 안할 경우
            	  roomName = '지정안함';
            	  deskName = '지정안함';
            	  //중복데이터 구분
	 	            var reservations = ReservationList.filter({ memberId: values['memberId'], deskId: "" });
	    			
	 	           	//예약이 있는 경우
	                if (reservations.length > 0) {
	      				//var reservationDeskStartDt = makeDateFormat($('#deskStartDt').val()); //함수호출
	      				//입력된 StartDt
	      				var inpuStartDt = $modal.find('[name=deskStartDt]').val();
	      				var dateStartDt = new Date(inpuStartDt*1);
	      				var reservationDeskStartDt = moment(dateStartDt).format('YYYY-MM-DD');

	 					//입력된 EndDt
	      				var inpuEndDt = $modal.find('[name=deskEndDt]').val();
	      				var dateEndDt = new Date(inpuEndDt*1);
	      				var reservationDeskEndDt = moment(dateEndDt).format('YYYY-MM-DD');
						
		    			var valuesDeskStartDtTm = new moment(reservationDeskStartDt + values['deskStartTm'], 'YYYY-MM-DD HH:mm:ss');
		    			var valuesDeskEndDtTm = new moment(reservationDeskEndDt + values['deskEndTm'], 'YYYY-MM-DD HH:mm:ss');
		    			
	                	var now = moment();
	                	var reservationId = []; //겹치는 날짜의 reservationId가 들어가는 변수
						reservations.forEach(function(item) {
							var reservationTemp;
							var reservation = item;
							
			    			var strDeskStartDtTm = moment(reservation.get('deskStartDt')).format("YYYY-MM-DD") + reservation.get('deskStartTm');
			    			var strDeskEndDtTm = moment(reservation.get('deskEndDt')).format("YYYY-MM-DD") + reservation.get('deskEndTm');
	
			    			var deskStartDtTm = new moment(strDeskStartDtTm, 'YYYY-MM-DD HH:mm:ss');
			    			var deskEndDtTm = new moment(strDeskEndDtTm, 'YYYY-MM-DD HH:mm:ss');
			    			
			    			if (deskStartDtTm.isSame(valuesDeskStartDtTm)) {	    				
			    				reservationId.push(reservation.get('reservationId'));
			    				return false;
			    			}
	
			    			else if (deskStartDtTm.isSame(valuesDeskEndDtTm)) {    				
			    				reservationId.push(reservation.get('reservationId'));
			    				return false;
			    			}
	
			    			else if (deskEndDtTm.isSame(valuesDeskStartDtTm)) {
			    				reservationId.push(reservation.get('reservationId'));
			    				return false;
			    			}
			    			
			    			else if (deskEndDtTm.isSame(valuesDeskEndDtTm)) {
			    				reservationId.push(reservation.get('reservationId'));
			    				return false;
			    			}
			    			
			    			else if (deskStartDtTm.isBetween(valuesDeskStartDtTm, valuesDeskEndDtTm)) { 
			    				reservationId.push(reservation.get('reservationId'));
			    				return false;
	 		    			}
	 		    			else if (deskEndDtTm.isBetween(valuesDeskStartDtTm, valuesDeskEndDtTm)) { 
	 		    				reservationId.push(reservation.get('reservationId'));
	 		    				return false;
			    			}
	
	 		    			else if (valuesDeskStartDtTm.isBetween(deskStartDtTm, deskEndDtTm)) { 
	 		    				reservationId.push(reservation.get('reservationId'));
	 		    				return false;
			    			}
			    			
	 		    			else if (valuesDeskEndDtTm.isBetween(deskStartDtTm, deskEndDtTm)) { 
	 		    				reservationId.push(reservation.get('reservationId'));
	 		    				return false;
			    			}
	 		    			else { //안겹침
	 		    			}
							
						});
						
	                	if (reservationId.length > 0) { //존재한다면 겹치는 등록이 있음
	                		alert('이 회원은 해당 시간에 이미 지정되지 않은 좌석에 등록되어 있습니다');
	                	}
	                	else {
							model.reservationsOfOrder.create(values, {
								wait : true,
							    success : function (model, response) {
							    	var orderId = response.orderId;
							    	var reservationId = response.reservationId;
							    	
							        model.set('reservationId', response.reservationId);
									model.set('insertDt', response.insertDt);
									model.set('updateDt', response.updateDt);
									
									PayList.create({
				                        orderId: orderId,
				                        reservationId: reservationId,
				                        payDt: sysDate.clone().valueOf(),
				                        payType: values['payType'],
				                        payInOutType: values['payInOutType'],
				                        payAmount: values['payAmount'],
				                        payNote: values['payNote'],
				                        memberId: values['memberId'],	
									}, {
										wait : true,
				                        success : function (model, response) {
            								if (amount > 0) {
	                            				/*
            									if (phone_number != '' || phone_number != null) {		                            							                            				
	                            					//고객에게 알림톡 전송
	                            					var Alimtalk = {
		                            						roomName: roomName,
		                            						deskName: deskName,
			                            					memberName: memberName,
			                            					phone_number: phone_number,
			                            					school: school,
			                            					registration: registration,
			                            					amount: values['payAmount']
			                            			};			            			                    
	                            					alimtalkSend(Alimtalk, branchId);
	                            				}
	                            				*/
	            			                  	//센터장에게 알림톡 전송
	            			                    var AlimtalkManager = {
	                            						roomName: roomName,
	                            						deskName: deskName,
		                            					memberName: memberName,
		                            					phone_number: branchTel,
		                            					school: school,
		                            					registration: registration,
		                            					amount: values['payAmount']
		                            			};
	            			                    alimtalkSend(AlimtalkManager, branchId);
                            				}
            								
				                        	$view.hide();
									        EditReservation_flag = true;
									        $('.orderSection .deskReservationList .btn-form-change-reservation').trigger('click');
				                        }
									});
									
							        
							    }
							});	                		
	                	}

	                }
		            else { //중복되는 예약이 없는 경우
						  model.reservationsOfOrder.create(values, {
						  	wait : true,
						      success : function (model, response) {
						      	
						    	  var orderId = response.orderId;
							    	var reservationId = response.reservationId;
							    	
							        model.set('reservationId', response.reservationId);
									model.set('insertDt', response.insertDt);
									model.set('updateDt', response.updateDt);
									
									PayList.create({
				                        orderId: orderId,
				                        reservationId: reservationId,
				                        payDt: sysDate.clone().valueOf(),
				                        payType: values['payType'],
				                        payInOutType: values['payInOutType'],
				                        payAmount: values['payAmount'],
				                        payNote: values['payNote'],
				                        memberId: values['memberId'],	
									}, {
										wait : true,
				                        success : function (model, response) {
            								if (amount > 0) {
	                            				/*
            									if (phone_number != '' || phone_number != null) {		                            							                            					                            					
	                            					//고객에게 알림톡 전송
	                            					var Alimtalk = {
		                            						roomName: roomName,
		                            						deskName: deskName,
			                            					memberName: memberName,
			                            					phone_number: phone_number,
			                            					school: school,
			                            					registration: registration,
			                            					amount: values['payAmount']
			                            			};			            			                    
	                            					alimtalkSend(Alimtalk, branchId);
	                            					
	                            				}
	                            				*/
	            			                  	//센터장에게 알림톡 전송
	            			                    var AlimtalkManager = {
	                            						roomName: roomName,
	                            						deskName: deskName,
		                            					memberName: memberName,
		                            					phone_number: branchTel,
		                            					school: school,
		                            					registration: registration,
		                            					amount: values['payAmount']
		                            			};
	            			                    alimtalkSend(AlimtalkManager, branchId);
                            				}
            								
				                        	$view.hide();
									        EditReservation_flag = true;
									        $('.orderSection .deskReservationList .btn-form-change-reservation').trigger('click');
				                        }
									});
						      }
						  });
		             }
              }
              else { //좌석지정을 할 경우 
            	  //중복데이터 구분
 	            var reservations = ReservationList.filter({ deskId: values['deskId'] });
 	            
 	           var deskList = DeskList.filter({ deskId: values['deskId']}); 	            
	            if (deskList[0] != null) {
	            	if (deskList[0].get('deskId') != null) {
    	            	var roomList = RoomList.filter({ roomId: deskList[0].get('roomId')});
    	            	if (roomList[0] != null || roomList[0] != '') {
    	            		roomName = roomList[0].get('name');
    	            	}
    	            	else {
    	            		roomName = '미입력';
    	            	}
	            	}
	            	if (deskList[0].get('name') != null || deskList[0] != '') {
	           			deskName = deskList[0].get('name');
	            	}
	            	else {
	            		deskName = '미입력';
	            	}
	            }
 	           	
 	            //예약이 있는 경우
                if (reservations.length > 0) {
      				//입력된 StartDt	      				
      				var inpuStartDt = $modal.find('[name=deskStartDt]').val();
      				var dateStartDt = new Date(inpuStartDt*1);
      				var reservationDeskStartDt = moment(dateStartDt).format('YYYY-MM-DD');

 					//입력된 EndDt
      				var inpuEndDt = $modal.find('[name=deskEndDt]').val();
      				var dateEndDt = new Date(inpuEndDt*1);
      				var reservationDeskEndDt = moment(dateEndDt).format('YYYY-MM-DD');		
					
	    			var valuesDeskStartDtTm = new moment(reservationDeskStartDt + values['deskStartTm'], 'YYYY-MM-DD HH:mm:ss');
	    			var valuesDeskEndDtTm = new moment(reservationDeskEndDt + values['deskEndTm'], 'YYYY-MM-DD HH:mm:ss');
	    			
                	var now = moment();
                	var reservationId = []; //겹치는 날짜의 reservationId가 들어가는 변수
					reservations.forEach(function(item) {
						var reservationTemp;
						var reservation = item;

		    			var strDeskStartDtTm = moment(reservation.get('deskStartDt')).format("YYYY-MM-DD") + reservation.get('deskStartTm');
		    			var strDeskEndDtTm = moment(reservation.get('deskEndDt')).format("YYYY-MM-DD") + reservation.get('deskEndTm');

		    			var deskStartDtTm = new moment(strDeskStartDtTm, 'YYYY-MM-DD HH:mm:ss');
		    			var deskEndDtTm = new moment(strDeskEndDtTm, 'YYYY-MM-DD HH:mm:ss');
		    			
		    			if (deskStartDtTm.isSame(valuesDeskStartDtTm)) {	    				
		    				reservationId.push(reservation.get('reservationId'));
		    			}

		    			else if (deskStartDtTm.isSame(valuesDeskEndDtTm)) {    				
		    				reservationId.push(reservation.get('reservationId'));
		    			}

		    			else if (deskEndDtTm.isSame(valuesDeskStartDtTm)) {
		    				reservationId.push(reservation.get('reservationId'));
		    			}
		    			
		    			else if (deskEndDtTm.isSame(valuesDeskEndDtTm)) {
		    				reservationId.push(reservation.get('reservationId'));
		    			}
		    			
		    			else if (deskStartDtTm.isBetween(valuesDeskStartDtTm, valuesDeskEndDtTm)) { //날짜가 겹치면
		    				reservationId.push(reservation.get('reservationId'));
 		    			}
 		    			else if (deskEndDtTm.isBetween(valuesDeskStartDtTm, valuesDeskEndDtTm)) { //시작날짜가 겹치지 않으면 end날짜와 다시한번 조회
 		    				reservationId.push(reservation.get('reservationId')); 		    					
		    			}

 		    			else if (valuesDeskStartDtTm.isBetween(deskStartDtTm, deskEndDtTm)) { 
 		    				reservationId.push(reservation.get('reservationId')); 		    					
		    			}
		    			
 		    			else if (valuesDeskEndDtTm.isBetween(deskStartDtTm, deskEndDtTm)) { 
 		    				reservationId.push(reservation.get('reservationId')); 		    					
		    			}
 		    			else { //안겹침
 		    			}
						
					});
					
                	var temp = 0;
					reservationId.forEach(function(item) {
					var reservationTemp = ReservationList.filter({ reservationId: item });
                	temp++;
                	
                	});					
                	//좌석의 deskMax(최대수용인원) 체크
     	            var deskList = DeskList.filter({ deskId: values['deskId']});
     	            var deskMax = deskList[0].get('deskMax');
     	           	if (temp >= deskMax) {
     	           		alert('해당 시간에는 이미 좌석이 등록되어 있습니다');
     	           	}
     	           	else {
                        model.reservationsOfOrder.create(values, {
                        	wait : true,
                            success : function (model, response) {
                            	
                            	var orderId = response.orderId;
						    	var reservationId = response.reservationId;
						    	
						        model.set('reservationId', response.reservationId);
								model.set('insertDt', response.insertDt);
								model.set('updateDt', response.updateDt);
								
								PayList.create({
			                        orderId: orderId,
			                        reservationId: reservationId,
			                        payDt: sysDate.clone().valueOf(),
			                        payType: values['payType'],
			                        payInOutType: values['payInOutType'],
			                        payAmount: values['payAmount'],
			                        payNote: values['payNote'],
			                        memberId: values['memberId'],	
								}, {
									wait : true,
			                        success : function (model, response) {
			                        	if (amount > 0) {
                            				/*
			                        		if (phone_number != '' || phone_number != null) {		                            							                            				
                            					//고객에게 알림톡 전송
                            					var Alimtalk = {
	                            						roomName: roomName,
	                            						deskName: deskName,
		                            					memberName: memberName,
		                            					phone_number: phone_number,
		                            					school: school,
		                            					registration: registration,
		                            					amount: values['payAmount']
		                            			};			            			                    
                            					alimtalkSend(Alimtalk, branchId);
                            				}
                            				*/
            			                  	//센터장에게 알림톡 전송
            			                    var AlimtalkManager = {
                            						roomName: roomName,
                            						deskName: deskName,
	                            					memberName: memberName,
	                            					phone_number: branchTel,
	                            					school: school,
	                            					registration: registration,
	                            					amount: values['payAmount']
	                            			};
            			                    alimtalkSend(AlimtalkManager, branchId);
                        				}
			                        	
			                        	$view.hide();
								        EditReservation_flag = true;
								        $('.orderSection .deskReservationList .btn-form-change-reservation').trigger('click');
			                        }
								});

                            }

                        });
     	           	}
                }
                
                else {
                    model.reservationsOfOrder.create(values, {
                    	wait : true,
                        success : function (model, response) {
                        	
                        	var orderId = response.orderId;
					    	var reservationId = response.reservationId;
					    	
					        model.set('reservationId', response.reservationId);
							model.set('insertDt', response.insertDt);
							model.set('updateDt', response.updateDt);
							
							PayList.create({
		                        orderId: orderId,
		                        reservationId: reservationId,
		                        payDt: sysDate.clone().valueOf(),
		                        payType: values['payType'],
		                        payInOutType: values['payInOutType'],
		                        payAmount: values['payAmount'],
		                        payNote: values['payNote'],
		                        memberId: values['memberId'],	
							}, {
								wait : true,
		                        success : function (model, response) {
		                        	if (amount > 0) {
                        				/*
		                        		if (phone_number != '' || phone_number != null) {		                            							                            				
                        					//고객에게 알림톡 전송
                        					var Alimtalk = {
                            						roomName: roomName,
                            						deskName: deskName,
	                            					memberName: memberName,
	                            					phone_number: phone_number,
	                            					school: school,
	                            					registration: registration,
	                            					amount: values['payAmount']
	                            			};			            			                    
                        					alimtalkSend(Alimtalk, branchId);
                        				}
                        				*/
        			                  	//센터장에게 알림톡 전송
        			                    var AlimtalkManager = {
                        						roomName: roomName,
                        						deskName: deskName,
                            					memberName: memberName,
                            					phone_number: branchTel,
                            					school: school,
                            					registration: registration,
                            					amount: values['payAmount']
                            			};
        			                    alimtalkSend(AlimtalkManager, branchId);
                    				}
		                        	
		                        	$view.hide();
							        EditReservation_flag = true;
							        $('.orderSection .deskReservationList .btn-form-change-reservation').trigger('click');
							        
		                        }
							});

                        }

                    });
                }
              }

            }

        },
        doNotSaveChangeReservationOfOrder: function(e) {
            this.$('.changeReservation').hide();

        },
        doNotSaveAddReservationOfOrder: function(e) {
            this.$('.addReservation').hide();

        },
        saveReservation: function(e) {
        	
        	
            var values = {};
            var $modal = $('#modal-reservation-of-order-form');
            var $form = $modal.find('form');
            if($form.valid()){
            		//submitHandler: function(form) {
            			var $inputs = $form.find(':input');
            			$inputs.each(function(i, element) {
                            var $element = $(element);
                            if ($element.is('[type=radio]')) {
                                if ($element.is(':checked')) {
                                    var name = $element.attr('name');
                                    if (!(_.isEmpty(name))) {
                                        values[name] = $element.val();

                                    }
                                }

                            } else {
                                var name = $element.attr('name');
                                if (!(_.isEmpty(name))) {
                                    values[name] = $element.val();                                    
                                }

                            }
                        });

                        if (values['memberId'] == "") {
                        	alert("memberId null!");
                        }
                        
//                         else if (values['deskId'] == "") {
//                         	alert("deskId null!");
//                         }
                        else {
                            this.currentReservationOfOrderModel.save(values, {
                                wait: true,
                                success: function (model, response) {
                                    $modal.modal('hide');

                                    model.collection.fetch();

                                },

                            });
                        }
            		//},
            };
            //$form.submit();
            
        },
        formAddPay: function(addCostModel){        	        	        
        	var reservationId = addCostModel.currentTarget.offsetParent.offsetParent.id;
        	var memberId = this.currentReservationModel.get("memberId");
        	var orderId = this.currentReservationModel.get("orderId");
        	
        	var model = new PayModel();
        	
        	this.$('.addPay').show();
        	
        	var data = model.toJSON();
        	
        	data['orderId'] = orderId;
        	data['reservationId'] = reservationId;
        	data['memberId'] = memberId;
        	data['members'] = MemberList.toJSON();
        	data['payTypes'] = payTypes;
        	
        	var currentDate = sysDate.clone();
        	data['payDt'] = currentDate.valueOf();
        	data['payTm'] = currentDate.valueOf();
        	
        	// 추가 요금이 발생한 경우        	
        	if (addCostModel.cid) {
        		data['payAmount'] = addCostModel.get('addCost');
        		data['payNote'] = addCostModel.get('payNote');
        	}
        	
        	var formHtml = this.templateForPayForm(data);
        	
        	var $f = this.$('.addPayForm');
        	$f.html(formHtml);
        		
        		$f.find('.chosen').chosen({width: "100%"});
        		
                /*$f.find('form').on('submit', function(e) {
                    e.preventDefault();
                    
                });*/
            
        	$.validator.addMethod(
            		'chkNumeric', function (value, element) {
            			value = value.replace(/,/g, '');
		                return this.optional(element) || /^[0-9]+$/.test(value);
		                
		                	}, '숫자만 사용 가능합니다'
            );
        		
            $f.find('form').validate({
            	ignore: "not:hidden",	
            	rules: {
            			memberId: {            			
            				required: true,
            			},
            			payType: {
            				required: true,
            				number: true,
            			},
            			payAmount: {
            				//min: 1000
            				chkNumeric: true,
       			}
            			
            		},
        		messages: {
            			memberId: {            	
            	    		required: "회원을 선택하세요",
            			},
            			payType: {
            				required: "결제유형을 선택하세요",
            				number : "결제유형을 선택하세요",
            			},
            			payAmount: {
            				//min: "금액을 선택하세요"
            			}
        		}		
            });
        

        },
        
        formAddRental: function(addCostModel){        	
        	var memberId = this.currentReservationModel.get("memberId");
        	var orderId = this.currentReservationModel.get("orderId");
        	
        	var model = new RentalModel();
        	
        	this.$('.addRental').show();
        	
        	var data = model.toJSON();
        	
        	data['orderId'] = orderId;
        	data['memberId'] = memberId;
        	data['members'] = MemberList.toJSON();
        	data['rentalTypes'] = rentalTypes;
        	
        	var currentDate = sysDate.clone();
        	
        	var formHtml = this.templateForPayForm(data);
        	
        	var $f = this.$('.addRentalForm');
        	$f.html(formHtml);
        		
        	$f.find('.chosen').chosen({width: "100%"});
 
            $f.find('form').validate({
            	ignore: "not:hidden",	
            	rules: {
            			memberId: {            			
            				required: true,
            			},
            			rentalType: {
            				required: true,
            				number: true,
            			},

            		},
        		messages: {
            			memberId: {            	
            	    		required: "회원을 선택하세요",
            			},
            			rentalType: {
            				required: "결제유형을 선택하세요",
            				number : "결제유형을 선택하세요",
            			},
            			
        		}		
            });
        

        },
        addPay: function() {
        	var values = {};
            var $modal = $('#modal-order-form');
            
            var $view = $('.addPay');
			var $form = $view.find('form');
			//$form.validate();
            
			
			if($form.valid()) {
                //var $inputs = $modal.find(':input');
				var $inputs = $form.find(':input');
                $inputs.each(function(i, element) {
                    var $element = $(element);
                    if ($element.is('[type=radio]')) {
                        if ($element.is(':checked')) {
                            var name = $element.attr('name');
                            if (!(_.isEmpty(name))) {
                                values[name] = $element.val();

                            }
                        }

                    } else {
                        var name = $element.attr('name');
                        if (!(_.isEmpty(name))) {
                        	values[name] = $element.val();
//                            if(!(_.isEmpty(values[name]))) { //임시 적용 -> 등록이 없는 상태를 지금의 시간 기준으로 정하여서 과거의 데이터가 비어있는 데이터로 들어감.
//                        		
//    						}
//                            else if (_.isEmpty(values[name])) {	//비어있을 경우 (임시 적용)                            	
//                            	values[name] = $element.val();
//                            	
//                            }
                        }

                    }
                });
                values['payInOutType'] = '20'; //임시
                var currentDate = sysDate.clone();                
                values['payDt'] = currentDate.valueOf();
               
                values['payAmount'] = values['payAmount'].replace(/,/g, '');
                
              	//알림톡 정보
 	            var roomName;
 	            var deskName;
 	            var memberName;
 	            var phone_number;
 	            var school;
 	            var registration;
 	            var amount;
 	             	           
 	           	amount = values['payAmount'];
 	           	registration = '결제추가';
 	           	
 	           	var reservationList = ReservationList.filter({ reservationId: values['reservationId'] });
 	           	
 	           	if (reservationList[0] != null) {
 	           		if (reservationList[0].get('deskId') != null || reservationList[0].get('deskId') != '') {
 	           			var deskList = DeskList.filter({ deskId: reservationList[0].get('deskId') });
 	           			if (deskList[0] != null) {
 	     	            	if (deskList[0].get('deskId') != null) {
 		     	            	var roomList = RoomList.filter({ roomId: deskList[0].get('roomId')});
 		     	            	if (roomList[0] != null || roomList[0] != '') {
 		     	            		roomName = roomList[0].get('name');
 		     	            	}
 		     	            	else {
 		     	            		roomName = '미입력';
 		     	            	}
 	     	            	}
 	     	            	if (deskList[0].get('name') != null || deskList[0] != '') {
 	     	           			deskName = deskList[0].get('name');
 	     	            	}
 	     	            	else {
 	     	            		deskName = '미입력';
 	     	            	}
 	           			}
 	           		}
 	           	}
 	           	
 	           	
 	           	var memberList = MemberList.filter({ memberId: values['memberId']});
	            if (memberList[0] != null) {	     	           	
	            	if (memberList[0].get('name') != null) {
	            		memberName = memberList[0].get('name');
	            	}
	           		if (memberList[0].get('telParent') != null) {
	            		phone_number = memberList[0].get('telParent');	//고객용
	           		}
	           		if (memberList[0].get('school') != null || memberList[0].get('school') != '') {
	           			school = memberList[0].get('school');
	           		}
	           		else {
	           			school = '미입력';
	           		}
	            }	           
 	            
                if (values['memberId'] == "") {
                	alert("memberId null!");
                }
                
                else if (values['payType'] == "") {
                	alert("payType null!");
                }
                else {                	                	

                	model.paysOfOrder.create(values, {
                		
                        wait : true,
                        success : function (model, response) {
//                            model.set('payId', response.payId);
//                            model.set('insertDt', response.insertDt);
//                            model.set('updateDt', response.updateDt);
                        	if (amount > 0) {
                				/*
                        		if (phone_number != '' || phone_number != null) {		                            							                            				
                					//고객에게 알림톡 전송
                					var Alimtalk = {
                    						roomName: roomName,
                    						deskName: deskName,
                        					memberName: memberName,
                        					phone_number: phone_number,
                        					school: school,
                        					registration: registration,
                        					amount: values['payAmount']
                        			};			            			                    
                					alimtalkSend(Alimtalk, branchId);
                					
                				}
                				*/
        	                  	//센터장에게 알림톡 전송
        	                    var AlimtalkManager = {
                						roomName: roomName,
                						deskName: deskName,
                    					memberName: memberName,
                    					phone_number: branchTel,
                    					school: school,
                    					registration: registration,
                    					amount: values['payAmount']
                    			};
        	                    alimtalkSend(AlimtalkManager, branchId);
            				}
                        	
                        	$view.hide();                        	
                        	model.collection.fetch();
                        	
                            //$modal.modal('hide');
                            //PayList.fetch();
                        }

                    });
                }
			}

        },
        
        refundPay: function(e) {
        	var values = {};
            var $modal = $('#modal-order-form');
            
            var $view = $('.addPay');
			var $form = $view.find('form');
			//$form.validate();
            
			
			if($form.valid()) {
                //var $inputs = $modal.find(':input');
				var $inputs = $form.find(':input');
                $inputs.each(function(i, element) {
                    var $element = $(element);
                    if ($element.is('[type=radio]')) {
                        if ($element.is(':checked')) {
                            var name = $element.attr('name');
                            if (!(_.isEmpty(name))) {
                                values[name] = $element.val();

                            }
                        }

                    } else {
                        var name = $element.attr('name');
                        if (!(_.isEmpty(name))) {
                        	values[name] = $element.val();
//                            if(!(_.isEmpty(values[name]))) { //임시 적용 -> 등록이 없는 상태를 지금의 시간 기준으로 정하여서 과거의 데이터가 비어있는 데이터로 들어감.
//                        		
//    						}
//                            else if (_.isEmpty(values[name])) {	//비어있을 경우 (임시 적용)
//                            	values[name] = $element.val();
//                            	
//                            }
                        }

                    }
                });
                values['payInOutType'] = '20'; //임시
                var currentDate = sysDate.clone();                
                values['payDt'] = currentDate.valueOf();
               
                values['payAmount'] = '-' + values['payAmount'].replace(/,/g, '');
                
              	//알림톡 정보
 	            var roomName;
 	            var deskName;
 	            var memberName;
 	            var phone_number;
 	            var school;
 	            var registration;
 	            var amount;
 	             	           
 	           	amount = values['payAmount'];
 	           	
 	           	var reservationList = ReservationList.filter({ reservationId: values['reservationId'] });
 	           	

 	            
                if (values['memberId'] == "") {
                	alert("memberId null!");
                }
                
                else if (values['payType'] == "") {
                	alert("payType null!");
                }
                else {                	                	

                	model.paysOfOrder.create(values, {
                		
                        wait : true,
                        success : function (model, response) {           	
                        	$view.hide();                        	
                        	model.collection.fetch();
                        }

                    });
                }
			}
        },
        
        doNotAddPay: function(e) {
        	this.$('.addPay').hide();
        },
        doNotAddRental: function(e) {
        	this.$('.addRental').hide();
        },        
        setReservationTime: function(e) {
        	var $btn = $(e.currentTarget);
            var $form = $btn.parentsUntil('form').last().parent();

            var unit = $btn.attr('data-add-unit');
            var value = $btn.attr('data-add-value');
            if(unit == 'h') {
                var deskStartDt = $form.find('#deskStartDt').val();
                var deskStartTm = $form.find('#deskStartTm').val();

                var deskEndTmDt = moment(deskStartDt + ' ' + deskStartTm, momentDatepickerFormat + ' ' + momentClockpickerFormat).add(value, unit);
                
                $form.find('#deskEndDt').datepicker('setDate', deskEndTmDt.format(momentDatepickerFormat));
//                 $form.find('#deskEndDt').val(deskEndTmDt.format(momentDatepickerFormat))
//                 						.trigger($.Event('changeDate', { date:deskEndTmDt.toDate()}));
                $form.find('#deskEndTm').val(deskEndTmDt.format(momentClockpickerFormat))
                						.trigger('change');

            } else if(unit == 'd') {
                var deskStartDt = $form.find('#deskStartDt').val();

                if(value == "1") {
                    // 당일
                    var d = moment(deskStartDt, momentDatepickerFormat);
                    $form.find('#deskEndDt').datepicker('setDate', d.format(momentDatepickerFormat));

                } else {
                    // 며칠
                    var d = moment(deskStartDt, momentDatepickerFormat).add(value, unit);
                    $form.find('#deskEndDt').datepicker('setDate', d.format(momentDatepickerFormat));

                }

                $form.find('#deskEndTm').val('23:59')
                						.trigger('change');

            } else if(unit == 'M') {
                var deskStartDt = $form.find('#deskStartDt').val();                
                
                /*
                var d = moment(deskStartDt, momentDatepickerFormat).add(-1, 'd').add(value, unit);                
                
                $form.find('#deskEndDt').datepicker('setDate', d.format(momentDatepickerFormat));
                
//                 $form.find('#deskEndDt').trigger($.Event('changeDate', { date:d.toDate()}))
//                 						.val(d.format(momentDatepickerFormat));
				*/
				
                var d = moment(deskStartDt, momentDatepickerFormat).add(-1, 'd').add(value, unit);
                $form.find('#deskEndDt').val(d.format(momentDatepickerFormat))
                						.trigger($.Event('changeDate', { date:d.toDate()}));

                $form.find('#deskEndTm').val('23:59')
		                				.trigger('change');
               


            } else if(unit == 'PM') {
            	var deskStartDt = $form.find('#deskStartDt').val();
            	var deskEndDt = $form.find('#deskEndDt').val();
            	
            	if(value == "1") {
            		//+1일
                    var d = moment(deskEndDt, momentDatepickerFormat).add(value, 'd'); 
                    $form.find('#deskEndDt').datepicker('setDate', d.format(momentDatepickerFormat));
                    
//                     $form.find('#deskEndTm').val('23:59')
//     										.trigger('change');
            	}
            	else if (value == "-1") {
					//-1일
                    var d = moment(deskEndDt, momentDatepickerFormat).add(value, 'd');
                    $form.find('#deskEndDt').datepicker('setDate', d.format(momentDatepickerFormat));

//                     $form.find('#deskEndTm').val('23:59')
//     		                				.trigger('change');
            	}
            	else if(value == "2") {
            		monthFlag++;
            		if (monthFlag==1) {
            			var deskDt = deskStartDt.replace(/ /g, '');
            		}
            		else if (monthFlag > 1) {
            			var deskDt = deskEndDt.replace(/ /g, '');
            		}
            		var regMonth = deskDt.substring(5,7);
            		regMonth = regMonth.replace('월', '');
            		
					//몇월인지 구분
            		if(regMonth == 1 || regMonth == 3 || regMonth == 5 || regMonth == 7 || regMonth == 8 || regMonth == 10 || regMonth == 12) { //31일
            		
            			if (monthFlag==1) {
            				var d = moment(deskEndDt, momentDatepickerFormat).add(30, 'd');
            			}
            			else if (monthFlag > 1) {
            				var d = moment(deskEndDt, momentDatepickerFormat).add(31, 'd');
            			}
            			
            		}
            		else if (regMonth == 2 || regMonth == 4 || regMonth == 6 || regMonth == 9 || regMonth == 11) { //30일            			
            			if (regMonth == 2) {
            				var d = moment(deskEndDt, momentDatepickerFormat).add(moment(deskEndDt, momentDatepickerFormat).daysInMonth(), 'd');
            			}
            			else {	
	            			if (monthFlag==1) {
	            				var d = moment(deskEndDt, momentDatepickerFormat).add(29, 'd');
	            			}
	            			else if (monthFlag > 1) {
	            				var d = moment(deskEndDt, momentDatepickerFormat).add(30, 'd');
	            			}
            			}
            		}
            			
            		 
                   $form.find('#deskEndDt').datepicker('setDate', d.format(momentDatepickerFormat));
            	}
            }
            


        },
        
        deleteReservationOne: function(e) {
        	var reservationId = e.attributes.reservationId;        	
        	this.currentReservation = ReservationList.findWhere({reservationId: reservationId});        	
        	
        	reservationModels = model.reservationsOfOrder;
        	payModels = model.paysOfOrder;

        	var $modal = $('#modal-order-form');
        	
        	if(confirm('등록 삭제할 경우 해당 등록 내역과 해당된 결제 내역까지 삭제됩니다. 정말로 삭제하시겠습니까?')) {
    			this.currentReservation.destroy({
	                wait: true,
	                success: function (model, response) {
	                
		        	},
	        	
	        	});
        		
	        	payModels.forEach(function(item) {
	        		if (item.attributes.reservationId == reservationId) {
		        		currentPayOfOrderModel = item;		                	
			        	this.currentPayOfOrderModel.destroy({
		        		   headers: {
		        			      'flag': true
		        		   },
			        	
			               wait: true,
			               success: function (model, response) {
			                		                    
			
			               },		        		 
			        	});
		        	
	        		}
		        	
	        	});
		        
		        //$modal.modal('hide');
		        location.reload();
        	}
        },
        
    });



    var ModalOrderFormApp = new ModalOrderFormView;

    var data = model.toJSON();
    data['members'] = MemberList.toJSON();
    data['desks'] = DeskList.toJSON();
    data['rooms'] = RoomList.toJSON();

    var titleHtml = '등록';
    //var bodyHtml = this.templateForForm(data);

    $('#modal-order-form')
    .one('show.bs.modal', function(e) {

        model.reservationsOfOrder.fetch({reset: true,});
        model.paysOfOrder.fetch({reset: true,});

        var $modal = $(this);
        $modal.find('.modal-title').html(titleHtml);

    })
    .on('hidden.bs.modal', function(e) {

		if (flag == 'branch_op') {
	    	var $modal = $(this);
	    	$modal.find('.changeReservation,.addReservation,.addPay').hide();
	    	$modal.find('.changeReservationForm,.addReservationForm,.addPayForm').empty();

	    	ModalOrderFormApp.undelegateEvents();
	    	
	    	ModalOrderFormApp.stopListening();
	    	//location.reload()
	    	if (EditReservation_flag) location.reload();
			EditReservation_flag = false;
		}
		else if (flag == 'branch_reservations') {
	    	var $modal = $(this);
	    	$modal.find('.changeReservation,.addReservation,.addPay').hide();
	    	$modal.find('.changeReservationForm,.addReservationForm,.addPayForm').empty();

	    	ModalOrderFormApp.undelegateEvents();
	    	
	    	ModalOrderFormApp.stopListening();
			
			if (EditReservation_flag) location.reload();
			EditReservation_flag = false;
		}
    })
    .modal();

    return ModalOrderFormApp;
        
}

var currentScannerInput;
var currentScannerTarget;
function setScannerDetectionConfig($input, $target) {
	currentScannerInput = $input;
	currentScannerTarget = $target;

	currentScannerInput
	    .off('keydown.enterkey')
	    .on('keydown.enterkey', function(e) {
	        if(e.keyCode == 13) {
	            currentScannerTarget.trigger('scannerDetected');

	        }

	    });

}

function scannerDetection() {
	
    $(document).scannerDetection({	
	//$input.scannerDetection({
		avgTimeByChar: 40,
		onComplete: function(barcode, qty){
			currentScannerInput.val(barcode);			
			currentScannerTarget.trigger('scannerDetected');
			//onKeyDetect: function(event){console.log(event.which); return false;}
		},
		onError: function(string){
			//alert('Error ' + string);
		},

    });
}


