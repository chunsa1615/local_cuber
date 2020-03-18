package kr.co.cntt.scc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.model.User;
import kr.co.cntt.scc.service.BranchService;
import kr.co.cntt.scc.service.UserService;
import kr.co.cntt.scc.util.InternalServerError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

/**
 * 관리자 컨트롤러
 * Created by jslivane on 2016. 5. 3..
 */
@Controller
@RequestMapping(value = "/admin")
@PreAuthorize("hasAuthority('admin')")
public class AdminController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private UserService userService;

    private static final String VIEW_NAME_OF_LAYOUT = "layout";

    private ModelAndView modelView(ModelMap model, String title, String view) {
    	model.addAttribute("brand", Constants.BRAND_FULL);
        model.addAttribute("title", title);
        model.addAttribute("view", view);

        // 지점 목록 조회
        List<Branch> branches = branchService.selectBranchList();
        model.addAttribute("branches", branches);

        return new ModelAndView(VIEW_NAME_OF_LAYOUT);
    }


    @RequestMapping("/")
    public ModelAndView main(ModelMap model) {

        return modelView(model, "관리자 메인", "admin_main");

    }


    @RequestMapping("/branches/")
    public ModelAndView branches(ModelMap model) {

    	
        try {
            ObjectMapper om = new ObjectMapper();

            // 지점 목록 조회
            List<Branch> branches = branchService.selectBranchList();
            model.addAttribute("branchListJSON", om.writeValueAsString(branches));
            model.addAttribute("branchTypeMapJSON", om.writeValueAsString(Constants.BranchType.getMap()));
            model.addAttribute("branchTypes", om.writeValueAsString(Constants.BranchType.getList()));
            List<Branch.AdOrNotice> branchesAdOrNotice = branchService.getAdOrNoticeList(null);
            model.addAttribute("branchesAdOrNoticeJSON", om.writeValueAsString(branchesAdOrNotice));
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        
        return modelView(model, "지점 관리", "admin_branches");

    }

    @RequestMapping("/users/")
    public ModelAndView users(ModelMap model) {

        try {
            ObjectMapper om = new ObjectMapper();

            // 지점 목록 조회
            List<Branch> branchList = branchService.selectBranchList();
            model.addAttribute("branchListJSON", om.writeValueAsString(branchList));

            // 사용자 조회
            List<User> userList = userService.selectUserList();
            model.addAttribute("userListJSON", om.writeValueAsString(userList));


        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }

        return modelView(model, "사용자 관리", "admin_users");

    }

    /******************************************************************************************************************/

    /**
     * 지점 목록 조회
     * @return
     */
    @RequestMapping(value = "/api/branches", method = RequestMethod.GET)
    public @ResponseBody
    List<Branch> getBranchList() {

        List<Branch> branchList = branchService.selectBranchList();

        return branchList;

    }

    /**
     * 새 지점 추가
     * @param branch
     * @return
     */
    @RequestMapping(value = "/api/branches", method = RequestMethod.POST)
    public @ResponseBody
    Branch postBranch(@RequestBody Branch branch) {
        // 외부 아이디 생성
        String branchId = UUID.randomUUID().toString();

        branchService.insertBranch(branchId, branch);

        Branch insertedBranch = branchService.selectBranch(branchId);

        return insertedBranch;

    }

    /**
     * 특정 지점 조회
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/api/branches/{branchId}", method = RequestMethod.GET)
    public @ResponseBody
    Branch getBranch(@PathVariable String branchId) {

        Branch branch = branchService.selectBranch(branchId);

        return branch;

    }

    /**
     * 특정 지점 수정
     * @param branchId
     * @param member
     * @return
     */
    @RequestMapping(value = "/api/branches/{branchId}", method = RequestMethod.PUT)
    public @ResponseBody
    int putBranch(@PathVariable String branchId, @RequestBody Branch branch) {

        return branchService.updateBranch(branchId, branch);

    }

    /**
     * 특정 지점 삭제
     * @param branchId
     * @return
     */
    @RequestMapping(value = "/api/branches/{branchId}", method = RequestMethod.DELETE)
    public @ResponseBody
    int deleteBranch(@PathVariable String branchId) throws InternalServerError {

        return branchService.deleteBranch(branchId);

    }

    /******************************************************************************************************************/

    /**
     * 사용자(계정) 목록 조회
     * @return
     */
    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public @ResponseBody
    List<User> getUserList() {

        List<User> userList = userService.selectUserList();

        return userList;

    }

    /**
     * 새 사용자(계정) 추가
     * @param user
     * @return
     */
    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public @ResponseBody
    User postUser(@RequestBody User user) {
        // 외부 아이디 생성
        String userId = UUID.randomUUID().toString();
        userService.insertUser(userId, user);

        User insertedUser = userService.selectUser(userId);

        return insertedUser;

    }

    /**
     * 특정 사용자(계정) 조회
     * @param userId
     * @return
     */
    @RequestMapping(value = "/api/users/{userId}", method = RequestMethod.GET)
    public @ResponseBody
    User getUser(@PathVariable String userId) {

        User user = userService.selectUser(userId);

        return user;

    }

    /**
     * 특정 사용자(계정) 수정
     * @param userId
     * @param member
     * @return
     */
    @RequestMapping(value = "/api/users/{userId}", method = RequestMethod.PUT)
    public @ResponseBody
    int putUser(@PathVariable String userId, @RequestBody User user) {

        return userService.updateUser(userId, user);

    }

    /**
     * 특정 사용자(계정) 삭제
     * @param userId
     * @return
     */
    @RequestMapping(value = "/api/users/{userId}", method = RequestMethod.DELETE)
    public @ResponseBody
    int deleteUser(@PathVariable String userId) throws InternalServerError {

        return userService.deleteUser(userId);

    }
    
    /******************************************************************************************************************/


}
