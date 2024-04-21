package cn.donting.web.os.core.server;

import cn.donting.web.os.api.user.User;
import cn.donting.web.os.core.domain.DigestAuthInfo;
import cn.donting.web.os.core.entity.OsSetting;
import cn.donting.web.os.core.entity.OsUser;
import cn.donting.web.os.core.properties.ServerProperties;
import cn.donting.web.os.core.repository.OsSettingRepository;
import cn.donting.web.os.core.repository.OsUserRepository;
import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.service.api.UserApi;
import cn.donting.web.os.core.util.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ServletRequestPathUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WebOsJettyServer implements ApplicationRunner, Servlet {

    private static final String LOGIN_USER_SESSION_KEY = "os-login-user";
    private static final String NONCE_SESSION_KEY = "login-nonce";
    private static final int NONCE_EXPIRED_MINUTES = 30;

    @Autowired
    DispatcherServlet dispatcherServlet;
    @Autowired
    UserApi userApi;
    @Autowired
    OsUserRepository osUserRepository;
    @Autowired
    OsSettingRepository osSettingRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        long l = System.currentTimeMillis();
        ServerProperties serverProperties = new ServerProperties();
        Server server = new Server(serverProperties.getPort());

        SessionHandler sessions = new SessionHandler();
        ErrorHandler errorHandler = new OsErrorHandler();

        ServletContextHandler contextHandler = new ServletContextHandler(server, "/", false, false);
        ServletHolder servletHolder = new ServletHolder(this);
        contextHandler.addServlet(servletHolder, "/");
        contextHandler.setSessionHandler(sessions);
        contextHandler.setErrorHandler(errorHandler);

        server.setHandler(contextHandler);
        server.start();
        long end = System.currentTimeMillis();
        log.info("jetty start:[{}]  {}ms", serverProperties.getPort(), end - l);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        dispatcherServlet.init(config);
    }

    @Override
    public ServletConfig getServletConfig() {
        return dispatcherServlet.getServletConfig();
    }

    /**
     * {@link ServletRequestPathUtils#parseAndCache}
     *
     * @param req the <code>ServletRequest</code> object that contains the client's request
     * @param res the <code>ServletResponse</code> object that contains the servlet's response
     * @throws ServletException
     * @throws IOException
     * @see HttpServletMapping / 首页转发
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest servletRequest = (HttpServletRequest) req;
        HttpServletResponse servletResponse = (HttpServletResponse) res;
        String requestURI = servletRequest.getRequestURI();

        User loginUser = getLoginUser(servletRequest);
        if (loginUser == null) {
            sendWWAuthenticate(servletRequest, servletResponse);
            return;
        }
        if (requestURI.equals("/")) {
            //去首页桌面
            OsSetting osSetting = osSettingRepository.findByKey(OsSetting.DEF_DESKTOP_WAP_ID);
            if (osSetting != null) {
                String desktopUrl = osSetting.getValue();
                ((HttpServletResponse) res).sendRedirect(desktopUrl);
                return;
            }
            //无桌面
            res.getOutputStream().write("无桌面".getBytes(StandardCharsets.UTF_8));
            ((HttpServletResponse) res).setStatus(404);
            ((HttpServletResponse) res).setContentType("text/html; charset=utf-8");
            return;
        }
        String[] split = requestURI.split("/");
        if (split.length < 1) {
            return;
        }
        String wapId = split[1];
        Request request = (Request) req;
        request.setContextPath("/" + wapId);
        dispatcherServlet.service(req, res);
    }

    @Override
    public String getServletInfo() {
        return dispatcherServlet.getServletInfo();
    }

    @Override
    public void destroy() {
        dispatcherServlet.destroy();
    }

    private void sendWWAuthenticate(HttpServletRequest request, HttpServletResponse response) {
        String nonce = UUID.randomUUID().toString();
        response.setHeader("WWW-Authenticate", "Digest qop=\"auth\",nonce=\"" + nonce + "\"");
        response.setStatus(401);
        request.getSession().setAttribute("nonce", nonce);
    }

    /**
     * 获取登陆用户
     *
     * @param request
     * @return
     */
    private User getLoginUser(HttpServletRequest request) {
        String loginNonce = (String) request.getSession().getAttribute("nonce");

        // 检查请求头中是否有Authorization头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        DigestAuthInfo authInfo = getAuthInfoObject(authHeader);
        Optional<OsUser> userOp = osUserRepository.findById(authInfo.getUsername());
        if (!userOp.isPresent()) {
            //用户不存在
            return null;
        }
        OsUser osUser = userOp.get();
        /*
         * 生成 response 的算法：
         *  response = MD5(MD5(username:realm:password):nonce:nc:cnonce:qop:MD5(<request-method>:url))
         */
        String HA1 = DigestUtil.MD5(authInfo.getUsername() + ":" + authInfo.getRealm() + ":" + osUser.getPassword());
        String HD = String.format(authInfo.getNonce() + ":" + authInfo.getNc() + ":" + authInfo.getCnonce() + ":"
                + authInfo.getQop());
        String HA2 = DigestUtil.MD5(request.getMethod() + ":" + authInfo.getUri());
        String responseValid = DigestUtil.MD5(HA1 + ":" + HD + ":" + HA2);
        if (!responseValid.equals(authInfo.getResponse())) {
            //前端加密错误
            return null;
        }
        //代表此次请求是登陆
        if (loginNonce != null) {
            osUser.setNonce(loginNonce);
            //30分钟
            osUser.setNonceExpiredTime(System.currentTimeMillis() + NONCE_EXPIRED_MINUTES * 60 * 1000);
            request.getSession().removeAttribute("nonce");
            osUserRepository.save(osUser);
            log.info("login:[{}]", osUser.getUsername());
            return osUser;
        }
        String nonce = osUser.getNonce();
        if (authInfo.getNonce().equals(nonce)) {
            //登陆过期(长时间未操作)
            if (System.currentTimeMillis() > osUser.getNonceExpiredTime()) {
                log.info("username 登陆过期(长时间未操作):[{}]", osUser.getUsername());
                return null;
            }
            log.info("username nonce续期:[{}]", osUser.getUsername());
            osUserRepository.updateNonceExpiredTimeByUsername(System.currentTimeMillis() + NONCE_EXPIRED_MINUTES * 60 * 1000, osUser.getUsername());
            return osUser;
        }
        //登陆过期
        log.info("username 登陆过期:[{}]", osUser.getUsername());
        return null;
    }

    /**
     * 该方法用于将 Authorization 请求头的内容封装成一个对象。
     * <p>
     * Authorization 请求头的内容为：
     * Digest username="aaa", realm="no auth", nonce="b2b74be03ff44e1884ba0645bb961b53",
     * uri="/BootDemo/login", response="90aff948e6f2207d69ecedc5d39f6192", qop=auth,
     * nc=00000002, cnonce="eb73c2c68543faaa"
     */
    public static DigestAuthInfo getAuthInfoObject(String authStr) {
        if (authStr == null || authStr.length() <= 7)
            return null;

        if (authStr.toLowerCase().indexOf("digest") >= 0) {
            // 截掉前缀 Digest
            authStr = authStr.substring(6);
        }
        // 将双引号去掉
        authStr = authStr.replaceAll("\"", "");
        DigestAuthInfo digestAuthObject = new DigestAuthInfo();
        String[] authArray = new String[8];
        authArray = authStr.split(",");
        // System.out.println(java.util.Arrays.toString(authArray));
        for (int i = 0, len = authArray.length; i < len; i++) {
            String auth = authArray[i];
            String key = auth.substring(0, auth.indexOf("=")).trim();
            String value = auth.substring(auth.indexOf("=") + 1).trim();
            switch (key) {
                case "username":
                    digestAuthObject.setUsername(value);
                    break;
                case "realm":
                    digestAuthObject.setRealm(value);
                    break;
                case "nonce":
                    digestAuthObject.setNonce(value);
                    break;
                case "uri":
                    digestAuthObject.setUri(value);
                    break;
                case "response":
                    digestAuthObject.setResponse(value);
                    break;
                case "qop":
                    digestAuthObject.setQop(value);
                    break;
                case "nc":
                    digestAuthObject.setNc(value);
                    break;
                case "cnonce":
                    digestAuthObject.setCnonce(value);
                    break;
            }
        }
        return digestAuthObject;
    }
}
