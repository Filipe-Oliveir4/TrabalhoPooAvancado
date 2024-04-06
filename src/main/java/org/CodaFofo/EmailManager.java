package org.CodaFofo;

import org.CodaFofo.anotations.Password;
import org.CodaFofo.anotations.User;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailManager {
    private  final String smtp = "smtp.gmail.com";
    private  final int port = 465;
    private String userName;
    private String password;
    ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * Construtor que recebe como parametro o nome do usuario e a senha como string. Usa como padrão o smtp do gmail e a porta 465.
     * @param userName Nome do usuário da conta que enviará o email
     * @param password Senha da que enviará o email
     */
    public EmailManager(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    /**
     * Construtor que recebe como parametro um objeto genérico com os atributos usuario, senha anotados
     * @param objeto
     */
    public EmailManager(Object objeto) {
        List<String> invalidos = new ArrayList<>();
        Class<?> classe = objeto.getClass();
        Field[] atributos = classe.getDeclaredFields();
        for(Field atributo : atributos){
            atributo.setAccessible(true);
            if (atributo.isAnnotationPresent(User.class)){
                try {
                    this.userName = (String) atributo.get(objeto);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }else if (atributo.isAnnotationPresent(Password.class)){
                try {
                    this.password = (String) atributo.get(objeto);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Envia um email simples para uma lista de destinatários
     * @param message Mensagem do email que será enviado
     * @param subject Assunto do email que será enviado
     * @param to Lista de destinatários do email
     */
    public void sendSimpleEmailToMany(List<String> to, String message, String subject ) {
       for (String target : to) {
           executor.execute(new SimpleEmailRunnable(userName, password,target, message, subject));
       }
    }

    /**
     * Envia um simples email HTML para uma lista de destinatários
     * @param htmlMessage Mensagem em HTML do email que será enviado
     * @param subject Assunto do email que será enviado
     * @param to Lista de destinatários do email
     * @param altMessage Mensagem alternativa do email que será enviado caso o cliente de email não suporte HTML
     */
public void sendSimpleEmailHtmlToMany(  List<String> to, String htmlMessage, String subject, String altMessage) {
       for (String target : to) {
           executor.execute(new SimpleEmailHtlmRunnable(userName, password,target, subject, htmlMessage, altMessage));
       }
    }
}
