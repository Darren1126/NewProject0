package com.banking.app;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import com.banking.dao.UserDao;
import com.banking.models.AccountTypes;
import com.banking.models.BankAccounts;
import com.banking.models.User;
import com.banking.service.AccountTypeService;
import com.banking.service.BankAccountService;
import com.banking.service.UserService;
import com.banking.util.ConnectionManager;
import com.banking.util.PrintList;

//This is my main menu class
public class MainMenu extends PrintList {



    static AccountTypeService accTypeServ = new AccountTypeService();
    static BankAccountService bankAccServ = new BankAccountService();
    static UserService userServ = new UserService();

    public static void main(String[] args) {

        Connection conn = ConnectionManager.getConnection();
        if(conn != null) {
            System.out.println("Database connected!");
        }

        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to The Bank!"
                +"\n 1. Login"
                +"\n 2. Create a New Bank Account"
                +"\n 3. New User");
        //Switch statement to check for Login or Create a New Account
        int choice;
        choice = input.nextInt();
        switch (choice) {

            case 1:
                loginScreen();
                break;

            case 2:
                createAcc();
                break;
            case 3:

                //Registers account fo a user//
                User tempUser = new User();
                Scanner newUser = new Scanner(System.in);
                System.out.println("Enter your first name");
                String tempfirstname = newUser.nextLine();
                System.out.println("Enter your last name");
                String templastname = newUser.nextLine();
                System.out.println("Enter your user name");
                String tempusername = newUser.nextLine();
                System.out.println("Enter your password");
                String temppassword = newUser.nextLine();
                tempUser.setFirstName(tempfirstname);
                tempUser.setLastName(templastname);
                tempUser.setUsername(tempusername);
                tempUser.setPassword(temppassword);

                UserDao dao = new UserDao();

                List<User> UserList = dao.findAll();
                int i = 0;
                int max = 0;
                while(i < UserList.size()){
                    if(max < UserList.get(i).getId()){
                        max = UserList.get(i).getId();

                    }
                    i++;

            } tempUser.setId(max + 1);


                dao.save(tempUser);

            default:
                System.out.println("Not a valid choice");
                System.exit(0);
                break;

        }

    }

    // ----------- CREATES A NEW ACCOUNT -----------
    public static void createAcc() {

        //User fields

        String firstname;
        String lastname;
        String username;
        String password;
        String accT = "";


        Scanner input = new Scanner(System.in);

        //---------------- Asks for user info ----------------

        System.out.println("Enter your first name:");
        firstname = input.nextLine();

        System.out.println("Enter your last name:");
        lastname = input.nextLine();

        System.out.println("Create your username");
        username = input.nextLine();

        System.out.println("Enter your password:");
        password = input.nextLine();



        User newUser = new User(firstname, lastname, username, password);

        //Check DB if username exists
        //get list of users
        List<User> users = userServ.findAllUsers();
        boolean checkUser = false;

        for(User u : users) {

            if(u.getUsername().equals(username)) {

                System.out.println("Username already exists. Please try again.");
                checkUser = true;
                createAcc();
                break;

            }

        }
        if (!checkUser) {

            userServ.saveUser(newUser);
            System.out.println("Congratulations, You've created an account!");
            accountSetup(newUser);
        }

    }

    // ----------- ACCOUNT SETUP -----------

    public static void accountSetup(User user) {


        int accChoice;
        int transaction;
        int accOwner = user.getId();
        Double balance;
        String accType = "";

        Scanner input = new Scanner(System.in);

        AccountTypes accT = new AccountTypes();

        BankAccounts bk_acc = new BankAccounts();


        bk_acc.setAccountOwner(accOwner);
        System.out.println("What type of account would you like to open?"
                + "\n 1. Credit"
                + "\n 2. Checking"
                + "\n 3. Saving");

        accChoice = input.nextInt();
        switch (accChoice) {

            case 1:
                accType = "Credit";
                bk_acc.setAccountType(1);
                break;

            case 2:
                accType = "Checking";
                bk_acc.setAccountType(2);
                break;

            case 3:
                accType = "Saving";
                bk_acc.setAccountType(3);
                break;

            default:
                System.out.println("Invalid option, try again.");
                accountSetup(user);
                break;

        }


        System.out.println("How much would you like to deposit today?");
        balance = input.nextDouble();

        bk_acc.setBalance(balance);

        //Add bank account info to Bank

        bankAccServ.saveBankAcc(bk_acc);

        System.out.println("You now have $" + customFormat(bk_acc.getBalance()) + " in your account.");
        System.out.println("Would you like to: "
                + "\n 1. Make another transaction"
                + "\n 2. Create another account"
                + "\n 3. Logout");

        transaction = input.nextInt();

        switch (transaction) {

            case 1:
                WelcomeBack(user.getId());
                break;
            case 2:
                accountSetup(user);
                break;
            case 3:
                logout(user.getId());
                break;
            default:
                System.out.println("Not a valid option");
                accountSetup(user);
                break;


        }

    }


    // ----------- EXISTING USER LOGIN SCREEN -----------

    public static void loginScreen() {

        //Creating a new account
        //set user inputs

        String username;
        String password;
        boolean checkUser = false;

        Scanner input = new Scanner(System.in);

        // Asks for username and password
        System.out.println("Enter your username:");
        username = input.nextLine();

        //Checks username for empty string or null
        //Empty string is known to be nothing
        if (username.equals("") || username.isEmpty()) {

            System.out.println("Invalid username");
            loginScreen();

        }

        System.out.println("Enter your password");
        password = input.nextLine();

        //Check password for empty string or null
        if (password.equals("") || password.isEmpty()) {

            System.out.println("Invalid password");
            loginScreen();

        }

        List<User> users = userServ.findAllUsers();

        for (User u : users) {

            if(u.getUsername().equals(username) && u.getPassword().equals(password)) {

                checkUser = true;
                WelcomeBack(u.getId());
                break;

            }

        }
        if (!checkUser) {

            System.out.println("Username or Password is incorrect, please try again.");
            loginScreen();
        }


    }

    // ----------- EXISTING USER WELCOME SCREEN -----------

    public static void WelcomeBack(int id) {
        System.out.println("inside welcome back");
        int choice;

        Scanner input = new Scanner(System.in);

        User currentUser = userServ.findUser(id);
        BankAccounts bAcc = bankAccServ.findBankAcc(currentUser.getId());

        System.out.println("Welcome back " + currentUser.getFirstName() + "!"
                + "\n Your current balance is: $" +  customFormat(bAcc.getBalance())
                + "\n Would you like to"
                + "\n 1. Make a deposit"
                + "\n 2. Make a withdrawal"
                + "\n 3. Logout");

        choice = input.nextInt();


        switch(choice) {

            case 1:
                deposit(id);
                break;
            case 2:
                withdraw(id);
                break;
            case 3:
                logout(id);
                break;
            case 4:
                PrintAccount(id);
                break;
            default:
                System.out.println("Not a valid choice");
                break;

        }
    }


    // --------------- LOGOUT MENU ---------------

    public static void logout(int id) {

        Scanner input = new Scanner(System.in);

        int choice;

        System.out.println("Would you like to: "
                + "\n 1. Logout"
                + "\n 2. Complete another transaction?");


        choice = input.nextInt();

        switch (choice ) {

            case 1:
                System.out.println("Goodbye.");
                //Clear the console
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.exit(0);
                break;

            case 2:
                WelcomeBack(id);
                break;

            default:
                System.out.println("Not a valid choice");
                break;

        }


    }

    // --------------- DEPOSIT TRANSACTION ---------------

    public static void deposit(int id) {

        double balance = 0;
        double deposit = 0;

        //Creates a temporary account to grab current user info
        BankAccounts temp = null;

        Scanner input = new Scanner(System.in);

        //Finds account associated with id
        temp = bankAccServ.findBankAcc(id);

        balance = temp.getBalance();

        System.out.println("How much would you like to deposit?");

        deposit = input.nextDouble();

        balance += deposit;


        //Initialize updateUser with accountType, accountOwner, and new balance

        BankAccounts updateUser = new BankAccounts(temp.getAccountType(), temp.getAccountOwner(), balance);

        bankAccServ.updateBankAcc(updateUser);

        System.out.println("Your current balance is :$ " + customFormat(updateUser.getBalance()));

        logout(id);
    }

    // --------------- WITHDRAW TRANSACTION ---------------

    public static void withdraw(int id) {

        double balance = 0;
        double withdrawal = 0;

        //Create a temporary account to grab current user info
        BankAccounts temp = null;

        Scanner input = new Scanner(System.in);

        //Find account associated with id
        temp = bankAccServ.findBankAcc(id);

        balance = temp.getBalance();

        System.out.println("How much would you like to withdraw?");

        withdrawal = input.nextDouble();

        //Makes sure withdrawal is not greater than the current balance
        if (withdrawal > balance) {

            System.out.println("You do not have sufficient funds to continue!");
            logout(id);

        }
        else {

            balance -= withdrawal;

        }

        BankAccounts updateUser = new BankAccounts(temp.getAccountType(), temp.getAccountOwner(), balance);

        bankAccServ.updateBankAcc(updateUser);

        System.out.println("Your current balance is: $" + customFormat(updateUser.getBalance()));

        logout(id);

    }




}
