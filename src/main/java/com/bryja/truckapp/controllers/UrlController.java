package com.bryja.truckapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UrlController {

    @RequestMapping(value="/login")
    public ModelAndView getlogi() {
        return new ModelAndView("login");
    }

    @RequestMapping(value="/register")
    public ModelAndView getreg() {
        return new ModelAndView("register");
    }
    @RequestMapping(value="/profile")
    public ModelAndView getprof() {
        return new ModelAndView("profile");
    }
    @RequestMapping(value="/chat")
    public ModelAndView getchat() {
        return new ModelAndView("chat");
    }
    @RequestMapping(value="/approve")
    public ModelAndView getapprove() {
        return new ModelAndView("approve");
    }
    @RequestMapping(value="/confirm")
    public ModelAndView getconfirm() {
        return new ModelAndView("confirm");
    }
    @RequestMapping(value="/delivery")
    public ModelAndView getdelivery() {
        return new ModelAndView("delivery");
    }
    @RequestMapping(value="/drivers")
    public ModelAndView getdrivers() {
        return new ModelAndView("drivers");
    }
    @RequestMapping(value="/raports")
    public ModelAndView getraports() {
        return new ModelAndView("raports");
    }
    @RequestMapping(value="/schedule")
    public ModelAndView getschedule() {
        return new ModelAndView("schedule");
    }
    @RequestMapping(value="/track")
    public ModelAndView gettrack() {
        return new ModelAndView("track");
    }
    @RequestMapping(value="/trucks")
    public ModelAndView gettrucks() {
        return new ModelAndView("trucks");
    }
    @RequestMapping(value="/workdays")
    public ModelAndView getworkdays() {
        return new ModelAndView("workdays");
    }
    @RequestMapping(value="/workday")
    public ModelAndView getworkdayedit() {
        return new ModelAndView("workdays2");
    }

    @RequestMapping(value="/workday-preview")
    public ModelAndView previewWorkday() {
        return new ModelAndView("workdays3");
    }
    @RequestMapping(value="/")
    public ModelAndView getindex() {
        return new ModelAndView("login");
    }

}
