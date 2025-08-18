package LLDQuestions.LibraryManagementSystem;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class to demonstrate the Library Management System using the Facade Pattern.
 */
public class LibraryDemo {
    public static void main(String[] args) {
        // The client interacts with the simple, unified facade, not the complex subsystems.
        LibraryFacade library = new LibraryFacade();

        // --- SCENARIO 1: Successful Borrow ---
        System.out.println("----- SCENARIO 1: Tushit borrows 'Clean Code' -----");
        library.borrowBook("MEMBER-001", "CC-1");
        System.out.println();

        // --- SCENARIO 2: Attempt to borrow an already loaned book ---
        System.out.println("----- SCENARIO 2: Ankit tries to borrow the same copy of 'Clean Code' -----");
        library.borrowBook("MEMBER-002", "CC-1");
        System.out.println();

        // --- SCENARIO 3: Successful Return ---
        System.out.println("----- SCENARIO 3: Tushit returns 'Clean Code' -----");
        library.returnBook("CC-1");
        System.out.println();

        // --- SCENARIO 4: Ankit successfully borrows the returned book ---
        System.out.println("----- SCENARIO 4: Ankit now borrows 'Clean Code' -----");
        library.borrowBook("MEMBER-002", "CC-1");
    }
}


// --- The Complex Subsystem Components ---

// 1. InventoryService: Manages the state of all book copies.
class InventoryService {
    private Map<String, BookItem> bookItems = new HashMap<>();

    public InventoryService() {
        // Pre-populate with some data
        bookItems.put("CC-1", new BookItem("Clean Code", "Robert C. Martin", "CC-1"));
        bookItems.put("DP-1", new BookItem("Design Patterns", "GoF", "DP-1"));
    }

    public boolean isAvailable(String barcode) {
        BookItem item = bookItems.get(barcode);
        return item != null && item.getStatus() == BookStatus.AVAILABLE;
    }

    public void updateStatus(String barcode, BookStatus status) {
        BookItem item = bookItems.get(barcode);
        if (item != null) {
            item.setStatus(status);
            System.out.println("  Inventory: Book '" + item.getTitle() + "' status updated to " + status);
        }
    }
}

// 2. MemberService: Manages member accounts.
class MemberService {
    private Map<String, Member> members = new HashMap<>();

    public MemberService() {
        // Pre-populate with some data
        members.put("MEMBER-001", new Member("MEMBER-001", "Tushit"));
        members.put("MEMBER-002", new Member("MEMBER-002", "Ankit"));
    }

    public boolean isActive(String memberId) {
        return members.containsKey(memberId); // Simplified check
    }
}

// 3. LoanService: Manages loan records.
class LoanService {
    private Map<String, Loan> loans = new HashMap<>();

    public void createLoan(String memberId, String barcode) {
        Loan loan = new Loan(memberId, barcode);
        loans.put(barcode, loan);
        System.out.println("  Loan Service: Loan created for member " + memberId + " for book " + barcode);
    }

    public void closeLoan(String barcode) {
        Loan loan = loans.get(barcode);
        if (loan != null) {
            loan.setReturnDate(new Date());
            System.out.println("  Loan Service: Loan for book " + barcode + " is now closed.");
        }
    }
}


// --- Helper Models ---
enum BookStatus { AVAILABLE, LOANED }

class BookItem {
    private String title;
    private String author;
    private String barcode;
    private BookStatus status;

    public BookItem(String title, String author, String barcode) {
        this.title = title;
        this.author = author;
        this.barcode = barcode;
        this.status = BookStatus.AVAILABLE;
    }
    public String getTitle() { return title; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
}

class Member {
    private String id;
    private String name;
    public Member(String id, String name) { this.id = id; this.name = name; }
}

class Loan {
    private String memberId;
    private String barcode;
    private Date loanDate;
    private Date returnDate;

    public Loan(String memberId, String barcode) {
        this.memberId = memberId;
        this.barcode = barcode;
        this.loanDate = new Date();
    }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
}


// --- The Facade Class ---

class LibraryFacade {
    // The facade holds references to all the complex subsystems.
    private InventoryService inventoryService;
    private MemberService memberService;
    private LoanService loanService;

    public LibraryFacade() {
        this.inventoryService = new InventoryService();
        this.memberService = new MemberService();
        this.loanService = new LoanService();
    }

    /**
     * A simple, unified method to borrow a book.
     * It hides the complex interactions between the subsystems.
     */
    public void borrowBook(String memberId, String barcode) {
        System.out.println("Processing borrow request for member " + memberId + " and book " + barcode + "...");
        
        // 1. Check member status
        if (!memberService.isActive(memberId)) {
            System.out.println("Error: Member account is not active.");
            return;
        }

        // 2. Check book availability
        if (!inventoryService.isAvailable(barcode)) {
            System.out.println("Error: Book is currently not available.");
            return;
        }

        // 3. If all checks pass, proceed with the loan
        inventoryService.updateStatus(barcode, BookStatus.LOANED);
        loanService.createLoan(memberId, barcode);
        
        System.out.println("Borrow request successful.");
    }

    /**
     * A simple, unified method to return a book.
     */
    public void returnBook(String barcode) {
        System.out.println("Processing return request for book " + barcode + "...");
        
        inventoryService.updateStatus(barcode, BookStatus.AVAILABLE);
        loanService.closeLoan(barcode);
        
        System.out.println("Return request successful.");
    }
}
