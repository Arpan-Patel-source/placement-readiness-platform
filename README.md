# AI-Powered Placement Preparation & Readiness Platform

An all-in-one platform designed to help students prepare for campus placements through resume analysis, AI mock interviews, coding practice, aptitude training, and personalized preparation roadmaps.

## Problem Statement

Students preparing for placements often use separate platforms for different needs:

- Resume analysis and ATS checking
- Mock interviews
- Coding and DSA practice
- Aptitude preparation
- HR interview preparation
- Technical interview preparation
- Learning roadmaps and career guidance

This makes preparation fragmented, difficult to track, and less personalized.

## Solution

The AI-Powered Placement Preparation & Readiness Platform brings these preparation activities together in one system.

It will analyze a student’s resume and skills, conduct AI-based mock interviews, provide coding and aptitude practice, track performance, calculate placement readiness, and generate a personalized roadmap for improvement.

## Core Objectives

- Help students identify gaps in their resume and technical skills.
- Provide personalized preparation based on a target job role.
- Simulate HR, technical, and coding interview rounds.
- Track learning progress and interview performance.
- Provide actionable feedback instead of only scores.
- Give students a clear roadmap toward placement readiness.

## Planned Core Modules

### 1. Authentication and User Management

- Student registration and login
- Secure authentication using JWT
- Student profile management
- Target role selection, such as Java Backend Developer or Full Stack Developer
- Skills, education, projects, and experience profile

### 2. AI Resume Analyzer

Students will be able to upload a resume in PDF or DOCX format.

Planned analysis includes:

- ATS compatibility score
- Resume strength score
- Skill extraction
- Missing keyword detection
- Grammar and formatting suggestions
- Project quality analysis
- Skill-gap detection based on the target role
- Placement readiness score
- Actionable suggestions to improve the resume

Example suggestions:

```text
Current Score: 72/100

Missing Skills:
- Spring Boot
- REST APIs
- GitHub Projects
- Deployment Experience

Recommendations:
- Add two backend-focused projects
- Include measurable achievements
- Improve project descriptions
```

### 3. AI Mock Interview

The platform will simulate an interview experience based on the student’s profile, skills, target role, and interview type.

Interview types:

- HR interview
- Technical interview
- Resume-based interview
- Coding interview

Example technical questions for a student skilled in Java, Spring Boot, and MySQL:

- What is the difference between JDK, JRE, and JVM?
- Explain Dependency Injection in Spring Boot.
- What is database normalization?
- What are REST APIs and HTTP methods?
- Explain the difference between `ArrayList` and `LinkedList`.

Planned evaluation areas:

- Correctness
- Relevance
- Technical depth
- Communication clarity
- Confidence
- Areas for improvement
- Overall interview score

### 4. Competitive Coding Arena

A coding-practice module inspired by platforms such as LeetCode.

Planned features:

- Problem bank organized by topic and difficulty
- Support for Java, Python, and C++
- Code editor and submission system
- Hidden and visible test cases
- Time and space complexity feedback
- Problem-solving history
- Performance tracking
- DSA topic recommendations

### 5. Aptitude Training

A practice module for common placement aptitude topics.

Planned areas:

- Quantitative aptitude
- Logical reasoning
- Verbal ability
- Data interpretation
- Time-based quizzes
- Topic-wise practice
- Performance reports
- Weak-area recommendations

### 6. HR and Technical Preparation

Structured preparation material and practice for common placement rounds.

HR preparation topics:

- Tell me about yourself
- Strengths and weaknesses
- Why should we hire you?
- Career goals
- Teamwork and conflict questions

Technical preparation topics:

- Programming fundamentals
- Object-oriented programming
- DBMS
- Operating systems
- Computer networks
- Java and Spring Boot concepts
- SQL and REST APIs

### 7. Placement Readiness Dashboard

A central dashboard that gives students a clear view of their preparation status.

Planned metrics:

- Resume score
- Interview performance
- Coding performance
- Aptitude performance
- Skill coverage
- Completed learning activities
- Placement readiness score
- Recent feedback and improvement areas

### 8. Personalized Roadmap Generator

The platform will generate a personalized preparation plan using the student’s:

- Current skills
- Resume analysis
- Target role
- Coding performance
- Aptitude performance
- Interview feedback
- Available preparation time

Example roadmap:

```text
Week 1:
- Improve Java fundamentals
- Solve 10 array and string problems
- Update resume project descriptions

Week 2:
- Learn Spring Boot REST APIs
- Build one CRUD backend project
- Practice HR introduction answers

Week 3:
- Practice SQL and DBMS questions
- Complete aptitude quizzes
- Attempt a technical mock interview
```

## Future Enhancements

The following modules will be added after the core platform is stable.

### Voice-Based Interview Analysis

- Speech-to-text answers
- Speaking speed analysis
- Filler-word detection
- Fluency feedback
- Communication score
- Confidence analysis
- Pronunciation feedback

### AI Coding Mentor

- Explain coding mistakes
- Suggest optimized approaches
- Provide hints without immediately revealing answers
- Analyze time and space complexity
- Recommend related DSA topics

### Company-Specific Preparation

- Company-wise interview patterns
- Frequently asked questions
- Role-specific preparation plans
- Coding and aptitude difficulty levels
- Company readiness score

### Placement Prediction Engine

- Estimate placement readiness from performance data
- Identify high-risk preparation areas
- Recommend priority topics

### AI Career Coach

- Doubt-solving assistant
- Study planner
- Resume guidance
- Interview feedback assistant
- Career and role recommendations

## Proposed Technology Stack

### Frontend

- React
- JavaScript or TypeScript
- HTML and CSS
- Component-based UI design

### Backend

- Java
- Spring Boot
- Spring Security
- REST APIs
- Maven

### Database

- PostgreSQL
- JPA / Hibernate

### AI and Resume Processing

- LLM API for question generation and answer evaluation
- PDF resume parsing
- DOCX resume parsing
- Prompt and evaluation pipeline

### Deployment and DevOps

- Git and GitHub
- Docker
- Cloud deployment
- Environment variables for secrets and API keys

## Proposed High-Level Architecture

```text
React Frontend
      |
      | REST APIs
      v
Spring Boot Backend
      |
      |------------------------------|
      |                              |
      v                              v
PostgreSQL Database              AI Services
      |                              |
Users, resumes, interviews,     Resume analysis,
coding results, roadmap data    question generation,
                                 answer evaluation
```

## Suggested Backend Modules

```text
backend/
  auth/
  user/
  resume/
  interview/
  coding/
  aptitude/
  dashboard/
  roadmap/
  common/
  config/
```

## Suggested Frontend Modules

```text
frontend/
  src/
    components/
    pages/
    services/
    hooks/
    context/
    features/
      auth/
      resume/
      interview/
      coding/
      aptitude/
      dashboard/
      roadmap/
```

## Development Approach

This project will be built in phases to ensure that each module is understandable, testable, and interview-ready.

### Phase 1: Foundation

- Repository setup
- Backend project setup
- Frontend project setup
- PostgreSQL configuration
- Authentication and user profile foundation

### Phase 2: Resume Analyzer

- Resume upload
- PDF/DOCX parsing
- Skill extraction
- Resume score and suggestions

### Phase 3: AI Mock Interview

- Interview question generation
- Answer submission
- AI-based evaluation
- Feedback report

### Phase 4: Practice Modules

- Coding problem bank
- Code submissions
- Aptitude quizzes
- Performance tracking

### Phase 5: Dashboard and Roadmap

- Placement readiness score
- Progress dashboard
- Personalized roadmap

### Phase 6: Advanced AI Features

- Voice-based interviews
- AI coding mentor
- Company-specific preparation
- Career coach and prediction engine

## Project Status

The project is currently in the foundation and architecture phase.

The immediate focus is to establish a clean, modular application structure with secure authentication before implementing advanced AI and assessment features.

## Contributors

- Arpan Patel
- [Add your collaborator's name]

## License

This project is intended for educational and portfolio purposes.

## Technology Stack

### Backend

- **Java** — primary backend programming language
- **Spring Boot** — backend application framework
- **Spring Web** — REST API development
- **Spring Security** — authentication and authorization
- **JWT** — secure user login sessions
- **Spring Data JPA / Hibernate** — database interaction and object-relational mapping
- **Maven** — dependency and build management

### Frontend

- **React** — frontend user-interface library
- **JavaScript** — frontend programming language
- **HTML5 and CSS3** — page structure and styling
- **React Router** — page navigation
- **Axios or Fetch API** — communication with backend REST APIs

### Database

- **PostgreSQL** — relational database for users, resumes, interviews, coding submissions, scores, and roadmaps

### AI and File Processing

- **LLM API** — resume analysis, interview-question generation, answer evaluation, and personalized feedback
- **Apache PDFBox** — PDF resume text extraction
- **Apache POI** — DOCX resume text extraction

### Tools and Deployment

- **Git and GitHub** — version control and team collaboration
- **Postman** — API testing
- **Docker** — containerization
- **Cloud deployment** — deployment of frontend, backend, and database services
- **Environment variables** — safe storage of database credentials, JWT secrets, and API keys

## Quick Start & Running Locally

### 1. Prerequisites
- **Node.js**: v18+ (v20+ recommended)
- **Java**: JDK 21
- **PostgreSQL**: Running locally on port 5432 (default DB: `placement_ai`)

### 2. Run the Frontend
The frontend is built with React, TanStack Start/Router, Tailwind CSS v4, Lucide icons, and shadcn/ui.

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at: **http://localhost:5173**

Key application routes:
- **`http://localhost:5173/`**: PlacementAI Landing Page (Hero, Features, Mission, CTA)
- **`http://localhost:5173/login`**: Student Sign In (connected to `/api/auth/login`)
- **`http://localhost:5173/register`**: Student Registration (connected to `/api/auth/register`)
- **`http://localhost:5173/dashboard`**: Readiness score gauge, ATS snapshot, predictions & roadmap

### 3. Run the Backend
The backend is a Spring Boot REST application with Spring Security and JWT authentication.

```bash
cd backend
./mvnw spring-boot:run
```

The backend server runs on: **http://localhost:8080**

Active REST Endpoints:
- `POST /api/auth/register` — Create student account
- `POST /api/auth/login` — Sign in and obtain JWT
- `GET  /api/profile` — Fetch student profile (Bearer token)
- `PUT  /api/profile` — Update student profile (Bearer token)