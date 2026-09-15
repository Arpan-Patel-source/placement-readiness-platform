package com.majorproject.backend.aptitude;

import com.majorproject.backend.aptitude.dto.FormulaCardDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AptitudeQuestionBank {

    private final List<AptitudeQuestion> questions = new ArrayList<>();
    private final List<FormulaCardDto> formulas = new ArrayList<>();

    public AptitudeQuestionBank() {
        initQuantitativeQuestions();
        initLogicalQuestions();
        initVerbalQuestions();
        initFormulaCheatsheet();
    }

    public List<AptitudeQuestion> getAllQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public Optional<AptitudeQuestion> findById(String id) {
        return questions.stream().filter(q -> q.getId().equalsIgnoreCase(id)).findFirst();
    }

    public List<FormulaCardDto> getFormulaCheatsheet() {
        return Collections.unmodifiableList(formulas);
    }

    private void initQuantitativeQuestions() {
        // --- Percentages ---
        questions.add(AptitudeQuestion.builder()
                .id("QA_PERC_01")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Percentages")
                .difficulty(AptitudeDifficulty.EASY)
                .question("A student must obtain 33% of the total marks to pass. He got 125 marks and failed by 40 marks. What was the maximum aggregate marks of the examination?")
                .options(List.of("450", "500", "550", "600"))
                .correctOptionIndex(1)
                .explanation("Passing marks required = 125 + 40 = 165 marks.\nGiven that passing marks = 33% of Maximum Marks (M).\nTherefore: (33 / 100) * M = 165\nM = (165 * 100) / 33 = 5 * 100 = 500.")
                .formulaTip("Pass Marks = Scored Marks + Failing Deficit = (Pass % * Total Marks) / 100")
                .companiesAsked(List.of("TCS", "Wipro", "Cognizant"))
                .build());

        questions.add(AptitudeQuestion.builder()
                .id("QA_PERC_02")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Percentages")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("If the price of petrol increases by 25%, by what percentage must a car owner reduce fuel consumption so that the overall expenditure remains unchanged?")
                .options(List.of("15%", "20%", "25%", "33.33%"))
                .correctOptionIndex(1)
                .explanation("Let original price be 100 and consumption be 100 units (Expenditure = 10,000).\nNew price = 125.\nNew consumption needed for 10,000 expenditure = 10,000 / 125 = 80 units.\nReduction in consumption = 100 - 80 = 20%.\nFormula shortcut: [R / (100 + R)] * 100 = [25 / 125] * 100 = 20%.")
                .formulaTip("Consumption reduction % = [r / (100 + r)] * 100 when expenditure is constant.")
                .companiesAsked(List.of("Infosys", "Accenture", "Capgemini"))
                .build());

        // --- Profit and Loss ---
        questions.add(AptitudeQuestion.builder()
                .id("QA_PNL_01")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Profit and Loss")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("A shopkeeper sells two laptops at ₹36,000 each. On one he gains 20% and on the other he loses 20%. What is his overall gain or loss percentage?")
                .options(List.of("No profit, no loss", "2% Gain", "4% Loss", "1% Loss"))
                .correctOptionIndex(2)
                .explanation("When two items are sold at the same selling price, one at a gain of x% and the other at a loss of x%, there is always an overall loss.\nLoss percentage = (x / 10)^2 = (20 / 10)^2 = 2^2 = 4% Loss.")
                .formulaTip("Common SP with equal gain and loss %: Loss % = (Common % / 10)^2")
                .companiesAsked(List.of("Amazon", "TCS Digital", "Tech Mahindra"))
                .build());

        questions.add(AptitudeQuestion.builder()
                .id("QA_PNL_02")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Profit and Loss")
                .difficulty(AptitudeDifficulty.HARD)
                .question("A dishonest dealer professes to sell his goods at cost price, but uses a false weight of 950 grams for a 1 kg (1000g) weight. What is his net gain percentage?")
                .options(List.of("5%", "5.26%", "4.76%", "6.25%"))
                .correctOptionIndex(1)
                .explanation("Gain % = [Error / (True Value - Error)] * 100\nHere Error = 1000 - 950 = 50g.\nTrue Value - Error = 950g.\nGain % = (50 / 950) * 100 = (5 / 95) * 100 = 100 / 19 ≈ 5.263%.")
                .formulaTip("Dishonest Dealer Gain % = [Error / False Weight] * 100")
                .companiesAsked(List.of("LTI Mindtree", "Infosys", "Goldman Sachs"))
                .build());

        // --- Time and Work ---
        questions.add(AptitudeQuestion.builder()
                .id("QA_TNW_01")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Time and Work")
                .difficulty(AptitudeDifficulty.EASY)
                .question("Worker A can complete a software sprint in 12 days, while Worker B can complete it in 24 days. Working together, in how many days can they complete the sprint?")
                .options(List.of("6 days", "8 days", "10 days", "18 days"))
                .correctOptionIndex(1)
                .explanation("Rate of A = 1/12 work/day.\nRate of B = 1/24 work/day.\nCombined Rate = 1/12 + 1/24 = 2/24 + 1/24 = 3/24 = 1/8 work/day.\nTotal days required = 8 days.\nShortcut: (A * B) / (A + B) = (12 * 24) / (12 + 24) = 288 / 36 = 8 days.")
                .formulaTip("Combined time for two individuals = (A * B) / (A + B)")
                .companiesAsked(List.of("TCS", "HCL", "Accenture"))
                .build());

        questions.add(AptitudeQuestion.builder()
                .id("QA_TNW_02")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Time and Work")
                .difficulty(AptitudeDifficulty.HARD)
                .question("A is twice as efficient as B and together they can complete a job in 18 days. In how many days can A alone complete the same job?")
                .options(List.of("27 days", "36 days", "54 days", "24 days"))
                .correctOptionIndex(0)
                .explanation("Efficiency ratio A : B = 2 : 1.\nCombined efficiency = 2 + 1 = 3 units/day.\nTotal work = Combined efficiency * Total days = 3 * 18 = 54 units.\nTime taken by A alone = Total work / A's efficiency = 54 / 2 = 27 days.")
                .formulaTip("Total Work = Total Efficiency * Number of Days")
                .companiesAsked(List.of("Oracle", "Amazon", "Wipro Turbo"))
                .build());

        // --- Probability ---
        questions.add(AptitudeQuestion.builder()
                .id("QA_PROB_01")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Probability")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("Two fair standard six-sided dice are rolled simultaneously. What is the probability that the sum of the numbers appearing on the dice is 8?")
                .options(List.of("5/36", "1/6", "7/36", "1/9"))
                .correctOptionIndex(0)
                .explanation("Total possible outcomes when two dice are rolled = 6 * 6 = 36.\nOutcomes yielding sum of 8:\n(2, 6), (3, 5), (4, 4), (5, 3), (6, 2) => Total 5 favorable outcomes.\nProbability = Favorable / Total = 5/36.")
                .formulaTip("P(Event) = n(E) / n(S) where n(S) = 36 for two dice.")
                .companiesAsked(List.of("Deloitte", "Infosys", "EY"))
                .build());

        questions.add(AptitudeQuestion.builder()
                .id("QA_PROB_02")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Probability")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("A bag contains 4 red, 5 green, and 6 blue balls. If two balls are drawn at random one after another without replacement, what is the probability that both are red?")
                .options(List.of("2/35", "4/15", "6/105", "2/15"))
                .correctOptionIndex(0)
                .explanation("Total balls = 4 + 5 + 6 = 15 balls.\nProbability of 1st ball being red = 4 / 15.\nRemaining balls = 14 (with 3 red).\nProbability of 2nd ball being red = 3 / 14.\nCombined probability = (4 / 15) * (3 / 14) = 12 / 210 = 2 / 35.")
                .formulaTip("Without replacement: P(A and B) = P(A) * P(B|A)")
                .companiesAsked(List.of("Morgan Stanley", "TCS Ninja", "IBM"))
                .build());

        // --- Permutations and Combinations ---
        questions.add(AptitudeQuestion.builder()
                .id("QA_PNC_01")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Permutation & Combination")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("In how many different ways can the letters of the word 'LEADER' be arranged such that the vowels always come together?")
                .options(List.of("72", "144", "288", "360"))
                .correctOptionIndex(0)
                .explanation("Word 'LEADER' has 6 letters: L, E, A, D, E, R.\nVowels are E, A, E (3 vowels with E repeated twice).\nConsonants are L, D, R (3 consonants).\nBundle the vowels (E, A, E) into 1 unit.\nNow we arrange 4 units: {EAE}, L, D, R => 4! = 24 ways.\nThe vowels inside the bundle (E, A, E) can be arranged among themselves in 3! / 2! = 6 / 2 = 3 ways.\nTotal distinct arrangements = 24 * 3 = 72.")
                .formulaTip("Grouping method: Treat grouped items as 1 entity, multiply by internal permutations accounting for duplicate letters.")
                .companiesAsked(List.of("Capgemini", "Accenture", "Cognizant"))
                .build());

        questions.add(AptitudeQuestion.builder()
                .id("QA_PNC_02")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Permutation & Combination")
                .difficulty(AptitudeDifficulty.EASY)
                .question("At an international tech conference, each of the 12 participants shakes hands exactly once with every other participant. How many total handshakes occur?")
                .options(List.of("144", "132", "66", "72"))
                .correctOptionIndex(2)
                .explanation("A handshake occurs between any pair of 2 persons chosen out of 12.\nTotal handshakes = 12C2 = (12 * 11) / (2 * 1) = 132 / 2 = 66 handshakes.")
                .formulaTip("Handshake formula: n*(n - 1) / 2 = nC2")
                .companiesAsked(List.of("Infosys", "Wipro", "TCS"))
                .build());

        // --- Speed Distance Time ---
        questions.add(AptitudeQuestion.builder()
                .id("QA_SDT_01")
                .category(AptitudeCategory.QUANTITATIVE)
                .topic("Speed, Distance & Time")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("A train 280 meters long is travelling at a speed of 63 km/h. How many seconds will it take to pass a stationary tree?")
                .options(List.of("14 sec", "16 sec", "18 sec", "20 sec"))
                .correctOptionIndex(1)
                .explanation("To pass a point object like a tree or pole, the train must cover its own length (280 m).\nConvert speed from km/h to m/s: 63 * (5/18) = 35 / 2 = 17.5 m/s.\nTime taken = Distance / Speed = 280 / 17.5 = 16 seconds.")
                .formulaTip("Convert km/h to m/s by multiplying by 5/18. Time = Distance / Speed.")
                .companiesAsked(List.of("TCS", "Cognizant", "Mindtree"))
                .build());
    }

    private void initLogicalQuestions() {
        // --- Blood Relations ---
        questions.add(AptitudeQuestion.builder()
                .id("LR_BLD_01")
                .category(AptitudeCategory.LOGICAL_REASONING)
                .topic("Blood Relations")
                .difficulty(AptitudeDifficulty.EASY)
                .question("Pointing to a photograph of a boy, Suresh said, 'He is the only son of the mother of my mother.' How is Suresh related to the boy in the photograph?")
                .options(List.of("Brother", "Uncle", "Nephew", "Cousin"))
                .correctOptionIndex(2)
                .explanation("Break it down backwards:\n1. 'Mother of my mother' = Suresh's maternal grandmother.\n2. 'Only son of maternal grandmother' = Suresh's maternal uncle.\n3. Therefore, the boy in the photograph is Suresh's maternal uncle.\n4. So Suresh is the Nephew of the boy (since the question asks how Suresh is related to the boy).")
                .formulaTip("Trace relations from the end of the sentence: 'Mother of my mother' -> Grandmother -> Son -> Maternal Uncle.")
                .companiesAsked(List.of("Wipro", "TCS", "Accenture"))
                .build());

        questions.add(AptitudeQuestion.builder()
                .id("LR_BLD_02")
                .category(AptitudeCategory.LOGICAL_REASONING)
                .topic("Blood Relations")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("If 'A + B' means A is the daughter of B; 'A - B' means A is the brother of B; 'A % B' means A is the father of B. Which of the following expressions indicates that 'P is the son of Q'?")
                .options(List.of("P - R % Q", "Q % P - R", "P - Q + R", "Q + P - R"))
                .correctOptionIndex(1)
                .explanation("Let's analyze 'Q % P - R':\n- 'Q % P' means Q is the father of P.\n- 'P - R' means P is the brother of R, establishing that P is male.\nSince Q is the father and P is male, P is conclusively the son of Q.")
                .formulaTip("For coded relations, verify both generation level and biological gender of the subject.")
                .companiesAsked(List.of("Infosys", "Amazon", "Capgemini"))
                .build());

        // --- Coding-Decoding ---
        questions.add(AptitudeQuestion.builder()
                .id("LR_COD_01")
                .category(AptitudeCategory.LOGICAL_REASONING)
                .topic("Coding-Decoding")
                .difficulty(AptitudeDifficulty.EASY)
                .question("In a certain code language, if 'COMPUTER' is written as 'RFUVQNPC', how will 'MEDICINE' be written in that code?")
                .options(List.of("EOJDJEFM", "EOJDEJFM", "MFEJDJOE", "EOJDJTEM"))
                .correctOptionIndex(0)
                .explanation("Analyze the pattern between COMPUTER and RFUVQNPC:\n1. First and last letters are swapped: C moves to end, R moves to front.\n2. All interior letters are shifted by +1 and reversed:\nO(+1) = P, M(+1) = N, P(+1) = Q, U(+1) = V, T(+1) = U, E(+1) = F.\nApply same transformation to MEDICINE:\n- M becomes last, E becomes first.\n- Interior letters: E(+1)=F, D(+1)=E, I(+1)=J, C(+1)=D, I(+1)=J, N(+1)=O.\nReversed interior sequence = O, J, D, J, E, F.\nCombining gives: E O J D J E F M.")
                .formulaTip("Look for reverse ordering combined with fixed positional shifts (+1, -1, +2).")
                .companiesAsked(List.of("TCS", "Mindtree", "Cognizant"))
                .build());

        // --- Seating Arrangement ---
        questions.add(AptitudeQuestion.builder()
                .id("LR_SEA_01")
                .category(AptitudeCategory.LOGICAL_REASONING)
                .topic("Seating Arrangement")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("Six friends A, B, C, D, E, and F are sitting in a circle facing the center. E is to the immediate left of D. C sits between A and B. F is between E and A. Who is sitting to the immediate right of C?")
                .options(List.of("A", "B", "D", "E"))
                .correctOptionIndex(0)
                .explanation("Facing center:\n- Immediate left of D is E.\n- F is between E and A -> sequence: D - E - F - A.\n- C is between A and B -> sequence: D - E - F - A - C - B (and B is next to D to complete circle).\nWalking clockwise facing center: C is between A and B. With A to its right and B to its left.\nTherefore, sitting to the immediate right of C is A.")
                .formulaTip("When facing the center, clockwise is to the left, and counter-clockwise is to the right.")
                .companiesAsked(List.of("Infosys", "Tech Mahindra", "Wipro"))
                .build());

        // --- Puzzles & Syllogisms ---
        questions.add(AptitudeQuestion.builder()
                .id("LR_SYL_01")
                .category(AptitudeCategory.LOGICAL_REASONING)
                .topic("Puzzles & Deduction")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("Statements:\n1. All clouds are rain.\n2. All rain are water.\nConclusions:\nI. Some water are clouds.\nII. All clouds are water.\nWhich conclusion(s) logically follow?")
                .options(List.of("Only I follows", "Only II follows", "Either I or II follows", "Both I and II follow"))
                .correctOptionIndex(3)
                .explanation("Venn Diagram representation:\n- Set 'Clouds' is completely inside set 'Rain'.\n- Set 'Rain' is completely inside set 'Water'.\nTherefore, 'Clouds' is completely enclosed inside 'Water', which means:\nConclusion II: 'All clouds are water' is completely true.\nConclusion I: Since the entire set of clouds is in water, a portion of water is definitely clouds, so 'Some water are clouds' is also true.\nHence, Both I and II follow.")
                .formulaTip("Universal affirmative syllogisms: All A are B, All B are C => All A are C, and Some C are A.")
                .companiesAsked(List.of("Amazon", "Capgemini", "Accenture"))
                .build());
    }

    private void initVerbalQuestions() {
        // --- Vocabulary ---
        questions.add(AptitudeQuestion.builder()
                .id("VA_VOC_01")
                .category(AptitudeCategory.VERBAL_ABILITY)
                .topic("Vocabulary")
                .difficulty(AptitudeDifficulty.EASY)
                .question("Select the word that is most nearly SYNONYMOUS to 'CANDID':")
                .options(List.of("Secretive", "Frank", "Deceitful", "Cautious"))
                .correctOptionIndex(1)
                .explanation("'Candid' means truthful, straightforward, and sincere in speech or expression.\n'Frank' conveys the exact same meaning (open, honest, direct).\n'Secretive' and 'Deceitful' are direct antonyms.")
                .formulaTip("Look at word roots and prefixes: Candidus (Latin: pure white, open, direct).")
                .companiesAsked(List.of("TCS", "Infosys", "Deloitte"))
                .build());

        questions.add(AptitudeQuestion.builder()
                .id("VA_VOC_02")
                .category(AptitudeCategory.VERBAL_ABILITY)
                .topic("Vocabulary")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("Choose the word that is opposite in meaning (ANTONYM) to 'METICULOUS':")
                .options(List.of("Painstaking", "Scrupulous", "Careless", "Thorough"))
                .correctOptionIndex(2)
                .explanation("'Meticulous' refers to showing great attention to detail; very careful and precise.\n'Painstaking', 'Scrupulous', and 'Thorough' are synonyms.\n'Careless' is the exact opposite (lacking attention or concern for detail).")
                .formulaTip("Identify whether the prompt asks for a Synonym (same) or Antonym (opposite).")
                .companiesAsked(List.of("Cognizant", "Wipro", "Amazon"))
                .build());

        // --- Error Detection ---
        questions.add(AptitudeQuestion.builder()
                .id("VA_ERR_01")
                .category(AptitudeCategory.VERBAL_ABILITY)
                .topic("Error Detection")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("Identify the segment containing a grammatical error in the following sentence:\n'Neither the software architect (A) / nor the frontend developers (B) / was able to reproduce the concurrency bug. (C) / No error (D)'")
                .options(List.of("Segment (A)", "Segment (B)", "Segment (C)", "Segment (D)"))
                .correctOptionIndex(2)
                .explanation("Rule of Proximity: When two subjects are connected by 'neither... nor', the verb agrees with the subject closest to it.\nHere, the nearer subject is 'the frontend developers' (plural).\nTherefore, the verb must be plural 'were able to reproduce', not 'was able to reproduce'. Error is in Segment (C).")
                .formulaTip("Neither... nor / Either... or: Verb agrees with the subject closest to the verb.")
                .companiesAsked(List.of("Accenture", "TCS Digital", "Infosys"))
                .build());

        // --- Sentence Correction ---
        questions.add(AptitudeQuestion.builder()
                .id("VA_COR_01")
                .category(AptitudeCategory.VERBAL_ABILITY)
                .topic("Sentence Correction")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("Choose the correct alternative for the underlined part:\n'Hardly had the engineer deployed the hotfix, than the production servers crashed.'")
                .options(List.of(
                        "deployed the hotfix, when the production servers crashed.",
                        "deployed the hotfix, then the production servers crashed.",
                        "had deployed the hotfix, when the production servers crashed.",
                        "No correction required"
                ))
                .correctOptionIndex(0)
                .explanation("The correlative conjunction pair is 'Hardly / Scarcely... when', never 'than'. ('No sooner... than' uses 'than').\nHence: 'Hardly had the engineer deployed the hotfix, when the production servers crashed.'")
                .formulaTip("Remember pairs: 'Hardly... when', 'Scarcely... when', 'No sooner... than'.")
                .companiesAsked(List.of("Goldman Sachs", "Amazon", "Wipro"))
                .build());

        // --- Reading Comprehension ---
        questions.add(AptitudeQuestion.builder()
                .id("VA_RC_01")
                .category(AptitudeCategory.VERBAL_ABILITY)
                .topic("Reading Comprehension")
                .difficulty(AptitudeDifficulty.MEDIUM)
                .question("Read the following excerpt:\n'Modern microservice architectures decompose monolithic software systems into small, independently deployable services that communicate via lightweight protocols. While this decouples development velocities, it significantly elevates operational complexity in observability and distributed tracing.'\n\nWhat is the primary drawback highlighted by the author?")
                .options(List.of(
                        "Microservices slow down independent team deployment velocities.",
                        "Increased difficulty in monitoring and tracing requests across distributed services.",
                        "Inability to communicate using lightweight network protocols.",
                        "Complete loss of decoupling across software teams."
                ))
                .correctOptionIndex(1)
                .explanation("The passage states: 'While this decouples development velocities, it significantly elevates operational complexity in observability and distributed tracing.' This directly identifies monitoring and distributed tracing complexity as the drawback.")
                .formulaTip("In RC questions, focus on contrast markers like 'While', 'However', 'In contrast'.")
                .companiesAsked(List.of("TCS Digital", "Infosys", "Persistent"))
                .build());
    }

    private void initFormulaCheatsheet() {
        formulas.add(FormulaCardDto.builder()
                .category("Quantitative")
                .topic("Percentages")
                .title("Percentage Change & Price-Consumption")
                .formula("% Change = [(New - Old) / Old] * 100\nReduction = [R / (100 + R)] * 100")
                .tip("When price goes up by R%, consumption must drop by [R / (100 + R)] * 100 to keep spending equal.")
                .example("If sugar price increases by 25%, consumption must decrease by [25/125]*100 = 20%.")
                .build());

        formulas.add(FormulaCardDto.builder()
                .category("Quantitative")
                .topic("Profit and Loss")
                .title("Successive Discounts & Same SP Gain/Loss")
                .formula("Net Discount = d1 + d2 - (d1 * d2 / 100)\nOverall Loss % = (Common % / 10)^2")
                .tip("Selling two identical priced items with +x% and -x% always produces a loss of (x/10)^2 percent.")
                .example("Sold two items at same SP: +20% on one, -20% on other. Net result = (20/10)^2 = 4% Loss.")
                .build());

        formulas.add(FormulaCardDto.builder()
                .category("Quantitative")
                .topic("Time and Work")
                .title("Two-Worker Joint Completion")
                .formula("Time Together = (A * B) / (A + B)\nWork = Efficiency * Days")
                .tip("If A takes 12 days and B takes 24 days, joint time is (12 * 24) / (12 + 24) = 8 days.")
                .example("12 * 24 / 36 = 8 days.")
                .build());

        formulas.add(FormulaCardDto.builder()
                .category("Quantitative")
                .topic("Speed & Distance")
                .title("Relative Speed & Unit Conversions")
                .formula("km/h to m/s: Multiply by 5/18\nm/s to km/h: Multiply by 18/5\nOpposite: S1 + S2 | Same: |S1 - S2|")
                .tip("Trains passing poles cover their own length. Passing bridges cover Length + Bridge.")
                .example("54 km/h = 54 * (5/18) = 15 m/s.")
                .build());

        formulas.add(FormulaCardDto.builder()
                .category("Logical")
                .topic("Coding & Deduction")
                .title("Alphabet Positions (EJOTY)")
                .formula("E=5, J=10, O=15, T=20, Y=25\nOpposite Letters Sum = 27 (A <-> Z, B <-> Y)")
                .tip("Use the EJOTY benchmark to instantly locate any letter index in coding-decoding questions.")
                .example("Opposite of 'M' (13): 27 - 13 = 14 ('N').")
                .build());

        formulas.add(FormulaCardDto.builder()
                .category("Verbal")
                .topic("Grammar Rules")
                .title("Correlative Conjunction Pairs")
                .formula("Hardly / Scarcely ... WHEN\nNo Sooner ... THAN\nNeither ... NOR / Either ... OR")
                .tip("Never mix 'Hardly' with 'Than'. Always use 'Hardly had... when'.")
                .example("'Hardly had I opened the door when the phone rang.'")
                .build());
    }
}
