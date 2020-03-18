package kr.co.cntt.scc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.co.cntt.scc.app.admin.model.statusList.StatusListEntry;
import kr.co.cntt.scc.app.admin.model.statusList.TotalLearning;
import kr.co.cntt.scc.app.student.model.AppClientMember;
import kr.co.cntt.scc.app.student.model.ReportingTime;
import kr.co.cntt.scc.app.student.service.AppClientMemberService;
import kr.co.cntt.scc.app.student.service.SeatApplicationService;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.model.Entry;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.service.BranchMemberService;
import kr.co.cntt.scc.service.BranchReservationService;
import kr.co.cntt.scc.service.BranchSafeServiceService;
import kr.co.cntt.scc.service.BranchService;
import kr.co.cntt.scc.service.SalesDataService;
import kr.co.cntt.scc.service.SmsService;
import kr.co.cntt.scc.util.DateUtil;


@EnableScheduling
@Component
public class Scheduler {
	
	@Autowired
	private BranchReservationService branchReservationService;
	
	@Autowired
	private BranchMemberService branchMemberService;
	
	@Autowired
	private BranchService branchService;
    
	@Autowired
    private SmsService smsService;
		
	@Autowired
	private SalesDataService sds;
	
	@Autowired
	private AppClientMemberService appMemberService;
	
	@Autowired
	private SeatApplicationService seatApplicationService;
	
	@Autowired
	private BranchSafeServiceService branchSafeServiceService;
	
	//@Scheduled(cron="*/30 * * * * *") 30초 마다 실행
	/*
	 *  표현식       			의미 
        0 0 12 * * *    	매일 12시에 실행 
 		0 15 10 * * *		매일 10시 15분에 실행 
 		0 * 14 * * *		매일 14시에 0분~59분까지 매분 실행
 		0 0/5 14 * * *		매일 14시에 시작해서 5분 간격으로 실행 
 		0 0/5 14,18 * * *	매일 14시, 18시에 시작해서 5분 간격으로 실행 
 		0 0-5 14 * * *		매일 14시에 0분, 1분, 2분, 3분, 4분, 5분에 실행 
 		0 0 20 ? * MON-FRI	월~금일 20시 0분 0초에 실행 
 		0 0/5 14 * * ?		아무요일, 매월, 매일 14:00부터 14:05분까지 매분 0초 실행 (6번 실행됨)
 		0 15 10 ? * 6L		매월 마지막 금요일 아무날이나 10:15:00에 실행
 		0 15 10 15 * ?		아무요일, 매월 15일 10:15:00에 실행 
	 	* /1 * * * * 		매 1분마다 실행
	 	* /10 * * * * 		매 10분마다 실행 	 
	 */
	
	@Scheduled(cron = "00 30 02 * * *")	
	public void entryScheduler() {
		List<Entry> entryList = branchMemberService.selectCurrentMemberEntryList();
		if (entryList.size() > 0) {
			for(Entry entry: entryList) {				
				if (entry.getEntryType() != 2) {
					branchMemberService.insertMemberEntryOUT(entry.getBranchId(), entry.getMemberId(), entry.getReservationId(), entry.getDeskId());
				}
			}
		}
	}
	
	@Scheduled(cron = "00 00 10 * * *")
	//@Scheduled(cron="*/10 * * * * *") //30초 마다 실행
	public void smsScheduler() { //매 오전 10시
        // 지점 목록 조회
        List<Branch> branches = branchService.selectBranchList();        
        
        
        for(Branch branch: branches) {
        	
        	List<BranchMember> members = branchMemberService.selectAllMemberList(branch.getBranchId());
        	String branchId = branch.getBranchId();
        	for(BranchMember member: members) {
        		if(member.getEnterexitYes() == 1) { //3일 전 SMS 및 프로모션 동의한 회원들에게만 문자전송   
        			
        			String memberId = member.getMemberId();
        			
        			List<Reservation> reservationCountList = branchReservationService.selectReservationCount(branchId, memberId);
        			if (reservationCountList.size() == 1) {
            			// 3일 남은 등록 확인
    					Reservation reservation = branchReservationService.select3daysReservation(branchId, memberId);
    					if (reservation != null) {
    						//String currentDate = DateUtil.getCurrentDateString().substring(8, 10);
    						//String DeskEndDt = reservation.getDeskEndDt().toString().substring(8, 10); 
    						//int dayGap = Integer.parseInt(DeskEndDt) - Integer.parseInt(currentDate);
    						int dayGap = reservation.getDayGap();
    						
    						if (dayGap == 0) { //하루
                                String msg = String.format("[CNT %s] %s님은 당일 좌석 만기입니다.", branch.getName(), member.getName());                        	
                              
                            	smsService.sendSms(branchId, memberId,
                                        branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
                                
                               
    						}
    						else if (dayGap == 1) { //이틀
                                String msg = String.format("[CNT %s] %s님은 좌석 만기 1일 전입니다.", branch.getName(), member.getName());

                            	smsService.sendSms(branchId, memberId,
                                        branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
                                
                                
    						}
    						else if (dayGap == 2) { //삼일
                                String msg = String.format("[CNT %s] %s님은 좌석 만기 2일 전입니다.", branch.getName(), member.getName());
                           
                            	smsService.sendSms(branchId, memberId,
                                        branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
                            
                                
    						}
    						
    						else if (branchId.equals("ef83445e-e6c9-4901-954b-35d145fcf73b") && dayGap == 6) {
//    							 String msg = String.format("[CNT %s] %s님은 좌석 만기 7일 전입니다", branch.getName(), member.getName());
//                             	
//                             	if (member.getTelParent().equals(member.getTel())) {
//                             		smsService.sendSms(branchId, memberId,
//                                            branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
//                             	} else {
//                             		smsService.sendSms(branchId, memberId,
//                                            branch.getName(), branch.getTel(), member.getName(), member.getTel(), msg);
//                             	}

                                 
    						}
    					}
        			}
        			else {
        				
        			}

        		}
        	
        	}
        	
        }
        
        
	}
	
	
	//statistics_sales_day
	@Scheduled(cron = "0 40 02 * * *")
	public void salesDataScheduler() { 
        	sds.salesData_scheduler();
	}

	//statistics_entry_day
	@Scheduled(cron = "0 50 02 * * *")	
	public void entryDataScheduler() {
		//시간 계산
    	List<StatusListEntry> myTotalLearningTm = null;
    			    	
    	List<ReportingTime.Temp> entryMemberIdList = branchMemberService.selectEntryMemberId(null, null);
    	
        //시간 계산
    	TotalLearning totalLearning = new TotalLearning();
    	ReportingTime.TimeTest myTime = new ReportingTime.TimeTest();
    	List<ReportingTime.TimeTest> myTimeList = new ArrayList<ReportingTime.TimeTest>();
    	
    	
    	if(entryMemberIdList.size() > 0) {
        	totalLearning = new TotalLearning();
        	myTime = new ReportingTime.TimeTest();
        	myTimeList = new ArrayList<ReportingTime.TimeTest>();
        	
    		for(ReportingTime.Temp em : entryMemberIdList) {
				myTotalLearningTm = branchMemberService.selectStatusListEntryList2(em.getBranchId(), em.getMemberId(), null, null, null);
				
				if(myTotalLearningTm.size() > 0) {
					
				} else {
					//에러
				}

	
		        int myDiff = 0;
		        for (StatusListEntry sle : myTotalLearningTm) {
		        	////////total 시간 계산    		
		        	int tempIndex = myTotalLearningTm.indexOf(sle);
		        			        	
		        	if (sle.getEntryType().equals("1") || sle.getEntryType().equals("4")) {
		            	//dateIn.
		            	totalLearning.setEntryDt(sle.getEntryDtOg());
		            	totalLearning.setEntryType(sle.getEntryType());
		            	totalLearning.setBusinessDt(sle.getBusinessDt());
		
		            } else if ( (sle.getEntryType().equals("2") || sle.getEntryType().equals("3")) && totalLearning.getEntryType() != null && sle.getBusinessDt().equals(totalLearning.getBusinessDt()) ) {
		            	if (totalLearning.getEntryType().equals("2") || totalLearning.getEntryType().equals("3") ) {
		            		
		            	} else if (totalLearning.getEntryType().equals("1") || totalLearning.getEntryType().equals("4")) {
		            		
		            		Calendar startCal = Calendar.getInstance();
			            	Calendar endCal = Calendar.getInstance();
			            	
			            	startCal.setTime(totalLearning.getEntryDt());
			            	endCal.setTime(sle.getEntryDtOg());
			            	
			            	totalLearning = new TotalLearning();
			            	totalLearning.setEntryDt(sle.getEntryDtOg());
			            	//totalLearning.setEntryType("2");
			            	totalLearning.setEntryType(sle.getEntryType());
			            	totalLearning.setBusinessDt(sle.getBusinessDt());
		            		long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
		        			
			            	myDiff += (int)(diffMillis/(1000 * 60));

		            	
		            	}
	
		            }
		        			        	
		        	//날짜가 다른 데이터
		        	if(tempIndex != 0 ) {
			        	if ( !sle.getBusinessDt().equals(myTotalLearningTm.get(tempIndex-1).getBusinessDt()) || tempIndex == myTotalLearningTm.size()-1 ) {		        	
				        	
			        		if (myDiff != 0) {
			            		myTime.setBusinessDt((myTotalLearningTm.get(tempIndex-1).getBusinessDt()));
			            		myTime.setTime(myDiff);
			            		myTime.setBranchId(em.getBranchId());
			            		myTime.setMemberId(em.getMemberId());
			            		
			    				//받아온 정보로 매장명 및 회원명 찾기
			    				List<BranchMember> codiMemberInfo = branchMemberService.selectMemberList(em.getBranchId(), em.getMemberId(), null); //코디에서 멤버찾기
			    								
			    				if (codiMemberInfo.size() > 0) { 
			    					myTime.setBranchNm(codiMemberInfo.get(0).getBranchNm());
									myTime.setMemberNm(codiMemberInfo.get(0).getName());
									myTime.setNo(codiMemberInfo.get(0).getNo());
			    				} else { //코디 정보가 없으면 App 에서 찾기
			    					List<AppClientMember> appClientMemberInfo = appMemberService.selectAppBranchManagerForMemberId(em.getMemberId());
			    					if (appClientMemberInfo.size() > 0) {
			    						List<AppClientMember> appClientName = appMemberService.selectAppMemberList(appClientMemberInfo.get(0).getNo(), 10);
			    						
			    						if (appClientName.size() > 0 ) {
			    							myTime.setBranchNm(appClientMemberInfo.get(0).getBranchNm());
			    							myTime.setMemberNm(appClientName.get(0).getName());
			    							myTime.setNo(appClientMemberInfo.get(0).getNo());
			    						}
			    					}
			    				}
			            		
			    				//String timeTempStr = myTotalLearningTm.get(tempIndex-1).getEntryTm().replaceAll(":", "");
			    				String timeTempStr = myTotalLearningTm.get(tempIndex).getEntryTm().replaceAll(":", "");
			    				Integer timeTempInt = Integer.parseInt(timeTempStr); 
			    				
			    				//String timeTemp1Str = "023000";
			    				//String timeTemp1Str = "022900";
			    				//Integer timeTemp1 = Integer.parseInt(timeTemp1Str);
			    				Integer timeTemp1 = 22900;
			    				
			    				//String timeTemp2Str = "023300";
			    				//Integer timeTemp2 = Integer.parseInt(timeTemp2Str);
			    				Integer timeTemp2 = 23300;			    				
			    				
			    				if (timeTemp1 < timeTempInt && timeTempInt < timeTemp2) {

			    					myTime.setAutoYn(true);
			    				} else {
			    					myTime.setAutoYn(false);
			    				}
			    				
			    				branchMemberService.insertStatisticsEntry(myTime);
			            		myTimeList.add(myTime);
			            		
			            		myDiff = 0;
			            		myTime = new ReportingTime.TimeTest();	            		
			            		//totalLearning = new TotalLearning();

				        	}
			        	}
		        	}
		        	
        	
		        	
		        }
        
        
    		}
        
    	}
	}
	
	
	
	 
	//지난 무료체험/좌석예약 & 학부모 자동취소 처리
	@Scheduled(cron = "0 00 03 * * *")
	public void seatApplicationScheduler() {
		int seatApplicationResult = seatApplicationService.schedulerSeatApplication();
		int safeServiceResult = branchSafeServiceService.schedulerParentsSafeservice();
		
	}
	
	//만기 체크
//	@Scheduled(cron = "0 01 00 * * *") // (새벽)12시 1분
//	public void expireScheduler() {
//		 // 지점 목록 조회
//        List<Branch> branches = branchService.selectBranchList();        
//        String endDate = DateUtil.getCurrentDatePlusDaysString(-1);
//        
//        for(Branch branch: branches) {
//        	List<BranchMember> members = branchMemberService.selectAllMemberList(branch.getBranchId());
//        	String branchId = branch.getBranchId();
//        	for(BranchMember member: members) {
//        		List<Reservation> reservations = branchReservationService.selectReservationList(branchId, null, endDate, member.getMemberId(), null, null, null, Constants.ReservationStatusType.CONFIRMED, null);
//        		if (reservations.size() > 0) {
//        			List<Reservation> reservationCountList = branchReservationService.selectReservationCount(branchId, member.getMemberId());
//        			if(reservationCountList.size() == 0) {
//        				branchMemberService.updateMemberExpire(branchId, reservations.get(0).getMemberId(), true);	
//        			}
//        			
//        		}
//        	}
//        }
//	}
	
	
}
