package com.majorproject.backend.techtraining;

import com.majorproject.backend.techtraining.dto.TechFormulaCardDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TechQuestionBank {

    private final List<TechQuestion> questions = new ArrayList<>();

    public TechQuestionBank() {
        buildOopQuestions();
        buildDbmsQuestions();
        buildOsQuestions();
        buildCnQuestions();
        buildDsaQuestions();
        buildWebQuestions();
    }

    public List<TechQuestion> getAllQuestions() {
        return questions;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // OOP — 15 questions
    // ═══════════════════════════════════════════════════════════════════════
    private void buildOopQuestions() {
        questions.add(TechQuestion.builder()
                .id("OOP-001").category(TechCategory.OOP).topic("Encapsulation").difficulty(TechDifficulty.EASY)
                .question("Which OOP principle restricts direct access to an object's internal state?")
                .options(List.of("Inheritance", "Polymorphism", "Encapsulation", "Abstraction"))
                .correctOptionIndex(2)
                .explanation("Encapsulation bundles data with the methods that operate on it and restricts direct access to some of the object's components using access modifiers.")
                .conceptTip("Encapsulation = data hiding via private fields + public getters/setters.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-002").category(TechCategory.OOP).topic("Inheritance").difficulty(TechDifficulty.EASY)
                .question("Which keyword is used in Java to inherit a class?")
                .options(List.of("implements", "extends", "inherits", "super"))
                .correctOptionIndex(1)
                .explanation("The 'extends' keyword is used to create a subclass that inherits from a parent class in Java.")
                .conceptTip("'extends' for class inheritance, 'implements' for interfaces.")
                .companiesAsked(List.of("TCS", "Cognizant")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-003").category(TechCategory.OOP).topic("Polymorphism").difficulty(TechDifficulty.MEDIUM)
                .question("What is the difference between method overloading and method overriding?")
                .options(List.of(
                        "Overloading changes return type; overriding changes parameters",
                        "Overloading is compile-time polymorphism; overriding is runtime polymorphism",
                        "Both are the same concept",
                        "Overloading requires inheritance; overriding does not"))
                .correctOptionIndex(1)
                .explanation("Method overloading (same name, different parameters) is resolved at compile time. Method overriding (same signature in parent-child) is resolved at runtime via dynamic dispatch.")
                .conceptTip("Overloading = static/compile-time binding. Overriding = dynamic/runtime binding.")
                .companiesAsked(List.of("Infosys", "Accenture", "Capgemini")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-004").category(TechCategory.OOP).topic("Abstraction").difficulty(TechDifficulty.MEDIUM)
                .question("Which of the following is TRUE about abstract classes in Java?")
                .options(List.of(
                        "Abstract classes can be instantiated directly",
                        "Abstract classes cannot have constructors",
                        "Abstract classes can have both abstract and concrete methods",
                        "Abstract classes must have all methods abstract"))
                .correctOptionIndex(2)
                .explanation("An abstract class can contain both abstract methods (without body) and concrete methods (with implementation). It cannot be instantiated directly but can have constructors used by subclasses.")
                .conceptTip("Abstract class = partial abstraction. Interface = full abstraction (pre-Java 8).")
                .companiesAsked(List.of("Wipro", "TCS", "HCL")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-005").category(TechCategory.OOP).topic("Interfaces").difficulty(TechDifficulty.MEDIUM)
                .question("From Java 8 onwards, interfaces can contain:")
                .options(List.of(
                        "Only abstract methods",
                        "Default methods and static methods",
                        "Instance variables",
                        "Constructors"))
                .correctOptionIndex(1)
                .explanation("Java 8 introduced default methods (with body using 'default' keyword) and static methods in interfaces. Interfaces still cannot have constructors or mutable instance variables.")
                .conceptTip("Java 8+ interfaces: abstract + default + static methods. Fields are always public static final.")
                .companiesAsked(List.of("Amazon", "Flipkart", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-006").category(TechCategory.OOP).topic("Inheritance").difficulty(TechDifficulty.HARD)
                .question("Why does Java not support multiple inheritance of classes?")
                .options(List.of(
                        "To reduce memory usage",
                        "To avoid the diamond problem (ambiguity)",
                        "Because the JVM cannot handle it",
                        "Because interfaces are always faster"))
                .correctOptionIndex(1)
                .explanation("Multiple class inheritance can cause the diamond problem where a class inherits two implementations of the same method from two parent classes, leading to ambiguity. Java avoids this by allowing single class inheritance but multiple interface implementation.")
                .conceptTip("Diamond problem: class D extends B, C; both B and C extend A and override method(). Which one does D use?")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-007").category(TechCategory.OOP).topic("Design Principles").difficulty(TechDifficulty.HARD)
                .question("Which SOLID principle states that a class should have only one reason to change?")
                .options(List.of(
                        "Open/Closed Principle",
                        "Liskov Substitution Principle",
                        "Single Responsibility Principle",
                        "Dependency Inversion Principle"))
                .correctOptionIndex(2)
                .explanation("The Single Responsibility Principle (SRP) states that every class should have only one reason to change, meaning it should have only one job or responsibility.")
                .conceptTip("SOLID: S=Single Responsibility, O=Open-Closed, L=Liskov, I=Interface Segregation, D=Dependency Inversion.")
                .companiesAsked(List.of("Google", "Amazon", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-008").category(TechCategory.OOP).topic("Encapsulation").difficulty(TechDifficulty.EASY)
                .question("Which access modifier provides the most restricted access in Java?")
                .options(List.of("public", "protected", "default (package-private)", "private"))
                .correctOptionIndex(3)
                .explanation("'private' members are accessible only within the same class. 'default' is accessible within the same package. 'protected' extends to subclasses. 'public' is accessible everywhere.")
                .conceptTip("Access levels: private < default < protected < public.")
                .companiesAsked(List.of("TCS", "Wipro", "Cognizant")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-009").category(TechCategory.OOP).topic("Polymorphism").difficulty(TechDifficulty.HARD)
                .question("What will happen if a subclass method has the same name as a parent's private method?")
                .options(List.of(
                        "It will override the parent method",
                        "Compilation error",
                        "It is treated as a new method, not an override",
                        "Runtime exception"))
                .correctOptionIndex(2)
                .explanation("Private methods are not visible to subclasses, so defining a method with the same name in the subclass is not overriding — it's a completely independent new method.")
                .conceptTip("Private and static methods cannot be overridden. They can be hidden (for static) or independently redefined (for private).")
                .companiesAsked(List.of("Amazon", "Flipkart")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-010").category(TechCategory.OOP).topic("Design Principles").difficulty(TechDifficulty.MEDIUM)
                .question("What does the Liskov Substitution Principle (LSP) state?")
                .options(List.of(
                        "Subclasses should not be substitutable for their base class",
                        "Objects of a superclass should be replaceable with objects of its subclasses without breaking the program",
                        "Every class must implement an interface",
                        "Use composition over inheritance always"))
                .correctOptionIndex(1)
                .explanation("LSP states that if S is a subtype of T, then objects of type T may be replaced with objects of type S without altering any desirable property of the program.")
                .conceptTip("LSP ensures behavioral compatibility: a Square should not break code expecting a Rectangle if Square restricts setWidth/setHeight independently.")
                .companiesAsked(List.of("Google", "Microsoft", "Adobe")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-011").category(TechCategory.OOP).topic("Abstraction").difficulty(TechDifficulty.EASY)
                .question("Which of the following cannot be instantiated directly?")
                .options(List.of("Concrete class", "Abstract class", "Final class", "Static class"))
                .correctOptionIndex(1)
                .explanation("Abstract classes cannot be instantiated directly; you must create a subclass that implements all abstract methods, then instantiate the subclass.")
                .conceptTip("new AbstractClass() → compile error. Must use: new ConcreteSubclass().")
                .companiesAsked(List.of("TCS", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-012").category(TechCategory.OOP).topic("Constructors").difficulty(TechDifficulty.MEDIUM)
                .question("What happens if a class has no explicit constructor in Java?")
                .options(List.of(
                        "Compilation error",
                        "The JVM provides a default no-argument constructor",
                        "Objects of that class cannot be created",
                        "A parameterized constructor is auto-generated"))
                .correctOptionIndex(1)
                .explanation("If no constructor is explicitly defined, the Java compiler automatically provides a default no-argument constructor. However, if any constructor is defined, the default is NOT provided.")
                .conceptTip("Define at least one constructor → default disappears. Use overloading for multiple constructors.")
                .companiesAsked(List.of("Wipro", "Cognizant", "TCS")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-013").category(TechCategory.OOP).topic("Interfaces").difficulty(TechDifficulty.HARD)
                .question("What is the purpose of the 'sealed' keyword introduced in Java 17 for classes?")
                .options(List.of(
                        "To make a class immutable",
                        "To restrict which classes can extend or implement it",
                        "To prevent method overriding",
                        "To mark a class for garbage collection"))
                .correctOptionIndex(1)
                .explanation("Sealed classes restrict which other classes or interfaces may extend or implement them, using 'permits' to list allowed subclasses. This gives the developer control over the class hierarchy.")
                .conceptTip("sealed class Shape permits Circle, Rectangle {} — only Circle and Rectangle can extend Shape.")
                .companiesAsked(List.of("Google", "Amazon")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-014").category(TechCategory.OOP).topic("Polymorphism").difficulty(TechDifficulty.EASY)
                .question("Which of the following is an example of compile-time polymorphism?")
                .options(List.of("Method overriding", "Method overloading", "Dynamic dispatch", "Virtual method invocation"))
                .correctOptionIndex(1)
                .explanation("Method overloading (same method name, different parameter lists) is resolved at compile time by the compiler based on the method signature.")
                .conceptTip("Compile-time: overloading, operator overloading. Runtime: overriding, dynamic method dispatch.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("OOP-015").category(TechCategory.OOP).topic("Design Principles").difficulty(TechDifficulty.HARD)
                .question("What does 'Composition over Inheritance' mean?")
                .options(List.of(
                        "Always use interfaces instead of classes",
                        "Prefer containing instances of other classes (has-a) over extending classes (is-a)",
                        "Use abstract classes for all designs",
                        "Avoid using any inheritance at all"))
                .correctOptionIndex(1)
                .explanation("Composition over inheritance means preferring to build complex types by combining simpler objects (has-a relationship) rather than inheriting from a base class (is-a relationship). This leads to more flexible and less coupled code.")
                .conceptTip("Inheritance = tight coupling, fragile base class problem. Composition = loose coupling, easier testing, flexible behavior.")
                .companiesAsked(List.of("Google", "Amazon", "Microsoft")).build());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DBMS — 15 questions
    // ═══════════════════════════════════════════════════════════════════════
    private void buildDbmsQuestions() {
        questions.add(TechQuestion.builder()
                .id("DBMS-001").category(TechCategory.DBMS).topic("Normalization").difficulty(TechDifficulty.EASY)
                .question("What is the primary goal of database normalization?")
                .options(List.of("Increase query speed", "Reduce data redundancy and anomalies", "Compress data storage", "Add more indexes"))
                .correctOptionIndex(1)
                .explanation("Normalization organizes data to reduce redundancy and dependency, preventing insertion, update, and deletion anomalies.")
                .conceptTip("1NF → atomic values. 2NF → no partial dependency. 3NF → no transitive dependency. BCNF → every determinant is a candidate key.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-002").category(TechCategory.DBMS).topic("SQL").difficulty(TechDifficulty.EASY)
                .question("Which SQL clause is used to filter rows AFTER grouping?")
                .options(List.of("WHERE", "HAVING", "GROUP BY", "ORDER BY"))
                .correctOptionIndex(1)
                .explanation("WHERE filters rows before grouping; HAVING filters groups after the GROUP BY aggregation.")
                .conceptTip("Execution order: FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT.")
                .companiesAsked(List.of("Accenture", "Capgemini", "TCS")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-003").category(TechCategory.DBMS).topic("Transactions").difficulty(TechDifficulty.MEDIUM)
                .question("What does the ACID property 'Isolation' ensure?")
                .options(List.of(
                        "All operations in a transaction succeed or none do",
                        "Concurrent transactions do not interfere with each other",
                        "Data is written to disk permanently",
                        "Database remains in a valid state"))
                .correctOptionIndex(1)
                .explanation("Isolation ensures that concurrent transactions execute as if they were sequential, preventing dirty reads, non-repeatable reads, and phantom reads depending on the isolation level.")
                .conceptTip("ACID: Atomicity=all-or-nothing, Consistency=valid state, Isolation=no interference, Durability=permanent.")
                .companiesAsked(List.of("Amazon", "Flipkart", "Oracle")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-004").category(TechCategory.DBMS).topic("Indexing").difficulty(TechDifficulty.MEDIUM)
                .question("What is the default data structure used for indexing in most RDBMS?")
                .options(List.of("Hash Table", "B+ Tree", "Binary Search Tree", "Red-Black Tree"))
                .correctOptionIndex(1)
                .explanation("Most RDBMS (MySQL, PostgreSQL, Oracle) use B+ trees for indexing because they support efficient range queries and maintain sorted order with O(log n) search, insert, and delete.")
                .conceptTip("B+ Tree: all data in leaves, internal nodes are guides. Hash index: O(1) exact match but no range queries.")
                .companiesAsked(List.of("Amazon", "Google", "Oracle")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-005").category(TechCategory.DBMS).topic("SQL").difficulty(TechDifficulty.MEDIUM)
                .question("What is the difference between DELETE and TRUNCATE in SQL?")
                .options(List.of(
                        "DELETE is DDL; TRUNCATE is DML",
                        "DELETE can have a WHERE clause and is logged row-by-row; TRUNCATE removes all rows and is minimally logged",
                        "TRUNCATE can have a WHERE clause; DELETE cannot",
                        "There is no difference"))
                .correctOptionIndex(1)
                .explanation("DELETE is DML (can use WHERE, triggers fire, logged per row, slower). TRUNCATE is DDL (no WHERE, no triggers, minimally logged, faster, resets auto-increment).")
                .conceptTip("TRUNCATE: fast, resets identity. DELETE: flexible, triggers execute, slower. DROP: removes table structure entirely.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro", "Cognizant")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-006").category(TechCategory.DBMS).topic("Joins").difficulty(TechDifficulty.EASY)
                .question("Which JOIN returns all rows from the left table and matched rows from the right table?")
                .options(List.of("INNER JOIN", "RIGHT JOIN", "LEFT JOIN", "CROSS JOIN"))
                .correctOptionIndex(2)
                .explanation("LEFT JOIN (LEFT OUTER JOIN) returns all rows from the left table. If no match is found in the right table, NULL values are returned for the right table's columns.")
                .conceptTip("INNER = matching rows only. LEFT = all left + matching right. RIGHT = all right + matching left. FULL = all rows from both.")
                .companiesAsked(List.of("TCS", "Wipro", "Accenture")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-007").category(TechCategory.DBMS).topic("Normalization").difficulty(TechDifficulty.HARD)
                .question("A relation is in BCNF if and only if:")
                .options(List.of(
                        "It has no multi-valued dependencies",
                        "Every determinant is a candidate key",
                        "It has no partial dependencies",
                        "All attributes are atomic"))
                .correctOptionIndex(1)
                .explanation("Boyce-Codd Normal Form (BCNF) requires that for every non-trivial functional dependency X→Y, X must be a superkey (or candidate key). This is stricter than 3NF.")
                .conceptTip("3NF allows non-prime → non-prime if determined by candidate key. BCNF doesn't allow any non-superkey determinant.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-008").category(TechCategory.DBMS).topic("Transactions").difficulty(TechDifficulty.HARD)
                .question("Which isolation level in SQL prevents phantom reads?")
                .options(List.of("READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE"))
                .correctOptionIndex(3)
                .explanation("SERIALIZABLE is the strictest isolation level. It prevents dirty reads, non-repeatable reads, AND phantom reads by effectively serializing transactions.")
                .conceptTip("READ UNCOMMITTED: dirty reads allowed. READ COMMITTED: no dirty. REPEATABLE READ: no non-repeatable. SERIALIZABLE: no phantoms.")
                .companiesAsked(List.of("Amazon", "Oracle", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-009").category(TechCategory.DBMS).topic("Keys").difficulty(TechDifficulty.EASY)
                .question("A foreign key in a table references:")
                .options(List.of(
                        "Any column in the same table",
                        "The primary key (or unique key) of another table",
                        "A column with NULL values only",
                        "An index in the same table"))
                .correctOptionIndex(1)
                .explanation("A foreign key creates a referential integrity constraint by pointing to the primary key (or a unique key) of another table, ensuring that the referenced value exists.")
                .conceptTip("FK constraints: ON DELETE CASCADE, SET NULL, RESTRICT, NO ACTION.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-010").category(TechCategory.DBMS).topic("SQL").difficulty(TechDifficulty.MEDIUM)
                .question("What does the COALESCE function do in SQL?")
                .options(List.of(
                        "Concatenates strings",
                        "Returns the first non-NULL value from a list of arguments",
                        "Converts data types",
                        "Counts non-NULL values"))
                .correctOptionIndex(1)
                .explanation("COALESCE takes multiple arguments and returns the first non-NULL value. It is commonly used to provide default values for nullable columns.")
                .conceptTip("COALESCE(col1, col2, 'default') — great for handling NULLs in SELECT queries.")
                .companiesAsked(List.of("Accenture", "Cognizant")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-011").category(TechCategory.DBMS).topic("ER Modeling").difficulty(TechDifficulty.MEDIUM)
                .question("In an ER diagram, a weak entity is identified by:")
                .options(List.of(
                        "Its own primary key alone",
                        "The primary key of its owner entity combined with its partial key",
                        "A foreign key reference",
                        "A composite attribute"))
                .correctOptionIndex(1)
                .explanation("A weak entity does not have a sufficient primary key on its own. It depends on the primary key of its owner (strong) entity combined with its own discriminator (partial key).")
                .conceptTip("Weak entity: double rectangle. Identifying relationship: double diamond. Partial key: dashed underline.")
                .companiesAsked(List.of("TCS", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-012").category(TechCategory.DBMS).topic("SQL").difficulty(TechDifficulty.HARD)
                .question("What is a correlated subquery?")
                .options(List.of(
                        "A subquery that runs once for the entire outer query",
                        "A subquery that references columns from the outer query and executes once per outer row",
                        "A subquery that uses only constants",
                        "A subquery inside a HAVING clause only"))
                .correctOptionIndex(1)
                .explanation("A correlated subquery references one or more columns from the outer query. Unlike a regular subquery that executes once, it executes once for each row processed by the outer query.")
                .conceptTip("Correlated subquery example: SELECT * FROM emp e WHERE salary > (SELECT AVG(salary) FROM emp WHERE dept = e.dept).")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-013").category(TechCategory.DBMS).topic("Indexing").difficulty(TechDifficulty.EASY)
                .question("What is the purpose of an index in a database?")
                .options(List.of(
                        "To enforce primary key constraints",
                        "To speed up data retrieval operations",
                        "To compress table data",
                        "To replicate data across servers"))
                .correctOptionIndex(1)
                .explanation("An index creates a separate data structure that allows the DBMS to find rows faster without scanning the entire table, trading extra storage and slower writes for faster reads.")
                .conceptTip("Index = phone book for data. Speeds up SELECT/WHERE but slows INSERT/UPDATE/DELETE due to index maintenance.")
                .companiesAsked(List.of("TCS", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-014").category(TechCategory.DBMS).topic("Transactions").difficulty(TechDifficulty.MEDIUM)
                .question("What is a deadlock in database systems?")
                .options(List.of(
                        "A query that takes too long to execute",
                        "A situation where two or more transactions wait indefinitely for each other to release locks",
                        "A corrupted index",
                        "A full transaction log"))
                .correctOptionIndex(1)
                .explanation("A deadlock occurs when two or more transactions are each waiting for a lock held by the other, creating a circular wait condition where none can proceed.")
                .conceptTip("Prevention: lock ordering, timeout, wait-die/wound-wait schemes. Detection: wait-for graph cycle detection.")
                .companiesAsked(List.of("Oracle", "Amazon", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DBMS-015").category(TechCategory.DBMS).topic("Joins").difficulty(TechDifficulty.HARD)
                .question("What is the result of a CROSS JOIN between a table with 5 rows and a table with 4 rows?")
                .options(List.of("9 rows", "20 rows", "5 rows", "4 rows"))
                .correctOptionIndex(1)
                .explanation("CROSS JOIN produces the Cartesian product of both tables: every row from the first table is paired with every row from the second. 5 × 4 = 20 rows.")
                .conceptTip("CROSS JOIN = Cartesian product. m rows × n rows = m*n rows. Rarely used directly; more common as implicit join without WHERE.")
                .companiesAsked(List.of("TCS", "Infosys")).build());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // OS — 15 questions
    // ═══════════════════════════════════════════════════════════════════════
    private void buildOsQuestions() {
        questions.add(TechQuestion.builder()
                .id("OS-001").category(TechCategory.OPERATING_SYSTEMS).topic("Process Management").difficulty(TechDifficulty.EASY)
                .question("What is the difference between a process and a thread?")
                .options(List.of(
                        "A thread is heavier than a process",
                        "A process has its own memory space; threads share the process's memory space",
                        "Threads cannot run concurrently",
                        "Processes are always single-threaded"))
                .correctOptionIndex(1)
                .explanation("A process is an independent execution unit with its own memory space. Threads are lightweight sub-units within a process that share the same memory, heap, and resources but have their own stack and program counter.")
                .conceptTip("Thread = lightweight process. Shared: code, data, heap, files. Own: stack, PC, registers.")
                .companiesAsked(List.of("TCS", "Infosys", "Amazon")).build());

        questions.add(TechQuestion.builder()
                .id("OS-002").category(TechCategory.OPERATING_SYSTEMS).topic("Scheduling").difficulty(TechDifficulty.MEDIUM)
                .question("Which CPU scheduling algorithm can cause starvation?")
                .options(List.of("Round Robin", "FCFS", "Shortest Job First (SJF)", "All of the above"))
                .correctOptionIndex(2)
                .explanation("SJF (especially non-preemptive) can starve long processes because shorter processes keep getting scheduled first. FCFS is fair but slow. Round Robin prevents starvation via time slicing.")
                .conceptTip("Solution to SJF starvation: Aging — gradually increase priority of waiting processes.")
                .companiesAsked(List.of("Wipro", "Cognizant", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("OS-003").category(TechCategory.OPERATING_SYSTEMS).topic("Deadlocks").difficulty(TechDifficulty.MEDIUM)
                .question("Which of the following is NOT a necessary condition for deadlock?")
                .options(List.of("Mutual Exclusion", "Hold and Wait", "Preemption", "Circular Wait"))
                .correctOptionIndex(2)
                .explanation("The four necessary conditions for deadlock are: Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait. Preemption (allowing resource forceful takeover) actually prevents deadlock.")
                .conceptTip("Deadlock conditions: Mutual exclusion + Hold & wait + No preemption + Circular wait. Break any one → no deadlock.")
                .companiesAsked(List.of("Amazon", "Google", "TCS")).build());

        questions.add(TechQuestion.builder()
                .id("OS-004").category(TechCategory.OPERATING_SYSTEMS).topic("Memory Management").difficulty(TechDifficulty.EASY)
                .question("What is virtual memory?")
                .options(List.of(
                        "Physical RAM installed on the motherboard",
                        "A technique that uses disk space to extend available memory beyond physical RAM",
                        "Cache memory in the CPU",
                        "ROM used for booting"))
                .correctOptionIndex(1)
                .explanation("Virtual memory allows the OS to use disk space (swap/page file) to simulate additional RAM, enabling processes to use more memory than physically available through page swapping.")
                .conceptTip("Virtual memory: logical address → page table → physical address. Pages on disk = swap space.")
                .companiesAsked(List.of("TCS", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("OS-005").category(TechCategory.OPERATING_SYSTEMS).topic("Memory Management").difficulty(TechDifficulty.HARD)
                .question("What is thrashing in operating systems?")
                .options(List.of(
                        "Excessive context switching between processes",
                        "A situation where the system spends more time swapping pages than executing processes",
                        "A deadlock between memory-intensive processes",
                        "Corrupted memory pages"))
                .correctOptionIndex(1)
                .explanation("Thrashing occurs when a system's page fault rate is so high that the OS spends almost all its time swapping pages in and out of disk, leaving little time for actual process execution. CPU utilization drops drastically.")
                .conceptTip("Thrashing solution: increase RAM, use working set model, reduce multiprogramming degree, use local page replacement.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("OS-006").category(TechCategory.OPERATING_SYSTEMS).topic("Scheduling").difficulty(TechDifficulty.EASY)
                .question("In Round Robin scheduling, what determines when a process is preempted?")
                .options(List.of("Process priority", "Time quantum expiry", "Memory usage", "I/O request"))
                .correctOptionIndex(1)
                .explanation("In Round Robin, each process gets a fixed time quantum (time slice). When the quantum expires, the running process is preempted and moved to the back of the ready queue.")
                .conceptTip("Small quantum → more context switches, responsive. Large quantum → approaches FCFS. Typical: 10–100ms.")
                .companiesAsked(List.of("TCS", "Infosys", "Cognizant")).build());

        questions.add(TechQuestion.builder()
                .id("OS-007").category(TechCategory.OPERATING_SYSTEMS).topic("Synchronization").difficulty(TechDifficulty.MEDIUM)
                .question("What is a semaphore in OS?")
                .options(List.of(
                        "A CPU register for scheduling",
                        "A signaling mechanism used for process synchronization and controlling access to shared resources",
                        "A type of memory allocation algorithm",
                        "A hardware interrupt handler"))
                .correctOptionIndex(1)
                .explanation("A semaphore is a synchronization tool that uses an integer variable to control access to shared resources. Operations: wait() (P) decrements; signal() (V) increments. Binary semaphore = mutex.")
                .conceptTip("Counting semaphore: controls access to resource with N instances. Binary semaphore: 0 or 1, acts like a lock/mutex.")
                .companiesAsked(List.of("Amazon", "Flipkart", "Oracle")).build());

        questions.add(TechQuestion.builder()
                .id("OS-008").category(TechCategory.OPERATING_SYSTEMS).topic("Memory Management").difficulty(TechDifficulty.MEDIUM)
                .question("What is the difference between paging and segmentation?")
                .options(List.of(
                        "Paging divides memory into fixed-size pages; segmentation divides into variable-size logical segments",
                        "Segmentation uses fixed sizes; paging uses variable sizes",
                        "Both are identical concepts",
                        "Paging is a software concept; segmentation is hardware only"))
                .correctOptionIndex(0)
                .explanation("Paging divides physical memory into fixed-size frames and logical memory into equal-sized pages. Segmentation divides memory into variable-sized segments based on logical units (code, stack, data).")
                .conceptTip("Paging: no external fragmentation, has internal. Segmentation: no internal fragmentation, has external.")
                .companiesAsked(List.of("TCS", "Wipro", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("OS-009").category(TechCategory.OPERATING_SYSTEMS).topic("File Systems").difficulty(TechDifficulty.EASY)
                .question("What does an inode store in a Unix file system?")
                .options(List.of(
                        "File name and content",
                        "File metadata (permissions, size, timestamps, disk block pointers) but NOT the file name",
                        "Only the file content",
                        "Directory structure only"))
                .correctOptionIndex(1)
                .explanation("An inode stores metadata about a file: owner, permissions, timestamps, size, and pointers to data blocks on disk. The file name is stored in the directory entry, not the inode.")
                .conceptTip("Directory entry: (filename → inode number). Inode: (metadata + block pointers). Hard links share the same inode.")
                .companiesAsked(List.of("Amazon", "Google")).build());

        questions.add(TechQuestion.builder()
                .id("OS-010").category(TechCategory.OPERATING_SYSTEMS).topic("Deadlocks").difficulty(TechDifficulty.HARD)
                .question("Banker's Algorithm is used for:")
                .options(List.of(
                        "CPU scheduling",
                        "Deadlock avoidance by checking safe states before resource allocation",
                        "Memory allocation",
                        "Disk scheduling"))
                .correctOptionIndex(1)
                .explanation("Banker's Algorithm checks whether granting a resource request will leave the system in a safe state (where all processes can eventually finish). If not, the request is deferred to avoid deadlock.")
                .conceptTip("Need[i] = Max[i] - Allocation[i]. Safe state: exists a sequence where every process can finish.")
                .companiesAsked(List.of("TCS", "Amazon", "Google")).build());

        questions.add(TechQuestion.builder()
                .id("OS-011").category(TechCategory.OPERATING_SYSTEMS).topic("Process Management").difficulty(TechDifficulty.MEDIUM)
                .question("What are the different states of a process?")
                .options(List.of(
                        "Start, Run, Stop",
                        "New, Ready, Running, Waiting, Terminated",
                        "Active, Inactive, Blocked",
                        "Created, Executing, Completed"))
                .correctOptionIndex(1)
                .explanation("The 5-state process model includes: New (created), Ready (waiting for CPU), Running (executing), Waiting/Blocked (waiting for I/O or event), and Terminated (finished).")
                .conceptTip("Transitions: New→Ready (admitted), Ready→Running (dispatch), Running→Ready (interrupt/preempt), Running→Waiting (I/O), Waiting→Ready (I/O done).")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("OS-012").category(TechCategory.OPERATING_SYSTEMS).topic("Memory Management").difficulty(TechDifficulty.HARD)
                .question("What page replacement algorithm is known as the 'optimal' algorithm?")
                .options(List.of("FIFO", "LRU", "OPT (Bélády's Algorithm)", "Clock"))
                .correctOptionIndex(2)
                .explanation("OPT (Bélády's Optimal Algorithm) replaces the page that will not be used for the longest period in the future. It has the lowest page fault rate but is impractical since future references are unknown.")
                .conceptTip("OPT = theoretical best (needs future knowledge). LRU = practical best approximation (uses past behavior). FIFO = simplest but suffers Bélády's anomaly.")
                .companiesAsked(List.of("Google", "Amazon")).build());

        questions.add(TechQuestion.builder()
                .id("OS-013").category(TechCategory.OPERATING_SYSTEMS).topic("Synchronization").difficulty(TechDifficulty.HARD)
                .question("What is priority inversion?")
                .options(List.of(
                        "A high-priority process runs before a low-priority one",
                        "A high-priority process is blocked because a low-priority process holds a needed resource",
                        "Two processes with the same priority cause a conflict",
                        "The scheduler assigns wrong priorities"))
                .correctOptionIndex(1)
                .explanation("Priority inversion occurs when a high-priority process is indirectly blocked by a low-priority process holding a shared resource. A medium-priority process can preempt the low-priority one, making the high-priority process wait even longer.")
                .conceptTip("Solution: Priority Inheritance Protocol — temporarily raise the low-priority holder's priority to the blocked high-priority level.")
                .companiesAsked(List.of("Google", "Amazon", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("OS-014").category(TechCategory.OPERATING_SYSTEMS).topic("Process Management").difficulty(TechDifficulty.EASY)
                .question("What system call is used to create a new process in Unix/Linux?")
                .options(List.of("exec()", "fork()", "spawn()", "create()"))
                .correctOptionIndex(1)
                .explanation("fork() creates a new child process by duplicating the calling parent process. The child gets a copy of the parent's address space. exec() replaces the process image with a new program.")
                .conceptTip("fork() returns 0 to child, child PID to parent, -1 on error. fork() + exec() = common pattern to run a new program.")
                .companiesAsked(List.of("Amazon", "Google", "TCS")).build());

        questions.add(TechQuestion.builder()
                .id("OS-015").category(TechCategory.OPERATING_SYSTEMS).topic("Scheduling").difficulty(TechDifficulty.MEDIUM)
                .question("Which scheduling algorithm gives the minimum average waiting time?")
                .options(List.of("FCFS", "Round Robin", "SJF (Shortest Job First)", "Priority Scheduling"))
                .correctOptionIndex(2)
                .explanation("SJF (non-preemptive or its preemptive variant SRTF) is proven to give the minimum average waiting time for a given set of processes.")
                .conceptTip("SJF is optimal for avg waiting time but needs burst time prediction. Exponential averaging: τ(n+1) = α*t(n) + (1-α)*τ(n).")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CN — 15 questions
    // ═══════════════════════════════════════════════════════════════════════
    private void buildCnQuestions() {
        questions.add(TechQuestion.builder()
                .id("CN-001").category(TechCategory.COMPUTER_NETWORKS).topic("OSI Model").difficulty(TechDifficulty.EASY)
                .question("How many layers does the OSI model have?")
                .options(List.of("4", "5", "7", "6"))
                .correctOptionIndex(2)
                .explanation("The OSI (Open Systems Interconnection) model has 7 layers: Physical, Data Link, Network, Transport, Session, Presentation, Application.")
                .conceptTip("Mnemonic: 'Please Do Not Throw Sausage Pizza Away' — Physical, Data Link, Network, Transport, Session, Presentation, Application.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("CN-002").category(TechCategory.COMPUTER_NETWORKS).topic("TCP vs UDP").difficulty(TechDifficulty.EASY)
                .question("Which protocol provides reliable, ordered delivery of data?")
                .options(List.of("UDP", "TCP", "ICMP", "ARP"))
                .correctOptionIndex(1)
                .explanation("TCP (Transmission Control Protocol) provides reliable, ordered, connection-oriented data delivery with error checking, flow control, and congestion control. UDP is unreliable but faster.")
                .conceptTip("TCP: 3-way handshake, ACKs, retransmission. UDP: fire-and-forget, used for streaming, DNS, gaming.")
                .companiesAsked(List.of("TCS", "Cognizant")).build());

        questions.add(TechQuestion.builder()
                .id("CN-003").category(TechCategory.COMPUTER_NETWORKS).topic("IP Addressing").difficulty(TechDifficulty.MEDIUM)
                .question("What is the purpose of a subnet mask?")
                .options(List.of(
                        "To encrypt data packets",
                        "To divide an IP address into network and host portions",
                        "To assign MAC addresses",
                        "To route packets between continents"))
                .correctOptionIndex(1)
                .explanation("A subnet mask identifies which portion of an IP address is the network address and which is the host address, allowing routers to determine if a destination is on the local subnet or needs routing.")
                .conceptTip("AND operation: IP & Mask = Network Address. Example: 192.168.1.10 & 255.255.255.0 = 192.168.1.0 (network).")
                .companiesAsked(List.of("Wipro", "Infosys", "Accenture")).build());

        questions.add(TechQuestion.builder()
                .id("CN-004").category(TechCategory.COMPUTER_NETWORKS).topic("DNS").difficulty(TechDifficulty.EASY)
                .question("What does DNS stand for and what does it do?")
                .options(List.of(
                        "Data Network System — manages network bandwidth",
                        "Domain Name System — translates domain names to IP addresses",
                        "Digital Naming Service — assigns names to devices",
                        "Dynamic Network Switching — routes traffic dynamically"))
                .correctOptionIndex(1)
                .explanation("DNS (Domain Name System) translates human-readable domain names (e.g., google.com) into IP addresses (e.g., 142.250.190.46) that computers use to locate resources on a network.")
                .conceptTip("DNS hierarchy: Root → TLD (.com) → Authoritative (google.com). Query types: recursive (resolver does all work) vs iterative (step-by-step referrals).")
                .companiesAsked(List.of("TCS", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("CN-005").category(TechCategory.COMPUTER_NETWORKS).topic("HTTP").difficulty(TechDifficulty.MEDIUM)
                .question("What is the difference between HTTP and HTTPS?")
                .options(List.of(
                        "HTTPS is faster than HTTP",
                        "HTTPS uses TLS/SSL encryption for secure data transfer",
                        "HTTP supports larger payloads",
                        "There is no significant difference"))
                .correctOptionIndex(1)
                .explanation("HTTPS (HTTP Secure) encrypts data using TLS/SSL, providing confidentiality, integrity, and authentication. HTTP transmits data in plaintext, making it vulnerable to eavesdropping and MITM attacks.")
                .conceptTip("HTTPS: port 443 (vs HTTP 80). TLS handshake: client hello → server certificate → key exchange → encrypted communication.")
                .companiesAsked(List.of("Amazon", "Google", "Flipkart")).build());

        questions.add(TechQuestion.builder()
                .id("CN-006").category(TechCategory.COMPUTER_NETWORKS).topic("TCP vs UDP").difficulty(TechDifficulty.MEDIUM)
                .question("What is the purpose of the TCP 3-way handshake?")
                .options(List.of(
                        "To encrypt data before transmission",
                        "To establish a reliable connection between client and server",
                        "To assign IP addresses",
                        "To resolve domain names"))
                .correctOptionIndex(1)
                .explanation("The TCP 3-way handshake (SYN → SYN-ACK → ACK) establishes a reliable connection by synchronizing sequence numbers and confirming that both sides are ready to communicate.")
                .conceptTip("SYN(seq=x) → SYN-ACK(seq=y, ack=x+1) → ACK(ack=y+1). Connection termination: 4-way (FIN → ACK → FIN → ACK).")
                .companiesAsked(List.of("Amazon", "Google", "TCS")).build());

        questions.add(TechQuestion.builder()
                .id("CN-007").category(TechCategory.COMPUTER_NETWORKS).topic("Routing").difficulty(TechDifficulty.HARD)
                .question("What is the main difference between distance-vector and link-state routing protocols?")
                .options(List.of(
                        "Distance-vector uses flooding; link-state uses hop count",
                        "Distance-vector shares routing tables with neighbors; link-state shares topology info with all routers",
                        "Link-state is slower to converge",
                        "There is no difference in principle"))
                .correctOptionIndex(1)
                .explanation("Distance-vector protocols (RIP) share entire routing tables with direct neighbors and use hop count. Link-state protocols (OSPF) flood topology information to all routers, which then independently compute shortest paths using Dijkstra's algorithm.")
                .conceptTip("Distance-vector: Bellman-Ford, slow convergence, count-to-infinity problem. Link-state: Dijkstra, fast convergence, more memory/CPU.")
                .companiesAsked(List.of("Amazon", "Cisco", "Google")).build());

        questions.add(TechQuestion.builder()
                .id("CN-008").category(TechCategory.COMPUTER_NETWORKS).topic("IP Addressing").difficulty(TechDifficulty.HARD)
                .question("How many usable host addresses are in a /24 subnet?")
                .options(List.of("256", "254", "255", "252"))
                .correctOptionIndex(1)
                .explanation("/24 means 24 bits for network, 8 bits for hosts. 2^8 = 256 total addresses, but 2 are reserved (network address .0 and broadcast address .255), leaving 254 usable host addresses.")
                .conceptTip("Usable hosts = 2^(32-prefix) - 2. Example: /28 = 2^4 - 2 = 14 hosts. /30 = 2 hosts (point-to-point link).")
                .companiesAsked(List.of("Amazon", "Google", "Cisco")).build());

        questions.add(TechQuestion.builder()
                .id("CN-009").category(TechCategory.COMPUTER_NETWORKS).topic("OSI Model").difficulty(TechDifficulty.MEDIUM)
                .question("At which OSI layer does a router operate?")
                .options(List.of("Layer 1 — Physical", "Layer 2 — Data Link", "Layer 3 — Network", "Layer 4 — Transport"))
                .correctOptionIndex(2)
                .explanation("Routers operate at Layer 3 (Network layer), making forwarding decisions based on IP addresses. Switches operate at Layer 2 (Data Link) using MAC addresses. Hubs at Layer 1 (Physical).")
                .conceptTip("Hub: L1 (broadcasts all). Switch: L2 (MAC table). Router: L3 (IP routing). Firewall: L3-L7.")
                .companiesAsked(List.of("TCS", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("CN-010").category(TechCategory.COMPUTER_NETWORKS).topic("HTTP").difficulty(TechDifficulty.EASY)
                .question("Which HTTP status code indicates 'Not Found'?")
                .options(List.of("200", "301", "403", "404"))
                .correctOptionIndex(3)
                .explanation("HTTP 404 means the requested resource was not found on the server. 200=OK, 301=Moved Permanently, 403=Forbidden, 500=Internal Server Error.")
                .conceptTip("2xx=success, 3xx=redirection, 4xx=client error, 5xx=server error.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("CN-011").category(TechCategory.COMPUTER_NETWORKS).topic("Network Security").difficulty(TechDifficulty.MEDIUM)
                .question("What is a Man-in-the-Middle (MITM) attack?")
                .options(List.of(
                        "An attacker sends excessive requests to crash a server",
                        "An attacker intercepts and possibly alters communication between two parties without their knowledge",
                        "An attacker guesses a user's password",
                        "An attacker injects malicious code into a website"))
                .correctOptionIndex(1)
                .explanation("In a MITM attack, the attacker secretly positions themselves between two communicating parties, intercepting and potentially modifying their messages while both parties believe they are communicating directly.")
                .conceptTip("Prevention: HTTPS/TLS, certificate pinning, HSTS. Detection: unexpected certificate changes.")
                .companiesAsked(List.of("Amazon", "Google")).build());

        questions.add(TechQuestion.builder()
                .id("CN-012").category(TechCategory.COMPUTER_NETWORKS).topic("Protocols").difficulty(TechDifficulty.EASY)
                .question("Which protocol is used to assign IP addresses automatically to devices on a network?")
                .options(List.of("DNS", "DHCP", "ARP", "SMTP"))
                .correctOptionIndex(1)
                .explanation("DHCP (Dynamic Host Configuration Protocol) automatically assigns IP addresses, subnet masks, default gateways, and DNS server addresses to devices joining a network.")
                .conceptTip("DHCP process: Discover → Offer → Request → Acknowledge (DORA). Lease time determines how long the IP is valid.")
                .companiesAsked(List.of("TCS", "Wipro", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("CN-013").category(TechCategory.COMPUTER_NETWORKS).topic("Protocols").difficulty(TechDifficulty.MEDIUM)
                .question("What is ARP used for?")
                .options(List.of(
                        "Resolving domain names to IP addresses",
                        "Resolving IP addresses to MAC addresses",
                        "Encrypting network traffic",
                        "Managing network bandwidth"))
                .correctOptionIndex(1)
                .explanation("ARP (Address Resolution Protocol) maps a known IP address to a MAC (hardware) address on a local network. When a device knows the destination IP but not the MAC, it broadcasts an ARP request.")
                .conceptTip("ARP: IP → MAC (local network). RARP: MAC → IP (reverse). ARP cache: stores recent mappings to avoid repeated requests.")
                .companiesAsked(List.of("TCS", "Cognizant")).build());

        questions.add(TechQuestion.builder()
                .id("CN-014").category(TechCategory.COMPUTER_NETWORKS).topic("Network Security").difficulty(TechDifficulty.HARD)
                .question("What is the difference between symmetric and asymmetric encryption?")
                .options(List.of(
                        "Symmetric uses one key for both encryption and decryption; asymmetric uses a public-private key pair",
                        "Asymmetric uses one key; symmetric uses two keys",
                        "Both use the same approach but different algorithms",
                        "Symmetric is for networks only; asymmetric is for storage only"))
                .correctOptionIndex(0)
                .explanation("Symmetric encryption (AES, DES) uses one shared secret key for both encryption and decryption — fast but key distribution is hard. Asymmetric (RSA, ECC) uses a public key for encryption and private key for decryption — slower but solves key distribution.")
                .conceptTip("TLS uses both: asymmetric for key exchange (handshake), then symmetric (AES) for bulk data encryption.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("CN-015").category(TechCategory.COMPUTER_NETWORKS).topic("Routing").difficulty(TechDifficulty.HARD)
                .question("What routing protocol uses Dijkstra's algorithm for shortest path calculation?")
                .options(List.of("RIP", "BGP", "OSPF", "EIGRP"))
                .correctOptionIndex(2)
                .explanation("OSPF (Open Shortest Path First) is a link-state routing protocol that uses Dijkstra's Shortest Path First (SPF) algorithm. Each router has a complete topology map and independently computes the best paths.")
                .conceptTip("OSPF: link-state, Dijkstra, fast convergence, area-based hierarchy. RIP: distance-vector, Bellman-Ford, max 15 hops.")
                .companiesAsked(List.of("Cisco", "Amazon", "Google")).build());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DSA — 15 questions
    // ═══════════════════════════════════════════════════════════════════════
    private void buildDsaQuestions() {
        questions.add(TechQuestion.builder()
                .id("DSA-001").category(TechCategory.DSA).topic("Arrays").difficulty(TechDifficulty.EASY)
                .question("What is the time complexity of accessing an element by index in an array?")
                .options(List.of("O(n)", "O(log n)", "O(1)", "O(n²)"))
                .correctOptionIndex(2)
                .explanation("Arrays provide O(1) constant-time random access because elements are stored in contiguous memory locations, and the address of any element can be calculated directly using the base address + offset.")
                .conceptTip("Array access: O(1). Insertion/deletion at arbitrary position: O(n) due to shifting. Search (unsorted): O(n). Search (sorted): O(log n) with binary search.")
                .companiesAsked(List.of("TCS", "Infosys", "Amazon")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-002").category(TechCategory.DSA).topic("Linked Lists").difficulty(TechDifficulty.EASY)
                .question("Which data structure uses nodes connected by pointers?")
                .options(List.of("Array", "Linked List", "Stack", "Queue"))
                .correctOptionIndex(1)
                .explanation("A linked list consists of nodes where each node contains data and a pointer (reference) to the next node. Unlike arrays, linked lists don't require contiguous memory allocation.")
                .conceptTip("Singly: next pointer only. Doubly: next + prev pointers. Circular: last node points to first. Insertion/deletion at known position: O(1).")
                .companiesAsked(List.of("TCS", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-003").category(TechCategory.DSA).topic("Trees").difficulty(TechDifficulty.MEDIUM)
                .question("What is the maximum number of nodes in a binary tree of height h?")
                .options(List.of("2^h", "2^(h+1) - 1", "2*h", "h^2"))
                .correctOptionIndex(1)
                .explanation("A binary tree of height h (root at height 0) has at most 2^(h+1) - 1 nodes when it is a complete (perfect) binary tree. Each level i has at most 2^i nodes.")
                .conceptTip("Height h: max nodes = 2^(h+1)-1 (perfect tree). Min nodes = h+1 (skewed tree). Balanced BST: height = O(log n).")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-004").category(TechCategory.DSA).topic("Sorting").difficulty(TechDifficulty.EASY)
                .question("What is the average time complexity of QuickSort?")
                .options(List.of("O(n)", "O(n log n)", "O(n²)", "O(log n)"))
                .correctOptionIndex(1)
                .explanation("QuickSort has an average-case time complexity of O(n log n) using divide-and-conquer. The worst case is O(n²) when the pivot selection is consistently poor (already sorted array with first/last pivot).")
                .conceptTip("QuickSort: avg O(n log n), worst O(n²). In-place, not stable. Optimization: random pivot, 3-way partition, median-of-3.")
                .companiesAsked(List.of("TCS", "Infosys", "Amazon")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-005").category(TechCategory.DSA).topic("Stacks").difficulty(TechDifficulty.EASY)
                .question("Which data structure follows LIFO (Last In, First Out) order?")
                .options(List.of("Queue", "Stack", "Deque", "Priority Queue"))
                .correctOptionIndex(1)
                .explanation("A Stack follows LIFO — the last element pushed is the first one popped. Operations: push(), pop(), peek(). Used in function call stacks, undo operations, expression evaluation, and DFS.")
                .conceptTip("Stack: LIFO. Queue: FIFO. Applications: balanced parentheses, infix-to-postfix conversion, DFS, backtracking.")
                .companiesAsked(List.of("TCS", "Wipro", "Cognizant")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-006").category(TechCategory.DSA).topic("Graphs").difficulty(TechDifficulty.MEDIUM)
                .question("Which algorithm is used for finding the shortest path in an unweighted graph?")
                .options(List.of("Dijkstra's", "BFS", "DFS", "Bellman-Ford"))
                .correctOptionIndex(1)
                .explanation("BFS (Breadth-First Search) naturally finds the shortest path in an unweighted graph because it explores all vertices at distance d before any vertex at distance d+1.")
                .conceptTip("Unweighted shortest path: BFS. Weighted (non-negative): Dijkstra. Weighted (negative edges): Bellman-Ford. All-pairs: Floyd-Warshall.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-007").category(TechCategory.DSA).topic("Hashing").difficulty(TechDifficulty.MEDIUM)
                .question("What is the average time complexity of search in a hash table?")
                .options(List.of("O(n)", "O(log n)", "O(1)", "O(n log n)"))
                .correctOptionIndex(2)
                .explanation("Hash tables provide O(1) average-case time for search, insert, and delete by using a hash function to map keys to array indices. Worst case (all keys collide) is O(n).")
                .conceptTip("Collision handling: chaining (linked lists at each bucket) or open addressing (linear probing, quadratic probing, double hashing). Load factor = n/m.")
                .companiesAsked(List.of("Amazon", "Google", "Flipkart")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-008").category(TechCategory.DSA).topic("Dynamic Programming").difficulty(TechDifficulty.HARD)
                .question("What are the two key properties required for a problem to be solvable by Dynamic Programming?")
                .options(List.of(
                        "Greedy choice property and optimal substructure",
                        "Optimal substructure and overlapping subproblems",
                        "Divide and conquer with merge step",
                        "Memoization and recursion only"))
                .correctOptionIndex(1)
                .explanation("DP requires: 1) Optimal substructure — an optimal solution contains optimal solutions to subproblems, and 2) Overlapping subproblems — the same subproblems are solved multiple times. DP stores results to avoid redundant computation.")
                .conceptTip("Top-down: memoization (recursion + cache). Bottom-up: tabulation (iterative fill). Classic: Fibonacci, Knapsack, LCS, LIS.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-009").category(TechCategory.DSA).topic("Trees").difficulty(TechDifficulty.MEDIUM)
                .question("What is the time complexity of search in a balanced BST?")
                .options(List.of("O(n)", "O(log n)", "O(1)", "O(n²)"))
                .correctOptionIndex(1)
                .explanation("In a balanced BST (like AVL or Red-Black tree), the height is O(log n), so search takes O(log n) time. An unbalanced BST (skewed) degrades to O(n).")
                .conceptTip("BST property: left < root < right. Balanced variants: AVL (strict balance, slower insert), Red-Black (relaxed, faster insert), B-Tree (disk-based, databases).")
                .companiesAsked(List.of("Amazon", "Google")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-010").category(TechCategory.DSA).topic("Graphs").difficulty(TechDifficulty.HARD)
                .question("What is Dijkstra's algorithm unable to handle correctly?")
                .options(List.of("Undirected graphs", "Weighted graphs", "Graphs with negative edge weights", "Dense graphs"))
                .correctOptionIndex(2)
                .explanation("Dijkstra's algorithm assumes non-negative edge weights. With negative edges, once a node is 'finalized,' its distance might not actually be shortest, leading to incorrect results. Use Bellman-Ford for negative weights.")
                .conceptTip("Dijkstra: greedy, O((V+E)log V) with min-heap. No negative edges. Bellman-Ford: O(VE), handles negative edges, detects negative cycles.")
                .companiesAsked(List.of("Google", "Amazon", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-011").category(TechCategory.DSA).topic("Sorting").difficulty(TechDifficulty.MEDIUM)
                .question("Which sorting algorithm is stable and has O(n log n) worst-case complexity?")
                .options(List.of("QuickSort", "HeapSort", "MergeSort", "Selection Sort"))
                .correctOptionIndex(2)
                .explanation("MergeSort is stable (preserves relative order of equal elements) and guarantees O(n log n) in all cases. QuickSort is unstable with O(n²) worst case. HeapSort is O(n log n) but unstable.")
                .conceptTip("Stable sorts: Merge, Insertion, Bubble, Tim. Unstable: Quick, Heap, Selection. In-place: Quick, Heap, Insertion, Selection.")
                .companiesAsked(List.of("TCS", "Amazon", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-012").category(TechCategory.DSA).topic("Heaps").difficulty(TechDifficulty.MEDIUM)
                .question("In a max-heap, the parent node is always:")
                .options(List.of(
                        "Smaller than its children",
                        "Greater than or equal to its children",
                        "Equal to its children",
                        "Unrelated to its children"))
                .correctOptionIndex(1)
                .explanation("In a max-heap, the value of each parent node is greater than or equal to the values of its children. The root holds the maximum element. In a min-heap, the parent is less than or equal to children.")
                .conceptTip("Max-heap: root is max, used for heap sort and max priority queue. Min-heap: root is min, used for Dijkstra, Prim's, min priority queue.")
                .companiesAsked(List.of("Amazon", "Google")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-013").category(TechCategory.DSA).topic("Dynamic Programming").difficulty(TechDifficulty.HARD)
                .question("What is the time complexity of the 0/1 Knapsack problem using DP?")
                .options(List.of("O(n)", "O(n*W)", "O(2^n)", "O(n²)"))
                .correctOptionIndex(1)
                .explanation("The 0/1 Knapsack DP solution builds a 2D table of size n×W (items × capacity), filling each cell in O(1) time. Total: O(n*W). This is pseudo-polynomial because W is not polynomial in the input size.")
                .conceptTip("dp[i][w] = max(dp[i-1][w], val[i] + dp[i-1][w-wt[i]]). Space optimization: 1D array, iterate W backwards.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-014").category(TechCategory.DSA).topic("Linked Lists").difficulty(TechDifficulty.MEDIUM)
                .question("How do you detect a cycle in a linked list efficiently?")
                .options(List.of(
                        "Use a hash set to store visited nodes",
                        "Floyd's Cycle Detection (tortoise and hare) — O(1) space",
                        "Both A and B work, but B is more space efficient",
                        "Cycles cannot be detected"))
                .correctOptionIndex(2)
                .explanation("Both approaches work. Hash set uses O(n) space. Floyd's algorithm uses two pointers (slow moves 1 step, fast moves 2 steps) and detects a cycle in O(n) time with O(1) space.")
                .conceptTip("Floyd's: if fast meets slow → cycle exists. To find cycle start: reset one pointer to head, move both at speed 1 → they meet at cycle start.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("DSA-015").category(TechCategory.DSA).topic("Searching").difficulty(TechDifficulty.EASY)
                .question("What is the time complexity of binary search on a sorted array?")
                .options(List.of("O(n)", "O(log n)", "O(n log n)", "O(1)"))
                .correctOptionIndex(1)
                .explanation("Binary search repeatedly halves the search space by comparing the target with the middle element. Each comparison eliminates half the remaining elements, giving O(log n) time.")
                .conceptTip("Precondition: array must be sorted. Variants: lower_bound, upper_bound, search in rotated sorted array, binary search on answer.")
                .companiesAsked(List.of("TCS", "Infosys", "Amazon")).build());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Web Technologies — 15 questions
    // ═══════════════════════════════════════════════════════════════════════
    private void buildWebQuestions() {
        questions.add(TechQuestion.builder()
                .id("WEB-001").category(TechCategory.WEB_TECHNOLOGIES).topic("HTML/CSS").difficulty(TechDifficulty.EASY)
                .question("What does the 'box model' in CSS consist of?")
                .options(List.of(
                        "Header, Body, Footer, Sidebar",
                        "Content, Padding, Border, Margin",
                        "Flexbox, Grid, Float, Position",
                        "Inline, Block, Inline-Block, None"))
                .correctOptionIndex(1)
                .explanation("The CSS box model describes how every HTML element is rendered as a rectangular box with four layers: Content (innermost), Padding (space around content), Border (around padding), and Margin (outermost, space between elements).")
                .conceptTip("box-sizing: content-box (default, width=content only) vs border-box (width includes padding+border). Use border-box for predictable layouts.")
                .companiesAsked(List.of("TCS", "Infosys", "Accenture")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-002").category(TechCategory.WEB_TECHNOLOGIES).topic("JavaScript").difficulty(TechDifficulty.EASY)
                .question("What is the difference between 'let', 'const', and 'var' in JavaScript?")
                .options(List.of(
                        "They are all identical",
                        "var is function-scoped; let and const are block-scoped. const cannot be reassigned",
                        "let is global; var is local",
                        "const allows reassignment; let does not"))
                .correctOptionIndex(1)
                .explanation("var is function-scoped and hoisted. let is block-scoped and not hoisted (TDZ). const is block-scoped, not hoisted, and cannot be reassigned (but object properties can be mutated).")
                .conceptTip("Best practice: use const by default, let when reassignment needed, avoid var. TDZ = Temporal Dead Zone (accessing before declaration).")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-003").category(TechCategory.WEB_TECHNOLOGIES).topic("REST APIs").difficulty(TechDifficulty.MEDIUM)
                .question("Which HTTP method is idempotent and used to update a resource completely?")
                .options(List.of("POST", "PUT", "PATCH", "DELETE"))
                .correctOptionIndex(1)
                .explanation("PUT is idempotent — calling it multiple times with the same data produces the same result. It replaces the entire resource. PATCH is for partial updates. POST is not idempotent (creates new resources).")
                .conceptTip("Idempotent: GET, PUT, DELETE, HEAD, OPTIONS. NOT idempotent: POST, PATCH (may not be). Safe: GET, HEAD, OPTIONS (no side effects).")
                .companiesAsked(List.of("Amazon", "Flipkart", "Google")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-004").category(TechCategory.WEB_TECHNOLOGIES).topic("JavaScript").difficulty(TechDifficulty.MEDIUM)
                .question("What is a closure in JavaScript?")
                .options(List.of(
                        "A way to close browser windows",
                        "A function that has access to variables from its outer (enclosing) function's scope, even after the outer function has returned",
                        "A method to close database connections",
                        "A loop termination mechanism"))
                .correctOptionIndex(1)
                .explanation("A closure is created when an inner function retains access to variables in its lexical scope (outer function), even after the outer function has finished executing. Closures are fundamental to JavaScript patterns like module pattern, callbacks, and event handlers.")
                .conceptTip("Closure = function + its lexical environment. Used in: data privacy, factory functions, memoization, currying.")
                .companiesAsked(List.of("Amazon", "Google", "Flipkart")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-005").category(TechCategory.WEB_TECHNOLOGIES).topic("Web Security").difficulty(TechDifficulty.MEDIUM)
                .question("What is Cross-Site Scripting (XSS)?")
                .options(List.of(
                        "Attacking a server by sending too many requests",
                        "Injecting malicious scripts into web pages viewed by other users",
                        "Stealing cookies by intercepting network traffic",
                        "Guessing user passwords"))
                .correctOptionIndex(1)
                .explanation("XSS attacks inject malicious client-side scripts into web pages that are then executed in other users' browsers. Types: Stored XSS (persisted in DB), Reflected XSS (in URL), DOM-based XSS (client-side manipulation).")
                .conceptTip("Prevention: input sanitization, output encoding, Content Security Policy (CSP), HttpOnly cookies, escaping user input before rendering.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-006").category(TechCategory.WEB_TECHNOLOGIES).topic("HTML/CSS").difficulty(TechDifficulty.EASY)
                .question("What is the difference between 'id' and 'class' selectors in CSS?")
                .options(List.of(
                        "id can be used for multiple elements; class is unique",
                        "id is unique per page and has higher specificity; class can be reused on multiple elements",
                        "They are identical in behavior",
                        "class has higher specificity than id"))
                .correctOptionIndex(1)
                .explanation("An id must be unique within a page (one element) and has higher CSS specificity (0,1,0,0). A class can be applied to multiple elements and has lower specificity (0,0,1,0).")
                .conceptTip("Specificity: inline(1000) > id(100) > class(10) > element(1). !important overrides all but is bad practice.")
                .companiesAsked(List.of("TCS", "Infosys")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-007").category(TechCategory.WEB_TECHNOLOGIES).topic("JavaScript").difficulty(TechDifficulty.HARD)
                .question("What is the event loop in JavaScript?")
                .options(List.of(
                        "A loop that handles DOM events",
                        "The mechanism that handles asynchronous callbacks by moving them from the task queue to the call stack when the stack is empty",
                        "A recursive function pattern",
                        "A way to iterate over arrays"))
                .correctOptionIndex(1)
                .explanation("The event loop continuously checks if the call stack is empty. If it is, it picks the next callback from the task queue (macrotask) or microtask queue and pushes it onto the call stack for execution. This enables JS's non-blocking, single-threaded concurrency model.")
                .conceptTip("Microtasks (Promises, queueMicrotask) run before macrotasks (setTimeout, setInterval, I/O). Microtask queue is fully drained before next macrotask.")
                .companiesAsked(List.of("Amazon", "Google", "Flipkart")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-008").category(TechCategory.WEB_TECHNOLOGIES).topic("Web Security").difficulty(TechDifficulty.HARD)
                .question("What is CSRF (Cross-Site Request Forgery)?")
                .options(List.of(
                        "Injecting scripts into another user's session",
                        "Tricking a user's browser into making unintended requests to a server where they are authenticated",
                        "Stealing API keys from source code",
                        "A SQL injection variant"))
                .correctOptionIndex(1)
                .explanation("CSRF tricks an authenticated user's browser into sending a forged request to a server (e.g., transferring money, changing password). The server processes it because the browser automatically includes cookies.")
                .conceptTip("Prevention: CSRF tokens (unique per session/request), SameSite cookie attribute, checking Origin/Referer headers, re-authentication for sensitive actions.")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-009").category(TechCategory.WEB_TECHNOLOGIES).topic("Cookies & Sessions").difficulty(TechDifficulty.MEDIUM)
                .question("What is the difference between cookies and sessions?")
                .options(List.of(
                        "Cookies are server-side; sessions are client-side",
                        "Cookies store data on the client; sessions store data on the server (with a session ID cookie on the client)",
                        "They are the same thing",
                        "Sessions are more secure because data never leaves the client"))
                .correctOptionIndex(1)
                .explanation("Cookies store small key-value pairs directly on the client browser (visible, limited size ~4KB). Sessions store data on the server and use a session ID cookie on the client to identify the session.")
                .conceptTip("Cookie attributes: HttpOnly (no JS access), Secure (HTTPS only), SameSite (CSRF protection), Max-Age/Expires. JWT: stateless alternative to sessions.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-010").category(TechCategory.WEB_TECHNOLOGIES).topic("REST APIs").difficulty(TechDifficulty.EASY)
                .question("What does REST stand for?")
                .options(List.of(
                        "Remote Execution of Server Tasks",
                        "Representational State Transfer",
                        "Reliable Encrypted Secure Transfer",
                        "Resource Endpoint Service Technology"))
                .correctOptionIndex(1)
                .explanation("REST (Representational State Transfer) is an architectural style for designing networked applications. It uses HTTP methods (GET, POST, PUT, DELETE) to operate on resources identified by URIs.")
                .conceptTip("REST principles: stateless, client-server, uniform interface (resources, representations, self-descriptive, HATEOAS), cacheable, layered system.")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-011").category(TechCategory.WEB_TECHNOLOGIES).topic("JavaScript").difficulty(TechDifficulty.MEDIUM)
                .question("What is the difference between '==' and '===' in JavaScript?")
                .options(List.of(
                        "They are identical",
                        "'==' compares values with type coercion; '===' compares both value and type without coercion",
                        "'===' is slower than '=='",
                        "'==' is strict equality"))
                .correctOptionIndex(1)
                .explanation("'==' (abstract equality) performs type coercion before comparison (e.g., '5' == 5 is true). '===' (strict equality) compares both value and type without coercion (e.g., '5' === 5 is false).")
                .conceptTip("Always prefer ===. Gotchas: null == undefined (true), NaN == NaN (false), [] == false (true due to coercion).")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-012").category(TechCategory.WEB_TECHNOLOGIES).topic("JavaScript").difficulty(TechDifficulty.HARD)
                .question("What is 'this' keyword behavior in arrow functions vs regular functions?")
                .options(List.of(
                        "Arrow functions have their own 'this'; regular functions inherit 'this'",
                        "Arrow functions inherit 'this' from their enclosing lexical scope; regular functions get 'this' based on how they are called",
                        "Both behave identically",
                        "Neither supports 'this'"))
                .correctOptionIndex(1)
                .explanation("Arrow functions do NOT have their own 'this' — they inherit it from the enclosing lexical scope at the time of definition. Regular functions determine 'this' based on how they are called (method call, constructor, explicit bind, etc.).")
                .conceptTip("Arrow: lexical this (good for callbacks). Regular: dynamic this. Use bind/call/apply to set 'this' for regular functions.")
                .companiesAsked(List.of("Amazon", "Google", "Flipkart")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-013").category(TechCategory.WEB_TECHNOLOGIES).topic("HTML/CSS").difficulty(TechDifficulty.MEDIUM)
                .question("What is the difference between Flexbox and CSS Grid?")
                .options(List.of(
                        "Flexbox is for 2D layouts; Grid is for 1D layouts",
                        "Flexbox is for 1D layouts (row OR column); Grid is for 2D layouts (rows AND columns)",
                        "They cannot be used together",
                        "Grid is older than Flexbox"))
                .correctOptionIndex(1)
                .explanation("Flexbox is designed for one-dimensional layouts (either a row or a column). CSS Grid is designed for two-dimensional layouts (rows and columns simultaneously). They complement each other and can be used together.")
                .conceptTip("Flexbox: content-first (items determine layout). Grid: layout-first (define grid, place items). Combine: Grid for page layout, Flexbox for component internals.")
                .companiesAsked(List.of("TCS", "Infosys", "Accenture")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-014").category(TechCategory.WEB_TECHNOLOGIES).topic("REST APIs").difficulty(TechDifficulty.HARD)
                .question("What is CORS and why is it needed?")
                .options(List.of(
                        "A CSS framework for responsive design",
                        "Cross-Origin Resource Sharing — a security mechanism that allows servers to specify which origins can access their resources",
                        "A JavaScript testing framework",
                        "A database connection pooling protocol"))
                .correctOptionIndex(1)
                .explanation("CORS (Cross-Origin Resource Sharing) is a browser security feature that restricts web pages from making requests to a different domain than the one serving the page. Servers must explicitly allow cross-origin requests via response headers like Access-Control-Allow-Origin.")
                .conceptTip("Preflight: OPTIONS request for non-simple methods. Headers: Access-Control-Allow-Origin, -Methods, -Headers, -Credentials.")
                .companiesAsked(List.of("Amazon", "Google", "Flipkart")).build());

        questions.add(TechQuestion.builder()
                .id("WEB-015").category(TechCategory.WEB_TECHNOLOGIES).topic("Web Security").difficulty(TechDifficulty.MEDIUM)
                .question("What is SQL Injection?")
                .options(List.of(
                        "A method to optimize SQL queries",
                        "Inserting malicious SQL code through user inputs to manipulate the database",
                        "A way to inject CSS into web pages",
                        "A database backup technique"))
                .correctOptionIndex(1)
                .explanation("SQL Injection occurs when an attacker inserts malicious SQL statements through unsanitized user inputs (forms, URLs) to read, modify, or delete database data, or even execute system commands.")
                .conceptTip("Prevention: parameterized queries/prepared statements, ORM, input validation, least privilege DB accounts, WAF.")
                .companiesAsked(List.of("TCS", "Infosys", "Amazon")).build());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Concept Cheatsheet
    // ═══════════════════════════════════════════════════════════════════════
    public List<TechFormulaCardDto> getConceptCheatsheet() {
        return List.of(
                // OOP
                TechFormulaCardDto.builder().category("OOP").topic("Pillars of OOP").title("Four Pillars")
                        .formula("Encapsulation | Inheritance | Polymorphism | Abstraction")
                        .tip("Encapsulation = data hiding. Inheritance = code reuse. Polymorphism = many forms. Abstraction = hiding complexity.")
                        .example("class Dog extends Animal { @Override void speak() { bark(); } }").build(),
                TechFormulaCardDto.builder().category("OOP").topic("SOLID Principles").title("SOLID")
                        .formula("S=Single Responsibility | O=Open-Closed | L=Liskov | I=Interface Segregation | D=Dependency Inversion")
                        .tip("Each principle aims to reduce coupling and increase cohesion in object-oriented design.")
                        .example("SRP: UserService handles users only, not email sending. Use EmailService for that.").build(),
                TechFormulaCardDto.builder().category("OOP").topic("Design Patterns").title("Common Patterns")
                        .formula("Singleton | Factory | Observer | Strategy | Builder | Adapter")
                        .tip("Creational: how objects are created. Structural: how objects compose. Behavioral: how objects communicate.")
                        .example("Singleton: private constructor + static getInstance(). Factory: switch(type) → new ConcreteClass().").build(),

                // DBMS
                TechFormulaCardDto.builder().category("DBMS").topic("Normal Forms").title("Normalization Levels")
                        .formula("1NF → 2NF → 3NF → BCNF → 4NF")
                        .tip("1NF: atomic values. 2NF: no partial dependency. 3NF: no transitive dependency. BCNF: every determinant is a candidate key.")
                        .example("Student(id, name, course, instructor) has transitive dependency instructor→course. Split to normalize.").build(),
                TechFormulaCardDto.builder().category("DBMS").topic("SQL Execution Order").title("Query Processing")
                        .formula("FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT")
                        .tip("Aliases defined in SELECT aren't available in WHERE/GROUP BY because SELECT runs later.")
                        .example("SELECT dept, COUNT(*) FROM emp WHERE salary > 50000 GROUP BY dept HAVING COUNT(*) > 5 ORDER BY dept;").build(),
                TechFormulaCardDto.builder().category("DBMS").topic("ACID Properties").title("Transaction Guarantees")
                        .formula("Atomicity | Consistency | Isolation | Durability")
                        .tip("Atomicity: all-or-nothing. Consistency: valid state. Isolation: no interference. Durability: committed = permanent.")
                        .example("Bank transfer: debit A + credit B must both succeed or both fail (atomicity).").build(),

                // OS
                TechFormulaCardDto.builder().category("OS").topic("Process vs Thread").title("Key Differences")
                        .formula("Process: own memory space | Thread: shares process memory")
                        .tip("Thread creation is faster (no new address space). Context switch between threads is cheaper than between processes.")
                        .example("Web server: one process, multiple threads handling concurrent requests.").build(),
                TechFormulaCardDto.builder().category("OS").topic("Deadlock Conditions").title("Coffman Conditions")
                        .formula("Mutual Exclusion + Hold & Wait + No Preemption + Circular Wait")
                        .tip("All 4 must hold simultaneously for deadlock. Breaking any one prevents it.")
                        .example("Prevention: order resources (break circular wait), allow preemption, request all resources at once (break hold & wait).").build(),
                TechFormulaCardDto.builder().category("OS").topic("Page Replacement").title("Algorithms")
                        .formula("FIFO | LRU | OPT | Clock (Second Chance)")
                        .tip("OPT is optimal but impractical. LRU is best practical approximation. FIFO can suffer Bélády's anomaly.")
                        .example("LRU with 3 frames, access: 1,2,3,4,1,2 → faults: 1,2,3,4(evict 1),1(evict 2),2(evict 3) = 6 faults.").build(),

                // CN
                TechFormulaCardDto.builder().category("CN").topic("OSI vs TCP/IP").title("Layer Comparison")
                        .formula("OSI: 7 layers | TCP/IP: 4 layers (Application, Transport, Internet, Network Access)")
                        .tip("TCP/IP is the practical model used in the internet. OSI is the theoretical reference model.")
                        .example("HTTP(Application) → TCP(Transport) → IP(Internet) → Ethernet(Network Access)").build(),
                TechFormulaCardDto.builder().category("CN").topic("Subnetting").title("Host Calculation")
                        .formula("Usable hosts = 2^(32 - prefix) - 2")
                        .tip("Subtract 2 for network address and broadcast address. /24 = 254 hosts, /28 = 14 hosts, /30 = 2 hosts.")
                        .example("/26 subnet: 2^(32-26) - 2 = 64 - 2 = 62 usable hosts.").build(),
                TechFormulaCardDto.builder().category("CN").topic("TCP Handshake").title("3-Way Handshake")
                        .formula("SYN → SYN-ACK → ACK")
                        .tip("Establishes reliable connection. Termination: 4-way (FIN → ACK → FIN → ACK). TIME_WAIT ensures late packets are handled.")
                        .example("Client SYN(seq=100) → Server SYN-ACK(seq=300, ack=101) → Client ACK(ack=301)").build(),

                // DSA
                TechFormulaCardDto.builder().category("DSA").topic("Time Complexity").title("Common Complexities")
                        .formula("O(1) < O(log n) < O(n) < O(n log n) < O(n²) < O(2ⁿ) < O(n!)")
                        .tip("Hash table: O(1) avg. Binary search: O(log n). Sorting: O(n log n). Brute force: O(n²) or worse.")
                        .example("For n=10^6: O(n log n) ≈ 20M ops (fast). O(n²) ≈ 10^12 ops (too slow).").build(),
                TechFormulaCardDto.builder().category("DSA").topic("Tree Traversals").title("DFS & BFS")
                        .formula("Inorder (L-Root-R) | Preorder (Root-L-R) | Postorder (L-R-Root) | Level-order (BFS)")
                        .tip("Inorder on BST gives sorted output. Preorder for serialization. Postorder for deletion. BFS uses queue.")
                        .example("Tree: [1,2,3]. Inorder: 2,1,3. Preorder: 1,2,3. Postorder: 2,3,1.").build(),
                TechFormulaCardDto.builder().category("DSA").topic("Graph Algorithms").title("Key Algorithms")
                        .formula("BFS | DFS | Dijkstra | Bellman-Ford | Floyd-Warshall | Kruskal | Prim")
                        .tip("BFS: shortest path (unweighted). DFS: cycle detection, topological sort. Dijkstra: shortest path (non-negative weights).")
                        .example("Topological sort: DAG only. Kruskal: MST with union-find. Prim: MST with min-heap.").build(),

                // Web
                TechFormulaCardDto.builder().category("Web").topic("HTTP Methods").title("REST Methods")
                        .formula("GET (read) | POST (create) | PUT (replace) | PATCH (partial update) | DELETE (remove)")
                        .tip("Idempotent: GET, PUT, DELETE. Safe: GET, HEAD, OPTIONS. POST is neither safe nor idempotent.")
                        .example("GET /users/1 → read. POST /users → create. PUT /users/1 → replace. DELETE /users/1 → remove.").build(),
                TechFormulaCardDto.builder().category("Web").topic("Web Security").title("OWASP Top Threats")
                        .formula("XSS | CSRF | SQL Injection | Broken Auth | Security Misconfiguration")
                        .tip("XSS: sanitize output. CSRF: use tokens. SQL Injection: use prepared statements. Always validate input on server side.")
                        .example("Parameterized query: PreparedStatement ps = conn.prepareStatement(\"SELECT * FROM users WHERE id = ?\"); ps.setInt(1, userId);").build()
        );
    }
}
