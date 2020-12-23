package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	
	private DataSource dataSource;
	
	public StudentDbUtil(DataSource theDataSource) {
		
		dataSource = theDataSource;
		
	}
	
	// We Will get the List of Students from the Database and then from here we will return that list
	public List<Student> getStudents() throws Exception {
		
		List<Student> students = new ArrayList<>();
		Connection myConn = null;
		Statement mySmt = null;
		ResultSet myRs = null;
		
		try {
			// get a connection
			myConn = dataSource.getConnection();
			
			// create a sql statement
			String sql = "select * from student order by last_name";
			
			mySmt = myConn.createStatement();
			
			// execute query 
			myRs = mySmt.executeQuery(sql);
			
			// process the result set
			while(myRs.next()) {
				
				// retrieve data from result set now
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				// create new student object
				Student tempStudent = new Student(id, firstName, lastName, email);
				
				// add it to the list of students
				students.add(tempStudent);
				
			}
			
			   return students;
		}
		finally {
			close(myConn,mySmt,myRs);
		}
		
		
	}

	private void close(Connection myConn, Statement mySmt, ResultSet myRs) {
		
		try {
			
			if (myRs != null) {
				myRs.close();
			}
			
			if (mySmt != null) {
				mySmt.close();
			}
			
			if (myConn != null) {
				myConn.close();       // this line will not close the database connection, it basically put it back to the connection pool
									  // like in rent you have taken a car and after using it you have returned it
			}
			
		}catch(Exception exc) {
			exc.printStackTrace();
		}
		
	}

	public void addStudent(Student theStudent) throws Exception{
		
		Connection myConn = null;
		PreparedStatement mySmt = null; 
		
		try {
			//get DataBase Connecion
			myConn = dataSource.getConnection();
			
			//Create SQL for Insert
			String sql = "insert into student "
						  + "(first_name, last_name, email) "
						  + "values(?,?,?)";
			mySmt = myConn.prepareStatement(sql);
			
			//Set the Param values for the student
			mySmt.setString(1, theStudent.getFirst_name());
			mySmt.setString(2, theStudent.getLast_name());
			mySmt.setString(3, theStudent.getEmail());
			
			//execute SQL insert
			mySmt.execute();
		}
		finally {
		    //clean up JDBC objects
			close(myConn, mySmt, null);
		}
		
	}

	public Student getStudent(String theStudentId) throws Exception {
		Student theStudent = null;
		
		Connection myConn = null;
		PreparedStatement mySmt = null;
		ResultSet myRs = null;
		int studentId;
		try {
			//Convert Studentid to int
			studentId = Integer.parseInt(theStudentId);
			
			//get Connection to database
			myConn = dataSource.getConnection();
			
			//create sql to get selected student
			String sql = "select * from student where id = ?";
			
			//create prepared statement
			mySmt = myConn.prepareStatement(sql);
			
			//set params
			mySmt.setInt(1, studentId);
			
			//execute statement
			myRs = mySmt.executeQuery();
			
			//retrieve data from result set row
			if(myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				//use the studentId during Construction
				theStudent = new Student(studentId, firstName, lastName, email);
			}
			else {
				throw new Exception("Could not Find Student id : " + studentId);
			}
			
			
			return theStudent;
		} finally {
			
			//clean up jdbc objects
			close(myConn, mySmt, myRs);
			
		}
		
	}

	public void updateStudent(Student theStudent) throws Exception {
		
		Connection myConn = null;
		PreparedStatement mySmt = null;
		
		try {
			//Get db Connection
			myConn = dataSource.getConnection();
			
			//Create sql Update Statement
			String sql = "update student "
						 + "set first_name = ?, last_name = ?, email = ? "
						 + "where id = ?";
			
			//prepare statement
			mySmt = myConn.prepareStatement(sql);
			
			//set params in statement
			mySmt.setString(1, theStudent.getFirst_name());
			mySmt.setString(2, theStudent.getLast_name());
			mySmt.setString(3,theStudent.getEmail());
			mySmt.setInt(4, theStudent.getId());
			
			//execute sql stetment
			mySmt.execute();
	       }
			finally {
		         //clean up the jdbc objects
	    	     close(myConn,mySmt,null);
	       }
	}

	public void deleteStudent(String theStudentId) throws Exception{
		
		Connection myConn = null;
		PreparedStatement mySmt = null;
		
		try {
			
			//Convert student id to int
			int studentId = Integer.parseInt(theStudentId);
			
			//get Connection to database	
			myConn = dataSource.getConnection();
			
			//Create sql to delete student
			String sql = "delete from student where id=?";
					
			//Prepare Statements
			mySmt = myConn.prepareStatement(sql);
			
			//Set Params
			mySmt.setInt(1, studentId);
			
			//Execute sql Statement
			mySmt.execute();
			
		}
		finally {
			
			//Clean up the JDBC objects used
			close(myConn,mySmt,null);
			
		}
		
	}
	
	

}
