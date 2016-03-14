package com.rinlama.me.TestCase;

import java.io.InputStream;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.*;

import org.junit.*;
import org.w3c.dom.*;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import static org.junit.Assert.*;

/**
 * Test a Solver & Student class's methods with junit
 * 
 * @author Rinjin
 * @version 1.0
 * @since 03-12-2016
 */
public class TestCase {
	private Solver solver=null;

	@Before
	public void setup() {
		solver = new Solver();
	}

	@Test
	public void testGetAgeShouldThrowValidOutput() {
		assertEquals(28, solver.getAge("12/12/1988"));

	};

	@Test(expected = UnsupportedOperationException.class)
	public void testSolverShouldThrowAnException_For_AgeisWrongFormat() {
		solver.getAge("12/12");
	}

	@Test
	public void testAuthenticateShouldThrowValidOutput() {
		// testing authentication
		assertTrue(solver.authenticate("username", "username"));
	};

	@Test
	public void testSearchShouldThrowValidFlag() {
		String[] name = { "Joe", "Mark" };
		assertTrue(solver.search(name, "Joe"));
	};

	@Test
	public void testEstimateShouldThrowValidOutput() {
		String espectedAmount = "1005466";
		String actualAmount = solver.callZillowAPI("9410+57th+ave", "Elmhurst",
				"NY", "11373");
		assertEquals(espectedAmount, actualAmount);
	};

	@Test
	public void testSortObjPropShouldSortObjList() {
		// sorted list created
		ArrayList<Student> sortList = new ArrayList<Student>();
		sortList.add(new Student("Albert"));
		sortList.add(new Student("Joe"));
		sortList.add(new Student("Mark"));
		sortList.add(new Student("Smith"));

		// unsorted list created
		ArrayList<Student> list = new ArrayList<Student>();
		list.add(new Student("Mark"));
		list.add(new Student("Joe"));
		list.add(new Student("Smith"));
		list.add(new Student("Albert"));
		Student.sortObjProp(list);

		assertEquals(sortList.toString(), list.toString());

	}
	
	@Test
	public void testDatabaseShouldConnected() throws Exception{
		Connection connection=Student.getConnection();
		Assert.assertNotNull(connection);
		Assert.assertTrue(connection.isValid(0));
		connection.close();
	}
	
	@Test
	public void testStudentTableShouldReturnValidOutput() throws Exception{
		ArrayList expectedlist= new ArrayList<Student>();
		expectedlist.add(new Student("Joe"));
		ArrayList studentList=(ArrayList) Student.getAllStudent();
		Assert.assertEquals(expectedlist, studentList);
		
	}

	@After
	public void teardown() {
		solver = null;
	}

	public static void main(String[] args) {
		try {
			ArrayList studentList=(ArrayList) Student.getAllStudent();
			System.out.println(studentList.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

/*
 * For a Demo purpose: create an member class on a same page
 */
class Solver {
	/*
	 * getAge() method accepts a parameter on 12/12/1989 format & return a age
	 */
	public int getAge(String dateOfBirth) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("MM/dd/yyyy");
			LocalDate date = LocalDate.parse(dateOfBirth, formatter);
			LocalDate today = LocalDate.now();
			int todayyear = today.getYear();
			int dobyear = date.getYear();
			int age = todayyear - dobyear;
			return age;

		} catch (Exception e) {
			throw new UnsupportedOperationException("Error in getAge()");
		}

	}

	/*
	 * authenticate() method accepts two parameters if both parameters has same
	 * value then return a true
	 */
	public boolean authenticate(String username, String password) {
		if (username.equals(password)) {
			return true;
		}
		return false;
	}

	/*
	 * search() method search a value in array if found then return a true
	 */
	public boolean search(String[] name, String username) {
		try {
			for (int i = 0; i < name.length; i++) {
				if (name[i].equalsIgnoreCase(username)) {
					return true;
				}
			}
		} catch (Exception objE) {
			throw new UnsupportedOperationException(
					"Error in search method() - " + objE.toString());
		}
		return false;
	}

	/*
	 * callZillowAPI() method call zillow api return
	 * estimate cost of house
	 */
	public String callZillowAPI(String address, String city, String state,
			String zipcode) {
		URL xmlURL, xmlURLChart;
		try {

			String link = "http://www.zillow.com/webservice/GetSearchResults.htm?zws-id=X1-ZWz1f05nvrhjpn_7nbg7&address="
					+ address + "&citystatezip=" + city + state + zipcode;
			xmlURL = new URL(link);
			InputStream xml = xmlURL.openStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xml);

			NodeList estimate = doc.getElementsByTagName("zestimate");
			for (int i = 0; i < estimate.getLength(); i++) {
				Node newEstimate = estimate.item(i);
				if (newEstimate.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) newEstimate;
					NodeList childlist = e.getChildNodes();
					for (int j = 0; j < childlist.getLength(); j++) {
						Node newchildnode = childlist.item(j);
						if (newchildnode.getNodeType() == Node.ELEMENT_NODE) {
							Element childe = (Element) newchildnode;
							System.out.println(childe.getTextContent());
							return childe.getTextContent();
						}
					}

				}

			}

		} catch (Exception ex) {
			System.out.println("error in callzillowAPi()" + ex.toString());
		}
		return null;
	}

}

/*
 *For a demo purpose. Student class has one property and 3 methods.
 */
class Student {
	private String name;

	public Student(String name) {
		this.name = name;
	}

	public Student() {
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Student [name=" + name + "]";
	}

	public String getName() {
		return name;
	}

	/*
	 * sortObjProp() sort a Solver object by its property type name
	 */
	public static void sortObjProp(ArrayList<Student> list) {

		Collections.sort(list, new Comparator<Student>() {

			@Override
			public int compare(Student o1, Student o2) {

				return o1.getName().compareTo(o2.getName());
			}

		});
	}
	/*
	 * JDBC connection for student table 
	 */
	public static Connection getConnection() throws Exception {
		try {

			Class.forName("com.mysql.jdbc.Driver");
			String connectionUrl = "jdbc:mysql://localhost/businessApplication";
			String user = "root";
			String password = "abc123";
			Connection objConn = (Connection) DriverManager.getConnection(
					connectionUrl, user, password);
			return objConn;
		} catch (Exception objE) {
			throw new Exception("Error in getConnection() " + objE.toString());
		}

	}
	/*
	 * JDBC used to get All students from student table
	 */
	public static List<Student> getAllStudent() throws Exception {
		List<Student> studentlist = null;
		try {
			studentlist = new ArrayList<Student>();
			Connection connection = getConnection();
			Statement statement = (Statement) connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from Student");
			while (rs.next()) {
			Student student= new Student();
			student.setName(rs.getString("name"));
			studentlist.add(student);
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentlist;
	}
	

}