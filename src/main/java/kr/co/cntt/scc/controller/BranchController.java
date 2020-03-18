package kr.co.cntt.scc.controller;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.student.service.AppClientMemberService;
import kr.co.cntt.scc.model.*;
import kr.co.cntt.scc.service.*;
import kr.co.cntt.scc.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 메인 컨트롤러
 * Created by jslivane on 2016. 4. 4..
 */
@Controller
@Slf4j
@PreAuthorize("hasAuthority('admin') OR hasAuthority('user')")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private BranchDesignService branchDesignService;

    @Autowired
    private BranchMemberService branchMemberService;

    @Autowired
    private BranchOrderService branchOrderService;

    @Autowired
    private BranchReservationService branchReservationService;

    @Autowired
    private BranchPayService branchPayService;
    
    @Autowired
    private BranchExpenseService branchExpenseService;
    
    @Autowired
    private BranchRentalService branchRentalService;
    
    @Autowired
    private BranchNotificationService branchNotificationService;

    @Autowired
    private BranchPreReservationService branchPreReservationService;
    
    @Autowired
    private BranchFreeApplicationService branchFreeApplicationService; 
    
    @Autowired
    private BranchSeatReservationService branchSeatReservationService;
    
    @Autowired
    private BranchSafeServiceService branchSafeServiceService;
    
    @Autowired
    private AppClientMemberService appClientMemberService;
    
    @Autowired
    private BranchCommonCodeService branchCommonCodeService;

    private static final String VIEW_NAME_OF_LAYOUT = "layout";

    //@Override
    public void run() {
    	try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private ModelAndView modelView(ModelMap model, String title, String view) {
    	model.addAttribute("brand", Constants.BRAND_FULL);
        model.addAttribute("title", title);
        model.addAttribute("view", view);

        // 지점 목록 조회
        List<Branch> branches = branchService.selectBranchList();
        model.addAttribute("branches", branches);
        

        return new ModelAndView(VIEW_NAME_OF_LAYOUT);
    }

    @RequestMapping("/branch")
    public ModelAndView branch(ModelMap model) {

        return modelView(model, "지점", "branch");

    }

    @RequestMapping("/admin")
    public ModelAndView main(ModelMap model) throws JsonProcessingException {
        // 지점 목록 조회
        List<Branch> branches = branchService.selectBranchList();
        DecimalFormat Commas = new DecimalFormat("#,###");
        
        String pattern = "#####.##";
        DecimalFormat dformat = new DecimalFormat( pattern );
        
        
		for (Branch branch : branches ) {
			
			List<Reservation> reservationList = branchReservationService.selectReservationCountList(branch.getBranchId(), "today");
			Integer deskList = branchDesignService.selectDeskCountList(branch.getBranchId());
			
			//현재 등록 수
			if(reservationList.size() > 0) {
				branch.setCurDesk(reservationList.size());
				branches = branchService.selectRoomType(branch.getBranchId());
				
				if (branches.size() > 0) {
					branch.setMulti_sum(branches.get(0).getMulti_sum());
					branch.setSingle_sum(branches.get(0).getSingle_sum());
					branch.setPrivate_sum(branches.get(0).getPrivate_sum());
				}
				else {
					branch.setMulti_sum(0);
					branch.setSingle_sum(0);
					branch.setPrivate_sum(0);
				}

			}
			//전체 좌석 수
			if(deskList != null && deskList > 0) {
				branch.setTotalDesk(deskList);
			} else {
				branch.setTotalDesk(0);
			}
			
			
				
			
			//입실율
			if (branch.getCurDesk() > 0) {
				double enterRatio = (double) branch.getCurDesk() / (double)branch.getTotalDesk() * 100;

				branch.setEnterRatio(Math.round(enterRatio * 100) / 100.0);
				
			}
			else {
				branch.setEnterRatio(0.0);
			}
			//전일변동 
			List<Reservation> reservationTwodaysagoList = branchReservationService.selectReservationCountList(branch.getBranchId(), "twodaysago");
			List<Reservation> reservationYesterdayList = branchReservationService.selectReservationCountList(branch.getBranchId(), "yesterday");					
												
			if (reservationTwodaysagoList.size() > 0 ) {
				double dayRatio =  ( (double)reservationYesterdayList.size() - (double)reservationTwodaysagoList.size() ) / (double)reservationTwodaysagoList.size() * 100;				
				branch.setDayRatio((Math.round(dayRatio * 100) / 100.0));
				
			}
			else {
				branch.setDayRatio(0.0);
			}
			
			String monthPay = branchPayService.selectMonthPayAmount(branch.getBranchId());
			if (monthPay != null){
				Integer payAmount = Integer.parseInt(monthPay);
				if (payAmount > 0) {
					branch.setPay((String)Commas.format(payAmount));
				}
				else {
					branch.setPay("0");
				}
			}
			else {
				branch.setPay("0");
			}

			
			if (branch.getBranchType() == Constants.BranchType.CAFE.getValue() ) {
				String yesterdayPay = branchPayService.selectYesterdayPayAmount(branch.getBranchId());
				if (yesterdayPay != null){
					Integer yesterdayPayAmount = Integer.parseInt(yesterdayPay);
					if (yesterdayPayAmount > 0) {
						branch.setYesterdayPay((String)Commas.format(yesterdayPayAmount));
					}
					else {
						branch.setYesterdayPay("0");
					}
				}
				else {
					branch.setYesterdayPay("0");
				}
				
			}
			//오픈일
			if (branch.getOpenDt() != null) {
				DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
				String openDt_S = sdFormat.format(branch.getOpenDt());
				
				branch.setOpenDt_S(openDt_S);
			}
			
			List<Integer> YesterDayReservationCounts = branchReservationService.selectYesterDayReservationCount(branch.getBranchId());
			if (YesterDayReservationCounts != null) {
				Integer count_monthReservation = 0;
				Integer count_dayReservation = 0;
				for (Integer YesterDayReservationCount : YesterDayReservationCounts ) {
					//30보다 크면 월 등록
					if (YesterDayReservationCount >= 29) {
						count_monthReservation++;						
					}
					else {
						count_dayReservation++;
					}
					branch.setMonthReservation(count_monthReservation);
					branch.setDayReservation(count_dayReservation);
				}
			}
			
			//2일전 등록개수
			List<Integer> TwodaysAgoStatisticsReservationCounts = branchReservationService.selectStatisticsReservationCount(branch.getBranchId(), "twodaysago");
			if (TwodaysAgoStatisticsReservationCounts != null) {
				Integer count_TwodaysAgoMonthReservation = 0;
				Integer count_TwodaysAgoDayReservation = 0;
				for (Integer TwodaysAgoStatisticsReservationCount : TwodaysAgoStatisticsReservationCounts ) {
					//30보다 크면 월 등록
					if (TwodaysAgoStatisticsReservationCount >= 29) {
						count_TwodaysAgoMonthReservation++;						
					}
					else {
						count_TwodaysAgoDayReservation++;
					}
							
					branch.setTwodaysAgoMonthReservation(count_TwodaysAgoMonthReservation);
					branch.setTwodaysAgoDayReservation(count_TwodaysAgoDayReservation);
				}
				
				//입실율
				if (branch.getTotalDesk() > 0) {
					double towdaysAgoMonthReservationRatio = (double) count_TwodaysAgoMonthReservation / (double)branch.getTotalDesk() * 100;
					branch.setTwodaysAgoMonthReservationRatio(Math.round(towdaysAgoMonthReservationRatio * 100) / 100.0);					
					
					double towdaysAgoDayReservationRatio = (double) count_TwodaysAgoDayReservation / (double)branch.getTotalDesk() * 100;
					branch.setTwodaysAgoDayReservationRatio(Math.round(towdaysAgoDayReservationRatio * 100) / 100.0); 
					
				}
				else {
					branch.setTwodaysAgoMonthReservationRatio(0.0);
					branch.setTwodaysAgoDayReservationRatio(0.0);
				}
				
			}
			
			//1일전 등록개수
			List<Integer> YesterdayStatisticsReservationCounts = branchReservationService.selectStatisticsReservationCount(branch.getBranchId(), "yesterday");
			if (YesterdayStatisticsReservationCounts != null) {
				Integer count_YesterdayMonthReservation = 0;
				Integer count_YesterdayDayReservation = 0;
				for (Integer YesterdayStatisticsReservationCount : YesterdayStatisticsReservationCounts ) {
					//30보다 크면 월 등록
					if (YesterdayStatisticsReservationCount >= 29) {
						count_YesterdayMonthReservation++;						
					}
					else {
						count_YesterdayDayReservation++;
					}

					branch.setYesterdayMonthReservation(count_YesterdayMonthReservation);
					branch.setYesterdayDayReservation(count_YesterdayDayReservation);
				}
			
				//입실율
				if (branch.getTotalDesk() > 0) {
					double yesterdayMonthReservationRatio = (double) count_YesterdayMonthReservation / (double)branch.getTotalDesk() * 100;
					branch.setYesterdayMonthReservationRatio(Math.round(yesterdayMonthReservationRatio * 100) / 100.0);

					double yesterdayDayReservationRatio = (double) count_YesterdayDayReservation / (double)branch.getTotalDesk() * 100;
					branch.setYesterdayDayReservationRatio(Math.round(yesterdayDayReservationRatio * 100) / 100.0);
				}
				else {
					branch.setYesterdayMonthReservationRatio(0.0);
					branch.setYesterdayDayReservationRatio(0.0);
				}
			
			}
			
			
			//당일 등록개수
			List<Integer> TodayStatisticsReservationCounts = branchReservationService.selectStatisticsReservationCount(branch.getBranchId(), "today");
			if (TodayStatisticsReservationCounts != null) {
				Integer count_todayMonthReservation = 0;
				Integer count_todayDayReservation = 0;
				for (Integer TodayStatisticsReservationCount : TodayStatisticsReservationCounts ) {
					//30보다 크면 월 등록
					if (TodayStatisticsReservationCount >= 29) {
						count_todayMonthReservation++;						
					}
					else {
						count_todayDayReservation++;
					}

					branch.setTodayMonthReservation(count_todayMonthReservation);
					branch.setTodayDayReservation(count_todayDayReservation);

				}
				
				//입실율
				if (branch.getTotalDesk() > 0) {
					double todayMonthReservationRatio = (double) count_todayMonthReservation / (double)branch.getTotalDesk() * 100;
					branch.setTodayMonthReservationRatio(Math.round(todayMonthReservationRatio * 100) / 100.0);
					
					double todayDayReservationRatio = (double) count_todayDayReservation / (double)branch.getTotalDesk() * 100;
					branch.setTodayDayReservationRatio(Math.round(todayDayReservationRatio * 100) / 100.0);
				}
				else {
					branch.setTodayMonthReservationRatio(0.0);
					branch.setTodayDayReservationRatio(0.0);
				}
			}
			//연장데이터
			List<Reservation> reservationExtensionLists = branchReservationService.selectReservationExtensionList(branch.getBranchId());
			
			Integer yesterdayMonthReservationExtension = 0;						
			
			if (reservationExtensionLists != null) {
				for (Reservation reservationExtensionList : reservationExtensionLists ) {
					
					int reservationExtensionCount = branchReservationService.selectReservationExtensionCount(branch.getBranchId(), reservationExtensionList.getReservationId());
					//pay에 같은 reservation으로 데이터가 있는지 체크 -> 있으면 중복이므로 연장데이터 아님
					if (reservationExtensionCount > 0) {
						int reservationExtensionCount2 = branchReservationService.selectReservationExtensionCount2(branch.getBranchId(), reservationExtensionList.getOrderId());
						
						//pay에 다른 reservation 데이터가 있으면 연장
						if (reservationExtensionCount2 > 0) {
							int reservationExtensionDate = branchReservationService.selectReservationExtensionDate(branch.getBranchId(), reservationExtensionList.getReservationId());
							if (reservationExtensionDate > 28) { //데이터가 있는것들은 월, 일인지 구분할수 있도록 날짜계산
								//월연장
								yesterdayMonthReservationExtension++;							
							} 
							else {
								//일연장
							}
						}

					}
	
					
				}
			}
			
			branch.setYesterdayMonthReservationExtension(yesterdayMonthReservationExtension);

		
			
			//신규데이터
			List<Reservation> reservationNewLists = branchReservationService.selectReservationNewList(branch.getBranchId());
			
			Integer yesterdayMonthReservationExtensionNew = 0;
			Integer yesterdayDayReservationExtensionNew = 0;
			
			if (reservationNewLists != null) {
				for (Reservation reservationNewList : reservationNewLists ) {
					//int reservationNewDate = branchReservationService.selectReservationExtensionDate(branch.getBranchId(), reservationNewList.getReservationId());
					int reservationNewCount = branchReservationService.selectReservationExtensionCount(branch.getBranchId(), reservationNewList.getReservationId());
					if (reservationNewCount > 0) { //pay에 같은 reservation으로 데이터가 있는지 체크(0보다 큰 것이 reservation 데이터 한개)
						//신규
						int reservationNewDate = branchReservationService.selectReservationExtensionDate(branch.getBranchId(), reservationNewList.getReservationId());
						if ( reservationNewDate > 28 ) {
							//월신규
							yesterdayMonthReservationExtensionNew++;
						}
						else {
							//일신규
							yesterdayDayReservationExtensionNew++;
						}
					}
					else {
						
					}
				}
			}
			branch.setYesterdayMonthReservationExtensionNew(yesterdayMonthReservationExtensionNew);
			branch.setYesterdayDayReservationExtensionNew(yesterdayDayReservationExtensionNew);
			
			//종료 데이터
			Integer yesterdayMonthReservationEnd = 0;
			List<Integer> reservationMonthEnds = branchReservationService.selectReservationMonthEnd(branch.getBranchId());
			if (reservationMonthEnds != null ) {
				yesterdayMonthReservationEnd = reservationMonthEnds.size();
			}
			branch.setYesterdayMonthReservationEnd(yesterdayMonthReservationEnd);
		
			int man_cnt_01 = 0;
			int woman_cnt_01 = 0;
			int stu_cnt_01 = 0;
			int adult_cnt_01 = 0;
			
			int man_cnt_02 = 0;
			int woman_cnt_02 = 0;
			int stu_cnt_02 = 0;
			int adult_cnt_02 = 0;
			
			int man_cnt_03 = 0;
			int woman_cnt_03 = 0;
			int stu_cnt_03 = 0;
			int adult_cnt_03 = 0;
			
			int man_cnt_04 = 0;
			int woman_cnt_04 = 0;
			int stu_cnt_04 = 0;
			int adult_cnt_04 = 0;
			
			int man_cnt_05 = 0;
			int woman_cnt_05 = 0;
			int stu_cnt_05 = 0;
			int adult_cnt_05 = 0;
			
			int man_cnt_06 = 0;
			int woman_cnt_06 = 0;
			int stu_cnt_06 = 0;
			int adult_cnt_06 = 0;
			
			int man_cnt_07 = 0;
			int woman_cnt_07 = 0;
			int stu_cnt_07 = 0;
			int adult_cnt_07 = 0;
			
			int man_cnt_08 = 0;
			int woman_cnt_08 = 0;
			int stu_cnt_08 = 0;
			int adult_cnt_08 = 0;
			
			int man_cnt_09 = 0;
			int woman_cnt_09 = 0;
			int stu_cnt_09 = 0;
			int adult_cnt_09 = 0;
			
			int man_cnt_10 = 0;
			int woman_cnt_10 = 0;
			int stu_cnt_10 = 0;
			int adult_cnt_10 = 0;
			
			int man_cnt_11 = 0;
			int woman_cnt_11 = 0;
			int stu_cnt_11 = 0;
			int adult_cnt_11 = 0;
			
			int man_cnt_12 = 0;
			int woman_cnt_12 = 0;
			int stu_cnt_12 = 0;
			int adult_cnt_12 = 0;
			
			// 남/여, 성인/학생 통계
			String currentDate = DateUtil.getCurrentDateString();
		    model.addAttribute("sDate", currentDate.substring(0, 4));
			List<Branch> reservationStatistics = branchReservationService.selectReservationStatisticsList(branch.getBranchId(), currentDate.substring(0, 4));
			
			if (reservationStatistics.size() > 0) {
				
				
//				int mid1_cnt = 0;
//				int mid2_cnt = 0;
//				int mid3_cnt = 0;
//				int high1_cnt = 0;
//				int high2_cnt = 0;
//				int high3_cnt = 0;
//				int orgAdult_cnt = 0;
//				int etc_cnt = 0;
				
				for (Branch b : reservationStatistics) {
					if (b.getM().equals("01")) {
						man_cnt_01 += b.getMan_cnt();	
						woman_cnt_01 += b.getWoman_cnt();
						stu_cnt_01 += b.getStu_cnt();
						adult_cnt_01 += b.getAdult_cnt();
					} else if (b.getM().equals("02")) {
						man_cnt_02 += b.getMan_cnt();	
						woman_cnt_02 += b.getWoman_cnt();
						stu_cnt_02 += b.getStu_cnt();
						adult_cnt_02 += b.getAdult_cnt();
					} else if (b.getM().equals("03")) {
						man_cnt_03 += b.getMan_cnt();	
						woman_cnt_03 += b.getWoman_cnt();
						stu_cnt_03 += b.getStu_cnt();
						adult_cnt_03 += b.getAdult_cnt();
					} else if (b.getM().equals("04")) {
						man_cnt_04 += b.getMan_cnt();	
						woman_cnt_04 += b.getWoman_cnt();
						stu_cnt_04 += b.getStu_cnt();
						adult_cnt_04 += b.getAdult_cnt();
					} else if (b.getM().equals("05")) {
						man_cnt_05 += b.getMan_cnt();	
						woman_cnt_05 += b.getWoman_cnt();
						stu_cnt_05 += b.getStu_cnt();
						adult_cnt_05 += b.getAdult_cnt();
					} else if (b.getM().equals("06")) {
						man_cnt_06 += b.getMan_cnt();	
						woman_cnt_06 += b.getWoman_cnt();
						stu_cnt_06 += b.getStu_cnt();
						adult_cnt_06 += b.getAdult_cnt();
					} else if (b.getM().equals("07")) {
						man_cnt_07 += b.getMan_cnt();	
						woman_cnt_07 += b.getWoman_cnt();
						stu_cnt_07 += b.getStu_cnt();
						adult_cnt_07 += b.getAdult_cnt();
					} else if (b.getM().equals("08")) {
						man_cnt_08 += b.getMan_cnt();	
						woman_cnt_08 += b.getWoman_cnt();
						stu_cnt_08 += b.getStu_cnt();
						adult_cnt_08 += b.getAdult_cnt();
					} else if (b.getM().equals("09")) {
						man_cnt_09 += b.getMan_cnt();	
						woman_cnt_09 += b.getWoman_cnt();
						stu_cnt_09 += b.getStu_cnt();
						adult_cnt_09 += b.getAdult_cnt();
					} else if (b.getM().equals("10")) {
						man_cnt_10 += b.getMan_cnt();	
						woman_cnt_10 += b.getWoman_cnt();
						stu_cnt_10 += b.getStu_cnt();
						adult_cnt_10 += b.getAdult_cnt();
					} else if (b.getM().equals("11")) {
						man_cnt_11 += b.getMan_cnt();	
						woman_cnt_11 += b.getWoman_cnt();
						stu_cnt_11 += b.getStu_cnt();
						adult_cnt_11 += b.getAdult_cnt();
					} else if (b.getM().equals("12")) {
						man_cnt_12 += b.getMan_cnt();	
						woman_cnt_12 += b.getWoman_cnt();
						stu_cnt_12 += b.getStu_cnt();
						adult_cnt_12 += b.getAdult_cnt();
					}
					

					
					
//					mid1_cnt += b.getMid1_cnt();
//					mid2_cnt += b.getMid2_cnt();
//					mid3_cnt += b.getMid3_cnt();
//					high1_cnt += b.getHigh1_cnt();
//					high2_cnt += b.getHigh2_cnt();
//					high3_cnt += b.getHigh3_cnt();
//					orgAdult_cnt += b.getOrgAdult_cnt();
//					etc_cnt += b.getEtc_cnt();
				}

				double manRatio = 0;
				double womanRatio = 0;
				double stuRatio = 0;
				double adultRatio = 0;
				
//				if ( ((double)man_cnt + (double)woman_cnt) == (double)0 ){
//					
//				} else {
//					manRatio = (double)man_cnt / ((double)man_cnt + (double)woman_cnt) * 100;
//					womanRatio = (double)woman_cnt / ((double)man_cnt + (double)woman_cnt) * 100;
//				} 
//				
//				if ( ((double)stu_cnt + (double)adult_cnt) == (double)0 ) {
//						
//				} else {
//					stuRatio = (double)stu_cnt / ((double)stu_cnt + (double)adult_cnt) * 100;
//					adultRatio = (double)adult_cnt / ((double)stu_cnt + (double)adult_cnt) * 100;
//				}
				
				branch.setMan_cnt_01(man_cnt_01);
				branch.setWoman_cnt_01(woman_cnt_01);
				branch.setStu_cnt_01(stu_cnt_01);
				branch.setAdult_cnt_01(adult_cnt_01);
				
				branch.setMan_cnt_02(man_cnt_02);
				branch.setWoman_cnt_02(woman_cnt_02);
				branch.setStu_cnt_02(stu_cnt_02);
				branch.setAdult_cnt_02(adult_cnt_02);
				
				branch.setMan_cnt_03(man_cnt_03);
				branch.setWoman_cnt_03(woman_cnt_03);
				branch.setStu_cnt_03(stu_cnt_03);
				branch.setAdult_cnt_03(adult_cnt_03);
				
				branch.setMan_cnt_04(man_cnt_04);
				branch.setWoman_cnt_04(woman_cnt_04);
				branch.setStu_cnt_04(stu_cnt_04);
				branch.setAdult_cnt_04(adult_cnt_04);
				
				branch.setMan_cnt_05(man_cnt_05);
				branch.setWoman_cnt_05(woman_cnt_05);
				branch.setStu_cnt_05(stu_cnt_05);
				branch.setAdult_cnt_05(adult_cnt_05);
				
				branch.setMan_cnt_06(man_cnt_06);
				branch.setWoman_cnt_06(woman_cnt_06);
				branch.setStu_cnt_06(stu_cnt_06);
				branch.setAdult_cnt_06(adult_cnt_06);
				
				branch.setMan_cnt_07(man_cnt_07);
				branch.setWoman_cnt_07(woman_cnt_07);
				branch.setStu_cnt_07(stu_cnt_07);
				branch.setAdult_cnt_07(adult_cnt_07);
				
				branch.setMan_cnt_08(man_cnt_08);
				branch.setWoman_cnt_08(woman_cnt_08);
				branch.setStu_cnt_08(stu_cnt_08);
				branch.setAdult_cnt_08(adult_cnt_08);
				
				branch.setMan_cnt_09(man_cnt_09);
				branch.setWoman_cnt_09(woman_cnt_09);
				branch.setStu_cnt_09(stu_cnt_09);
				branch.setAdult_cnt_09(adult_cnt_09);
				
				branch.setMan_cnt_10(man_cnt_10);
				branch.setWoman_cnt_10(woman_cnt_10);
				branch.setStu_cnt_10(stu_cnt_10);
				branch.setAdult_cnt_10(adult_cnt_10);
				
				branch.setMan_cnt_11(man_cnt_11);
				branch.setWoman_cnt_11(woman_cnt_11);
				branch.setStu_cnt_11(stu_cnt_11);
				branch.setAdult_cnt_11(adult_cnt_11);
				
				branch.setMan_cnt_12(man_cnt_12);
				branch.setWoman_cnt_12(woman_cnt_12);
				branch.setStu_cnt_12(stu_cnt_12);
				branch.setAdult_cnt_12(adult_cnt_12);
				
//				branch.setMid1_cnt(mid1_cnt);
//				branch.setMid2_cnt(mid2_cnt);
//				branch.setMid3_cnt(mid3_cnt);
//				branch.setHigh1_cnt(high1_cnt);
//				branch.setHigh2_cnt(high2_cnt);
//				branch.setHigh3_cnt(high3_cnt);
//				branch.setOrgAdult_cnt(orgAdult_cnt);
//				branch.setEtc_cnt(etc_cnt);
				
//				branch.setManRatio(Math.round(manRatio * 100) / 100.0 );
//				branch.setWomanRatio(Math.round(womanRatio * 100) / 100.0);
//				branch.setStuRatio(Math.round(stuRatio * 100) / 100.0);
//				branch.setAdultRatio(Math.round(adultRatio * 100) / 100.0);
				
			} else {
				branch.setMan_cnt_01(man_cnt_01);
				branch.setWoman_cnt_01(woman_cnt_01);
				branch.setStu_cnt_01(stu_cnt_01);
				branch.setAdult_cnt_01(adult_cnt_01);
				
				branch.setMan_cnt_02(man_cnt_02);
				branch.setWoman_cnt_02(woman_cnt_02);
				branch.setStu_cnt_02(stu_cnt_02);
				branch.setAdult_cnt_02(adult_cnt_02);
				
				branch.setMan_cnt_03(man_cnt_03);
				branch.setWoman_cnt_03(woman_cnt_03);
				branch.setStu_cnt_03(stu_cnt_03);
				branch.setAdult_cnt_03(adult_cnt_03);
				
				branch.setMan_cnt_04(man_cnt_04);
				branch.setWoman_cnt_04(woman_cnt_04);
				branch.setStu_cnt_04(stu_cnt_04);
				branch.setAdult_cnt_04(adult_cnt_04);
				
				branch.setMan_cnt_05(man_cnt_05);
				branch.setWoman_cnt_05(woman_cnt_05);
				branch.setStu_cnt_05(stu_cnt_05);
				branch.setAdult_cnt_05(adult_cnt_05);
				
				branch.setMan_cnt_06(man_cnt_06);
				branch.setWoman_cnt_06(woman_cnt_06);
				branch.setStu_cnt_06(stu_cnt_06);
				branch.setAdult_cnt_06(adult_cnt_06);
				
				branch.setMan_cnt_07(man_cnt_07);
				branch.setWoman_cnt_07(woman_cnt_07);
				branch.setStu_cnt_07(stu_cnt_07);
				branch.setAdult_cnt_07(adult_cnt_07);
				
				branch.setMan_cnt_08(man_cnt_08);
				branch.setWoman_cnt_08(woman_cnt_08);
				branch.setStu_cnt_08(stu_cnt_08);
				branch.setAdult_cnt_08(adult_cnt_08);
				
				branch.setMan_cnt_09(man_cnt_09);
				branch.setWoman_cnt_09(woman_cnt_09);
				branch.setStu_cnt_09(stu_cnt_09);
				branch.setAdult_cnt_09(adult_cnt_09);
				
				branch.setMan_cnt_10(man_cnt_10);
				branch.setWoman_cnt_10(woman_cnt_10);
				branch.setStu_cnt_10(stu_cnt_10);
				branch.setAdult_cnt_10(adult_cnt_10);
				
				branch.setMan_cnt_11(man_cnt_11);
				branch.setWoman_cnt_11(woman_cnt_11);
				branch.setStu_cnt_11(stu_cnt_11);
				branch.setAdult_cnt_11(adult_cnt_11);
				
				branch.setMan_cnt_12(man_cnt_12);
				branch.setWoman_cnt_12(woman_cnt_12);
				branch.setStu_cnt_12(stu_cnt_12);
				branch.setAdult_cnt_12(adult_cnt_12);
				
			}
		
		}
		
		
		
		model.addAttribute("branches", branches);		        
		

		
				
		return modelView(model, "메인", "admin_main");

    }

    
	@RequestMapping("/branch/{branchId}")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchOp(ModelMap model, @PathVariable String branchId, Page page) {

    	//PageMaker pageMaker = new PageMaker();
        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        try {
            ObjectMapper om = new ObjectMapper();

            
            // 지점의 열람실 목록 조회
            List<Room> roomList = branchDesignService.selectRoomList(branchId);
            model.addAttribute("roomListJSON", om.writeValueAsString(roomList));
            model.addAttribute("rooms", roomList);
            
            // 지점의 좌석 목록 조회
            List<Desk> deskList = branchDesignService.selectDeskList(branchId);
            model.addAttribute("deskListJSON", om.writeValueAsString(deskList));
            model.addAttribute("desks", deskList);
            
            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);
            model.addAttribute("memberListJSON", om.writeValueAsString(memberList));
            model.addAttribute("members", memberList);

            // 지점의 예약 목록 조회
            List<Reservation> reservationList = branchReservationService.selectReservationList(branchId,
                    DateUtil.getCurrentDateString(), null, null, null, null, null,
                    Constants.ReservationStatusType.CONFIRMED, null);
            
//            List<Reservation> reservationListAll = branchReservationService.selectReservationList(branchId,
//                    null, null, null, null, null, null,
//                    Constants.ReservationStatusType.CONFIRMED, "1");
            
            List<Reservation> reservationListAll = branchReservationService.selectReservationAllList(branchId, Constants.ReservationStatusType.CONFIRMED, "1" );

            model.addAttribute("reservationListJSON", om.writeValueAsString(reservationList));
            model.addAttribute("reservationListAllJSON", om.writeValueAsString(reservationListAll));
            // 지점의 총 예약 목록(자유석) 조회 (Total count)            
//            page.setPage(5);
//            page.setPerPageNum(5);
//            pageMaker.setPage(page);        	
//        	pageMaker.setTotalCount(branchReservationService.selectReservationCount(branchId, DateUtil.getCurrentDateString(), DateUtil.getCurrentTimeString()));
//        	model.addAttribute("pageMaker", pageMaker);
        
            // 지점의 결제 목록 조회
            //List<Pay> payList = branchPayService.selectPayList(branchId, null, null, null);
            //model.addAttribute("payListJSON", om.writeValueAsString(payList));

            // 지점의 출입 목록 조회
            List<Entry> entryList = branchMemberService.selectMemberEntryList(branchId, null, null, null, null, null, null);
            model.addAttribute("entryListJSON", om.writeValueAsString(entryList));
            
            // 결제구분
            model.addAttribute("payTypeMapJSON", om.writeValueAsString(Constants.PayType.getMap()));
            model.addAttribute("payTypes", Constants.PayType.getList());
            
            
            List<Pay> payList = branchPayService.selectPayList(branchId, null, null, null, null, null);
            model.addAttribute("payListJSON", om.writeValueAsString(payList));
            // reservationStatusType
            model.addAttribute("reservationStatusTypeMapJSON", om.writeValueAsString(Constants.ReservationStatusType.getMap()));            
            // 시험구분
            model.addAttribute("examTypeMapJSON", om.writeValueAsString(Constants.ExamType.getMap()));
            model.addAttribute("examTypes", Constants.ExamType.getList());
            
            // 직무분야
            model.addAttribute("jobTypeMapJSON", om.writeValueAsString(Constants.JobTypes.getMap()));
            model.addAttribute("jobTypes", Constants.JobTypes.getList());
            
            // 흥미분야
            model.addAttribute("interestTypeMapJSON", om.writeValueAsString(Constants.InterestTypes.getMap()));
            model.addAttribute("interestTypes", Constants.InterestTypes.getList());
          
            //학년 코드
            List<CommonCode> schoolGradeList = branchCommonCodeService.selectCommonCodeList(branchId, "schoolGrade"); 
            model.addAttribute("schoolGrades", schoolGradeList);


        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }


        return modelView(model, branch.getName(), "branch_op");

    }

    @RequestMapping(value = "/branch/{branchId}/design")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchDesign(ModelMap model, @PathVariable String branchId) {

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        // 열람실 유형
        model.addAttribute("roomTypes", Constants.RoomType.getList());
        
        // 좌석 유형
        //model.addAttribute("deskTypeMapJSON", om.writeValueAsString(Constants.PayType.getMap()));
        model.addAttribute("deskTypes", Constants.DeskType.getList());

        
        return modelView(model, branch.getName() + " 열람실/좌석 관리", "branch_design");

    }

    /*
    @RequestMapping(value = "/branch/{branchId}/design/load", method = RequestMethod.GET)
    public @ResponseBody BranchDesign branchDesignLoad(ModelMap model, @PathVariable String branchId) {
        BranchDesign branchDesign = branchDesignService.findByBranchId(branchId);

        return branchDesign;

    }

    @RequestMapping(value = "/branch/{branchId}/design/save", method = RequestMethod.POST)
    public @ResponseBody BranchDesign branchDesignSave(ModelMap model, @PathVariable String branchId, @RequestBody BranchDesign branchDesign) {

        branchDesignService.updateByBranchId(branchId, branchDesign);

        return branchDesignService.findByBranchId(branchId);

    }
    */

    @RequestMapping("/branch/{branchId}/members")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchMembers(ModelMap model, @PathVariable String branchId,
                                      @RequestParam(required = false) String sMember,
                                      Page page) {

    	PageMaker pageMaker = new PageMaker();
        // 파라미터 (검색)
        model.addAttribute("sMember", sMember);
        System.out.println("======================================sMember=============="+sMember);

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        try {
            ObjectMapper om = new ObjectMapper();

            // 지점의 열람실 목록 조회
            List<Room> roomList = branchDesignService.selectRoomList(branchId);
            model.addAttribute("roomListJSON", om.writeValueAsString(roomList));

            // 지점의 좌석 목록 조회
            List<Desk> deskList = branchDesignService.selectDeskList(branchId);
            model.addAttribute("deskListJSON", om.writeValueAsString(deskList));

            // 멤버쉽 번호 목록 조회
            List<MembershipCard> membershipCardList = branchMemberService.selectMembershipNoList();
            model.addAttribute("membershipNoListJSON", om.writeValueAsString(membershipCardList));
                        
            // 회원 조회 결과
			List<BranchMember> memberList = branchMemberService.selectMemberList(branchId, sMember, page);							    			
			
			
			model.addAttribute("memberListJSON", om.writeValueAsString(memberList));
			model.addAttribute("members", branchMemberService.selectMemberList(branchId));
            
			// 지점의 회원 목록 조회 (Total Count)        	
        	List<BranchMember> memberTotalList = branchMemberService.selectMemberList(branchId, sMember, null);
        	
        	pageMaker.setPage(page);
        	pageMaker.setTotalCount(memberTotalList.size());
        	model.addAttribute("pageMaker", pageMaker);
            
        	// 지점의 예약 목록 조회
            List<Reservation> reservationList = branchReservationService.selectReservationList(branchId,
                    DateUtil.getCurrentDateString(), DateUtil.getCurrentDateString(), null, null, null, page,
                    Constants.ReservationStatusType.CONFIRMED, null);
            model.addAttribute("reservationListJSON", om.writeValueAsString(reservationList));

            // 지점의 결제 목록 조회
//            List<Pay> payList = branchPayService.selectPayList(branchId, null, null, null);
//            model.addAttribute("payListJSON", om.writeValueAsString(payList));

            // 지점의 출입 목록 조회//////////////////&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            List<Entry> entryList = branchMemberService.selectMemberEntryList(branchId, null, null, null, null, null, null);
            model.addAttribute("entryListJSON", om.writeValueAsString(entryList));
            
            // 시험구분
            model.addAttribute("examTypeMapJSON", om.writeValueAsString(Constants.ExamType.getMap()));
            model.addAttribute("examTypes", Constants.ExamType.getList());

            // 직무분야
            model.addAttribute("jobTypeMapJSON", om.writeValueAsString(Constants.JobTypes.getMap()));
            model.addAttribute("jobTypes", Constants.JobTypes.getList());
            
            // 흥미분야
            model.addAttribute("interestTypeMapJSON", om.writeValueAsString(Constants.InterestTypes.getMap()));
            model.addAttribute("interestTypes", Constants.InterestTypes.getList());

            //학년 코드
            List<CommonCode> schoolGradeList = branchCommonCodeService.selectCommonCodeList(branchId, "schoolGrade"); 
            model.addAttribute("schoolGrades", schoolGradeList);
            
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        return modelView(model, branch.getName(), "branch_members");

    }

    @RequestMapping("/branch/{branchId}/pays")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchPays(ModelMap model, @PathVariable String branchId,
                                   @RequestParam(required = false) String sPayStartDt,
                                   @RequestParam(required = false) String sPayEndDt,
                                   @RequestParam(required = false) String sMember,
                                   @RequestParam(required = false) String sPayType,
                                   Page page) {
    	
    	PageMaker pageMaker = new PageMaker();

        // 파라미터 (검색)
        if(StringUtils.isEmpty(sPayStartDt)) {
            sPayStartDt = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(sPayEndDt)) {
            sPayEndDt = DateUtil.getCurrentDateString();

        }

        model.addAttribute("sPayStartDt", sPayStartDt);
        model.addAttribute("sPayEndDt", sPayEndDt);

        model.addAttribute("sMember", sMember);
        model.addAttribute("sPayType", sPayType);

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        try {
            ObjectMapper om = new ObjectMapper();

            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);
            			
            
            model.addAttribute("memberListJSON", om.writeValueAsString(memberList));

            model.addAttribute("members", memberList); // (검색 폼)

            // 지점의 결제 목록 조회 (결제일 기준)
            List<Pay> payList = branchPayService.selectPayList(branchId, sPayStartDt, sPayEndDt, sMember, sPayType, page);
            model.addAttribute("payListJSON", om.writeValueAsString(payList));
            
            // 지점의 결제 목록 조회 (결제일 기준)
            List<Pay> payTotalList = branchPayService.selectPayList(branchId, sPayStartDt, sPayEndDt, sMember, sPayType, null);
            pageMaker.setPage(page);
        	pageMaker.setTotalCount(payTotalList.size());
        	model.addAttribute("pageMaker", pageMaker);
            
            // 결제구분
            model.addAttribute("payTypes", Constants.PayType.getList());
            
            // 결제 지출유형
            model.addAttribute("payInOutTypes", Constants.PayInOutType.getList());

            List<Pay> payTotalPay =  branchPayService.selectTotalPay(branchId, sPayStartDt, sPayEndDt, sMember, sPayType);        
            model.addAttribute("payTotalPay", payTotalPay);
            
            model.addAttribute("PayStateTypeMapJSON", om.writeValueAsString(Constants.PayStateType.getMap()));            

            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        return modelView(model, branch.getName(), "branch_pays");

    }
    
    @RequestMapping("/branch/{branchId}/expenses")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchExpenses(ModelMap model, @PathVariable String branchId,
                                   @RequestParam(required = false) String sExpenseStartDt,
                                   @RequestParam(required = false) String sExpenseEndDt,
                                   @RequestParam(required = false) String sPayType,
                                   Page page) {
    	
    	PageMaker pageMaker = new PageMaker();

        // 파라미터 (검색)
        if(StringUtils.isEmpty(sExpenseStartDt)) {
        	sExpenseStartDt = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(sExpenseEndDt)) {
        	sExpenseEndDt = DateUtil.getCurrentDateString();

        }
        
        model.addAttribute("sExpenseStartDt", sExpenseStartDt);
        model.addAttribute("sExpenseEndDt", sExpenseEndDt);

        model.addAttribute("sPayType", sPayType);

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        try {
            ObjectMapper om = new ObjectMapper();


            // 지점의 결제 목록 조회 (결제일 기준)            
            List<Expense> expenseList = branchExpenseService.selectExpenseList(branchId, sExpenseStartDt, sExpenseEndDt, sPayType, page);
            model.addAttribute("expenseListJSON", om.writeValueAsString(expenseList));
            
            // 지점의 결제 목록 조회 (결제일 기준)
            List<Expense> expenseTotalList = branchExpenseService.selectExpenseList(branchId, sExpenseStartDt, sExpenseEndDt, sPayType, null);
            model.addAttribute("expenseTotal", expenseTotalList);            
            model.addAttribute("PayStateTypeMapJSON", om.writeValueAsString(Constants.PayStateType.getMap()));
            
            pageMaker.setPage(page);
        	pageMaker.setTotalCount(expenseTotalList.size());
        	model.addAttribute("pageMaker", pageMaker);
            
            // 결제구분
            model.addAttribute("payTypes", Constants.PayType.getList());
            
            // 결제 지출유형
            model.addAttribute("payInOutTypes", Constants.PayInOutType.getList());
            
            // ExpenseOption
            model.addAttribute("expenseOptions", Constants.ExpenseOption.getList());
            
            // ExpenseOption
            model.addAttribute("expenseGroups", Constants.ExpenseGroup.getList());
      

            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        return modelView(model, branch.getName(), "branch_expenses");

    }
    
    @RequestMapping("/branch/{branchId}/rentals")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchRentals(ModelMap model, @PathVariable String branchId,
                                   @RequestParam(required = false) String sRentalStartDt,
                                   @RequestParam(required = false) String sRentalEndDt,
                                   @RequestParam(required = false) String sMember,
                                   @RequestParam(required = false) String sGoodsId,
                                   Page page) {
    	
    	PageMaker pageMaker = new PageMaker();

        // 파라미터 (검색)
        if(StringUtils.isEmpty(sRentalStartDt)) {
            sRentalStartDt = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(sRentalEndDt)) {
            sRentalEndDt = DateUtil.getCurrentDateString();

        }

        model.addAttribute("sRentalStartDt", sRentalStartDt);
        model.addAttribute("sRentalEndDt", sRentalEndDt);

        model.addAttribute("sMember", sMember);
        model.addAttribute("sGoodsId", sGoodsId);

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        try {
            ObjectMapper om = new ObjectMapper();

            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);

            model.addAttribute("memberListJSON", om.writeValueAsString(memberList));

            model.addAttribute("members", memberList); // (검색 폼)

            // 지점의 대여 목록 조회 (대여일 기준)
            List<Rental> rentalList = branchRentalService.selectRentalList(branchId, sRentalStartDt, sRentalEndDt, sMember, sGoodsId, page);
            model.addAttribute("rentalListJSON", om.writeValueAsString(rentalList));
            model.addAttribute("rentalList" , rentalList);
            
            // 지점의 대여 목록 조회 (대여일 기준)
            List<Rental> rentalTotalList = branchRentalService.selectRentalList(branchId, sRentalStartDt, sRentalEndDt, sMember, sGoodsId, null);
          
            pageMaker.setPage(page);
        	pageMaker.setTotalCount(rentalTotalList.size());
        	model.addAttribute("pageMaker", pageMaker);
 
            // 대여구분
        	List<Rental> bRentalList = branchRentalService.selectGoodsList(branchId);
            model.addAttribute("rentalTypes", bRentalList);

            List<Goods> goodsList = branchRentalService.selectGoodsList2(branchId);
            model.addAttribute("goodsListJSON", om.writeValueAsString(goodsList));

            List<Rental> rentalTotalRental =  branchRentalService.selectTotalRental(branchId, sRentalStartDt, sRentalEndDt, sMember, sGoodsId);
            model.addAttribute("rentalTotalRental", rentalTotalRental);
            
            model.addAttribute("RentalStateTypeMapJSON", om.writeValueAsString(Constants.RentalStateType.getMap()));

            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        return modelView(model, branch.getName(), "branch_rentals");

    }
    @RequestMapping("/branch/{branchId}/reservations")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchReservations(ModelMap model, @PathVariable String branchId,
                                   @RequestParam(required = false) String sReservationStartDt,
                                   @RequestParam(required = false) String sReservationEndDt,
                                   @RequestParam(required = false) String sMember,
                                   @RequestParam(required = false) String sRoom,
                                   @RequestParam(required = false) String sDesk,
                                   Page page) {
    	    	
    	PageMaker pageMaker = new PageMaker();
        // 파라미터 (검색)
        if(StringUtils.isEmpty(sReservationStartDt)) {
            //sReservationStartDt = DateUtil.getCurrentDateString();
            sReservationStartDt = "2016-01-01";
        }

        if(StringUtils.isEmpty(sReservationEndDt)) {
            sReservationEndDt = DateUtil.getCurrentDateString();
        }

        model.addAttribute("sReservationStartDt", sReservationStartDt);
        model.addAttribute("sReservationEndDt", sReservationEndDt);

        model.addAttribute("sMember", sMember);
        model.addAttribute("sRoom", sRoom);
        model.addAttribute("sDesk", sDesk);

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        try {
            ObjectMapper om = new ObjectMapper();

            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);

            
            
            model.addAttribute("memberListJSON", om.writeValueAsString(memberList));

            model.addAttribute("members", memberList); // (검색 폼)

            // 지점의 열람실 목록 조회
            List<Room> roomList = branchDesignService.selectRoomList(branchId);
            model.addAttribute("roomListJSON", om.writeValueAsString(roomList));

            model.addAttribute("rooms", roomList); // (검색 폼)

            // 지점의 좌석 목록 조회
            List<Desk> deskList = branchDesignService.selectDeskList(branchId);
            model.addAttribute("deskListJSON", om.writeValueAsString(deskList));

            model.addAttribute("desks", deskList); // (검색 폼)
            
                        
            // 지점의 예약 목록 조회 (사용일 기준)
            List<Reservation> reservationList = branchReservationService.selectReservationList(branchId,
                    sReservationStartDt, sReservationEndDt, sMember, sRoom, sDesk, page, Constants.ReservationStatusType.CONFIRMED, null);
            model.addAttribute("reservationListJSON", om.writeValueAsString(reservationList));
            
        	// 지점의 총 예약 목록 조회 (Total count)            
        	pageMaker.setPage(page);
        	//pageMaker.setTotalCount(branchReservationService.selectReservationCount(branchId, sReservationStartDt, sReservationEndDt));
        	List<Reservation> reservationTotalList = branchReservationService.selectReservationList(branchId,
                    sReservationStartDt, sReservationEndDt, sMember, sRoom, sDesk, null, Constants.ReservationStatusType.CONFIRMED, null); 
        	pageMaker.setTotalCount(reservationTotalList.size());        	
        	model.addAttribute("pageMaker", pageMaker);
            

            model.addAttribute("reservationStatusTypeMapJSON", om.writeValueAsString(Constants.ReservationStatusType.getMap()));            
            
            // 결제구분
            model.addAttribute("payTypeMapJSON", om.writeValueAsString(Constants.PayType.getMap()));
            model.addAttribute("payTypes", Constants.PayType.getList());
            

        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        
        
        
        return modelView(model, branch.getName(), "branch_reservations");

    }

    @RequestMapping("/branch/{branchId}/orders")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchOrders(ModelMap model, @PathVariable String branchId,
                                           @RequestParam(required = false) String sOrderStartDt,
                                           @RequestParam(required = false) String sOrderEndDt,
                                           @RequestParam(required = false) String sMember) {

        // 파라미터 (검색)
        if(StringUtils.isEmpty(sOrderStartDt)) {
            sOrderStartDt = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(sOrderEndDt)) {
            sOrderEndDt = DateUtil.getCurrentDateString();

        }

        model.addAttribute("sOrderStartDt", sOrderStartDt);
        model.addAttribute("sOrderEndDt", sOrderEndDt);

        model.addAttribute("sMember", sMember);

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        try {
            ObjectMapper om = new ObjectMapper();

            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);
            model.addAttribute("memberListJSON", om.writeValueAsString(memberList));

            model.addAttribute("members", memberList); // (검색 폼)

            // 지점의 주문 목록 조회 (주문일 기준)
            List<Order> orderList = branchOrderService.selectOrderList(branchId,
                    sOrderStartDt, sOrderEndDt, sMember);
            model.addAttribute("orderListJSON", om.writeValueAsString(orderList));

            // 주문 상태
            model.addAttribute("orderStatusTypeMapJSON", om.writeValueAsString(Constants.OrderStatusType.getMap()));

        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        return modelView(model, branch.getName(), "branch_orders");

    }

    @RequestMapping("/branch/{branchId}/entries")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchEntries(ModelMap model, @PathVariable String branchId,
                                   @RequestParam(required = false) String sEntryStartDt,
                                   @RequestParam(required = false) String sEntryEndDt,
                                   @RequestParam(required = false) String sMember,
                                   @RequestParam(required = false) String sRoom,
                                   @RequestParam(required = false) String sDesk,
                                   Page page) {

    	PageMaker pageMaker = new PageMaker();
        // 파라미터 (검색)
        if(StringUtils.isEmpty(sEntryStartDt)) {
            sEntryStartDt = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(sEntryEndDt)) {
            sEntryEndDt = DateUtil.getCurrentDateString();

        }

        model.addAttribute("sEntryStartDt", sEntryStartDt);
        model.addAttribute("sEntryEndDt", sEntryEndDt);

        model.addAttribute("sMember", sMember);
        model.addAttribute("sRoom", sRoom);
        model.addAttribute("sDesk", sDesk);

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        try {
            ObjectMapper om = new ObjectMapper();

            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);
            model.addAttribute("memberListJSON", om.writeValueAsString(memberList));

            model.addAttribute("members", memberList); // (검색 폼)

            // 지점의 열람실 목록 조회
            List<Room> roomList = branchDesignService.selectRoomList(branchId);
            model.addAttribute("roomListJSON", om.writeValueAsString(roomList));

            model.addAttribute("rooms", roomList); // (검색 폼)

            // 지점의 좌석 목록 조회
            List<Desk> deskList = branchDesignService.selectDeskList(branchId);
            model.addAttribute("deskListJSON", om.writeValueAsString(deskList));

            model.addAttribute("desks", deskList); // (검색 폼)

            // 지점의 출입 목록 조회
            List<Entry> entryList = branchMemberService.selectMemberEntryList(branchId, sEntryStartDt, sEntryEndDt, sMember, sRoom, sDesk, page);
            model.addAttribute("entryListJSON", om.writeValueAsString(entryList));
            
            // 지점의 출입 목록 조회 (Total Count)
            List<Entry> entryTotalList = branchMemberService.selectMemberEntryList(branchId, sEntryStartDt, sEntryEndDt, sMember, sRoom, sDesk, null);
        	pageMaker.setPage(page);
        	pageMaker.setTotalCount(entryTotalList.size());
        	model.addAttribute("pageMaker", pageMaker);

        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        return modelView(model, branch.getName(), "branch_entries");

    }
    
    @RequestMapping("/branch/{branchId}/notifications")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchNofications(ModelMap model, @PathVariable String branchId,
                                   @RequestParam(required = false) String sNotificationStartDt,
                                   @RequestParam(required = false) String sNotificationEndDt,
                                   @RequestParam(required = false) String sMember,
                                   Page page) {
    	
    	PageMaker pageMaker = new PageMaker();
        // 파라미터 (검색)
        if(StringUtils.isEmpty(sNotificationStartDt)) {
        	sNotificationStartDt = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(sNotificationEndDt)) {
        	sNotificationEndDt = DateUtil.getCurrentDateString();

        }
        
        String sReservationStartDt = DateUtil.getCurrentDateString();
        String sReservationEndDt = DateUtil.getCurrentDateString();
        

        model.addAttribute("sNotificationStartDt", sNotificationStartDt);
        model.addAttribute("sNotificationEndDt", sNotificationEndDt);

        model.addAttribute("sMember", sMember);


        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        // 지점 목록 조회
        List<Branch> branches = branchService.selectBranchList();
        model.addAttribute("branches", branches);

        try {
            ObjectMapper om = new ObjectMapper();

            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);
            model.addAttribute("memberListJSON", om.writeValueAsString(memberList));

            model.addAttribute("members", memberList); // (검색 폼)


            // 지점의 알림 목록 조회                        
            List<Notification> notificationList = branchNotificationService.selectNotificationList(branchId, sNotificationStartDt, sNotificationEndDt, sMember, page);
            model.addAttribute("notificationListJSON", om.writeValueAsString(notificationList));
            
            // 지점의 알림 목록 조회 (Total Count)
            List<Notification> notificationTotalList = branchNotificationService.selectNotificationList(branchId, sNotificationStartDt, sNotificationEndDt, sMember, null);
            
        	pageMaker.setPage(page);
        	pageMaker.setTotalCount(notificationTotalList.size());
        	model.addAttribute("pageMaker", pageMaker);
        	
        	// 지점의 예약 목록 조회 (사용일 기준)
            List<Reservation> reservationList = branchReservationService.selectReservationList(branchId,
            		DateUtil.getCurrentDateString(), null, null, null, null, null, Constants.ReservationStatusType.CONFIRMED, null);
            model.addAttribute("reservationListJSON", om.writeValueAsString(reservationList));
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        											
        
        return modelView(model, branch.getName(), "branch_notifications");

    }
    

    @RequestMapping("/branch/{branchId}/branch_freeApplications")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchFreeApplications(ModelMap model,
    											@PathVariable String branchId,
    											@RequestParam(required = false) String sFreeApplicationStartDt,                      				
    											@RequestParam(required = false) String sFreeApplicationEndDt,
    											@RequestParam(required = false) String sName,
    											@RequestParam(required = false) String sTel,
    											@RequestParam(required = false) Integer sRoomType,
    											Page page
    											) {    	    	    
        
    	PageMaker pageMaker = new PageMaker();
        // 파라미터 (검색)
        if(StringUtils.isEmpty(sFreeApplicationStartDt)) {
        	sFreeApplicationStartDt = DateUtil.getCurrentDateString();        	

        }

        if(StringUtils.isEmpty(sFreeApplicationEndDt)) {
        	sFreeApplicationEndDt = DateUtil.getCurrentDateString();        	

        }
                
        model.addAttribute("sFreeApplicationStartDt", sFreeApplicationStartDt);
        model.addAttribute("sFreeApplicationEndDt", sFreeApplicationEndDt);
        model.addAttribute("sName", sName);
        model.addAttribute("sTel", sTel);
        model.addAttribute("sRoomType", sRoomType);
        
    	// 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);
        
        try {
            ObjectMapper om = new ObjectMapper();
            // 지점의 무료신청 목록 조회
            List<FreeApplication> FreeApplicationList = branchFreeApplicationService.selectFreeApplicationListAll(branchId, sFreeApplicationStartDt, sFreeApplicationEndDt, sName, sTel, sRoomType, page);
            model.addAttribute("FreeApplicationListJSON", om.writeValueAsString(FreeApplicationList));
            
            // 지점의 알림 목록 조회 (Total Count)
            List<FreeApplication> FreeApplicationCountList = branchFreeApplicationService.selectFreeApplicationListAll(branchId, sFreeApplicationStartDt, sFreeApplicationEndDt, sName, sTel, sRoomType, null);
        	pageMaker.setPage(page);
        	pageMaker.setTotalCount(FreeApplicationCountList.size());        	
        	model.addAttribute("pageMaker", pageMaker);
            
            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);
            model.addAttribute("memberListJSON", om.writeValueAsString(memberList));

            model.addAttribute("members", memberList); // (검색 폼)
            
	        // 성별 구분
            model.addAttribute("genderTypeMapJSON", om.writeValueAsString(Constants.GenderType.getMap()));
            model.addAttribute("genderTypes", Constants.GenderType.getList());
            // 알게 된 경로
            model.addAttribute("cmpRouteTypes", Constants.cmpRouteType.getList());
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        // 열람실 유형      
        model.addAttribute("roomTypes", Constants.RoomType.getList());       
        
        return modelView(model, branch.getName(), "branch_freeApplications");

    }
    
    @RequestMapping("/branch/{branchId}/preReservation")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView branchPreReservation(ModelMap model, @PathVariable String branchId
        , @RequestParam(required=false) String startDt 
        , @RequestParam(required=false) String endDt 
        , @RequestParam(required=false) String keyword 
        , Page page
        ) {
      
      ObjectMapper om = new ObjectMapper();
      
      // 지점 조회
      Branch branch = branchService.selectBranch(branchId);
      Branch branch_setup = branchPreReservationService.selectBranchWithOpenDt(branch.getBranchId());
      
      if(branch_setup == null){
    	  return modelView(model, branch.getName(), "not_available");
      }
      branch.setOpenDt(branch_setup.getOpenDt());
      model.addAttribute("branch", branch);
      
      
      // 사전 예약 데이터 조회
      PageMaker pageMaker = new PageMaker();
      List<PreReservation> preReservationList = branchPreReservationService.selectPreReservation(branchId, startDt, endDt, keyword, page);
      
      pageMaker.setPage(page);
      pageMaker.setTotalCount(branchPreReservationService.selectPreReservationCount(branchId, startDt, endDt, keyword));
      model.addAttribute("pageMaker", pageMaker);
      
      // 지점의 회원 목록 조회
      List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);
      try {
    	  model.addAttribute("memberListJSON", om.writeValueAsString(memberList));
      } catch (JsonProcessingException e1) {
    	  e1.printStackTrace();
      }

      model.addAttribute("members", memberList); // (검색 폼)
      
      try {
        model.addAttribute("preReservationListJSON", om.writeValueAsString(preReservationList));
      } catch (JsonProcessingException e) {
        model.addAttribute("preReservationListJSON", "");
        e.printStackTrace();
      }
      
      // 성별 구분
      model.addAttribute("genderTypes", Constants.GenderType.getList());
        
      
      // 검색 조건 전달
      if ( !StringUtils.isEmpty(startDt)){
        LocalDate startDate = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM-dd")); 
        model.addAttribute("startDt", DateUtil.asDate(startDate));
      }
      
      if ( !StringUtils.isEmpty(startDt)){
        LocalDate endDate = LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM-dd")); 
        model.addAttribute("endDt", DateUtil.asDate(endDate));
      }
      
      if ( !StringUtils.isEmpty(startDt)){
        model.addAttribute("keyword", keyword);
      }
      
      return modelView(model, branch.getName(), "branch_preReservation");
    }
    
    @RequestMapping("/branch/{branchId}/branch_seatReservation")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView getSeatReservationList(ModelMap model,
    											@PathVariable String branchId,
    											@RequestParam(required = false) String seatReservationStartDt,
    											@RequestParam(required = false) String seatReservationEndDt,
    											@RequestParam(required = false) String sMember,
    											@RequestParam(required = false) String tel,
    											@RequestParam(required = false) Integer roomType,
    											@RequestParam(required = false) Integer type,
    											Page page
    											) {    	    
    	
    	PageMaker pageMaker = new PageMaker();
        // 파라미터 (검색)
        if(StringUtils.isEmpty(seatReservationStartDt)) {
        	seatReservationStartDt = DateUtil.getCurrentDateString();        	

        } 

        if(StringUtils.isEmpty(seatReservationEndDt)) {
        	seatReservationEndDt = DateUtil.getCurrentDateString();        	

        }
                
        model.addAttribute("seatReservationStartDt", seatReservationStartDt);
        model.addAttribute("seatReservationEndDt", seatReservationEndDt);
        model.addAttribute("sMember", sMember);
        model.addAttribute("tel", tel);
        model.addAttribute("roomType", roomType);
        model.addAttribute("type", type);

        
    	// 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);
        
        try {
        	
            ObjectMapper om = new ObjectMapper();
            
            // 지점의 회원 목록 조회
            List<BranchMember> memberList = branchMemberService.selectMemberList(branchId);            
          	model.addAttribute("memberListJSON", om.writeValueAsString(memberList));
          	model.addAttribute("members", memberList); // (검색 폼)
          	
            // 지점의 예약목록 조회
            List<SeatReservation> SeatReservationList = branchSeatReservationService.selectSeatReservationList(branchId, seatReservationStartDt, seatReservationEndDt, sMember, tel, roomType, page);
            
            model.addAttribute("SeatReservationListJSON", om.writeValueAsString(SeatReservationList));
            
            
            // 페이징을 위한 TotalCount 조회
            List<SeatReservation> seatReservationListCount = branchSeatReservationService.selectSeatReservationListCount(branchId, seatReservationStartDt, seatReservationEndDt, sMember, tel, roomType, null);
            
            pageMaker.setPage(page);
            pageMaker.setTotalCount(seatReservationListCount.size());        	
            model.addAttribute("pageMaker", pageMaker);
            
            model.addAttribute("roomTypes", Constants.RoomType.getList());
            model.addAttribute("guestTypes", Constants.GuestTypes.getList());
            model.addAttribute("genderTypes", Constants.GenderType.getList());
            model.addAttribute("seatStatusTypes", Constants.SeatStatusType.getList());

            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        
        
        model.addAttribute("roomTypes", Constants.RoomType.getList());       
        model.addAttribute("guestTypes", Constants.GuestTypes.getList());
        model.addAttribute("genderTypes", Constants.GenderType.getList());
        
        
        return modelView(model, branch.getName(), "branch_seatReservation");

    }
    
    //학부모 안심 관리
    @RequestMapping("/branch/{branchId}/branch_safeService")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView getSafeServiceList( ModelMap model,
    											@PathVariable String branchId,
    											@RequestParam(required = false) String safeServiceStartDt,
    											@RequestParam(required = false) String safeServiceEndDt,
    											@RequestParam(required = false) String sParentsMember,
    											@RequestParam(required = false) String sStudentMember,
    											@RequestParam(required = false) String parentsTel,
    											@RequestParam(required = false) String studentTel,    											
    											@RequestParam(required = false) Integer statusType,
    											Page page
    											) {    	    

    	PageMaker pageMaker = new PageMaker();
        // 파라미터 (검색)
        if(StringUtils.isEmpty(safeServiceStartDt)) {
        	safeServiceStartDt = DateUtil.getCurrentDateString();        	

        } 

        if(StringUtils.isEmpty(safeServiceEndDt)) {
        	safeServiceEndDt = DateUtil.getCurrentDateString();        	

        }
                
        model.addAttribute("safeServiceStartDt", safeServiceStartDt);
        model.addAttribute("safeServiceEndDt", safeServiceEndDt);
        model.addAttribute("sParentsMember", sParentsMember);
        model.addAttribute("sStudentMember", sStudentMember);
        model.addAttribute("parentsTel", parentsTel);        
        model.addAttribute("studentTel", studentTel);
        model.addAttribute("statusType", statusType);

        
    	// 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);
        
        try {
        	
            ObjectMapper om = new ObjectMapper();
            
            //학부모 이름
            List<BranchMember> parentsMemberList = appClientMemberService.selectAppParentsForCodi();            
          	model.addAttribute("parentsMemberListJSON", om.writeValueAsString(parentsMemberList));
          	model.addAttribute("parentsMembers", parentsMemberList); // (검색 폼)
          	
          	//app회원 이름          	
          	List<BranchMember> studentMemberList = appClientMemberService.selectAppBranchManagerForCodi(branchId, null);            
          	model.addAttribute("studentMemberListJSON", om.writeValueAsString(studentMemberList));
          	model.addAttribute("studentMembers", studentMemberList); // (검색 폼)
          	
            // 지점의 예약목록 조회
            //List<SeatReservation> SeatReservationList = branchSeatReservationService.selectSeatReservationList(branchId, seatReservationStartDt, seatReservationEndDt, name, tel, roomType, page);
            List<SafeService> SafeServiceList = branchSafeServiceService.selectSafeServiceList(branchId, safeServiceStartDt, safeServiceEndDt, sParentsMember, sStudentMember, parentsTel, studentTel, statusType, page);
            
            model.addAttribute("SafeServiceListJSON", om.writeValueAsString(SafeServiceList));
            
            
            // 페이징을 위한 TotalCount 조회
            List<SafeService> SafeServiceListCount = branchSafeServiceService.selectSafeServiceList(branchId, safeServiceStartDt, safeServiceEndDt, sParentsMember, sStudentMember, parentsTel, studentTel, statusType, null);
            
            pageMaker.setPage(page);
            pageMaker.setTotalCount(SafeServiceListCount.size());        	
            model.addAttribute("pageMaker", pageMaker);
            
            //model.addAttribute("roomTypes", Constants.RoomType.getList());
            //model.addAttribute("guestTypes", Constants.GuestTypes.getList());
            //model.addAttribute("genderTypes", Constants.GenderType.getList());
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        
        
        model.addAttribute("statusTypes", Constants.StatusType.getList());
        //model.addAttribute("roomTypes", Constants.RoomType.getList());       
        //model.addAttribute("guestTypes", Constants.GuestTypes.getList());
        //odel.addAttribute("genderTypes", Constants.GenderType.getList());
        
        
        return modelView(model, branch.getName(), "branch_safeService");

    }
    
}
