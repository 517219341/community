package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.model.IModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    //返回设置界面
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    //上传修改的头像
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error","你还没有选择图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error","文件格式不正确!");
            return "/site/setting";
        }

        //生成随机的文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败:" + e.getMessage());
            throw  new RuntimeException("上传文件失败，服务器异常", e);
        }
        // 更新当前用户头像的路径(web路径)
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    //将头像输出到前端
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream() ;
                FileInputStream fis = new FileInputStream(fileName);
                ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取失败" + e.getMessage());
        }
    }

    //修改用户密码
    @RequestMapping(path = "/password", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String mkSurePassword, Model model) {
        if (oldPassword == null) {
            model.addAttribute("oldPasswordMsg", "请输入原密码!");
            return "/site/setting";
        } else if (StringUtils.isBlank(oldPassword)) {
            model.addAttribute("oldPasswordMsg", "请输入原密码!");
            return "/site/setting";
        }
        if (newPassword == null) {
            model.addAttribute("newPasswordMsg", "请输入新密码!");
            return "/site/setting";
        } else if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newPasswordMsg", "请输入新密码!");
            return "/site/setting";
        }

        if (mkSurePassword == null) {
            model.addAttribute("mkSurePasswordMsg", "请输入确认密码!");
            return "/site/setting";
        } else if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("mkSurePasswordMsg", "请输入确认密码!");
            return "/site/setting";
        }

        if (!mkSurePassword.equals(newPassword)) {
            model.addAttribute("mkSurePasswordMsg", "请输入正确的确认密码!");
            return "/site/setting";
        }

        User user = hostHolder.getUser();
        String md5OldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!md5OldPassword.equals(user.getPassword())) {
            model.addAttribute("oldPasswordMsg", "原密码输入错误!");
            return "/site/setting";
        }
        //修改为新密码
        userService.updatePassword(user.getId(), newPassword, user.getSalt());

        return "redirect:/index";
    }

    // 个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }

        // 发送消息
        model.addAttribute("user",user);
        // 点赞
        int count = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",count);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER );
        model.addAttribute("followeeCount",followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        // 是否关注
        if (hostHolder == null) {
            model.addAttribute("hasFollowed", false);
        } else {
            boolean hasFollow = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
            model.addAttribute("hasFollowed", hasFollow);
        }

        return "/site/profile";

    }

}
