package com.school.models;

import java.util.Date;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class Sql2oModel {

	private Sql2o sql2o;
	 	
	public Sql2oModel(Sql2o sql2o) {
        this.sql2o = sql2o;
    }
	
	// insert school, contacts
	public long createSchool(String name, String address, String description,  List<Contact> contacts)
	{
    	try(Connection con = sql2o.beginTransaction()){
		 long id = (long) con.createQuery(
				    "INSERT INTO schools (name, address, description, created_at, updated_at) "
			 		+"VALUES(:name, :address, :description, :created_at, :updated_at)")
			        .addParameter("name", name)
			        .addParameter("address", address)
			        .addParameter("description", description)
			        .addParameter("created_at", new Date())
			        .addParameter("updated_at", new Date())
			        .executeUpdate()
			        .getKey();
		 
		 contacts.forEach((contact) -> {
			 con.createQuery("INSERT INTO contacts (school_id, type, value, created_at, updated_at)"
			    		+ "VALUES (:school_id, :type, :value, :created_at, :updated_at)")
			            .addParameter("school_id", id)
			            .addParameter("type", contact.getType())
			            .addParameter("value", contact.getValue())
			            .addParameter("created_at", new Date())
			            .addParameter("updated_at", new Date())
			            .executeUpdate();
		 });
		 
		 con.commit();
		 return id;
		}
	}
	
	// select all schools with it's contacts
	public List<School> getAllSchools() 
	{
		try(Connection con = sql2o.open()){
			List<School> schools = con.createQuery("SELECT * FROM schools")
				    	.executeAndFetch(School.class);
			schools.forEach((school) -> {
				school.setContacts(getContacts(school.getId()));
			});
			return schools;
		}
	}
	
	// select single school with it's contact
	public List<School> getSchool(int school_id) 
	{
		try(Connection con = sql2o.open()){
			List<School> schools = con.createQuery("SELECT * FROM schools WHERE id = :school_id")
					     .addParameter("school_id", school_id)
				    	 .executeAndFetch(School.class);
			schools.forEach((school) -> {
				school.setContacts(getContacts(school.getId()));
			});
			return schools;
		}
	}
	
	// select all contacts of school
	public List<Contact> getContacts(int school_id)
	{
	  try(Connection con = sql2o.open()){
		  List<Contact> contacts = con.createQuery("SELECT * FROM contacts WHERE school_id = :school_id")
				  .addParameter("school_id", school_id)
				  .executeAndFetch(Contact.class);
		  return contacts;
	  }	
	}
	
	// select single contact where id
	public List<Contact> getContact(int id)
	{
	  try(Connection con = sql2o.open()){
		  List<Contact> contacts = con.createQuery("SELECT * FROM contacts WHERE id = :id")
				  .addParameter("id", id)
				  .executeAndFetch(Contact.class);
		  return contacts;
	  }	
	}
	
	// update school, contacts
	public int updateSchool(int school_id, String name, String address, String description, List<Contact> contacts)
	{
		try(Connection con = sql2o.open()){
			con.createQuery("UPDATE schools SET name = :name, "
					                        + " address = :address,"
					                        + " description = :description,"
					                        + " updated_at = :updated_at"
					                        + " WHERE id = :school_id")
			.addParameter("name", name)
			.addParameter("address", address)
			.addParameter("description", description)
			.addParameter("updated_at", new Date())
			.addParameter("school_id", school_id)
			.executeUpdate();
			
			contacts.forEach((contact) -> {
				
				if(contact.getId() != 0){
					con.createQuery("UPDATE contacts SET type = :type,"
							 + " value = :value,"
							 + " updated_at = :updated_at"
							 + " WHERE id = :id")
					.addParameter("type", contact.getType())
					.addParameter("value", contact.getValue())
					.addParameter("updated_at", new Date())
					.addParameter("id", contact.getId())
					.executeUpdate();
				}
				else{
					con.createQuery("INSERT INTO contacts (school_id, type, value, created_at, updated_at)"
				    		+ "VALUES (:school_id, :type, :value, :created_at, :updated_at)")
		            .addParameter("school_id", school_id)
		            .addParameter("type", contact.getType())
		            .addParameter("value", contact.getValue())
		            .addParameter("created_at", new Date())
		            .addParameter("updated_at", new Date())
		            .executeUpdate();
				}
				
			});
			return school_id;
		}
	}
	
	// delete school and it's contacts
	public int deleteSchool(int school_id)
	{
		try(Connection con = sql2o.open()){
			con.createQuery("DELETE FROM schools WHERE id = :school_id")
			   .addParameter("school_id", school_id)
			   .executeUpdate();
			
			con.createQuery("DELETE FROM contacts WHERE school_id = :school_id")
			   .addParameter("school_id", school_id)
			   .executeUpdate();
		}
		return school_id;
	}
}
