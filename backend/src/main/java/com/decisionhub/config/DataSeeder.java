package com.decisionhub.config;

import com.decisionhub.entity.*;
import com.decisionhub.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final DecisionRepository decisionRepository;
    private final DecisionOptionRepository optionRepository;
    private final DecisionFactorRepository factorRepository;
    private final OptionScoreRepository optionScoreRepository;
    private final FactorWeightRepository factorWeightRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seedData() {
        if (userRepository.count() > 0) {
            log.info("Database already has data, skipping seed.");
            return;
        }

        log.info("Seeding demo data...");

        // Create demo user
        User demoUser = userRepository.save(User.builder()
                .username("user")
                .email("user@decisionhub.com")
                .password(passwordEncoder.encode("password"))
                .fullName("Demo User")
                .roles("USER")
                .enabled(true)
                .build());
        log.info("Created demo user: user/password");

        // Decision 1: Cloud provider
        seedCloudProviderDecision(demoUser);
        // Decision 2: Hire developer
        seedHireDeveloperDecision(demoUser);
        // Decision 3: Frontend framework
        seedFrontendFrameworkDecision(demoUser);

        log.info("Seed data complete. 3 sample decisions created.");
    }

    private void seedCloudProviderDecision(User user) {
        Decision decision = decisionRepository.save(Decision.builder()
                .user(user)
                .title("Which cloud provider for new microservices?")
                .description("We need to choose a cloud provider for our new microservices architecture. Factors include cost, scalability, developer experience, and market share.")
                .category("Technology")
                .status("ACTIVE")
                .urgency("HIGH")
                .budget(new BigDecimal("50000"))
                .deadline(LocalDate.now().plusDays(30))
                .build());

        DecisionOption aws = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("AWS").description("Amazon Web Services - Market leader")
                .build());
        DecisionOption gcp = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("GCP").description("Google Cloud Platform - Strong in AI/ML")
                .build());
        DecisionOption azure = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("Azure").description("Microsoft Azure - Enterprise-friendly")
                .build());

        DecisionFactor costFactor = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Cost").description("Total cost of ownership")
                .build());
        DecisionFactor scaleFactor = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Scalability").description("Ability to scale horizontally")
                .build());
        DecisionFactor dxFactor = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Developer Experience").description("SDKs, APIs, documentation quality")
                .build());
        DecisionFactor marketFactor = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Market Share").description("Adoption and community size")
                .build());

        // Weights
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(costFactor).weight(1.5).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(scaleFactor).weight(1.0).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(dxFactor).weight(1.2).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(marketFactor).weight(0.8).build());

        // Scores (1-10 scale)
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(aws).factor(costFactor).score(6.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(aws).factor(scaleFactor).score(9.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(aws).factor(dxFactor).score(8.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(aws).factor(marketFactor).score(10.0).build());

        optionScoreRepository.save(OptionScore.builder().decision(decision).option(gcp).factor(costFactor).score(7.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(gcp).factor(scaleFactor).score(8.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(gcp).factor(dxFactor).score(7.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(gcp).factor(marketFactor).score(6.0).build());

        optionScoreRepository.save(OptionScore.builder().decision(decision).option(azure).factor(costFactor).score(7.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(azure).factor(scaleFactor).score(8.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(azure).factor(dxFactor).score(7.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(azure).factor(marketFactor).score(7.0).build());
    }

    private void seedHireDeveloperDecision(User user) {
        Decision decision = decisionRepository.save(Decision.builder()
                .user(user)
                .title("Hire junior or senior developer?")
                .description("Our team needs additional development capacity. Should we hire a junior developer, senior developer, or a contract team?")
                .category("HR")
                .status("ACTIVE")
                .urgency("MEDIUM")
                .budget(new BigDecimal("120000"))
                .deadline(LocalDate.now().plusDays(45))
                .build());

        DecisionOption junior = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("Junior Dev").description("Entry-level developer with training needed")
                .build());
        DecisionOption senior = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("Senior Dev").description("Experienced developer, immediate impact")
                .build());
        DecisionOption contract = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("Contract Team").description("Outsourced team for specific deliverables")
                .build());

        DecisionFactor budgetF = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Budget Impact").description("Cost to the organization")
                .build());
        DecisionFactor timeF = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Time to Productivity").description("How quickly they contribute")
                .build());
        DecisionFactor valueF = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Long-term Value").description("Sustained contribution over time")
                .build());
        DecisionFactor fitF = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Team Fit").description("Cultural and skills alignment")
                .build());

        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(budgetF).weight(1.3).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(timeF).weight(1.0).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(valueF).weight(1.4).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(fitF).weight(1.1).build());

        optionScoreRepository.save(OptionScore.builder().decision(decision).option(junior).factor(budgetF).score(9.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(junior).factor(timeF).score(4.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(junior).factor(valueF).score(6.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(junior).factor(fitF).score(7.0).build());

        optionScoreRepository.save(OptionScore.builder().decision(decision).option(senior).factor(budgetF).score(6.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(senior).factor(timeF).score(9.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(senior).factor(valueF).score(9.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(senior).factor(fitF).score(8.0).build());

        optionScoreRepository.save(OptionScore.builder().decision(decision).option(contract).factor(budgetF).score(7.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(contract).factor(timeF).score(6.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(contract).factor(valueF).score(5.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(contract).factor(fitF).score(5.0).build());
    }

    private void seedFrontendFrameworkDecision(User user) {
        Decision decision = decisionRepository.save(Decision.builder()
                .user(user)
                .title("Which frontend framework for new dashboard?")
                .description("We're building a new analytics dashboard and need to pick a frontend framework. Options: React, Vue, Svelte.")
                .category("Technology")
                .status("ACTIVE")
                .urgency("HIGH")
                .budget(new BigDecimal("30000"))
                .deadline(LocalDate.now().plusDays(14))
                .build());

        DecisionOption react = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("React").description("Facebook's UI library, largest ecosystem")
                .build());
        DecisionOption vue = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("Vue").description("Progressive framework, gentle learning curve")
                .build());
        DecisionOption svelte = optionRepository.save(DecisionOption.builder()
                .decision(decision).name("Svelte").description("Compiler-based, no virtual DOM")
                .build());

        DecisionFactor learningF = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Learning Curve").description("Time to onboard new developers")
                .build());
        DecisionFactor perfF = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Performance").description("Runtime speed and bundle size")
                .build());
        DecisionFactor ecoF = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Ecosystem").description("Available libraries and tools")
                .build());
        DecisionFactor hiringF = factorRepository.save(DecisionFactor.builder()
                .decision(decision).name("Hiring Pool").description("Availability of developers")
                .build());

        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(learningF).weight(1.0).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(perfF).weight(1.2).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(ecoF).weight(1.3).build());
        factorWeightRepository.save(FactorWeight.builder().decision(decision).factor(hiringF).weight(1.1).build());

        optionScoreRepository.save(OptionScore.builder().decision(decision).option(react).factor(learningF).score(7.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(react).factor(perfF).score(8.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(react).factor(ecoF).score(10.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(react).factor(hiringF).score(10.0).build());

        optionScoreRepository.save(OptionScore.builder().decision(decision).option(vue).factor(learningF).score(9.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(vue).factor(perfF).score(8.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(vue).factor(ecoF).score(7.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(vue).factor(hiringF).score(6.0).build());

        optionScoreRepository.save(OptionScore.builder().decision(decision).option(svelte).factor(learningF).score(8.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(svelte).factor(perfF).score(10.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(svelte).factor(ecoF).score(5.0).build());
        optionScoreRepository.save(OptionScore.builder().decision(decision).option(svelte).factor(hiringF).score(4.0).build());
    }
}