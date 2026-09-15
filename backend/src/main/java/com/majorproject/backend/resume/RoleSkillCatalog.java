package com.majorproject.backend.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleSkillCatalog {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleBenchmark {
        private String roleTitle;
        private List<String> primarySkills;
        private List<String> secondarySkills;
        private List<String> industryKeywords;
        private List<String> recommendedProjects;
    }

    private final Map<String, RoleBenchmark> roleRegistry = new HashMap<>();

    public RoleSkillCatalog() {
        initCatalog();
    }

    private void initCatalog() {
        // 1. Java Backend Developer
        roleRegistry.put("java backend developer", RoleBenchmark.builder()
                .roleTitle("Java Backend Developer")
                .primarySkills(List.of(
                        "Java", "Spring Boot", "REST APIs", "SQL", "Hibernate", "JPA", "Git", "Maven"
                ))
                .secondarySkills(List.of(
                        "Microservices", "Docker", "Redis", "Kafka", "PostgreSQL", "MySQL", "JUnit", "Spring Security", "AWS"
                ))
                .industryKeywords(List.of(
                        "API Design", "Database Normalization", "Indexing", "Multithreading", "Object-Oriented Programming (OOP)",
                        "Clean Architecture", "Unit Testing", "Transaction Management", "CI/CD Pipeline"
                ))
                .recommendedProjects(List.of(
                        "Build a secured RESTful E-Commerce or Banking API with Spring Boot, JWT, and PostgreSQL.",
                        "Implement an asynchronous event-driven notification microservice using Apache Kafka and Redis caching."
                ))
                .build());

        // 2. Full Stack Developer
        roleRegistry.put("full stack developer", RoleBenchmark.builder()
                .roleTitle("Full Stack Developer")
                .primarySkills(List.of(
                        "JavaScript", "TypeScript", "React", "Node.js", "REST APIs", "HTML5", "CSS3", "Git", "SQL"
                ))
                .secondarySkills(List.of(
                        "Next.js", "Express.js", "MongoDB", "PostgreSQL", "Tailwind CSS", "Docker", "Redux", "GraphQL"
                ))
                .industryKeywords(List.of(
                        "Responsive Design", "State Management", "Authentication & Authorization", "Full Stack Architecture",
                        "Database Schema Design", "RESTful Web Services", "Performance Optimization", "Single Page Application (SPA)"
                ))
                .recommendedProjects(List.of(
                        "Develop a full-stack collaborative Kanban dashboard with real-time WebSockets, React, and Node/Spring.",
                        "Create a full-stack SaaS portal with user authentication, role-based access control, and Stripe/Razorpay integration."
                ))
                .build());

        // 3. Frontend Developer
        roleRegistry.put("frontend developer", RoleBenchmark.builder()
                .roleTitle("Frontend Developer")
                .primarySkills(List.of(
                        "JavaScript", "TypeScript", "React", "HTML5", "CSS3", "Git", "Responsive Design"
                ))
                .secondarySkills(List.of(
                        "Next.js", "Tailwind CSS", "Redux", "Zustand", "Webpack", "Vite", "Jest", "Figma"
                ))
                .industryKeywords(List.of(
                        "Core Web Vitals", "Component Lifecycle", "Accessibility (WCAG)", "SEO Best Practices",
                        "Cross-Browser Compatibility", "CSS Grid & Flexbox", "REST API Integration"
                ))
                .recommendedProjects(List.of(
                        "Build a high-performance interactive Analytics Dashboard with charting libraries, themes, and offline support.",
                        "Implement an Accessible Component Design System published as an npm package or Storybook documentation."
                ))
                .build());

        // 4. Data Analyst / Data Scientist
        roleRegistry.put("data analyst", RoleBenchmark.builder()
                .roleTitle("Data Analyst")
                .primarySkills(List.of(
                        "Python", "SQL", "Pandas", "NumPy", "Excel", "Data Visualization", "Git"
                ))
                .secondarySkills(List.of(
                        "Tableau", "Power BI", "Matplotlib", "Seaborn", "Scikit-Learn", "Statistics", "PostgreSQL"
                ))
                .industryKeywords(List.of(
                        "Exploratory Data Analysis (EDA)", "Data Cleaning & Wrangling", "Statistical Hypothesis Testing",
                        "KPI Dashboards", "Data Storytelling", "SQL Window Functions", "A/B Testing"
                ))
                .recommendedProjects(List.of(
                        "Execute an end-to-end exploratory analysis on customer churn or retail transactions with an interactive Tableau/Power BI dashboard.",
                        "Build an automated SQL and Python ETL pipeline extracting and transforming market indicators."
                ))
                .build());

        // 5. Machine Learning Engineer
        roleRegistry.put("machine learning engineer", RoleBenchmark.builder()
                .roleTitle("Machine Learning Engineer")
                .primarySkills(List.of(
                        "Python", "Scikit-Learn", "TensorFlow", "PyTorch", "Pandas", "NumPy", "Git", "Mathematics/Linear Algebra"
                ))
                .secondarySkills(List.of(
                        "Docker", "MLflow", "FastAPI", "NLP", "Computer Vision", "HuggingFace", "AWS SageMaker"
                ))
                .industryKeywords(List.of(
                        "Model Training & Validation", "Hyperparameter Tuning", "Feature Engineering", "Data Preprocessing",
                        "Overfitting & Regularization", "Model Deployment (API)", "LLM Fine-tuning / RAG"
                ))
                .recommendedProjects(List.of(
                        "Deploy a containerized sentiment classification or recommendation system API using FastAPI and Docker.",
                        "Implement a Retrieval-Augmented Generation (RAG) assistant using LangChain and a Vector Database."
                ))
                .build());

        // 6. DevOps / Cloud Engineer
        roleRegistry.put("devops engineer", RoleBenchmark.builder()
                .roleTitle("DevOps Engineer")
                .primarySkills(List.of(
                        "Linux", "Docker", "Git", "CI/CD", "Bash", "AWS", "Kubernetes"
                ))
                .secondarySkills(List.of(
                        "Terraform", "Ansible", "Prometheus", "Grafana", "Python", "Jenkins", "GitHub Actions", "Nginx"
                ))
                .industryKeywords(List.of(
                        "Infrastructure as Code (IaC)", "Container Orchestration", "Continuous Integration / Deployment",
                        "Observability & Monitoring", "Network Security", "Cloud Architecture"
                ))
                .recommendedProjects(List.of(
                        "Set up a complete automated GitHub Actions CI/CD pipeline building, testing, and deploying a multi-tier app to AWS.",
                        "Provision a resilient Kubernetes cluster with Terraform and configure Prometheus/Grafana monitoring."
                ))
                .build());

        // 7. Software Development Engineer (General)
        roleRegistry.put("software development engineer", RoleBenchmark.builder()
                .roleTitle("Software Development Engineer")
                .primarySkills(List.of(
                        "Data Structures & Algorithms", "Java", "C++", "Python", "Git", "Object-Oriented Programming (OOP)", "SQL"
                ))
                .secondarySkills(List.of(
                        "System Design", "Operating Systems", "Computer Networks", "Database Management Systems (DBMS)", "Docker"
                ))
                .industryKeywords(List.of(
                        "Time & Space Complexity", "Design Patterns", "Clean Code", "Unit Testing", "Scalability",
                        "Concurrency", "Algorithm Optimization"
                ))
                .recommendedProjects(List.of(
                        "Implement custom data structures or an in-memory key-value cache demonstrating concurrency control.",
                        "Develop a multi-threaded web server or URL shortener with rate-limiting and unit tests."
                ))
                .build());
    }

    public RoleBenchmark findBenchmark(String targetRole) {
        if (targetRole == null || targetRole.isBlank()) {
            return roleRegistry.get("software development engineer");
        }

        String normalized = targetRole.trim().toLowerCase();

        // Direct or contains match
        for (Map.Entry<String, RoleBenchmark> entry : roleRegistry.entrySet()) {
            if (normalized.contains(entry.getKey()) || entry.getKey().contains(normalized)) {
                return entry.getValue();
            }
        }

        // Keywords in targetRole
        if (normalized.contains("backend") || normalized.contains("spring") || normalized.contains("java")) {
            return roleRegistry.get("java backend developer");
        }
        if (normalized.contains("fullstack") || normalized.contains("full stack") || normalized.contains("web")) {
            return roleRegistry.get("full stack developer");
        }
        if (normalized.contains("frontend") || normalized.contains("react") || normalized.contains("ui")) {
            return roleRegistry.get("frontend developer");
        }
        if (normalized.contains("data") || normalized.contains("analyst") || normalized.contains("bi")) {
            return roleRegistry.get("data analyst");
        }
        if (normalized.contains("ml") || normalized.contains("ai") || normalized.contains("machine learning")) {
            return roleRegistry.get("machine learning engineer");
        }
        if (normalized.contains("cloud") || normalized.contains("devops")) {
            return roleRegistry.get("devops engineer");
        }

        // Fallback default
        return roleRegistry.get("software development engineer");
    }

    public Collection<RoleBenchmark> getAllBenchmarks() {
        return roleRegistry.values();
    }
}
