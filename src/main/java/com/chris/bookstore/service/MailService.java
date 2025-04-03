package com.chris.bookstore.service;

import com.chris.bookstore.dto.DataMailDTO;
import com.chris.bookstore.dto.request.EmailVerifyRequest;
import com.chris.bookstore.dto.response.AuthenticationResponse;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.enums.Privilege;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public MailService(JavaMailSender mailSender,
                       UserRepository userRepository){
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public void sendHtmlMail(DataMailDTO dataMail) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            String htmlContent = buildHtmlContent(dataMail.getProps());
            helper.setTo(dataMail.getTo());
            helper.setSubject(dataMail.getSubject());
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new MessagingException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String buildHtmlContent(Map<String, Object> props) {
        String fullName = (String) props.get("fullName");
        String code = (String) props.get("code");
        String id = (String) props.get("id");
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: 'Helvetica Neue', Arial, sans-serif; background-color: #f0f4f8; margin: 0; padding: 20px; }");
        html.append(".container { background-color: #ffffff; padding: 30px; margin: 0 auto; max-width: 650px; border-radius: 8px; box-shadow: 0 4px 15px rgba(0, 0, 0); position: relative; }");
        html.append(".border-wrapper { background: linear-gradient(135deg, #2b5876, #4e4376); padding: 2px 10px 5px 2px; border-radius: 12px; margin: 0 auto; max-width: 655px; }");
        html.append("h1 { color: #1a3c5e; font-size: 28px; margin-bottom: 20px; border-bottom: 2px solid #4e4376; padding-bottom: 10px; }");
        html.append("p { font-size: 16px; color: #333333; line-height: 1.6; }");
        html.append(".code { font-size: 24px; color: #ffffff; font-weight: bold; background-color: #2b5876; padding: 8px 15px; border-radius: 5px; display: inline-block; margin: 10px 0; }");
        html.append(".header { text-align: center; margin-bottom: 30px; }");
        html.append("img.logo { max-width: 220px; height: auto; display: block; margin: 0 auto; }");
        html.append(".footer { font-size: 14px; color: #666666; margin-top: 20px; text-align: center; border-top: 1px solid #e0e0e0; padding-top: 15px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='border-wrapper'>");
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<img src='https://res.cloudinary.com/dmi7eg34g/image/upload/v1742891012/Bookstore-transparent_husw59.png' alt='Bookstore Logo' class='logo' style='max-width: 220px; height: auto; display: block; margin: 0 auto;'>");
        html.append("</div>");
        html.append("<h1>Email Verification</h1>");
        html.append("<p>Hello ").append(fullName).append(",</p>");
        html.append("<p>Thank you for joining our community! To complete your registration, please use the verification code below:</p>");
        html.append("<p class='code'>").append(code).append("</p>");
        html.append("<p>This verification code will remain valid for 24 hours.</p>");
        html.append("<p>If you did not initiate this registration, please disregard this message.</p>");
        html.append("<div class='footer'>");
        html.append("Best regards,<br>The Bookstore Team");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    public void verifyEmail(EmailVerifyRequest request) {
        User userChecker = userRepository.findByVerificationCode(request.getVerifyCode());
        if(userChecker == null)
            throw new AppException(ErrorCode.VERIFY_EMAIL_FAILED);

        userChecker.setVerified(true);
        userChecker.setVerificationExpiry(null);
        userChecker.setVerificationCode(null);

        userChecker.getPrivileges().clear();
        userChecker.getPrivileges().addAll(Set.of(
                Privilege.GET_ITEMS,
                Privilege.ADD_TO_CART,
                Privilege.REMOVE_FROM_CART,
                Privilege.PLACE_ORDER,
                Privilege.CREATE_SHOP,
                Privilege.ADD_ADDRESSES,
                Privilege.UPDATE_ADDRESSES,
                Privilege.DELETE_ADDRESSES,
                Privilege.RATE_SHOP,
                Privilege.FOLLOW_SHOP,
                Privilege.GET_ADDRESSES
        ));

        userRepository.save(userChecker);
    }
}