package com.suryakn.IssueTracker;

import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.entity.FAQ;
import com.suryakn.IssueTracker.entity.Procedure;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
import com.suryakn.IssueTracker.repository.FAQRepository;
import com.suryakn.IssueTracker.repository.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeDataInitializer implements CommandLineRunner {

    private final FAQRepository faqRepository;
    private final ProcedureRepository procedureRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {
        seedFaqs();
        seedProcedures();
    }

    private void seedFaqs() {
        if (faqRepository.count() > 0) {
            return;
        }

        faqRepository.saveAll(List.of(
                FAQ.builder()
                        .question("Comment obtenir un accès VPN ?")
                        .answer("Créez une demande Informatique en précisant votre matricule, le poste concerné et la justification d'accès distant. Le support IT active ensuite le profil VPN et vous transmet les paramètres de connexion.")
                        .category("informatique")
                        .keywords(List.of("vpn", "accès distant", "connexion", "informatique"))
                        .department(resolveDepartment("IT"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment réinitialiser mon mot de passe professionnel ?")
                        .answer("Si vous avez oublié votre mot de passe, ouvrez un ticket Informatique avec votre matricule et votre email professionnel. En cas d'urgence, mentionnez que l'accès à la messagerie est bloqué.")
                        .category("informatique")
                        .keywords(List.of("mot de passe", "password", "email", "compte"))
                        .department(resolveDepartment("IT"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment demander un ordinateur ou un écran supplémentaire ?")
                        .answer("Utilisez la catégorie Matériel et précisez le besoin, le site concerné, le responsable valideur et l'urgence. Le service Achats ou support matériel traitera la demande.")
                        .category("materiel")
                        .keywords(List.of("ordinateur", "écran", "matériel", "achat"))
                        .department(resolveDepartment("Achats"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment faire une demande de congé ou d'attestation ?")
                        .answer("Les demandes RH doivent être créées dans la catégorie Administratif. Indiquez la nature de la demande, la période concernée et toute pièce justificative si nécessaire.")
                        .category("administratif")
                        .keywords(List.of("congé", "attestation", "rh", "administratif"))
                        .department(resolveDepartment("RH"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment accéder au système de réservation des vols ?")
                        .answer("Le système Amadeus est accessible via le portail IT. Si vous n'avez pas accès, créez un ticket Informatique avec votre matricule et le motif de l'accès.")
                        .category("informatique")
                        .keywords(List.of("amadeus", "réservation", "vols", "sabre", "système"))
                        .department(resolveDepartment("IT"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment signaler un problème de climatisation dans les bureaux ?")
                        .answer("Créez un ticket Maintenance en précisant l'emplacement exact, le numéro de bureau et la description du problème. Le service maintenance interviendra sous 24h.")
                        .category("maintenance")
                        .keywords(List.of("climatisation", "chauffage", "température", "bureau"))
                        .department(resolveDepartment("Maintenance"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment demander une formation professionnelle ?")
                        .answer("Les demandes de formation doivent être validées par votre supérieur hiérarchique. Créez un ticket RH avec le thème de formation souhaité et la justification métier.")
                        .category("administratif")
                        .keywords(List.of("formation", "apprentissage", "certification", "développement"))
                        .department(resolveDepartment("RH"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment accéder aux procédures de sécurité aérienne ?")
                        .answer("Les procédures de sécurité sont disponibles dans l'intranet Air Algérie, section 'Procédures et Directives'. Pour un accès spécifique, contactez votre responsable sécurité.")
                        .category("administratif")
                        .keywords(List.of("sécurité", "procédures", "directives", "aviation"))
                        .department(resolveDepartment("RH"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment signaler un problème électrique ou d'éclairage ?")
                        .answer("Pour les problèmes électriques ou d'éclairage, créez immédiatement un ticket Maintenance prioritaire en précisant l'emplacement et la nature du problème.")
                        .category("maintenance")
                        .keywords(List.of("électricité", "lumière", "éclairage", "court-circuit"))
                        .department(resolveDepartment("Maintenance"))
                        .active(true)
                        .viewCount(0)
                        .build(),
                FAQ.builder()
                        .question("Comment obtenir une carte d'accès aux zones sécurisées ?")
                        .answer("Les cartes d'accès sont gérées par le service Sécurité. Créez un ticket Administratif avec votre photo d'identité et la zone d'accès requise.")
                        .category("administratif")
                        .keywords(List.of("carte d'accès", "badge", "sécurité", "zone sécurisée"))
                        .department(resolveDepartment("RH"))
                        .active(true)
                        .viewCount(0)
                        .build()
        ));

        log.info("Seeded default FAQ knowledge base.");
    }

    private void seedProcedures() {
        if (procedureRepository.count() > 0) {
            return;
        }

        procedureRepository.saveAll(List.of(
                Procedure.builder()
                        .title("Procédure d'accès VPN pour les employés")
                        .description("Étapes de création et de validation d'un accès distant VPN.")
                        .content("1. Vérifier que l'employé dispose d'un poste identifié. 2. Créer un ticket Informatique. 3. Joindre la justification métier. 4. Le support IT valide, configure le compte et communique les paramètres de connexion.")
                        .category("informatique")
                        .department(resolveDepartment("IT"))
                        .active(true)
                        .build(),
                Procedure.builder()
                        .title("Procédure de demande de matériel bureautique")
                        .description("Traitement d'une demande d'ordinateur, écran, imprimante ou accessoires.")
                        .content("1. Décrire précisément l'équipement demandé. 2. Mentionner le site ou service concerné. 3. Ajouter la validation hiérarchique si nécessaire. 4. Le service Achats planifie la fourniture ou l'approvisionnement.")
                        .category("materiel")
                        .department(resolveDepartment("Achats"))
                        .active(true)
                        .build(),
                Procedure.builder()
                        .title("Procédure de prise en charge d'une demande RH")
                        .description("Traitement des demandes administratives et RH.")
                        .content("1. Choisir la catégorie Administratif. 2. Indiquer le type de document ou de demande. 3. Ajouter les dates ou références utiles. 4. Le service RH répond via le ticket et archive la réponse.")
                        .category("administratif")
                        .department(resolveDepartment("RH"))
                        .active(true)
                        .build()
        ));

        log.info("Seeded default procedures knowledge base.");
    }

    private Department resolveDepartment(String name) {
        Optional<Department> department = departmentRepository.findByName(name);
        return department.orElse(null);
    }
}
