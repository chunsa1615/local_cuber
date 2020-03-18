package kr.co.cntt.scc.controller;

import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.model.Branch;
import kr.co.cntt.scc.service.BranchService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by jslivane on 2016. 7. 7..
 */
@Controller
@Slf4j
public class PublicController {

    @Autowired
    private BranchService branchService;
    

    private static final String VIEW_NAME_OF_PUBLIC_LAYOUT = "layout"; // "layout_public";

    private ModelAndView modelView(ModelMap model, String title, String view) {
    	model.addAttribute("brand", Constants.BRAND_FULL);
        model.addAttribute("title", title);
        model.addAttribute("view", view);

        // 지점 목록 조회
        List<Branch> branches = branchService.selectBranchList();
        model.addAttribute("branches", branches);

        return new ModelAndView(VIEW_NAME_OF_PUBLIC_LAYOUT);
    }

    @RequestMapping("/home")
    public ModelAndView home(ModelMap model) {

        return modelView(model, "메인", "main");

    }
    

    @RequestMapping("/test")
    @ResponseBody
    public String test(ModelMap model) {
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			socket = new Socket("192.168.170.191", 9005);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println("hello world");
			String response = in.readLine();
			System.out.println(response);
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
		return null;
    }
    
//    @RequestMapping("/admin")
//    public ModelAndView main(ModelMap model) {
//
//        return modelView(model, "메인", "admin_main");
//    	
//
//    }

    @RequestMapping(value = { "/" , "/login" })
    public ModelAndView login(ModelMap model) {

    	
        return modelView(model, "로그인", "public_login");

    }

    @RequestMapping("/b/{idOrName}")
    public ModelAndView branchPublicHome(ModelMap model, @PathVariable String idOrName) {

        // 지점 조회 (숫자아아디 또는 지점명)
        Branch branch = branchService.selectBranchByIdOrName(idOrName);

        if(branch == null) {
            branch = branchService.selectBranch(idOrName);

        }

        model.addAttribute("branch", branch);

        return modelView(model, branch.getName(), "public_branch");

    }

    @RequestMapping("/about")
    public ModelAndView about(ModelMap model) {

        return modelView(model, "안내", "about");

    }

}
