package com.school.services;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

import org.sql2o.Sql2o;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.school.models.School;
import com.school.models.Sql2oModel;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class SchoolService {

	private static final int HTTP_BAD_REQUEST = 400;
	
	/**
	 * @param args
	 * @
	 */
	public static void main(String[] args) {
		
		// if heroku set heroku port
		port(getHerokuAssignedPort());
		
		// set static files path (html, js, css)
		staticFileLocation("/app");
		
		// mysql DB connection
		Sql2o sql2o = new Sql2o("jdbc:mysql://127.0.0.1:3306/school", "root", "1234");
		//Sql2o sql2o = new Sql2o("jdbc:mysql://196.218.198.52:3306/school", "root", "Amrmen@60394987987");
		
		// school model for DB operations
		Sql2oModel model = new Sql2oModel(sql2o);
        
		// SELECT all schools with contacts of each school
		get("/schools", (request, response) -> {
		   response.status(200);
	       response.type("application/json");
	       return dataToJson(model.getAllSchools());
	    });
		
		// SELECT school with it's contacts
		get("/school/:id", (request, response) -> {
		   int school_id = Integer.parseInt(request.params(":id"));
		   response.status(200);
	       response.type("application/json");
	       return dataToJson(model.getSchool(school_id));
	    });
		
		// INSERT school and contact of each school
		post("/school", (request, response) -> {
			
			// map request body json parameters to School, Contact models
			ObjectMapper mapper = new ObjectMapper();
			School school = mapper.readValue(request.body(), School.class);
			
			// insert to school, contacts
			long id = model.createSchool(school.getName(), school.getAddress(), school.getDescription(), school.getContacts());
			
			// response data returned
			Map<String, Object> res = new HashMap<String, Object>();
			res.put("success" , true);
			res.put("message", "added successfully");
			
			return dataToJson(res);
		});
		
		// UPDATE school and ite's contact
		put("/school/:id", (request, response) -> {
		    
			// get id parameter from url
			int school_id = Integer.parseInt(request.params(":id"));
			
			ObjectMapper mapper = new ObjectMapper();
			School school = mapper.readValue(request.body(), School.class);
			
			// update School, Contacts
			int id = model.updateSchool(school_id, school.getName(), school.getAddress(), school.getDescription(), school.getContacts());
			
			Map<String, Object> res = new HashMap<String, Object>();
			res.put("success" , true);
			res.put("message", "Updated");
			
			return dataToJson(res);
		});
		
		// DELETE school with it's contacts
		delete("school/:id", (request, response) -> {
			int school_id = Integer.parseInt(request.params(":id"));
			
			// delete school, contacts
			int id = model.deleteSchool(school_id);
			
			return dataToJson(id);
		});
		
		// loading Template Engine 
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		Configuration freeMarkerConfiguration = new Configuration();
    	freeMarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(SchoolService.class, "/"));
    	freeMarkerEngine.setConfiguration(freeMarkerConfiguration);
    	
    	// set the initial state of application to view index.html file
    	get("/", (request, response) -> {
			return freeMarkerEngine.render(new ModelAndView(null, "/app/views/index.html"));
		});

	}
	
	// convert Objects to Json 
	public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, data);
            return sw.toString();
        } catch (IOException e){
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }
	
	/*
	 *  if heroku get heroku's port
	 *  if not return default port 4567
	 */
	static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

}
