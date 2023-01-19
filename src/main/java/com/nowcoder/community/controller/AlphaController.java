package com.nowcoder.community.controller;

//import ch.qos.logback.core.model.Model;
import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot.";
    }

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getDate(){
        return alphaService.testDIGetAlphaDao();
    }


    //接收处理浏览器的请求，一般做法
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String val = request.getHeader(name);
            System.out.println(name + ":" + val);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Get请求,以用封装的功能解决

    //提交students页表单：/students?current=10&limit=20。
    @RequestMapping(path = "/students", method = RequestMethod.GET) //规定路径和访问型式只能是GET更加安全
    @ResponseBody
    //@RequestParam(),是对传入参数的规定。name确保传入参数正确赋值，required是规定是否必须提交，defaultValue是不传入参数的默认值
    //老师说的意思好像不写也能正确传参，只要名字对应即可。如：limit对limit。
    public String students(@RequestParam(name = "current", required = false, defaultValue = "1")int  current,
                          @RequestParam(name = "limit", required = false, defaultValue = "2")int limit){
        System.out.println(current);
        System.out.println(limit);
        return "students";
    }
    //student/123   让传入参数成为路径的一部分（另一种GET访问方式）
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)  //使用{}规定参数
    @ResponseBody
    //@PathVariable()路径变量注解
    public String getStudent(@PathVariable("id")int id){
        System.out.println(id);
        return "find a student by PathVariable（路径变量）.";
    }

    //POST请求,解决提交大量数据问题
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String postStudent(String name, int age){
        System.out.println(name + ":" + age);
        return "ADD SUCCESSFULLY";
    }

    //响应HTML数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    //不需要@ResponseBody，因为会返回相应的参数到对应的网页
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",20);
        mav.setViewName("/demo/teacher.html");
        return mav;
    }

    //另一种方式返回Model和View的值
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京");
        model.addAttribute("age",20);

        return "/demo/teacher";
    }

    //响应JSON数据，用于异步请求（当前网页不刷新，网页自己访问了服务器）
    //java对象 通过 JSON字符串 转换为 js对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody   //只有加上这个注解才能正确的返回JSON字符串
    public Map<String, Object> getEmp(){        //在编译时发现 @ResponseBody 和 Map<String, Object>底层会自动将返回值转换为JSON
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age",18);
        map.put("sal",8000.00);
        return map;
    }

    //对于多个数据采用List将数据传回去
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody   //只有加上这个注解才能正确的返回JSON字符串
    public List<Map<String, Object>> getEmps(){        //在编译时发现 @ResponseBody 和 Map<String, Object>底层会自动将返回值转换为JSON
        List<Map<String, Object>> list= new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age",18);
        map.put("sal",8000.00);
        list.add(map);

        map = new HashMap<>();
        map.put("name", "李四");
        map.put("age",20);
        map.put("sal",10000.00);
        list.add(map);

        map = new HashMap<>();
        map.put("name", "王五");
        map.put("age",30);
        map.put("sal",12000.00);
        list.add(map);

        return list;
    }

    //cookie实列

    @RequestMapping(path = "/cookie/set" , method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        //创建Cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置生效范围
        cookie.setPath("/community/alpha");
        //设置Cookie生存时间,让cookie不止存在内存（关闭浏览器就没了）
        cookie.setMaxAge(60 * 10);//单位 ：S
        //发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }
    @RequestMapping(path = "/cookie/get" , method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) { //获得相对应的Cookie
        System.out.println(code);
        return "get cookie";
    }

    @RequestMapping(path = "/session/set" , method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) { //SpringBoot，自动创建注入
        session.setAttribute("id", 1);
        session.setAttribute("name", "LiHua");
        return "set session";
    }

    @RequestMapping(path = "/session/get" , method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) { //SpringBoot，自动创建注入
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);

        return CommunityUtil.getJSONString(0, "操作成功，");
    }
}

