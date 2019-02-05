package com.myapi;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.myapi.model.ApiModel;

@RestController
public class HelloController {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/",produces = { "application/json" },method = RequestMethod.GET)
    @ResponseBody
    public List<ApiModel> getApiData() {
    	List<ApiModel> apidataList = new ArrayList<ApiModel>();
    	ApiModel apiModel = null;
    	for(int i=0;i<10;i++) {
    		apiModel = new ApiModel();
    		apiModel.setId(i);
    		apiModel.setName("NameTesting" + i);
    		apiModel.setSurname("SurnameTesting" + i);
    		apidataList.add(apiModel);
    	}
        return apidataList;
    }
}
