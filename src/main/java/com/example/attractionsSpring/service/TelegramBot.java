package com.example.attractionsSpring.service;

import com.example.attractionsSpring.config.BotConfig;
import com.example.attractionsSpring.model.User;
import com.example.attractionsSpring.model.UserRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot{

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    private String url = "jdbc:postgresql://localhost:5432/attractionsDataBase";
    private String userName = "root";
    private String passDB = "root";
    static final String HELP_TEXT = "Этот бот создать для демонстрации достопримичательностей различных городов. \n\n" +
            "Вы можете использовать команды которые будут описаны ниже:\n" +
            "/start для начала работы с ботом;\n" +
            "/mydata показывает сохраненную информацию о вас;\n" +
            "/selectcity открывает менювыбора города;" +
            "/deletedata удалает сохраненную инфомрацию о Вас\n" +
            "/settings открывает меню изменений настроек";

    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Старт"));
        listofCommands.add(new BotCommand("/mydata","Информация обо мне"));
        listofCommands.add(new BotCommand("/selectcity", "Информацию какого города вы хотите посмотреть?"));
        listofCommands.add(new BotCommand("/deletedata","Удалить данные обо мне"));
        listofCommands.add(new BotCommand("/help","Как пользоваться этим ботом?"));
        listofCommands.add(new BotCommand("/settings","Изменить настройки"));
        try{
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(),null));
        }
        catch(TelegramApiException e){
            log.error("Error setting bot's command list: "+ e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() { return config.getToken(); }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId=update.getMessage().getChatId();
            switch(messageText){
                case "/start":
                        registerUser(update.getMessage());
                        startCommandRecived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:

                        sendMessage(chatId, "Sorry, command was not recognized");

            }
        }

    }

    private void registerUser(Message message) {
        if(userRepository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            try(Connection connection = DriverManager.getConnection(url,userName,passDB)){
                Statement statement = connection.createStatement();
                statement.executeUpdate("insert into users_data_table (chat_id,first_Name,last_Name,user_Name,registered_At) " +
                        "values ('"+user.getChatId()+"','"+user.getFirstName()+"','"+user.getLastName()+"','"+user.getUserName()+"','"+user.getRegisteredAt()+"')");
            }catch(Exception e){
                log.error("Error occurred: "+e.getMessage());
            }
            try(Connection connection = DriverManager.getConnection(url,userName,passDB)){
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from users_data_table where users_data_table.chat_Id ='"+user.getChatId()+"'");
                resultSet.next();
                String output = resultSet.getString("chat_Id");
                System.out.println("User: "+output);
            } catch(Exception e){
                log.error("User is not saved: "+e.getMessage());
            }
            userRepository.save(user);
            log.info("user saved: "+user);
        }
    }

    private void startCommandRecived(long chatId, String name) {
        String answer = "Hi, "+name+", nice to meet you!";
        log.info("Replied to user "+ name);
        sendMessage(chatId, answer);
    }
    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try{
            execute(message);
        }
        catch(TelegramApiException e){
            log.error("Error occurred: "+e.getMessage());
        }
    }
}
