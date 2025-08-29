/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package studentmanagementsystem;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// Student class
class Student implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private int rollNumber;
    private String grade;
    private String dob; // dd-MM-yyyy

    public Student(String name, int rollNumber, String grade, String dob)
    {
        this.name = name;
        this.rollNumber = rollNumber;
        this.grade = grade;
        this.dob = dob;
    }

    public String getName() { return name; }
    public int getRollNumber() { return rollNumber; }
    public String getGrade() { return grade; }
    public String getDob() { return dob; }

    public void setName(String name) { this.name = name; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setDob(String dob) { this.dob = dob; }

    @Override
    public String toString()
    {
        return "Roll: " + rollNumber + ", Name: " + name + ", Grade: " + grade + ", DOB: " + dob;
    }
}

// Manager class: handles collection + file persistence
class StudentManager
{
    private List<Student> students;
    private final String FILE_NAME = "students.dat";

    public StudentManager()
    {
        students = new ArrayList<>();
        loadFromFile();
    }

    public boolean addStudent(Student s)
    {
        if (findByRoll(s.getRollNumber()) != null)
        {
            return false; // duplicate roll
        }
        students.add(s);
        saveToFile();
        return true;
    }

    public boolean removeStudent(int roll)
    {
        Student found = findByRoll(roll);
        if (found != null)
        {
            students.remove(found);
            saveToFile();
            return true;
        }
        return false;
    }

    public Student findByRoll(int roll)
    {
        for (Student s : students)
        {
            if (s.getRollNumber() == roll) return s;
        }
        return null;
    }

    public List<Student> getAll()
    {
        return new ArrayList<>(students);
    }

    public void editStudent(int roll, String newName, String newGrade, String newDob)
    {
        Student s = findByRoll(roll);
        if (s != null)
        {
            if (newName != null && !newName.trim().isEmpty()) s.setName(newName.trim());
            if (newGrade != null && !newGrade.trim().isEmpty()) s.setGrade(newGrade.trim());
            if (newDob != null && !newDob.trim().isEmpty()) s.setDob(newDob.trim());
            saveToFile();
        }
    }

    // Persistence (simple serialization)
    @SuppressWarnings("unchecked")
    private void loadFromFile()
    {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f)))
        {
            Object obj = ois.readObject();
            if (obj instanceof List)
            {
                students = (List<Student>) obj;
            }
        }
        catch (Exception e)
        {
            // if any error, start with empty list
            students = new ArrayList<>();
            System.out.println("Warning: could not load saved data (" + e.getMessage() + "). Starting fresh.");
        }
    }

    private void saveToFile()
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME)))
        {
            oos.writeObject(students);
        }
        catch (IOException e)
        {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}

// Public class (file must be named StudentManagementSydtem.java)
public class StudentManagementSystem
{
    private static Scanner sc = new Scanner(System.in);
    private static StudentManager manager = new StudentManager();

    public static void main(String[] args)
    {
        while (true)
        {
            showMenu();
            int choice = readInt("Enter choice: ");
            switch (choice)
            {
                case 1:
                    handleAdd();
                    break;
                case 2:
                    handleEdit();
                    break;
                case 3:
                    handleRemove();
                    break;
                case 4:
                    handleSearch();
                    break;
                case 5:
                    handleDisplayAll();
                    break;
                case 6:
                    System.out.println("Exiting... Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showMenu()
    {
        System.out.println("\n==== Student Management System =====");
        System.out.println("1. Add Student");
        System.out.println("2. Edit Student");
        System.out.println("3. Remove Student");
        System.out.println("4. Search Student");
        System.out.println("5. Display All Students");
        System.out.println("6. Exit");
    }

    private static void handleAdd()
    {
        System.out.println("Add new student:");
        String name = readLine("Enter Name: ");
        if (name.trim().isEmpty())
        {
            System.out.println("Name cannot be empty. Aborting add.");
            return;
        }

        int roll = readInt("Enter Roll Number: ");
        if (manager.findByRoll(roll) != null)
        {
            System.out.println("Roll number already exists. Use a unique roll.");
            return;
        }

        String grade = readLine("Enter Grade: ");
        if (grade.trim().isEmpty())
        {
            System.out.println("Grade cannot be empty. Aborting add.");
            return;
        }

        String dob = readLine("Enter DOB (dd-MM-yyyy): ");
        if (!isValidDate(dob))
        {
            System.out.println("Invalid date format. Use dd-MM-yyyy. Aborting add.");
            return;
        }

        Student s = new Student(name.trim(), roll, grade.trim(), dob.trim());
        boolean ok = manager.addStudent(s);
        if (ok) System.out.println("Student added successfully.");
        else System.out.println("Could not add student (duplicate roll).");
    }

    private static void handleEdit()
    {
        int roll = readInt("Enter Roll Number to Edit: ");
        Student s = manager.findByRoll(roll);
        if (s == null)
        {
            System.out.println("Student not found.");
            return;
        }
        System.out.println("Found: " + s);
        String name = readLine("Enter New Name (leave blank to keep): ");
        String grade = readLine("Enter New Grade (leave blank to keep): ");
        String dob = readLine("Enter New DOB dd-MM-yyyy (leave blank to keep): ");
        if (!dob.trim().isEmpty() && !isValidDate(dob))
        {
            System.out.println("Invalid DOB format. Edit cancelled.");
            return;
        }
        manager.editStudent(roll, name, grade, dob);
        System.out.println("Student updated.");
    }

    private static void handleRemove()
    {
        int roll = readInt("Enter Roll Number to Remove: ");
        boolean removed = manager.removeStudent(roll);
        if (removed) System.out.println("Student removed.");
        else System.out.println("Student not found.");
    }

    private static void handleSearch()
    {
        int roll = readInt("Enter Roll Number to Search: ");
        Student s = manager.findByRoll(roll);
        if (s != null) System.out.println("Found: " + s);
        else System.out.println("Student not found.");
    }

    private static void handleDisplayAll()
    {
        List<Student> all = manager.getAll();
        if (all.isEmpty())
        {
            System.out.println("No students available.");
            return;
        }
        System.out.println("\n--- All Students ---");
        for (Student s : all)
        {
            System.out.println(s);
        }
    }

    // Helper input methods
    private static String readLine(String prompt)
    {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private static int readInt(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = sc.nextLine();
            try
            {
                return Integer.parseInt(line.trim());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // DOB validation: dd-MM-yyyy and valid date
    private static boolean isValidDate(String input)
    {
        if (input == null) return false;
        input = input.trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try
        {
            sdf.parse(input);
            return true;
        }
        catch (ParseException e)
        {
            return false;
        }
    }
}