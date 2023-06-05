package com.example.attractionsSpring.controllers;

import com.example.attractionsSpring.model.Admin;
import com.example.attractionsSpring.model.Attraction;
import com.example.attractionsSpring.repo.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Controller
public class MainController {

    private String url = "jdbc:postgresql://localhost:5432/attractionsDataBase";
    private String userName = "root";
    private String passDB = "root";

    private String adminLogin;
    private String adminPassword;
    private Boolean isAuthenticated(){
        if(adminLogin.equals("")&&adminPassword.equals("")){
            return true;
        }
        return false;
    }
    private Long IDCityBuf;

    @Autowired
    private AdminRepository adminRepository;


    @GetMapping("/")
    public String home(Model model) {
        //Iterable<Admin> admin = adminRepository.;
        if(isAuthenticated())
            model.addAttribute("isAuthenticated()");
        model.addAttribute("title", "Главная страница");
        return "home";
    }
    @GetMapping("/register")
    public String register(Model model){
        return "register";
    }
    @PostMapping("/register")
    public String register(@RequestParam String login,
                           @RequestParam String password){
        Admin admin = new Admin(login, password);
        try(Connection connection = DriverManager.getConnection(url,userName,passDB)){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from attractionsDataBase.admin_Data_Table where login ='"+login+"'");
            while(resultSet.next()){
                if(resultSet.getString("login").equals(login)){
                    System.out.println("Этот пользователь уже есть");
                }
            }
            adminLogin=login;
            adminPassword=password;
            statement.executeUpdate("insert into attractionsDataBase.admin_data_table(login,password)" +
                    "values ('"+login+"','"+password+"')");
            return "redirect:/login";
        } catch (Exception e){
            log.error("Error MainController: "+e.getMessage());
        }
        return "redirect:/register";
    }
    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password,Model model){
        try(Connection connection = DriverManager.getConnection(url,userName,passDB)){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from admin_data_table where login='"+login+"',password='"+password+"'");
            while (resultSet.next()){
                if(resultSet.getString("login").equals(login) && resultSet.getString("password").equals(password)){
                    return "redirect:/";
                }
            }
        } catch(Exception e){
            log.error("Error login: "+e.getMessage());
        }
        return "redirect:/";
    }
    @GetMapping("/addCity")
    public String addCity(Model model){
        return "addCity";
    }
    @PostMapping("/addCity")
    public String addCity(@RequestParam String name){
        try(Connection connection = DriverManager.getConnection(url,userName,passDB)){
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into city_data_table(name,count_of_attractions)" +
                    "values('"+name+"',0)");
        } catch(Exception e){
            log.error("Error AddCity: "+e.getMessage());
        }
        return "redirect:/";
    }
    @GetMapping("/updateCity/{IDCity}")
    public String updateCity(@PathVariable(value = "IDCity") Long IDCity, Model model){

        List<Attraction> attractions = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,userName,passDB)){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from attractions_data_table where city_id ='"+IDCity+"'");
            while (resultSet.next()){
                attractions.add(new Attraction(resultSet.getLong("Id"),resultSet.getString("attraction_name"),
                        resultSet.getString("info"),resultSet.getLong("city_id")));
            }
            model.addAttribute("attractions",attractions);
            model.addAttribute("cityId",IDCity);
        } catch(Exception e){
            log.error("Error updateCity: "+e.getMessage());
        }
        return"redirect:/updateCity";
    }

    @GetMapping("/addAttraction/{IDCity}")
    public String addAttraction(@PathVariable(value="IDCity")Long IDCity, Model model){
        IDCityBuf=IDCity;
        return "/addAttraction";
    }
    @PostMapping("/addAttraction")
    public String addAttraction(@RequestParam String name,@RequestParam String info,Model model){
        try(Connection connection = DriverManager.getConnection(url,userName,passDB)){
            Statement statement = connection.createStatement();
            statement.executeQuery("insert into attraction (attraction_name,info,city_id)" +
                    "values ('"+name+"','"+info+"','"+IDCityBuf+"')");
        } catch (Exception e){
            log.error("Error addAttraction: "+e.getMessage());
        }
        return "redirect:/";
    }


}
