package com.majorproject.backend.hrtraining;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HrPromptCatalog {

    private final List<HrPrompt> prompts = new ArrayList<>();

    public HrPromptCatalog() {
        initPrompts();
    }

    public List<HrPrompt> getAllPrompts() {
        return Collections.unmodifiableList(prompts);
    }

    public Optional<HrPrompt> findById(String id) {
        return prompts.stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst();
    }

    private void initPrompts() {
        // --- Self Introduction ---
        prompts.add(HrPrompt.builder()
                .id("HR_INTRO_01")
                .category(HrCategory.SELF_INTRODUCTION)
                .question("Tell me about yourself and walk me through your engineering journey.")
                .recruiterIntent("Testing communication clarity, storytelling ability, relevance to the applied position, and enthusiasm for technology.")
                .keyPointsToInclude(List.of(
                        "Present role/education & core technical stack.",
                        "Key achievement or high-impact project you engineered.",
                        "Why this specific company & role aligns with your career trajectory."
                ))
                .commonPitfalls(List.of(
                        "Reading your resume chronologically line-by-line.",
                        "Focusing on unrelated hobbies or childhood stories.",
                        "Talking continuously for more than 2 minutes without pausing."
                ))
                .sampleModelAnswer("I am an aspiring Software Engineer graduating in Computer Science with deep focus on Java backend ecosystems, distributed systems, and Spring Boot. Over the past two years, I built and deployed scalable services, including a full-featured microservices-based order processing engine that handles over 1,500 concurrent requests with Redis caching. What excites me most about joining your engineering team is your dedication to high-throughput platforms and architectural excellence.")
                .companyTags(List.of("Amazon", "Google", "TCS", "Infosys", "Microsoft"))
                .build());

        prompts.add(HrPrompt.builder()
                .id("HR_INTRO_02")
                .category(HrCategory.SELF_INTRODUCTION)
                .question("Why should our organization hire you over other qualified candidates?")
                .recruiterIntent("Evaluating self-awareness, unique value proposition, and how well you understand the company's tech stack and culture.")
                .keyPointsToInclude(List.of(
                        "Direct match between your technical competencies and the team's tech stack.",
                        "Proven problem-solving agility and rapid learning curve.",
                        "Ownership mindset and proactive collaboration."
                ))
                .commonPitfalls(List.of(
                        "Claiming to be 'the hardest worker' without concrete examples.",
                        "Putting down other candidates or sounding overly arrogant.",
                        "Giving a generic answer applicable to any arbitrary company."
                ))
                .sampleModelAnswer("You should consider me because I bridge the gap between solid foundational theory and hands-on system building. In my capstone project, when our database hit write bottlenecks, I didn't wait for direction—I profiled the slow queries, introduced indexing, and re-architected the connection pool, reducing response latency by 42%. I bring that exact proactive ownership and technical curiosity to your backend engineering team from day one.")
                .companyTags(List.of("Accenture", "Goldman Sachs", "Wipro", "Deloitte"))
                .build());

        // --- Leadership ---
        prompts.add(HrPrompt.builder()
                .id("HR_LEAD_01")
                .category(HrCategory.LEADERSHIP)
                .question("Describe a situation where you took the initiative to lead a project or resolve an unforeseen roadblock.")
                .recruiterIntent("Assessing ownership, proactivity, crisis handling, and ability to influence without formal authority.")
                .keyPointsToInclude(List.of(
                        "Situation: The unexpected technical or organizational roadblock.",
                        "Task: The objective and your self-assigned responsibility.",
                        "Action: The specific analytical steps, trade-offs, and decisions you executed.",
                        "Result: The tangible impact, metric improvement, and team outcome."
                ))
                .commonPitfalls(List.of(
                        "Using 'we did this' without clarifying your personal contribution.",
                        "Describing a trivial scenario with no genuine stakes.",
                        "Failing to mention what you learned from the leadership experience."
                ))
                .sampleModelAnswer("During our final year hackathon, 18 hours before final judging, our primary external payment gateway API suffered a breaking authentication outage. Seeing the team panic, I stepped up as project coordinator. I immediately organized a 10-minute huddle, divided tasks between frontend mocking and backend refactoring, and personally engineered a resilient fallback mock gateway with idempotent token simulation. As a result, we demonstrated an uninterrupted demo to the judges and secured 2nd place out of 60 teams.")
                .companyTags(List.of("Amazon", "Flipkart", "Oracle", "Uber"))
                .build());

        prompts.add(HrPrompt.builder()
                .id("HR_LEAD_02")
                .category(HrCategory.LEADERSHIP)
                .question("How do you motivate a teammate who is struggling or falling behind on their deliverables?")
                .recruiterIntent("Checking empathy, emotional intelligence, mentorship capability, and collaborative leadership.")
                .keyPointsToInclude(List.of(
                        "Initiating a private, non-judgmental 1-on-1 discussion to uncover root cause.",
                        "Offering structured assistance or pairing without taking over their work.",
                        "Aligning on incremental micro-milestones to restore confidence."
                ))
                .commonPitfalls(List.of(
                        "Complaining to a professor or manager immediately without attempting to help.",
                        "Doing all their work for them (enabling instead of mentoring).",
                        "Blaming the teammate's attitude or intelligence."
                ))
                .sampleModelAnswer("When a teammate was falling behind on implementing our project's JWT authentication filters due to unfamiliarity with Spring Security, I scheduled a private coffee chat. Rather than criticizing the delay, I asked where the friction was. We spent 90 minutes pair-programming through the filter lifecycle, after which I helped him break the remaining tasks into bite-sized tickets. He completed his module ahead of the revised deadline with renewed confidence.")
                .companyTags(List.of("Microsoft", "Cognizant", "TCS Digital"))
                .build());

        // --- Conflict Resolution ---
        prompts.add(HrPrompt.builder()
                .id("HR_CONF_01")
                .category(HrCategory.CONFLICT_RESOLUTION)
                .question("Tell me about a time you had a technical disagreement with a team member. How did you resolve it?")
                .recruiterIntent("Testing intellectual humility, objectivity, reliance on data/metrics, and maintaining strong professional relationships.")
                .keyPointsToInclude(List.of(
                        "The technical disagreement (e.g. SQL vs NoSQL, architectural style).",
                        "How you depersonalized the disagreement and moved to empirical evidence/benchmarking.",
                        "The collaborative resolution and how mutual respect was preserved."
                ))
                .commonPitfalls(List.of(
                        "Portraying yourself as 100% right and the other person as foolish.",
                        "Resolving conflict by simply 'agreeing to disagree' with no resolution.",
                        "Getting emotionally invested or arguing rather than benchmarking."
                ))
                .sampleModelAnswer("In our microservices project, a fellow engineer and I strongly disagreed on database selection: he advocated for MongoDB for rapid prototyping, while I favored PostgreSQL due to ACID transaction requirements in our order checkout flow. To prevent subjective debate, I suggested building a rapid POC to benchmark write consistency under concurrent load. When the benchmarks showed relational constraints prevented data corruption during race conditions, we aligned on PostgreSQL with full team buy-in.")
                .companyTags(List.of("Google", "Atlassian", "Amazon", "Salesforce"))
                .build());

        // --- Teamwork ---
        prompts.add(HrPrompt.builder()
                .id("HR_TEAM_01")
                .category(HrCategory.TEAMWORK)
                .question("Describe a time when you worked on a diverse team to deliver a project under tight deadlines.")
                .recruiterIntent("Assessing active listening, adaptability, cross-functional communication, and team-first orientation.")
                .keyPointsToInclude(List.of(
                        "Context of the diverse team (e.g. designers, frontend, backend).",
                        "How you established communication rhythms (daily standups, clear contracts).",
                        "Delivery outcome and appreciation of peers' contributions."
                ))
                .commonPitfalls(List.of(
                        "Minimizing others' contributions and claiming solo glory.",
                        "Mentioning deadline stress without explaining how the team managed it.",
                        "Lack of specific details on communication tools or methodologies."
                ))
                .sampleModelAnswer("In an inter-departmental capstone, I collaborated with UI designers and hardware IoT engineers. The biggest challenge was aligning API request formats. I created an OpenAPI/Swagger contract upfront so the frontend team could develop concurrently with mocked endpoints while I finalized backend business logic. This clear documentation cut integration debugging time by half, allowing us to launch 3 days ahead of demo day.")
                .companyTags(List.of("Infosys", "Cisco", "Deloitte", "Capgemini"))
                .build());

        // --- Failure & Resilience ---
        prompts.add(HrPrompt.builder()
                .id("HR_FAIL_01")
                .category(HrCategory.FAILURE_RESILIENCE)
                .question("Can you describe a significant mistake you made or a project that failed? What did you learn?")
                .recruiterIntent("Evaluating self-honesty, resilience, psychological safety, and growth mindset.")
                .keyPointsToInclude(List.of(
                        "Honest admission of an actual mistake without deflecting blame.",
                        "Immediate remediation steps taken to minimize damage.",
                        "Systemic preventative measures established so the error never recurs."
                ))
                .commonPitfalls(List.of(
                        "Claiming 'I've never really failed' (massive red flag).",
                        "Using a fake humble-brag like 'I worked too hard'.",
                        "Blaming circumstances, internet connection, or team members."
                ))
                .sampleModelAnswer("Early in my internship, I accidentally pushed database migration scripts with unindexed foreign keys directly into the staging environment, which spiked query latency from 80ms to over 2.4 seconds and blocked the QA team. I immediately owned up in our engineering channel, rolled back the migration, and analyzed execution plans. I then instituted an automated CI check that rejects migration scripts lacking explicit indexing. The failure taught me to prioritize defensive engineering and transparent communication.")
                .companyTags(List.of("Amazon", "Meta", "Adobe", "Goldman Sachs"))
                .build());

        // --- Career Vision ---
        prompts.add(HrPrompt.builder()
                .id("HR_VISN_01")
                .category(HrCategory.CAREER_VISION)
                .question("Where do you envision yourself professionally over the next 3 to 5 years?")
                .recruiterIntent("Checking retention probability, career ambition, realistic expectations, and alignment with engineering growth ladders.")
                .keyPointsToInclude(List.of(
                        "Mastering foundational engineering practices in the first 1-2 years.",
                        "Taking on architectural responsibility and mentoring junior developers in years 3-5.",
                        "Deep domain mastery aligned with high-performance software systems."
                ))
                .commonPitfalls(List.of(
                        "Saying 'I want your job' or 'I want to be CEO' (unrealistic).",
                        "Saying 'I want to pursue an MBA in 1 year' (indicates immediate attrition).",
                        "Vague cliches like 'I just want to be happy and successful'."
                ))
                .sampleModelAnswer("In the next 2 years, my primary goal is to become an indispensable backend engineer on your core product, achieving deep mastery of distributed microservices and low-latency data access patterns. By years 3 to 5, I aspire to take technical ownership of end-to-end service architectures, contribute to system design decisions, and mentor upcoming graduate recruits, driving measurable reliability and business velocity for the organization.")
                .companyTags(List.of("Microsoft", "Google", "TCS", "Accenture", "Infosys"))
                .build());
    }
}
