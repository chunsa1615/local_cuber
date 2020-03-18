package kr.co.cntt.scc.controller;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.Constants.EntryType;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.BranchMember;
import kr.co.cntt.scc.model.Entry;
import kr.co.cntt.scc.model.Reservation;
import kr.co.cntt.scc.service.*;
import kr.co.cntt.scc.util.DateUtil;
import kr.co.cntt.scc.util.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * EntryController
 *
 * Created by jslivane on 2016. 6. 29..
 */
@Controller
@Slf4j
@RequestMapping("/entry")
@PreAuthorize("hasAuthority('entry')")
public class EntryController {
	
    @Autowired
    private BranchService branchService;

    @Autowired
    private BranchMemberService branchMemberService;

    //@Autowired
    //private BranchPayService branchPayService;

    @Autowired
    private BranchReservationService branchReservationService;

    @Autowired
    SmsService smsService;

    private static final String VIEW_NAME_OF_ENTRY_LAYOUT = "layout_entry";

    private ModelAndView modelView(ModelMap model, String title, String view) {
    	model.addAttribute("brand", Constants.BRAND_FULL);
        model.addAttribute("title", title);
        model.addAttribute("view", view);

        return new ModelAndView(VIEW_NAME_OF_ENTRY_LAYOUT);

    }


    @RequestMapping("/{branchId}")
    @PreAuthorize("hasAuthority(#branchId)")
    public ModelAndView entryMain(ModelMap model, @PathVariable String branchId) {

        // 지점 조회
        Branch branch = branchService.selectBranch(branchId);
        model.addAttribute("branch", branch);

        return modelView(model, "메인", "entry_main");

    }


    /******************************************************************************************************************/


    /**
     * (특정지점의 특정회원의) 새 출입기록 추가
     * @param branchId
     * @param entry
     * @return
     */
    @RequestMapping(value = "/api/b/{branchId}/entries", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority(#branchId)")
    public @ResponseBody
    List<Entry> postEntry(@PathVariable String branchId, @RequestBody @Valid Entry.Create entry) {

        // 회원 확인
        BranchMember member = branchMemberService.selectMemberByNo(branchId, entry.getNo());
        if (member == null) {
            throw new InternalServerError("Failed to find member");

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
        Reservation reservation = branchReservationService.selectCurrentReservationByMemberId(branchId, memberId);
        if (reservation == null) {
            throw new InternalServerError("Failed to find reservation");

        }

        EntryType entryType = Constants.getEntryType(entry.getEntryType());
        
        // 출입 기록
        //int result = branchMemberService.insertMemberEntry(branchId, memberId, entry.getEntryType(), pay);
        int result = branchMemberService.insertMemberEntry(branchId, memberId, entryType.getValue(), reservation);

        if (result == 0) {
            throw new InternalServerError("Failed to create entry");

        } else {

            // SMS 발송
            if (member.getSmsYes() == 1) {
	        	if(!StringUtils.isEmpty(member.getTelParent())) {
	                Branch branch = branchService.selectBranch(branchId);
	
	                //String msg = String.format("[%s(%s)] %s님이 %s에 %s하였습니다", Constants.BRAND_SHORT, branch.getName(), member.getName(), DateUtil.getCurrentTimeShortString(), entryType.getText());
	                String msg = String.format("[CNT%s] %s님이 %s에 %s하였습니다", branch.getName(), member.getName(), DateUtil.getCurrentTimeShortString(), entryType.getText());
	
	                smsService.sendSms(branchId, memberId,
	                        branch.getName(), branch.getTel(), member.getName(), member.getTelParent(), msg);
	
	            }
            }

        }

        // TODO : 출입 기록 완료 메시지 생성
        List<Entry> entryList = branchMemberService.selectRecentMemberEntryListByMemberId(branchId, memberId);

        return entryList;

    }

    /**
     * 특정 지점 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/api/branches/{branchId}", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority(#branchId)")
    public @ResponseBody
    Branch getBranch(@PathVariable String branchId) {

        Branch branch = branchService.selectBranch(branchId);

        return branch;

    }

    /******************************************************************************************************************/


}
