package service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.time.LocalDateTime;

public class Template {
    public static  MimeMultipart notifyPlayer(LocalDateTime dateTime, String oppenent, String coach,String location) throws MessagingException {
        String body = "<!DOCTYPE html>" +
                "<html lang=en>n" +
                "<head>" +
                "  <meta charset=UTF-8>" +
                "  <meta name=viewport content= width=device-width, initial-scale=1.0 > " +
                "  <title>Confirmation d'Inscription au Match</title> " +
                "  <style> " +
                "    body { " +
                "      font-family: Arial, sans-serif; " +
                "      background-color: #f4f4f4; " +
                "      margin: 0; " +
                "      padding: 0; " +
                "      text-align: center; " +
                "    } " +
                " " +
                "    .container { " +
                "      background-color: #fff; " +
                "      border-radius: 8px; " +
                "      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); " +
                "      padding: 20px; " +
                "      width: 300px; " +
                "      margin: 20px auto; " +
                "    } " +
                " " +
                "    h2 { " +
                "      color: #333; " +
                "    } " +
                " " +
                "    p { " +
                "      color: #555; " +
                "    } " +
                " " +
                "    .match-details { " +
                "      font-size: 18px; " +
                "      color: #007BFF; " +
                "      font-weight: bold; " +
                "      margin-top: 10px; " +
                "    } " +
                "  </style> " +
                "</head> " +
                "<body> " +
                "  <div class= container > " +
                "    <h2>Convocation au Match du " + dateTime + "  contre " + oppenent + " </h2> " +
                "    <p>Félicitation le coach " + coach + "  vous a convoqué pour le match du " + dateTime.toLocalDate() + " à " + dateTime.getHour() + "H. Voici les détails :</p> " +
                "    <p class= match-details >Versus : " + oppenent + "</p>" +
                "    <p class= match-details >Date/Heure : " + dateTime + "</p> " +
                "    <p class= match-details >Lieu : " + location + "</p> " +
                "    <p>Assurez-vous d'être prêt et de venir soutenir votre équipe!</p> " +
                "  </div> " +
                "</body> " +
                "</html>";
        MimeBodyPart htmlPart =  new MimeBodyPart();
        htmlPart.setContent(body,"text/html");
        MimeMultipart mp = new MimeMultipart();
        mp.addBodyPart(htmlPart);
        return  mp;
    }
    public static MimeMultipart confirmEmail(int secretCode) throws MessagingException {
        String body  ="<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Code de Vérification</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      background-color: #f4f4f4;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "\n" +
                "    .container {\n" +
                "      background-color: #fff;\n" +
                "      border-radius: 8px;\n" +
                "      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "      padding: 20px;\n" +
                "      width: 300px;\n" +
                "      margin: 20px auto;\n" +
                "    }\n" +
                "\n" +
                "    h2 {\n" +
                "      color: #333;\n" +
                "    }\n" +
                "\n" +
                "    p {\n" +
                "      color: #555;\n" +
                "    }\n" +
                "\n" +
                "    .verification-code {\n" +
                "      font-size: 24px;\n" +
                "      color: #4caf50;\n" +
                "      font-weight: bold;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <h2>Code de Vérification</h2>\n" +
                "    <p>Merci de vérifier votre adresse e-mail en utilisant le code ci-dessous :</p>\n" +
                "    <p class=\"verification-code\">" + secretCode + "</p>\n" +
                "    <p>Si vous n'avez pas demandé cette vérification, veuillez ignorer ce message.</p>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";

        MimeBodyPart htmlPart =  new MimeBodyPart();
        htmlPart.setContent(body,"text/html");
        MimeMultipart mp = new MimeMultipart();
        mp.addBodyPart(htmlPart);
        return  mp;
    }

}
