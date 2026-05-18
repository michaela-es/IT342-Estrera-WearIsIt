package edu.cit.estrera.wearisit.features.email;

import edu.cit.estrera.wearisit.infrastructure.security.jwt.JwtService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JwtService jwtService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base.url:http://localhost:8080}")
    private String appBaseUrl;

    @Async
    public void sendVerificationEmail(String to, String username) {

        String token = jwtService.generateVerificationToken(to);
        String verificationLink = appBaseUrl + "/api/email/verify?token=" + token;

        EmailTemplate template = EmailTemplate.builder()
                .title("Verify your Wearisit account")
                .heading("Welcome to Wearisit")
                .greeting("Hi " + username + ",")
                .bodyText("Thanks for signing up! Please verify your email address to activate your account.")
                .buttonText("Verify Email Address")
                .buttonLink(verificationLink)
                .footerNote("This link will expire in 24 hours.")
                .build();

        sendEmail(to, username, template);
    }

//    @Async
//    public void sendPasswordResetEmail(String to, String username) {
//        String token = jwtService.generatePasswordResetToken(to);
//        String resetLink = appBaseUrl + "/api/auth/reset-password?token=" + token;
//
//        EmailTemplate template = EmailTemplate.builder()
//                .title("Reset your Wearisit password")
//                .heading("Forgot your password?")
//                .greeting("Hello " + username + ",")
//                .bodyText("We received a request to reset your password. Click the button below to create a new password.")
//                .buttonText("Reset Password")
//                .buttonLink(resetLink)
//                .footerNote("This link will expire in 1 hour. If you didn't request this, you can safely ignore this email.")
//                .build();
//
//        sendEmail(to, username, template);
//    }

    private void sendEmail(String to, String username, EmailTemplate template) {
        try {
            log.info("Sending email to: {}", to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(template.getTitle());
            helper.setText(buildHtml(template), true);

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to build email for {}: {}", to, e.getMessage(), e);
        } catch (MailException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    private String buildHtml(EmailTemplate template) {
        String buttonHtml = "";
        if (template.getButtonLink() != null && template.getButtonText() != null) {
            buttonHtml = String.format("""
                <table cellpadding="0" cellspacing="0" width="100%%">
                  <tr>
                    <td align="left" style="padding:12px 0 28px;">
                      <a href="%s"
                         style="display:inline-block;background-color:#111827;color:#ffffff;font-size:15px;font-weight:600;text-decoration:none;padding:12px 32px;border-radius:6px;">
                        %s
                      </a>
                    </td>
                  </tr>
                </table>
                """, template.getButtonLink(), template.getButtonText());
        }

        String fallbackLinkHtml = "";
        if (template.getButtonLink() != null) {
            fallbackLinkHtml = String.format("""
                <hr style="border:0;border-top:1px solid #e5e7eb;margin:0 0 20px 0;"/>
                <p style="margin:0 0 8px;color:#6b7280;font-size:13px;">
                  If the button above does not work, copy and paste this URL into your web browser:
                </p>
                <p style="margin:0;word-break:break-all;">
                  <a href="%s" style="color:#4f46e5;font-size:13px;text-decoration:none;">%s</a>
                </p>
                """, template.getButtonLink(), template.getButtonLink());
        }

        String footerNoteHtml = "";
        if (template.getFooterNote() != null) {
            footerNoteHtml = String.format("""
                <p style="margin:0 0 8px;color:#9ca3af;font-size:13px;line-height:1.5;">
                  %s
                </p>
                """, template.getFooterNote());
        }

        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
              <title>%s</title>
            </head>
            <body style="margin:0;padding:0;background-color:#f9fafb;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f9fafb;padding:40px 16px;">
                <tr>
                  <td align="center">
                    <table width="560" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border:1px solid #e5e7eb;border-radius:8px;overflow:hidden;box-shadow:0 1px 3px rgba(0,0,0,0.05);">
                      <!-- Header -->
                      <tr>
                        <td style="padding:40px 40px 0 40px;">
                          <p style="margin:0 0 8px;font-size:14px;font-weight:600;color:#4f46e5;text-transform:uppercase;letter-spacing:1px;">
                            Wearisit
                          </p>
                          <h1 style="margin:0 0 16px;color:#111827;font-size:24px;font-weight:700;line-height:1.2;">
                            %s
                          </h1>
                        </td>
                      </tr>
                      <!-- Body Content -->
                      <tr>
                        <td style="padding:0 40px;">
                          <p style="margin:0 0 8px;color:#4b5563;font-size:16px;line-height:1.6;">
                            %s
                          </p>
                          <p style="margin:24px 0 0;color:#4b5563;font-size:16px;line-height:1.6;">
                            %s
                          </p>
                          %s
                          %s
                        </td>
                      </tr>
                      <!-- Footer -->
                      <tr>
                        <td style="padding:0 40px 40px;">
                          %s
                          <p style="margin:0;color:#9ca3af;font-size:12px;">
                            &copy; 2026 Wearisit. All rights reserved.
                          </p>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """,
                template.getTitle(),           // HTML title
                template.getHeading(),         // H1 heading
                template.getGreeting(),        // Greeting line
                template.getBodyText(),        // Body text
                buttonHtml,                    // Button if exists
                fallbackLinkHtml,              // Fallback link if exists
                footerNoteHtml                 // Footer note if exists
        );
    }
}