package com.iimj.resultportal.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	 @Value("${recaptcha.site.key}")
	    private String siteKey;
	
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("siteKey", siteKey);

        return "index"; // Thymeleaf will look for templates/index.html
    }
    
    @GetMapping("/mba-hahm")
    public String mbaHahm(Model model) {
        model.addAttribute("siteKey", siteKey);

        return "hahm"; // Thymeleaf will look for templates/hahm.html
    }
    
    
    @GetMapping("/captcha")
    @ResponseBody
    public void captcha(HttpServletResponse response,
                        HttpSession session) throws IOException {

        int width = 160;
        int height = 50;

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            captcha.append(chars.charAt(random.nextInt(chars.length())));
        }

        // ✅ save in session
        session.setAttribute("captcha", captcha.toString());

        g.setFont(new Font("Arial", Font.BOLD, 30));

        for (int i = 0; i < captcha.length(); i++) {
            g.setColor(new Color(
                    random.nextInt(150),
                    random.nextInt(150),
                    random.nextInt(150)));

            g.drawString(String.valueOf(captcha.charAt(i)),
                    20 + (i * 25), 35);
        }

        // noise lines
        for (int i = 0; i < 8; i++) {
            g.setColor(new Color(
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(255)));

            g.drawLine(random.nextInt(width),
                    random.nextInt(height),
                    random.nextInt(width),
                    random.nextInt(height));
        }

        g.dispose();

        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
    }
    
//	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
//	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

}