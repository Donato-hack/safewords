package com.safewords.api.servicios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.safewords.api.modelo.Usuario;

@Service
public class CorreoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.enabled:false}")
    private boolean correoHabilitado;

    @Value("${app.mail.from:}")
    private String remitente;

    public CorreoService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public boolean enviarBienvenida(Usuario usuario) {
        if (!correoHabilitado) {
            log.info("Correo de bienvenida desactivado para desarrollo.");
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null || remitente == null || remitente.isBlank()) {
            log.warn("No se envió el correo: falta configurar MAIL_FROM o MAIL_USERNAME.");
            return false;
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(usuario.getCorreo());
        mensaje.setSubject("Bienvenido a SafeWords");
        mensaje.setText("Hola " + usuario.getNombreCompleto() + ",\n\n"
                + "Tu cuenta de SafeWords fue creada correctamente.\n\n"
                + "Ya puedes iniciar sesión, leer publicaciones y participar en el foro.\n"
                + "Recuerda no compartir datos personales y usar la comunidad con respeto.\n\n"
                + "Equipo SafeWords");

        try {
            mailSender.send(mensaje);
            log.info("Correo de bienvenida enviado a {}.", usuario.getCorreo());
            return true;
        } catch (MailException error) {
            log.error("No fue posible enviar el correo de bienvenida a {}.", usuario.getCorreo(), error);
            return false;
        }
    }
}
