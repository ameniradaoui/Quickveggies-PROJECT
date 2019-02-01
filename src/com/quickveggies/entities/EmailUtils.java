package com.quickveggies.entities;

import java.util.Date;
import java.util.Properties;
import java.util.Set;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtils {
	
//	private static class SMTPAuthenticator extends javax.mail.Authenticator {
//		String user;
//		String pwd;
//
//		public SMTPAuthenticator() {
//			user = "contact@onesquare.us";
//			pwd = "j20hQBHDmd@VT";
//
//		}
//
//		public PasswordAuthentication getPasswordAuthentication() {
//			return new PasswordAuthentication(user, pwd);
//		}
//	}
//
//	public static void broadcastEmail(Set<String> addresses, Message toSendMessage) {
//		int port = 587;
//		String host = "mail.privateemail.com";
//		String user = "contact@onesquare.us";
//		String pwd = "j20hQBHDmd@VT";
//
//		try {
//			Properties props = new Properties();
//			// required for gmail
//			props.put("mail.smtp.starttls.enable", "true");
//			props.put("mail.smtp.auth", "true");
//			props.put("mail.smtp.host", "mail.privateemail.com");
//			props.put("mail.smtp.socketFactory.port", "587");
//			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//			props.put("mail.smtp.auth", "true");
//			props.put("mail.smtp.port", "587");
//			// or use getDefaultInstance instance if desired...
//			Authenticator auth = new SMTPAuthenticator();
//			Session session = Session.getInstance(props, auth);
//			Transport transport = session.getTransport("smtp");
//			transport.connect(host, port, user, pwd);
//			MimeMessage msg = new MimeMessage(session);
//			msg.setFrom(new InternetAddress("contact@onesquare.us"));
//
////					msg.setRecipients(MimeMessage.RecipientType.TO,
////							InternetAddress.parse("bacem.bergoui@gmail.com", false));
//
//			InternetAddress[] toSend = new InternetAddress[addresses.size()];
//
//			int i = 0;
//
//			for (String s : addresses) {
//				try {
//					toSend[i] = InternetAddress.parse(s)[0];
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				i++;
//
//			}
//			msg.setRecipients(MimeMessage.RecipientType.TO, toSend);
//
//			msg.setSubject(toSendMessage.getTitle());
//			msg.setText(toSendMessage.getContent());
//			msg.setHeader("X-Mailer", toSendMessage.getTitle());
//			msg.setSentDate(new Date());
//			transport.send(msg);
//			transport.close();
//
//		} catch (AuthenticationFailedException e) {
//			System.out.println("AuthenticationFailedException - for authentication failures");
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			System.out.println("for other failures");
//			e.printStackTrace();
//		}

}
