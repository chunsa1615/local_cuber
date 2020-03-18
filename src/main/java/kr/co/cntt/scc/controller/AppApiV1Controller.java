package kr.co.cntt.scc.controller;
import kr.co.cntt.scc.util.AuthUtil;
import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.admin.model.AppAdminUserInfo;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListEntry;
import kr.co.cntt.scc.app.admin.model.statusList.TotalLearning;
import kr.co.cntt.scc.app.student.model.AppClientMember;
import kr.co.cntt.scc.app.student.model.Rank;
import kr.co.cntt.scc.app.student.model.ReportingTime;
import kr.co.cntt.scc.app.student.service.AppClientMemberService;
import kr.co.cntt.scc.model.*;
import kr.co.cntt.scc.service.*;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import javax.validation.Valid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
/**
 *
 * AppApiV1Controller
 *
 * Created by jslivane on 2016. 10. 3..
 */
@Controller
@Slf4j
@RequestMapping("/app/entry/api/v1")
@PreAuthorize("hasAuthority('entry')") // 2016-12-22 입출입시 속도문제로 임시로 허용
//@PreAuthorize("isAnonymous()")
public class AppApiV1Controller {

    @Autowired
    private BranchService branchService;

    @Autowired
    private BranchMemberService branchMemberService;

    @Autowired
    private BranchReservationService branchReservationService;

    @Autowired
    private BranchDesignService branchDesignService;
    
    @Autowired
	private AppClientMemberService appMemberService;

    @Autowired
    UserService userService;

    @Autowired
    SmsService smsService;
    
    @Getter
    @Setter
	static class DataModelTest<T>{
		private String mode;
		private T data;
	}
    
    @Getter
    @Setter
    static class StandDataModel {
    	private String mode;
    	private String address;
    	private int startTime;
    	private int endTime;
    }

    /******************************************************************************************************************/

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody
    Branch.Display getLogin() {

        String userId = AuthUtil.getCurrentUserId();

        String branchId = userService.selectPrimaryBranchIdByUserId(userId);

        Branch branch = branchService.selectBranch(branchId);

        // 결과
        ModelMapper modelMapper = new ModelMapper();

        // 센터정보 (표시용)
        Branch.Display display = modelMapper.map(branch, Branch.Display.class);
        System.out.println("========================================display============"+display);
        return display;
    }
    
    @RequestMapping(value = "/login2", method = RequestMethod.POST)
    public @ResponseBody
    AppAdminUserInfo.Response getLogin(String name, String userPw) {

        //String userId = AuthUtil.getCurrentUserId();

    	User user = null;    	

    	//user = userService.selectUser(name, userPw);
    	
    	
        //String branchId = userService.selectPrimaryBranchIdByUserId(userId);

        //Branch branch = branchService.selectBranch(branchId);

        
        // 결과
        ModelMapper modelMapper = new ModelMapper();        
        /*
        if (user == null) {
        	testest = modelMapper.map(null, String.class);
        }
        else {
        	testest = modelMapper.map(user.getUserId(), String.class);
        }
         */
        AppAdminUserInfo.Response response = modelMapper.map("", AppAdminUserInfo.Response.class);
        response.setUserId("5f37382e-8db3-4664-9be8-482105008a10");
        System.out.println("=======================================testest=========="+response);
        return response;
    }

    /******************************************************************************************************************/

    // 광고 또는 공지사항
    @RequestMapping(value = "/b/{branchId}/an", method = RequestMethod.GET)
    public @ResponseBody
    List<Branch.AdOrNotice> getAdOrNoticeList(@PathVariable String branchId) {
        List<Branch.AdOrNotice> result = branchService.getAdOrNoticeList(branchId);

        log.info("branchId={}", branchId);

        for(Branch.AdOrNotice item: result) {
            log.info("id={}", item.getId());

        }

        return result;

    }

    /******************************************************************************************************************/

    public int getStudyTime() {
    	
    	
    	return 0;
    }
    
    @RequestMapping(value = "/b/{branchId}/e", method = RequestMethod.POST)
    public @ResponseBody
    ApiResponse postEntry(@PathVariable String branchId, @RequestBody @Valid Entry.Create entry) {
//    List<Entry> postEntry(@PathVariable String branchId, @RequestBody @Valid Entry.Create entry) {

    	System.out.println("==============================entry : " + entry);
    	
        // 회원 확인
        BranchMember member =  null;
        try {
            member = branchMemberService.selectMemberByNo(branchId, entry.getNo());
            if (member == null) {
            	return new ApiResponse("등록되지 않은 회원입니다.");

            } else {
            	//시간제 회원
            	if (member.getTimeYn() == 1) {
            		return timeMember(branchId, member, entry);            		
            	}
            }

        } catch (EmptyResultDataAccessException erdae) {
        	return new ApiResponse("등록되지 않은 회원입니다.");

        }

        String memberId = member.getMemberId();

        /*
        // 결제 확인
        //Pay pay = branchPayService.selectCurrentPayByMemberId(branchId, memberId, entry.getPassword());
        Pay pay = branchPayService.selectCurrentPayByMemberId(branchId, memberId);
        if (pay == null) {
            throw new InternalServerError("Failed to find pay");

        }
        */

        // 예약 확인
        //Reservation reservation = branchReservationService.selectCurrentReservationByMemberId(branchId, memberId);
        
        Reservation reservation = null;
        try {
            reservation = branchReservationService.selectCurrentReservationByMemberId(branchId, memberId);
            if (reservation == null) {
                //throw new InternalServerError("Failed to find reservation");
                return new ApiResponse("좌석 등록되지 않은 회원입니다.");

            }

        } catch (EmptyResultDataAccessException erdae) {
            //throw new InternalServerError("Failed to find reservation");
        	 return new ApiResponse("좌석 등록되지 않은 회원입니다.");

        }
        

        Constants.EntryType entryType = Constants.getEntryType(entry.getEntryType());

        // 결과
        ModelMapper modelMapper = new ModelMapper();

        // 회원정보 (일부)
        Entry.Response response = modelMapper.map(member, Entry.Response.class);

        // 최근 출입기록
        List<Entry> entryList = branchMemberService.selectRecentMemberEntryListByMemberId(branchId, memberId);
        response.setEntryList(entryList);

        // 현재 예약정보
        response.setReservation(reservation);

        if(reservation != null) {

            // 출입유형 확인 (최종 출입기록과 비교)
            Entry lastEntry = (entryList == null || entryList.size() == 0) ? null : entryList.get(0);            
            //log.debug(lastEntry.getEntryDt().toString());
            // FIXME : 센터별 운영시간 고려
            // 우선 해당일 9시기준             
            if(lastEntry != null && LocalDate.now().atStartOfDay().plusHours(8).isAfter(LocalDateTime.ofInstant(lastEntry.getEntryDt().toInstant(), ZoneId.systemDefault()))) {
                // 해당일 8시이전이면 최종 출입기록이 없는 것으로 -> 센터가 새벽 2시까지 운영하기 때문에 2시까지 조건 추가
                if(LocalDate.now().atStartOfDay().plusHours(2).isAfter(LocalDateTime.ofInstant(lastEntry.getEntryDt().toInstant(), ZoneId.systemDefault()))){
                	
                }
                else {
                	lastEntry = null;
                }
            }
            
            if(lastEntry == null) {
                // 최종 출입기록이 없는 경우
                if(!entryType.equals(Constants.EntryType.IN)) {
                    // 입실이 아닌 경우
                    return new ApiResponse("입실 기록이 없습니다.");

                }

            } else {
                String lastEntryType = String.valueOf(lastEntry.getEntryType());
//                log.debug(lastEntryType);

                if(lastEntryType.equals(Constants.EntryType.IN.getValue())) {
                    // 최종 출입이 입실인 경우
                    if (entryType.equals(Constants.EntryType.IN) || entryType.equals(Constants.EntryType.RETURN)) {
                        // 입실이거나 복귀인 경우
                        return new ApiResponse("퇴실 또는 외출 기록이 없습니다.");

                    }

                } else if(lastEntryType.equals(Constants.EntryType.OUT.getValue())) {
                    // 최종 출입이 퇴실인 경우
                    if(!entryType.equals(Constants.EntryType.IN)) {
                        // 입실이 아닌 경우
                        return new ApiResponse("입실 기록이 없습니다.");

                    }

                } else if(lastEntryType.equals(Constants.EntryType.OUTING.getValue())) {
                    // 최종 출입이 외출인 경우
                    if(!(entryType.equals(Constants.EntryType.RETURN) || entryType.equals(Constants.EntryType.OUT))) {
                        // 복귀 또는 퇴실이 아닌 경우
                        return new ApiResponse("복귀 또는 퇴실 기록이 없습니다.");

                    }

                } else if(lastEntryType.equals(Constants.EntryType.RETURN.getValue())) {
                    // 최종 출입이 복귀인 경우
                    if(!(entryType.equals(Constants.EntryType.OUTING) || entryType.equals(Constants.EntryType.OUT))) {
                        // 외출 또는 퇴실이 아닌 경우
                        return new ApiResponse("외출 또는 퇴실 기록이 없습니다.");

                    }

                }



            }


            if (StringUtils.isEmpty(reservation.getDeskId())) {
                // 자유석 등록인 경우

                // 출입 기록하지 않음 (좌석선택후 출입 기록함)

                // 자유석 목록 조회 (예약되지 않은 자유석 목록)
                List<Desk> deskList = branchDesignService.selectNotReservedDeskListByType(branchId, Constants.DeskType.FREE);
                response.setDeskList(deskList);


            } else {
                // 일반석 등록인 경우
                // TODO : 자유석 퇴실인 경우 예약정보 수정

                // 출입 기록
                //int result = branchMemberService.insertMemberEntry(branchId, memberId, entry.getEntryType(), pay);
                int result = branchMemberService.insertMemberEntry(branchId, memberId, entryType.getValue(), reservation);

                if (result == 0) {
                    return new ApiResponse("출입을 기록할 수 없습니다.");

                } else {
                    // 입실, 퇴실인 경우에만 SMS발송 / 휴식과 복귀는 제외
                    //
                	
                	Branch branch = branchService.selectBranch(branchId);
                	StandDataModel stand = new StandDataModel();
                	
                	if (entryType.equals(Constants.EntryType.OUTING) || entryType.equals(Constants.EntryType.RETURN) ) {
                		
                		if (entryType.equals(Constants.EntryType.OUTING)) {
                			stand.setMode("LAMP_OFF,LOCK_ON");
                			
                		}
                		else if (entryType.equals(Constants.EntryType.RETURN)) {
                			stand.setMode("LOCK_OFF");
                		}
                		
                		
                		if (branchId.equals("ef83445e-e6c9-4901-954b-35d145fcf73b")) {
                    		// SMS 발송
                        	if (member.getSmsYes() == 1) {
    	                        if (!StringUtils.isEmpty(member.getTelParent())) {
    	                            //Branch branch = branchService.selectBranch(branchId);
    		                           
    	                            String msg = String.format("[CNT%s] %s님이 %s에 %s하였습니다", branch.getName(), member.getName(), DateUtil.getCurrentTimeShortString(), entryType.getText());
    	
    	                            smsService.sendSms(branchId, memberId,
    	                                    branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
    	
    	                        }
                        	}
                			
                		}
                			
                	}
                		
                	if (entryType.equals(Constants.EntryType.IN) || entryType.equals(Constants.EntryType.OUT) ) {
                    	
                		//정상 퇴실일 경우 당일 학습시간 계산
                		if (entryType.equals(Constants.EntryType.OUT)) {
                			//Branch branch = branchService.selectBranch(branchId);
                			stand.setMode("LAMP_OFF,LOCK_ON");
                			           			
                			int todayStudyTime = 0;
                			
                			List<Entry> recentEntry = branchMemberService.selectRecentMemberEntryListByMemberId(branchId, memberId);                			
                			List<StatusListEntry> recentEntry2 = branchMemberService.selectMemberEntryListByBusinessDt(branchId, memberId, recentEntry.get(0).getBusinessDt().toString());
                			TotalLearning totalLearning = new TotalLearning();
                			int myDiff = 0;                			
                			ReportingTime.TimeTest myTime = new ReportingTime.TimeTest();
                			List<ReportingTime.TimeTest> myTimeList = new ArrayList<ReportingTime.TimeTest>();
                			
                			for (StatusListEntry sle : recentEntry2) {
            		        	////////total 시간 계산
            		        	int tempIndex = recentEntry2.indexOf(sle);
            		        			        	
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
            			        	if ( !sle.getBusinessDt().equals(recentEntry2.get(tempIndex-1).getBusinessDt()) || tempIndex == recentEntry2.size()-1 ) {		        	
            				        	
            			        		if (myDiff != 0) {
            			            		myTime.setBusinessDt((recentEntry2.get(tempIndex-1).getBusinessDt()));
            			            		myTime.setTime(myDiff);
            			            		myTime.setBranchId(branchId);
            			            		myTime.setMemberId(memberId);
            			            		
            			    				//받아온 정보로 매장명 및 회원명 찾기
            			    				List<BranchMember> codiMemberInfo = branchMemberService.selectMemberList(branchId, memberId, null); //코디에서 멤버찾기
            			    								
            			    				if (codiMemberInfo.size() > 0) { 
            			    					myTime.setBranchNm(codiMemberInfo.get(0).getBranchNm());
            									myTime.setMemberNm(codiMemberInfo.get(0).getName());
            									myTime.setNo(codiMemberInfo.get(0).getNo());
            			    				} else { //코디 정보가 없으면 App 에서 찾기
            			    					List<AppClientMember> appClientMemberInfo = appMemberService.selectAppBranchManagerForMemberId(memberId);
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
            			    				String timeTempStr = recentEntry2.get(tempIndex).getEntryTm().replaceAll(":", "");
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
            			    				
            			    				//branchMemberService.insertStatisticsEntry(myTime);
            			            		myTimeList.add(myTime);
            			            		response.setTodayStudyTime(myTime.getTime() / 60 + "시간" + myTime.getTime() % 60 + "분");
            			            		//System.out.println("==============================myTime==================" + myTime.getTime()); //당일 학습시간
            			            		todayStudyTime = myTime.getTime();
            			            		myDiff = 0;
            			            		myTime = new ReportingTime.TimeTest();
            			            		
            			            		
            			            		//totalLearning = new TotalLearning();

            				        	}
            			        	}
            		        	}
            		        	
                    	
            		        	
            		        }
                			
                		}
                		else if (entryType.equals(Constants.EntryType.IN)) {
                			//Branch branch = branchService.selectBranch(branchId);
                			//stand.setMode("LOCK_ON,LAMP_ON");
                			stand.setMode("LOCK_OFF");
                		}
                		
                		
                		if (branch.getStandYn() == 1) {
                    		Desk desk = branchDesignService.selectDesk(branchId, reservation.getDeskId());                        		
                    		Socket socket = null;
            		        BufferedReader in = null;
            		        PrintWriter out = null;
            		        try {
//            		            socket = new Socket("127.0.0.1", 9000);
            		            socket = new Socket(branch.getIp(), 9005);
            		            out = new PrintWriter(socket.getOutputStream(), true);
            		            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//								StandDataModel stand = new StandDataModel();
            		            stand.setAddress(desk.getName().replaceAll("[^0-9]", ""));
//            		            stand.setMode("RELOAD");
//            		            stand.setMode("LOCK_ON,LAMP_ON");
//								stand.setMode("LAMP_OFF,LOCK_OFF");
//            		            stand.setMode("DISCONNECTION");
            		            DataModelTest<StandDataModel> standModel = new DataModelTest<StandDataModel>();
            		            standModel.setMode("StandModel");
            		            standModel.setData(stand);
            		            out.println(new Gson().toJson(standModel));
            		            String standResponse = in.readLine();
            		            System.out.println(standResponse);
            		        }catch (Exception e) {
            		            e.printStackTrace();
            		        } finally {
            		            try {
            		                if(out !=null) out.close();
            		                if(in != null) in.close();
            		                if(socket != null) socket.close();
            		            } catch (Exception e) {
            		            }
            		        }
                    	}
                		
                		
                		// SMS 발송
                    	if (member.getSmsYes() == 1) {
	                        if (!StringUtils.isEmpty(member.getTelParent())) {
	                            //Branch branch = branchService.selectBranch(branchId);
		                           
	                            String msg = String.format("[CNT%s] %s님이 %s에 %s하였습니다", branch.getName(), member.getName(), DateUtil.getCurrentTimeShortString(), entryType.getText());
	
	                            smsService.sendSms(branchId, memberId,
	                                    branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
	
	                        }
                    	}

                    }

                }

            }

            
	        //추가 : 이번 달 누적시간(time), 센터 내 순위(rank), 성별(gender), 등록 남은기간(deskDt)
	    	if (member.getSchoolGrade() == 0 || member.getSchoolGrade() == -1) {
	    		response.setSchoolGrade("성인");
	    	} else {
	    		response.setSchoolGrade("학생");
	    	}
	    	
	        List<Rank.RankInfo> rankList = branchMemberService.selectStatisticsEntryRank2(branchId, memberId);
	        if ( rankList.size() > 0 ) {
	        	
	        	response.setRank(Integer.parseInt(rankList.get(0).getRank()));
	        	Integer quotient = rankList.get(0).getTime() / 60;
	        	Integer remainder = rankList.get(0).getTime() % 60;
	        	response.setTime(quotient + "시간" + remainder + "분");
	        	
	        } else {
	        	response.setRank(0);
	        	response.setTime("00시간00분");
	        }
	        
	        //등록 남은 기간
	        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
	        Date date1 = null;	        
	        Date date2 = null;
	        
			try {
				date2 = format.parse(reservation.getDeskEndDt().toString());
				
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	
	        try {
				date1 = format.parse(DateUtil.getCurrentDateTimeString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        
	        long diff = date2.getTime() - date1.getTime();
	        long diffDays = diff / (24 * 60 * 60 * 1000);		

	        int tempint = (int)diffDays;
	        response.setDeskDt(tempint);
	        
	        String curDate = DateUtil.getCurrentDateStringAppAdminParse(DateUtil.getCurrentDateString());
	        response.setStudyDt(curDate.substring(5, 7) + ".01" + " ~ " + curDate.substring(5, 10));	        
	        response.setEndDt(format.format(reservation.getDeskEndDt()));
	        
        } else {
            return new ApiResponse("등록되지 않은 회원입니다.");

        }

        return response;

    }


    // 자유석 회원


	@RequestMapping(value = "/b/{branchId}/d/{deskId}/e", method = RequestMethod.POST)
    public @ResponseBody
    ApiResponse postEntryWithDeskId(@PathVariable String branchId,
                                       @PathVariable String deskId,
                                       @RequestBody @Valid Entry.Create entry) {
        // 회원 확인
        BranchMember member =  null;

        try {
            member = branchMemberService.selectMemberByNo(branchId, entry.getNo());
            if (member == null) {
                return new ApiResponse("등록되지 않은 회원입니다.");

            }

        } catch (EmptyResultDataAccessException erdae) {
            return new ApiResponse("등록되지 않은 회원입니다.");

        }

        String memberId = member.getMemberId();

        // 예약 확인
        //Reservation reservation = branchReservationService.selectCurrentReservationByMemberId(branchId, memberId);
        Reservation reservation = null;
        try {
            reservation = branchReservationService.selectCurrentReservationByMemberId(branchId, memberId);
            if (reservation == null) {
                //throw new InternalServerError("Failed to find reservation");
                return new ApiResponse("등록되지 않은 회원입니다.");

            }

        } catch (EmptyResultDataAccessException erdae) {
            //throw new InternalServerError("Failed to find reservation");
        	 return new ApiResponse("등록되지 않은 회원입니다.");

        }

        Constants.EntryType entryType = Constants.getEntryType(entry.getEntryType());

        // 결과
        ModelMapper modelMapper = new ModelMapper();

        // 회원정보 (일부)
        Entry.Response response = modelMapper.map(member, Entry.Response.class);

        // 최근 출입기록
        List<Entry> entryList = branchMemberService.selectRecentMemberEntryListByMemberId(branchId, memberId);
        response.setEntryList(entryList);
        
        // 현재 예약정보
        response.setReservation(reservation);

        //추가 : 이번 달 누적시간(time), 센터 내 순위(rank), 성별(gender), 등록 남은기간(deskDt)
    	if (member.getSchoolGrade() == 0 || member.getSchoolGrade() == -1) {
    		response.setSchoolGrade("성인");
    	} else {
    		response.setSchoolGrade("학생");
    	}
    	
        List<Rank.RankInfo> rankList = branchMemberService.selectStatisticsEntryRank2(branchId, memberId);
        if ( rankList.size() > 0 ) {
        	response.setRank(Integer.parseInt(rankList.get(0).getRank()));
        	Integer quotient = rankList.get(0).getTime() / 60;
        	Integer remainder = rankList.get(0).getTime() % 60;
        	response.setTime(quotient + "시간" + remainder + "분");
        	
        } else {
        	response.setRank(0);
        	response.setTime("00시간00분");
        }
        
        //등록 남은 기간
        Date date1 = reservation.getDeskEndDt();
        Date date2 = null;
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );

        try {
			date2 = format.parse(DateUtil.getCurrentDateTimeString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        long diff = date2.getTime() - date1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);		

        int tempint = (int)diffDays;
        response.setDeskDt(tempint);
        
        // 자유석 목록 조회 (예약되지 않은 자유석 목록)
        List<Desk> deskList = branchDesignService.selectNotReservedDeskListByType(branchId, Constants.DeskType.FREE);

        for(Desk d: deskList) {
            if(d.getDeskId().equals(deskId)) {

                createReservationAndEntry(branchId, member, deskId, entryType, reservation);

                break;

            }

        }

        return response;

    }

    private void createReservationAndEntry(String branchId, BranchMember member, String deskId, Constants.EntryType entryType, Reservation reservation) {

        String memberId = member.getMemberId();

        // 등록 (하위예약 추가)
        String newReservationId = UUID.randomUUID().toString();         // 외부 아이디 생성

        Reservation newReservation = new ModelMapper().map(reservation, Reservation.class);
        newReservation.setDeskId(deskId);
        newReservation.setDeskStartDt(DateUtil.getCurrentDate());
        newReservation.setDeskStartTm(DateUtil.getCurrentTime());
        newReservation.setDeskEndDt(DateUtil.getNextDate()); // FIXME
        newReservation.setDeskEndTm(DateUtil.getEndTime()); // FIXME
        newReservation.setReservationNote("자유석 선택");
        newReservation.setReservationStatus(Constants.ReservationStatusType.CONFIRMED.getValue()); // 예약 상태 : 확정(20)

        branchReservationService.insertReservation(branchId, newReservationId, newReservation);


        // 출입 기록
        int result = branchMemberService.insertMemberEntry(branchId, memberId, entryType.getValue(), newReservation);

        if (result == 0) {
            throw new InternalServerError("Failed to create entry");

        } else {

            // 입실인 경우에만
            //if (entryType.equals(Constants.EntryType.IN) || entryType.equals(Constants.EntryType.OUT) || entryType.equals(Constants.EntryType.OUTING) || entryType.equals(Constants.EntryType.RETURN) ) {
        	if (entryType.equals(Constants.EntryType.IN) || entryType.equals(Constants.EntryType.OUT)) {
                // SMS 발송
            	if (member.getSmsYes() == 1) {
	            	if (!StringUtils.isEmpty(member.getTelParent())) {
	                    Branch branch = branchService.selectBranch(branchId);
	
	                    //String msg = String.format("[%s(%s)] %s님이 %s에 %s하였습니다", Constants.BRAND_SHORT, branch.getName(), member.getName(), DateUtil.getCurrentTimeShortString(), entryType.getText());
	                    String msg = String.format("[CNT %s] %s님이 %s에 %s하였습니다", branch.getName(), member.getName(), DateUtil.getCurrentTimeShortString(), entryType.getText());
	
	                    smsService.sendSms(branchId, memberId,
	                            branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
	
	                }
            	}

            }

        }

    }

   public ApiResponse timeMember(String branchId, BranchMember member, Entry.Create entry) {
	   
   
    	Constants.EntryType entryType = Constants.getEntryType(entry.getEntryType());

        // 결과
        ModelMapper modelMapper = new ModelMapper();

        // 회원정보 (일부)
        Entry.Response response = modelMapper.map(member, Entry.Response.class);

        // 최근 출입기록
        List<Entry> entryList = branchMemberService.selectRecentMemberEntryListByMemberId(branchId, member.getMemberId());
        response.setEntryList(entryList);

        // 현재 예약정보
        //response.setReservation(reservation);


            // 출입유형 확인 (최종 출입기록과 비교)
            Entry lastEntry = (entryList == null || entryList.size() == 0) ? null : entryList.get(0);
            //log.debug(lastEntry.getEntryDt().toString());
            // FIXME : 센터별 운영시간 고려
            // 우선 해당일 9시기준             
            if(lastEntry != null && LocalDate.now().atStartOfDay().plusHours(8).isAfter(LocalDateTime.ofInstant(lastEntry.getEntryDt().toInstant(), ZoneId.systemDefault()))) {
                // 해당일 8시이전이면 최종 출입기록이 없는 것으로 -> 센터가 새벽 2시까지 운영하기 때문에 2시까지 조건 추가
                if(LocalDate.now().atStartOfDay().plusHours(2).isAfter(LocalDateTime.ofInstant(lastEntry.getEntryDt().toInstant(), ZoneId.systemDefault()))){
                	
                }
                else {
                	lastEntry = null;
                }
            }
            
            if(lastEntry == null) {
                // 최종 출입기록이 없는 경우
                if(!entryType.equals(Constants.EntryType.IN)) {
                    // 입실이 아닌 경우
                    return new ApiResponse("입실 기록이 없습니다.");

                }

            } else {
                String lastEntryType = String.valueOf(lastEntry.getEntryType());
//                log.debug(lastEntryType);

                if(lastEntryType.equals(Constants.EntryType.IN.getValue())) {
                    // 최종 출입이 입실인 경우
                    if (entryType.equals(Constants.EntryType.IN) || entryType.equals(Constants.EntryType.RETURN)) {
                        // 입실이거나 복귀인 경우
                        return new ApiResponse("퇴실 또는 외출 기록이 없습니다.");

                    }

                } else if(lastEntryType.equals(Constants.EntryType.OUT.getValue())) {
                    // 최종 출입이 퇴실인 경우
                    if(!entryType.equals(Constants.EntryType.IN)) {
                        // 입실이 아닌 경우
                        return new ApiResponse("입실 기록이 없습니다.");

                    }

                } else if(lastEntryType.equals(Constants.EntryType.OUTING.getValue())) {
                    // 최종 출입이 외출인 경우
                    if(!(entryType.equals(Constants.EntryType.RETURN) || entryType.equals(Constants.EntryType.OUT))) {
                        // 복귀 또는 퇴실이 아닌 경우
                        return new ApiResponse("복귀 또는 퇴실 기록이 없습니다.");

                    }

                } else if(lastEntryType.equals(Constants.EntryType.RETURN.getValue())) {
                    // 최종 출입이 복귀인 경우
                    if(!(entryType.equals(Constants.EntryType.OUTING) || entryType.equals(Constants.EntryType.OUT))) {
                        // 외출 또는 퇴실이 아닌 경우
                        return new ApiResponse("외출 또는 퇴실 기록이 없습니다.");

                    }

                }



            }
            
            int result = branchMemberService.insertMemberEntry(branchId, member.getMemberId(), entryType.getValue(), null);
            // 최근 출입기록 insert 한 후 재검색
            entryList = branchMemberService.selectRecentMemberEntryListByMemberId(branchId, member.getMemberId());
            
            if (result == 0) {
                return new ApiResponse("출입을 기록할 수 없습니다.");

            } else {
            	Branch branch = branchService.selectBranch(branchId);
	            if (entryType.equals(Constants.EntryType.IN) ) {
	            		// SMS 발송
                        if (!StringUtils.isEmpty(member.getTel())) {
                            //Branch branch = branchService.selectBranch(branchId);
	                           
                            String msg = String.format("[CNT%s] %s님이 %s에 %s하였습니다. 남은 시간은 %s 입니다.", branch.getName(), member.getName(), DateUtil.getCurrentTimeShortString(), entryType.getText(), Integer.parseInt(member.getRemainTime()) / 60 + "시간 " + Integer.parseInt(member.getRemainTime()) % 60 + "분");

                            smsService.sendSms(branchId, member.getMemberId(),
                                    branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
                            
                            	                            	                            	                           
                            response.setStudyDt( Integer.parseInt(member.getRemainTime()) / 60 + "시간 " + Integer.parseInt(member.getRemainTime()) % 60 + "분");	        
                	        response.setEndDt(Integer.parseInt(member.getRemainTime()) / 60 + "시간 " + Integer.parseInt(member.getRemainTime()) % 60 + "분");
                            
                        }	                	
	            }
	            else if (entryType.equals(Constants.EntryType.OUT)) {
            		// SMS 발송
                    if (!StringUtils.isEmpty(member.getTel())) {
                        //Branch branch = branchService.selectBranch(branchId);
                    	Date outDate = entryList.get(0).getEntryDt();
                    	Date inDate = entryList.get(1).getEntryDt();
                    	//Date outDate = new Date();
                    	//Date inDate = new Date();
                    	/*SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmm");
                    	
                    	try {
							outDate = dateFormat.parse(dateFormat.format(entryList.get(0).getEntryDt())); //퇴실
							inDate = dateFormat.parse(dateFormat.format(entryList.get(1).getEntryDt())); //입실
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} //퇴실
                    	
                    	long outTime = outDate.getTime();
                    	long inTime = inDate.getTime();
                    	
                    	long minute = (outTime - inTime) / 60000;
                    	
                        Integer useTime = (int)(long)minute;
                        Integer remainTime = Integer.parseInt(member.getRemainTime()) - useTime;*/
                    	
                    	long gap = (outDate.getTime() - inDate.getTime()) / 1000 / 60; //minite 로 변환
                    	Integer remainTime = (int) (Integer.parseInt(member.getRemainTime()) - gap);
                    	
                        if (remainTime <= 0) {
                        	remainTime = 0;
                        	member.setTimeYn(0);
                        }
                        
                        member.setRemainTime(remainTime.toString());
                        branchMemberService.updateMember(branchId, member.getMemberId(), member);

                        String msg = String.format("[CNT%s] %s님이 %s에 %s하였습니다. 남은 시간은 %s 입니다.", branch.getName(), member.getName(), DateUtil.getCurrentTimeShortString(), entryType.getText(), Integer.parseInt(member.getRemainTime()) / 60 + "시간 " + Integer.parseInt(member.getRemainTime()) % 60 + "분");

                        smsService.sendSms(branchId, member.getMemberId(),
                                branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
                        
                        response.setStudyDt( Integer.parseInt(member.getRemainTime()) / 60 + "시간 " + Integer.parseInt(member.getRemainTime()) % 60 + "분");	        
            	        response.setEndDt(Integer.parseInt(member.getRemainTime()) / 60 + "시간 " + Integer.parseInt(member.getRemainTime()) % 60 + "분");
                                                
                                                                        
                        
                    }
	            }
	            
            }
            
            
            return response;
   }
    
}


