package kr.co.cntt.scc.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.alimTalk.AlimTalk;
import kr.co.cntt.scc.alimTalk.AlimTalkService;
import kr.co.cntt.scc.app.admin.common.ApiResult;
import kr.co.cntt.scc.app.admin.service.AppSmsCertify;
import kr.co.cntt.scc.app.student.model.AppClientMember;
import kr.co.cntt.scc.app.student.service.AppClientMemberService;
import kr.co.cntt.scc.model.AdminStatistics;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.model.CommonCode;
import kr.co.cntt.scc.model.Desk;
import kr.co.cntt.scc.model.Entry;
import kr.co.cntt.scc.model.Expense;
import kr.co.cntt.scc.model.Goods;
import kr.co.cntt.scc.model.MembershipCard;
import kr.co.cntt.scc.model.Notification;
import kr.co.cntt.scc.model.Order;
import kr.co.cntt.scc.model.Page;
import kr.co.cntt.scc.model.Pay;
import kr.co.cntt.scc.model.Rental;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.model.Room;
import kr.co.cntt.scc.model.SafeService;
import kr.co.cntt.scc.model.SeatReservation;
import kr.co.cntt.scc.service.BranchCommonCodeService;
import kr.co.cntt.scc.service.BranchDesignService;
import kr.co.cntt.scc.service.BranchExpenseService;
import kr.co.cntt.scc.service.BranchMemberService;
import kr.co.cntt.scc.service.BranchOrderService;
import kr.co.cntt.scc.service.BranchPayService;
import kr.co.cntt.scc.service.BranchRentalService;
import kr.co.cntt.scc.service.BranchReservationService;
import kr.co.cntt.scc.service.BranchSafeServiceService;
import kr.co.cntt.scc.service.BranchSeatReservationService;
import kr.co.cntt.scc.service.BranchService;
import kr.co.cntt.scc.service.SmsService;
import kr.co.cntt.scc.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * API 컨트롤러
 * 버전 1
 * Created by jslivane on 2016. 5. 1..
 */
@RestController
@Slf4j 
@RequestMapping(value = "/api/v1")
@PreAuthorize("hasAuthority('admin') OR hasAuthority('user') OR hasAuthority('manager')")
public class ApiV1Controller {

    @Autowired
    private BranchMemberService branchMemberService;

    @Autowired
    private BranchDesignService branchDesignService;

    @Autowired
    private BranchPayService branchPayService;

    @Autowired
    private BranchExpenseService branchExpenseService;
    
    @Autowired
    private BranchRentalService branchRentalService;
    
    @Autowired
    private BranchReservationService branchReservationService;

    @Autowired
    private BranchOrderService branchOrderService;
    
    @Autowired
    private SmsService smsService;

    @Autowired
    private AlimTalkService alimTalkService;
    
    @Autowired
    private BranchSeatReservationService branchSeatReservationService;
    
    @Autowired
    private BranchSafeServiceService branchSafeServiceService;
    
    @Autowired
    private AppClientMemberService appClientMemberService;
    
	@Autowired
	private AppSmsCertify appSmsCertify;
	
	@Autowired
	private BranchCommonCodeService branchCommonCodeService;
	
	@Autowired
	private BranchService branchService;
	
	
    
    public ApiV1Controller() {
    }

    /**************************************************************************************/


    /**
     * (지점의) 회원 목록 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/members", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<BranchMember> getBranchMemberList(@PathVariable String branchId, @PathVariable String memberId) {

        List<BranchMember> memberList = branchMemberService.selectMemberList(branchId, memberId, null);
        //List<BranchMember> appMemberList = appClientMemberService.select
        
		//appMember 가져오기
    	List<BranchMember> appMemberList = appClientMemberService.selectAppBranchManagerForCodi(branchId, memberId);
		if (appMemberList.size() > 0) {
			memberList.addAll(appMemberList);
		}
        
        return memberList;

    }

    /**
     * (지점의) 새 회원 번호 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/members/nextNo", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    String getBranchMemberNextNo(@PathVariable String branchId) {

        return branchMemberService.selectMemberNextNo(branchId);

    }

    
    /**
     * 멤버쉽 카드 번호 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/members/membership", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<MembershipCard> getBranchMembershipNo(@PathVariable String branchId) {

        return branchMemberService.selectMembershipNoList();

    }
    /**
     * (지점의) 회원 번호 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/members/No", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<String> getBranchMemberNo(@PathVariable String branchId) {

        return branchMemberService.selectMemberNo(branchId);

    }
    
    /**
     * (지점의) 새 회원 추가
     * @param branchId
     * @param member
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/members", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    BranchMember postBranchMember(@PathVariable String branchId, @RequestBody BranchMember member) {
        // 외부 아이디 생성
    	String memberId = UUID.randomUUID().toString();        
        branchMemberService.insertMember(branchId, memberId, member);
        branchMemberService.insertMemberApprove(branchId,memberId,member);
        if(member.getMembershipNo() == null || member.getMembershipNo() == ""){
        }
        else {
        	branchMemberService.updateMembership(branchId, memberId, member);
        
        }
        
        BranchMember insertedMember = branchMemberService.selectMember(branchId, memberId); //********************************************

        return insertedMember;

    }

    /**
     * (지점의) 특정 회원 조회
     * @param branchId
     * @param memberId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/members/{memberId}", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    BranchMember getBranchMember(@PathVariable String branchId, @PathVariable String memberId) {

        BranchMember member = branchMemberService.selectMember(branchId, memberId);
        
        return member;

    }

    /**
     * (지점의) 특정 회원 수정
     * @param branchId
     * @param memberId
     * @param member
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/members/{memberId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchMember(@PathVariable String branchId, @PathVariable String memberId, @RequestBody BranchMember member) {
    	
    	log.info("=============================member=============" + member);
    	//앱회원은 정보수정 불가
    	if (member.getNo().contains("C")) {
    		return 1;
    	}
    	
    	if(member.getMembershipNo() == null || member.getMembershipNo() == "") {
    		
    	}
    	else {
    		branchMemberService.updateMembership(branchId, memberId, member);
    	}
	    
    	branchMemberService.updateMemberApprove(branchId, memberId, member);
        return branchMemberService.updateMember(branchId, memberId, member);
        
    }

    /**
     * (지점의) 특정 회원 삭제
     * @param branchId
     * @param memberId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/members/{memberId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchMember(@PathVariable String branchId, @PathVariable String memberId) {

        return branchMemberService.deleteMember(branchId, memberId);

    }


 // 배석해제
    @RequestMapping(value = "/branch/{branchId}/members/{memberId}/expireYn", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    ResponseEntity<?> postReservationCheckout2(@PathVariable String branchId,  @PathVariable String memberId) {

    	branchMemberService.updateMemberExpire(branchId, memberId, false);
    	
    	return new ResponseEntity<>("{\"RESULT\" : \"SUCCESS\"}", HttpStatus.OK);
    }
    
    /**************************************************************************************/


    /**
     * (지점의) 열람실 목록 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/rooms", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Room> getBranchRoomList(@PathVariable String branchId) {

        List<Room> roomList = branchDesignService.selectRoomList(branchId);

        return roomList;

    }

    /**
     * (지점의) 새 열람실 추가
     * @param branchId
     * @param room
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/rooms", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    Room postBranchRoom(@PathVariable String branchId, @RequestBody Room room) {
        // 외부 아이디 생성
        String roomId = UUID.randomUUID().toString();        
        branchDesignService.insertRoom(branchId, roomId, room);

        return branchDesignService.selectRoom(branchId, roomId);

    }

    /**
     * (지점의) 특정 열람실 수정
     * @param branchId
     * @param roomId
     * @param room
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/rooms/{roomId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchRoom(@PathVariable String branchId, @PathVariable String roomId, @RequestBody Room room) {

        return branchDesignService.updateRoom(branchId, roomId, room);

    }

    /**
     * (지점의) 특정 열람실 삭제
     * @param branchId
     * @param roomId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/rooms/{roomId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchRoom(@PathVariable String branchId, @PathVariable String roomId) {

        // TODO 해당 열람실의 좌석도 삭제?

        return branchDesignService.deleteRoom(branchId, roomId);

    }


    /**************************************************************************************/


    /**
     * (지점의) 좌석 목록 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/desks", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Desk> getBranchDeskList(@PathVariable String branchId) {

        List<Desk> deskList = branchDesignService.selectDeskList(branchId);

        return deskList;

    }

    /**
     * (지점의) 새 좌석 추가
     * @param branchId
     * @param desk
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/desks", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    Desk postBranchDesk(@PathVariable String branchId, @RequestBody Desk desk) {
        // 외부 아이디 생성
        String deskId = UUID.randomUUID().toString();

        branchDesignService.insertDesk(branchId, deskId, desk);

        Desk insertedDesk = branchDesignService.selectDesk(branchId, deskId);

        return insertedDesk;

    }

    /**
     * (지점의) 특정 좌석 수정
     * @param branchId
     * @param deskId
     * @param desk
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/desks/{deskId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchDesk(@PathVariable String branchId, @PathVariable String deskId, @RequestBody Desk desk) {

        return branchDesignService.updateDesk(branchId, deskId, desk);

    }

    /**
     * (지점의) 특정 좌석 삭제
     * @param branchId
     * @param deskId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/desks/{deskId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchDesk(@PathVariable String branchId, @PathVariable String deskId) {

        return branchDesignService.deleteDesk(branchId, deskId);

    }


    /**************************************************************************************/


    /**
     * (지점의 특정기간) 결제 목록 조회 (결제일 기준)
     * @param branchId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/pays", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Pay> getBranchPayList(ModelMap model,
    							@PathVariable String branchId,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(required = false) String memberId,
                               @RequestParam(required = false) String payType,
                               Page page) {

        // 파라미터 (검색)
        if(StringUtils.isEmpty(startDate)) {
            startDate = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(endDate)) {
            endDate = DateUtil.getCurrentDateString();

        }

        List<Pay> payList =  branchPayService.selectPayList(branchId, startDate, endDate, memberId, payType, page);
        List<Pay> payTotalPay =  branchPayService.selectTotalPay(branchId, startDate, endDate, memberId, payType);        
        model.addAttribute("payTotalPay", payTotalPay);
        
        return payList;
    }

    /**
     * (지점의) 새 결제 추가
     * @param branchId
     * @param pay
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/pays", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    Pay postBranchPay(@PathVariable String branchId, @RequestBody Pay pay) {
        // 외부 아이디 생성
        String payId = UUID.randomUUID().toString();

        branchPayService.insertPay(branchId, payId, pay);

        Pay insertedPay = branchPayService.selectPay(branchId, payId);

        return insertedPay;

    }
    
    /**
     * (지점의 특정기간) 지출 목록 조회 (지출일 기준)
     * @param branchId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/expenses", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Expense> getBranchExpenseList(ModelMap model,
    							@PathVariable String branchId,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(required = false) String payType,
                               Page page) {

        // 파라미터 (검색)
        if(StringUtils.isEmpty(startDate)) {
            startDate = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(endDate)) {
            endDate = DateUtil.getCurrentDateString();

        }
        
        List<Expense> ExpenseList = branchExpenseService.selectExpenseList(branchId, startDate, endDate, payType, page);

        List<Expense> ExpenseTotalList = branchExpenseService.selectExpenseList(branchId, startDate, endDate, payType, null);
        model.addAttribute("expenseTotal", ExpenseTotalList);
        
        return ExpenseList;
    }
    
    /**
     * (지점의) 새 지출 추가
     * @param branchId
     * @param expense
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/expenses", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    Expense postBranchExpense(@PathVariable String branchId, @RequestBody Expense expense) {
        // 외부 아이디 생성
        String expenseId = UUID.randomUUID().toString();


        branchExpenseService.insertExpense(branchId, expenseId, expense);

        Expense insertedExpense = branchExpenseService.selectExpense(branchId, expenseId);
        
        return insertedExpense;

    }    
    
    /**
     * (지점의) 특정 지출 수정
     * @param branchId
     * @param expenseId
     * @param expense
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/expenses/{expenseId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchExpense(@PathVariable String branchId, @PathVariable String expenseId, @RequestBody Expense expense) {
        
        return branchExpenseService.updateExpense(branchId, expenseId, expense);

    }
    
    /**
     * 특정 대여 물품 수정
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/expenses/{expenseId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public int deleteBranchExpense(@PathVariable String branchId, @PathVariable String expenseId) {
    	        
        return branchExpenseService.deleteExpense(branchId, expenseId);
        
    }    
    
    /**
     * (지점의 특정기간) 대여 목록 조회 (결제일 기준)
     * @param branchId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/rentals", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Rental> getBranchRentalList(ModelMap model,
    							@PathVariable String branchId,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(required = false) String memberId,
                               @RequestParam(required = false) String rentalType,
                               Page page) {

        // 파라미터 (검색)
        if(StringUtils.isEmpty(startDate)) {
            startDate = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(endDate)) {
            endDate = DateUtil.getCurrentDateString();

        }

        List<Rental> rentalList =  branchRentalService.selectRentalList(branchId, startDate, endDate, memberId, rentalType, page);
        
        List<Rental> rentalTotalRental =  branchRentalService.selectTotalRental(branchId, startDate, endDate, memberId, rentalType);        
        model.addAttribute("rentalList", rentalList);
        
        return rentalList;
    }
    
    /**
     * (지점의) 새 대여 추가
     * @param branchId
     * @param rental
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/rentals", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Rental> postBranchRental(@PathVariable String branchId, @RequestBody Rental rental ) {
        // 외부 아이디 생성
        String rentalId = UUID.randomUUID().toString();
        String rentalGoodsIds = rental.getGoodsIds();
        
        // 대여한 품목 String 쪼개기
        String[] b = StringUtils.isEmpty(rentalGoodsIds) ? new String[]{} : rentalGoodsIds.split(Constants.SEPERATOR);
        int l = b.length;
        
        // 대여 품목 각각 ID만들기
        String[] rentalTag = new String[l];
        for(int i = 0; i <  l; i++) rentalTag[i] = UUID.randomUUID().toString();
        
        branchRentalService.insertRental(branchId, rentalId, rental, b);

        String memberId = rental.getMemberId();
        
        List<Rental> insertedRental = branchRentalService.selectRental(branchId, memberId, rentalId);

        return insertedRental;

    }
    
    
    /**
     * 대여 물품 목록 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/goods", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public List<Goods> getBranchGoodsList(@PathVariable String branchId) {

    	List<Goods> goodsList = branchRentalService.selectGoodsList2(branchId);
        
        return goodsList;
    } 

    /**
     * 새 대여 물품 추가
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/goods", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public Goods postBranchGoods(@PathVariable String branchId, @RequestBody Goods goods) {    	
    	// 외부 아이디 생성
        String goodsId = UUID.randomUUID().toString();
    	
    	branchRentalService.insertGoodsList(branchId, goodsId, goods);
        Goods insertGoods = branchRentalService.selectGoods(branchId, goodsId);

        return insertGoods;
        
    } 

    /**
     * 특정 대여 물품 수정
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/goods/{goodsId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public int putBranchGoods(@PathVariable String branchId, @PathVariable String goodsId, @RequestBody Goods goods) {
    	
        return branchRentalService.updateGoods(branchId, goodsId, goods);
        
    }
    
    /**
     * 특정 대여 물품 수정
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/goods/{goodsId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public int deleteBranchGoods(@PathVariable String branchId, @PathVariable String goodsId, @RequestBody Goods goods) {
    	
        return branchRentalService.updateGoods(branchId, goodsId, goods);
        
    }    
    
    /**
     * (지점의) 특정 결제 수정
     * @param branchId
     * @param payId
     * @param pay
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/pays/{payId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchPay(@PathVariable String branchId, @PathVariable String payId, @RequestBody Pay pay) {

        return branchPayService.updatePay(branchId, payId, pay);

    }

    /**
     * (지점의) 특정 대여 수정
     * @param branchId
     * @param rentalId
     * @param rental
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/rentals/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchRental(@PathVariable String branchId, @PathVariable String id, @RequestBody Rental rental) {

    	//branchRentalService.deleteBranchRental(branchId, rental);
    	//int updateRental = branchRentalService.updateRental(branchId, rental); 
        return branchRentalService.updateRental(branchId, id, rental);
        
    	//return branchRentalService.updateRental(branchId, rentalTagId, rental);

    }

    /**
     * (지점의) 특정 대여 삭제
     * @param branchId
     * @param rentalId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/rentals/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchRental(@PathVariable String branchId, @PathVariable String id) {

    	int success_update = 0;
    	   	    	
    	
    
        return branchRentalService.deleteRental(branchId, id);

    }
    
    /**
     * (지점의) 특정 결제 삭제
     * @param branchId
     * @param payId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/pays/{payId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchPay(@PathVariable String branchId, @PathVariable String payId) {

    	
        return branchPayService.deletePay(branchId, payId);

    }
    

    /**************************************************************************************/


    /**
     * (지점의 특정기간)  주문 목록 조회 (사용일 기준)
     * @param branchId
     * @param memberId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Order> getBranchOrderList(@PathVariable String branchId,
                                   @RequestParam(required = false) String sOrderStartDt,
                                   @RequestParam(required = false) String sOrderEndDt,
                                   @RequestParam(required = false) String memberId) {

        // 파라미터 (검색)
        if(StringUtils.isEmpty(sOrderStartDt)) {
            sOrderStartDt = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(sOrderEndDt)) {
            sOrderEndDt = DateUtil.getCurrentDateString();

        }

        List<Order> orderList = branchOrderService.selectOrderList(branchId, sOrderStartDt, sOrderEndDt, memberId);

        return orderList;

    }

    /**
     * (지점의) 새 주문 추가
     * @param branchId
     * @param order
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    Order postBranchOrder(@PathVariable String branchId, @RequestBody Order order) {
        // 외부 아이디 생성
        String orderId = UUID.randomUUID().toString();

        // 주문 상태 : 확정(20)
        order.setOrderStatus(Constants.OrderStatusType.CONFIRMED.getValue());

        branchOrderService.insertOrder(branchId, orderId, order);

        Order insertedOrder = branchOrderService.selectOrder(branchId, orderId);

        return insertedOrder;

    }

    /**
     * (지점의) 특정 주문 수정
     * @param branchId
     * @param orderId
     * @param order
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchOrder(@PathVariable String branchId, @PathVariable String orderId, @RequestBody Order order) {

        return branchOrderService.updateOrder(branchId, orderId, order);

    }

    /**
     * (지점의) 특정 주문 삭제
     * @param branchId
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchOrder(@PathVariable String branchId, @PathVariable String orderId) {

        return branchOrderService.deleteOrder(branchId, orderId);

    }

    /**************************************************************************************/

    /**
     * (지점의 특정 주문의) 예약 목록 조회
     * @param branchId
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}/reservations", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Reservation> getBranchOrderReservationList(@PathVariable String branchId, @PathVariable String orderId) {

        List<Reservation> reservationList = branchReservationService.selectReservationList(branchId, orderId);

        return reservationList;

    }

    /**
     * (지점의 특정 주문의) 결제 목록 조회
     * @param branchId
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}/pays", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Pay> getBranchOrderPayList(@PathVariable String branchId, @PathVariable String orderId) {

        List<Pay> payList = branchPayService.selectPayList(branchId, orderId);
        return payList;

    }
    
    /**
     * (지점의 특정 주문의) 결제 추가
     * @param branchId
     * @param pay
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}/pays", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    Pay postBranchOrderPay(@PathVariable String branchId, @PathVariable String orderId, @RequestBody Pay pay) {
        // 외부 아이디 생성
        String payId = UUID.randomUUID().toString();

        branchPayService.insertPay(branchId, payId, pay);
        
        Pay insertedPay = branchPayService.selectPay(branchId, payId);

        return insertedPay;

    }
    
    /**
     * (지점의 특정 주문의) 결제 삭제
     * @param branchId
     * @param pay
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}/pays/{payId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchOrderPay(@PathVariable String branchId, @PathVariable String orderId, @PathVariable String payId, @RequestHeader(value="flag") Boolean paramFlag) {    	
    	
    	return branchPayService.deleteBranchOrderPay(branchId, orderId, payId, paramFlag);

    }

    /**
     * (지점의 특정 주문의) 결제 수정
     * @param branchId
     * @param pay
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}/pays/{payId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchOrderPay(@PathVariable String branchId, @PathVariable String orderId, @PathVariable String payId, @RequestBody Pay pay) {    	
    	
		//result = branchPayService.updateBranchOrderPay(branchId, orderId, payId, pay);
    	int result = branchPayService.updatePay(branchId, payId, pay);

    	
    	return result;
    }
    
    /**
     * 결제 목록 조회 for ReservationDelete
     * @param branchId
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}/pays/list", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Pay> selectPayListforReservationDelete(@PathVariable String branchId, @PathVariable String orderId) {

        List<Pay> payList = branchPayService.selectPayListforReservationDelete(branchId, orderId);
        return payList;

    }
    
    /**************************************************************************************/


    /**
     * (지점의 특정기간) 예약 목록 조회 (사용일 기준)
     * @param branchId
     * @param startDate
     * @param endDate
     * @param memberId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/reservations", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Reservation> getBranchReservationList(@PathVariable String branchId,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(required = false) String memberId,
                               @RequestParam(required = false) String roomId,
                               @RequestParam(required = false) String deskId,
                               Page page) {

        // 파라미터 (검색)
        if(StringUtils.isEmpty(startDate)) {
            startDate = DateUtil.getCurrentDateString();

        }

        if(StringUtils.isEmpty(endDate)) {
            endDate = DateUtil.getCurrentDateString();

        }

        List<Reservation> reservationList = branchReservationService.selectReservationList(branchId, startDate, endDate, memberId, roomId, deskId, page, null, null);

        return reservationList;

    }

    /**
     * (지점의) 새 예약 추가
     * @param branchId
     * @param reservation
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/reservations", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    Reservation postBranchReservation(@PathVariable String branchId, @RequestBody Reservation reservation) {
        // 외부 아이디 생성
        String reservationId = UUID.randomUUID().toString();
        // 예약 상태 : 확정(20)
        reservation.setReservationStatus(Constants.ReservationStatusType.CONFIRMED.getValue());

        branchReservationService.insertReservation(branchId, reservationId, reservation);
        
        Reservation insertedReservation = null;
        insertedReservation = branchReservationService.selectReservation(branchId, reservationId);

        return insertedReservation;

    }

    /**
     * (지점의) 특정 예약 수정
     * @param branchId
     * @param reservationId
     * @param reservation
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/reservations/{reservationId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchReservation(@PathVariable String branchId, @PathVariable String reservationId, @RequestBody Reservation reservation) {

        // 예약 상태 : 변경(50)
        int result = branchReservationService.updateReservationStatus(branchId,
                reservation.getOrderId(), reservationId,
                Constants.ReservationStatusType.CHANGED);

        /***************************************/

        // 외부 아이디 생성
        String newReservationId = UUID.randomUUID().toString();

        // 예약 상태 : 확정(20)
        reservation.setReservationStatus(Constants.ReservationStatusType.CONFIRMED.getValue());

        branchReservationService.insertReservation(branchId, newReservationId, reservation);

        return result;

    }

    /**
     * (지점의) 특정 예약 수정
     * @param branchId
     * @param reservationId
     * @param reservation
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderID}/reservations/{reservationId}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int putBranchReservationModification(@PathVariable String branchId, @PathVariable String reservationId, @RequestBody Reservation reservation) {

        // 예약 상태 : 변경(50)
        int result = branchReservationService.updateReservationStatus(branchId,
                reservation.getOrderId(), reservationId,
                Constants.ReservationStatusType.CHANGED);

        /***************************************/

        // 외부 아이디 생성
        String newReservationId = UUID.randomUUID().toString();

        // 예약 상태 : 확정(20)
        reservation.setReservationStatus(Constants.ReservationStatusType.CONFIRMED.getValue());

        result = branchReservationService.insertReservation(branchId, newReservationId, reservation);
        if (result == 0) { //오류
        	
        } else { 

        	branchPayService.updatePayForSync(branchId, reservation.getOrderId(), reservationId, newReservationId);
        }
        
        return result;

    }    
 
    /**
     * (지점의) 특정 예약 추가
     * @param branchId
     * @param reservation
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}/reservations", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    Reservation postBranchReservationAdd(@PathVariable String branchId, @PathVariable String orderId, @RequestBody Reservation reservation) {
        // 외부 아이디 생성
        String reservationId = UUID.randomUUID().toString();
        
        reservation.setOrderId(orderId);

        System.out.println("==========================reservationId : "+reservationId);
        // 예약 상태 : 확정(20)
        reservation.setReservationStatus(Constants.ReservationStatusType.CONFIRMED.getValue());

        branchReservationService.insertReservation(branchId, reservationId, reservation);

        Reservation insertedReservation = branchReservationService.selectReservation(branchId, reservationId);

        return insertedReservation;

    }
    
    /**
     * (지점의) 특정 예약 삭제
     * @param branchId
     * @param reservationId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/orders/{orderId}/reservations/{reservationId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchReservation(@PathVariable String branchId, @PathVariable String orderId, @PathVariable String reservationId, Reservation reservation) {

        return branchReservationService.deleteReservation(branchId, reservationId, reservation);
        					
    }
    
    /**
     * (지점의) 특정 예약 삭제
     * @param branchId
     * @param reservationId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/reservations/{reservationId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    int deleteBranchReservation(@PathVariable String branchId, @PathVariable String reservationId, Reservation reservation) {
    	
        return branchReservationService.deleteReservation(branchId, reservationId, reservation);
        					
    }

    /**************************************************************************************/


    /**
     * (지점의 특정기간) 회원 출입 목록 조회
     * @param branchId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/entries", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<Entry> getBranchMemberEntryList(@PathVariable String branchId,
                                         @RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate,
                                         @RequestParam(required = false) String memberId,
                                         @RequestParam(required = false) String roomId,
                                         @RequestParam(required = false) String deskId,
                                         Page page) {

//        log.info(startDate);
//        log.info(endDate);

        List<Entry> memberEntryList = branchMemberService.selectMemberEntryList(branchId, startDate, endDate, memberId, roomId, deskId, page);

        return memberEntryList;

    }

    /**************************************************************************************/


    /**
     * (지점의) 직업 / 학교 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/schools", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<String> getBranchSchoolList(@PathVariable String branchId) {

        List<String> schoolList = branchMemberService.selectSchoolList(branchId);

        return schoolList;

    }

    /**************************************************************************************/
    
    /**
     * (지점의) 좌석 이름 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/design/room/name", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<String> getBranchRoomNameList(@PathVariable String branchId) {

        List<String> roomNameList = branchDesignService.selectRoomNameList(branchId);
        
        return roomNameList;

    }

    /**************************************************************************************/
    
    
    /**
     * (지점의) 좌석 이름 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/branch/{branchId}/design/desk/name", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    List<String> getBranchDeskNameList(@PathVariable String branchId) {

        List<String> deskNameList = branchDesignService.selectDeskNameList(branchId);
        
        return deskNameList;

    }

    /**************************************************************************************/
    
    //알림 관리 SMS 전송
    @RequestMapping(value = "/branch/{branchId}/notifications", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    ResponseEntity<?> postNotificationSmsResend(@PathVariable String branchId, @RequestBody Notification notification) {
    	
    		smsService.sendSms(branchId, notification.getUserId(), "admin", notification.getFromNumber(), "user", notification.getToNumber(), notification.getMsg());

        	//for(Notification n: notification) {
            	//System.out.println("key="+n.getCmid()+" / value="+n.getMsg());
        		//smsService.sendSms(branchId, n.getUserId(), "admin", n.getFromNumber(), "user", n.getToNumber(), n.getMsg());
            //}
    	
    	return new ResponseEntity<>("{\"RESULT\" : \"SUCCESS\"}", HttpStatus.OK);
    }

    // 재전송
    @RequestMapping(value = "/branch/{branchId}/notifications/resend", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    ResponseEntity<?> postNotificationResend(@PathVariable String branchId, @RequestBody List<Notification> notification) {
    	
    	for(Notification n: notification) {
        	//System.out.println("key="+n.getCmid()+" / value="+n.getMsg());
    		smsService.sendSms(branchId, n.getUserId(), "admin", n.getFromNumber(), "user", n.getToNumber(), n.getMsg());
        }
    	
    	return new ResponseEntity<>("{\"RESULT\" : \"SUCCESS\"}", HttpStatus.OK);
    }
    
    // 퇴실처리 
    @RequestMapping(value = "/branch/{branchId}/reservations/checkout", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    ResponseEntity<?> postReservationCheckout(@PathVariable String branchId, @RequestBody List<Reservation> reservationCheckout) {
    	//System.out.println("==============================================================="+reservationCheckout);
    	for(Reservation n: reservationCheckout) {
        	//System.out.println("key="+n.getCmid()+" / value="+n.getMsg());
    		branchReservationService.updateReservationCheckout(branchId, n.getReservationId(), n);
        }
    	
    	return new ResponseEntity<>("{\"RESULT\" : \"SUCCESS\"}", HttpStatus.OK);
    }
    
    
    
    /**************************************************************************************/
    @RequestMapping(value = "/branch/{branchId}/alimTalk", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public
    void sendAlimTalk(@PathVariable String branchId, @RequestBody AlimTalk alimtalk) {
    	//branchId = "95060e0d-f234-4021-9cf0-7b78db725447";
    	alimtalk.setBranchId(branchId);

    	alimTalkService.send(alimtalk, AlimTalkService.getType("PAY"));
    	
    }
    
    
    @RequestMapping(value = "/{branchId}/seatReservationList", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")    
     public List<SeatReservation> selectSeatReservationList(@PathVariable String branchId, ModelMap model) {    
   	  
   	  try {
   		  
   		  List<SeatReservation> SeatReservationList = branchSeatReservationService.selectSeatReservationList(branchId);
   		  
   		  model.addAttribute("roomTypes", Constants.RoomType.getList());
   		  model.addAttribute("genderTypes", Constants.GenderType.getList());
   		  model.addAttribute("guestTypes", Constants.GuestTypes.getList());
   		  
   		  return SeatReservationList;
   		  
   	  } catch(EmptyResultDataAccessException e) {
   		  
   		  e.printStackTrace();
   		  
   	  }
         
   	  model.addAttribute("roomTypes", Constants.RoomType.getList());
   	  //model.addAttribute("cmpRouteTypes", Constants.cmpRouteType.getList());
   	  model.addAttribute("guestTypes", Constants.GuestTypes.getList());
   	  model.addAttribute("genderTypes", Constants.GenderType.getList());
   	  
   	  return null;
         
     }
     
    
     //좌석예약 추가 (테스트용 컬럼 속성 변경)
    // alter table app_seat_application  MODIFY appId varchar(45) null;
    // alter table app_seat_application  MODIFY role varchar(45) null;
    // alter table app_seat_application  MODIFY no varchar(45) null;
    // alter table app_seat_application  MODIFY memberId varchar(45) null;
    // alter table app_seat_application add column applicationId varchar(45) null;
     @RequestMapping(value = "/{branchId}/seatReservationList", method = RequestMethod.POST)
     @PreAuthorize("hasAuthority(#branchId)")
     public SeatReservation postSeatReservation(@PathVariable String branchId, @RequestBody SeatReservation seatReservation) {
   	  
   	    // 좌석예약 고유ID 생성 
   	    String applicationId = UUID.randomUUID().toString();
   	    
   	    seatReservation.setApplicationId(applicationId);
   	    
   	    branchSeatReservationService.insertedSeatReservation(seatReservation);
   	    
   	    
   	    SeatReservation insertedSeatReservation = branchSeatReservationService.selectSeatReservation(seatReservation.getBranchId(), seatReservation.getApplicationId());
   	    return insertedSeatReservation;

     }    
       
     //좌석예약 수정 
     @RequestMapping(value = "/{branchId}/seatReservationList/{applicationId}", method = RequestMethod.PUT)
     @PreAuthorize("hasAuthority(#branchId)")
     public int putSeatReservation(@PathVariable String branchId, @PathVariable String applicationId, @RequestBody SeatReservation SeatReservation) {
    	 
         return branchSeatReservationService.updateSeatReservation(applicationId, SeatReservation);

     }
     
     // 좌석예약  삭제
     @RequestMapping(value = "/{branchId}/seatReservationList/{applicationId}", method = RequestMethod.DELETE)
     @PreAuthorize("hasAuthority(#branchId)")
     public int deleteBranchSeatReservation(@PathVariable String branchId, @PathVariable String applicationId, SeatReservation SeatReservation) {
    	 
   	  return branchSeatReservationService.deleteSeatReservation(branchId, applicationId, SeatReservation);

     }
     
     ////////////////////////학부모 안심 서비스
     @RequestMapping(value = "/{branchId}/safeServiceList", method = RequestMethod.GET)
     @PreAuthorize("hasAuthority(#branchId)")    
      public List<SafeService> selectSafeServiceList(@PathVariable String branchId, ModelMap model) {    
    	  
    	  try {
    		  
    		  //List<SeatReservation> SeatReservationList = branchSeatReservationService.selectSeatReservationList(branchId);
    		  List<SafeService> SafeServiceList = branchSafeServiceService.selectSafeServiceList(branchId);

    		  
    		  return SafeServiceList;
    		  
    	  } catch(EmptyResultDataAccessException e) {
    		  
    		  e.printStackTrace();
    		  
    	  }
          
    	  model.addAttribute("statusTypes", Constants.StatusType.getList());
    	  
    	  return null;
          
      }
     //학부모 안심서비스 수정
     @RequestMapping(value = "/{branchId}/safeServiceList/{id}", method = RequestMethod.PUT)
     @PreAuthorize("hasAuthority(#branchId)")
     public int postSafeService(@PathVariable String branchId, @RequestBody SafeService safeService) {

   	    return branchSafeServiceService.updateSafeService(branchId, safeService);

     }
     
     
     //학부모 안심서비스 인증
     @RequestMapping(value = "/branch/{branchId}/parentsSafeService", method = RequestMethod.POST)
     @PreAuthorize("hasAuthority(#branchId)")
     public
     ResponseEntity<?> updateParentsSafeService(@PathVariable String branchId, @RequestBody SafeService safeService) {
     	
 		List<String> smscertify = appSmsCertify.chkAuthNum(safeService.getStudentTel(), safeService.getAuthNum());
	
		if (smscertify.size() > 0) {
			safeService.setStatus(20); //완료
			int result = branchSafeServiceService.updateSafeService(branchId, safeService);
			
//				if (result != 0) {
//					branchSafeServiceService.insertParentsSafeService(safeService);	
//				} 
			
			if (result != 0) {
				List<AppClientMember> studentList = appClientMemberService.selectAppStudentListById(safeService.getStudentId(), safeService.getStudentTel());
				if (studentList.size() > 0) {
					appClientMemberService.updateBranchStudentMemberFortelParent(safeService.getStudentNo(), 10, safeService.getParentsTel());	
				}
				
    			
    			List<AppClientMember> parentsList = appClientMemberService.selectAppMemberList(safeService.getParentsNo(), 20); //입력된 정보와 부모정보 확인
        		    
    			
    			if(parentsList.get(0).getMainChildNo() == null) { //주자식 정보 없으면 넣어줌-> 추후에는 코디에서 처리
    				appClientMemberService.updateAppParentsMemberMainChild(safeService.getParentsNo(), studentList.get(0).getNo());
				}
			}
			
		} else {
			return new ResponseEntity<>("{\"RESULT\" : \"SUCCESS\"}", HttpStatus.UNAUTHORIZED);
		}

     	
     	return new ResponseEntity<>("{\"RESULT\" : \"SUCCESS\"}", HttpStatus.OK);
     }
     
     
     //학부모 안심서비스 인증
     @RequestMapping(value = "/parentsSafeService/sendAuthNum", method = RequestMethod.POST)
     public
     ResponseEntity<?> sendSmsParentsSafeService(@RequestParam String tel) {
     	
    	 Random random = new Random();
			String authNum = String.valueOf(random.nextInt(1000000) + 100000);			
			String description = "학부모-자식인증";

			int result = appSmsCertify.insertSmsDB(tel, authNum, description, 30);

			if (result == 0) {
				
			} else {
				String msg = String.format("[%s] 학부모 안심서비스 인증번호는 [ %s ]입니다.", "CNT 스터디센터", authNum);
				smsService.sendSms("", "", "CNT 스터디센터", "1800-0109", "고객", tel, msg);
			}
			
     	
     	return new ResponseEntity<>("{\"RESULT\" : \"SUCCESS\"}", HttpStatus.OK);
     } 
    
     //학교코드 추가 (value="branchId", required=false)
     @RequestMapping(value = "/schoolCode", method = { RequestMethod.POST, RequestMethod.GET })
     public ApiResult schoolCode(ModelMap model, @RequestParam String branchId, @RequestParam String branchNm, 
    		 					@RequestParam String codeNm ) throws JsonProcessingException {

    	 ModelMapper modelMapper = new ModelMapper();
    	 	 
    	 ApiResult apiResult = null;
    	 ApiResult.header header = modelMapper.map("", ApiResult.header.class);

    	 codeNm = codeNm.replaceAll(" ", ""); 
    	     	 
    	 
    	 //codeNm 중복체크 (useYn = true)
    	 List<CommonCode> codeList = branchCommonCodeService.selectCommonCodeList(branchId, "schoolGrade");
    	 for (CommonCode c : codeList) {    		 
    		 if (c.getCodeNm().contains(codeNm)) {
    			 header.setResult_code("9999");
    			 header.setResult_message("이미 존재하는 코드명입니다.");
    			 apiResult = new ApiResult(null, header);
    			 //String result = om.writeValueAsString(apiResult);
    			 
    			 return apiResult;
    		 } 
    	 }
    	 
    	 
    	 Integer code =  branchCommonCodeService.selectCommonCode(branchId, Constants.SCHOOL_GRADE); 
    	 code++;

    	 String strCode = code.toString();
    	 
    	 if (strCode.length() == 1) {
    		 strCode = "000" + strCode;
    	 } 
    	 else if (strCode.length() == 2) {
    		 strCode = "00" + strCode;
    	 }
    	 else if (strCode.length() == 3) {
    		 strCode = "0" + strCode;
    	 }
    	 
    	 
    	 CommonCode commonCode = new CommonCode();
    	 commonCode.setBranchId(branchId);
    	 commonCode.setBranchNm(branchNm);
    	 commonCode.setCodeType(Constants.SCHOOL_GRADE);
    	 commonCode.setCode(strCode);
    	 commonCode.setCodeNm(codeNm);
    	 commonCode.setDescription("학년");
    	 
    	 List<CommonCode> schoolGradeList = branchCommonCodeService.insertCommonCode(commonCode);
    	 
    	 
    	 header.setResult_code("0000");
		 header.setResult_message("코드 등록성공!");
		 
		 apiResult = new ApiResult(schoolGradeList, header);

		 
		 
		 return apiResult;

    	 
     }
     
     

     
}
