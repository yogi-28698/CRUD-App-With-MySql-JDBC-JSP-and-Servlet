package com.luv2code.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private StudentDbUtil studentDbUtil;
	
	@Resource(name = "jdbc/web_student_tracker")
	private DataSource dataSource;;

	//work that we would normally do on a constructor is done in the init method in case of servlet
	//this method is inherited from generic servlet
	@Override
	public void init() throws ServletException {
		super.init();
		
		//create our student db util .... and pass in the connection pool / datasource
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		}catch (Exception exc) {
			throw new ServletException(exc);
		}
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			// read the "command" parameter
			String theCommand = request.getParameter("command");
			
			// if command is missing, then default to listing students
			if(theCommand == null) {
				theCommand = "LIST";
			}
			
			// route the appropriate method
			switch(theCommand) {
				
				case "LIST":
					listStudents(request,response);
					break;
				case "ADD":
					addStudents(request,response);
					break;
				case "LOAD":
					loadStudent(request,response);
					break;
				case "UPDATE":
					updateStudent(request,response);
					break;
				case "DELETE":
					deleteStudent(request,response);
					break;
				default:
					listStudents(request,response);
					
			}
			
			// list the students in......MVC fashion
			listStudents(request, response);
		} catch (Exception exc) {
			throw new ServletException(exc);
		}
		
		
		
	}


	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//Read Student id from form data
		String theStudentId = request.getParameter("studentId");
		
		//Delete student from database
		studentDbUtil.deleteStudent(theStudentId);
		
		//Send them back to the "list-students" page
		listStudents(request, response);
		
	}


	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//Read the Student info from the form data
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		//Create new Student object
		Student theStudent = new Student(id, firstName, lastName, email);
		
		//perform update on database
		studentDbUtil.updateStudent(theStudent);
		
		//send them back to the "list-students" page
		listStudents(request,response);
	}


	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//Read Student id from Form data
		String theStudentId = request.getParameter("studentId");
		
		//get Student from Database (db util)
		Student theStudent = studentDbUtil.getStudent(theStudentId);
		
		//place student in the request attribute
		request.setAttribute("THE_STUDENT", theStudent);
		
		//send to jsp page: update-student-form.jsp 
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request,response);
		
	}


	private void addStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
			
			//Read Student info from form data
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String email = request.getParameter("email");
		
			//Create new Student
			Student theStudent = new Student(firstName, lastName, email);
			
			//Add the Student to the Database
			studentDbUtil.addStudent(theStudent);
		
			//Send back to main page(the student list)
			listStudents(request,response);
	}


	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//get students from student db util
		List<Student> students;
		students = studentDbUtil.getStudents();
		
		//add students to the request
		request.setAttribute("STUDENT_LIST", students);
		
		
		//send to the JSP page(view) by using request dispatcher
		RequestDispatcher dispathcer = request.getRequestDispatcher("/list-students.jsp");
		dispathcer.forward(request, response);
	}

}
