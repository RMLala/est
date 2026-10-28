package com.example.data.readingplan

enum class PlanCategory(val displayNameFr: String, val displayNameMg: String) {
    CHRONOLOGIQUE("Chronologique", "Ara-potoana"),
    THEMATIQUE("Thématique", "Ara-lohahevitra"),
    NOUVEAU_TESTAMENT("Nouveau Testament", "Fanekena Vaovao"),
    ANNUEL("Bible en 1 An", "Baiboly Manontolo")
}

data class PlanDay(
    val day: Int,
    val titleFr: String,
    val titleMg: String,
    val scripturePassage: String,
    val targetBook: String,
    val targetChapter: Int,
    val focusFr: String,
    val focusMg: String
)

data class ReadingPlan(
    val id: String,
    val category: PlanCategory,
    val titleFr: String,
    val titleMg: String,
    val descriptionFr: String,
    val descriptionMg: String,
    val durationDays: Int,
    val days: List<PlanDay>
)

object ReadingPlanRepository {

    val allPlans: List<ReadingPlan> = listOf(
        // 1. Plan Chronologique
        ReadingPlan(
            id = "plan_chronologique",
            category = PlanCategory.CHRONOLOGIQUE,
            titleFr = "Plan Chronologique de la Rédemption",
            titleMg = "Teti-pamakiana ara-potoana ny Famonjena",
            descriptionFr = "Suivez le fil historique des interventions divines de la Création jusqu'au triomphe final en Apocalypse.",
            descriptionMg = "Araho ny lalan'ny tantaran'ny famonjena nanomboka tamin'ny Famoronana ka hatramin'ny Fandresena farany ao amin'ny Apokalypsy.",
            durationDays = 7,
            days = listOf(
                PlanDay(1, "La Création & l'Esprit qui planait", "Ny Famoronana sy ny Fanahy", "Genèse 1-3", "Genèse", 1, "Le Ruach HaKodesh dans la Genèse", "Ny herin'ny Fanahy tamin'ny famoronana"),
                PlanDay(2, "L'Alliance & la Foi d'Abraham", "Ny Fanekena sy Finoan'i Abrahama", "Genèse 12, 15", "Genèse", 12, "Justification par la foi", "Fanamarinana amin'ny finoana"),
                PlanDay(3, "La Loi et l'Onction prophétique", "Ny Lalàna sy ny Fandatsahana Onction", "Ésaïe 53, 61", "Ésaïe", 53, "L'Onction sur le Messie souffrant", "Ny Hosotra eo amin'i Mesia"),
                PlanDay(4, "L'Incarnation & l'Agneau de Dieu", "Ny Fahatongavan'ny Teny ho Nofo", "Jean 1, 3", "Jean", 1, "La nouvelle naissance par l'Esprit", "Fahateraham-baovao amin'ny Fanahy"),
                PlanDay(5, "La Pentecôte & la Naissance de l'Église", "Ny Pentekosta sy ny Fiangonana", "Actes 1, 2", "Actes", 1, "Le baptême de feu et de puissance", "Batisa tamin'ny afo sy hery"),
                PlanDay(6, "La Vie victorieuse par l'Esprit", "Ny Fandresena amin'ny Fanahy", "Romains 8, Galates 5", "Romains", 8, "Aucune condamnation en Christ", "Tsy misy fanamelohana intsony"),
                PlanDay(7, "La Consommation & la Jérusalem Céleste", "Ny Fandresena Farany", "Apocalypse 21, 22", "Apocalypse", 21, "L'Esprit et l'Épouse disent : Viens !", "Ny Fanahy sy ny Ampakarina manao hoe : Avia !")
            )
        ),

        // 2. Plan Thématique : Le Saint-Esprit
        ReadingPlan(
            id = "plan_saint_esprit",
            category = PlanCategory.THEMATIQUE,
            titleFr = "Immersion dans le Saint-Esprit (Pneumatologie)",
            titleMg = "Firoboroboana ao amin'ny Fanahy Masina",
            descriptionFr = "Découvrez la personne, la communion, les dons et la puissance opérante du Paraclet dans votre quotidien.",
            descriptionMg = "Fantaro ny maha-Olona Azy, ny firaisana Aminy, ny fanomezam-pahasoavana ary ny herin'ny Fanahy Masina.",
            durationDays = 7,
            days = listOf(
                PlanDay(1, "La Promesse de l'autre Consolateur", "Ilay Mpananatra Hafa nampanantenaina", "Jean 14:15-27", "Jean", 14, "Il demeurera éternellement avec vous", "Hitoetra aminareo mandrakizay Izy"),
                PlanDay(2, "Le Guide dans toute la Vérité", "Ny Mpitarika ho amin'ny Marina rehetra", "Jean 16:7-15", "Jean", 16, "Il révélera la gloire de Jésus", "Hanambara ny voninahitr'i Jesosy Izy"),
                PlanDay(3, "La Puissance d'en Haut pour être Témoin", "Ny Hery avy any ambony ho vavolombelona", "Actes 1:4-8", "Actes", 1, "Recevoir le Dunamis céleste", "Handray ny Dunamis avy any an-danitra"),
                PlanDay(4, "L'Effusion Souveraine de Pentecôte", "Ny Firotsahan'ny Fanahy tamin'ny Pentekosta", "Actes 2:1-21", "Actes", 2, "Remplis de l'Esprit et parlers nouveaux", "Feno ny Fanahy Masina sy fiteny maro"),
                PlanDay(5, "La Loi de l'Esprit de Vie", "Ny Lalàn'ny Fanahin'Aina", "Romains 8:1-17", "Romains", 8, "Conduits par l'Esprit, fils de Dieu", "Tarihin'ny Fanahy, zanak'Andriamanitra"),
                PlanDay(6, "Le Fruit de l'Esprit contre la chair", "Ny Vokatry ny Fanahy", "Galates 5:16-26", "Galates", 5, "Amour, joie, paix, fidélité", "Fitiavana, fifaliana, fiadanana"),
                PlanDay(7, "Les Dons spirituels pour l'édification", "Ireo Fanomezam-pahasoavana", "1 Corinthiens 12:1-11", "1 Corinthiens", 12, "Paroles de sagesse, foi, miracles", "Fahendrena, finoana, fahagagana")
            )
        ),

        // 3. Plan Thématique : La Foi Victorieuse
        ReadingPlan(
            id = "plan_foi",
            category = PlanCategory.THEMATIQUE,
            titleFr = "La Foi Vivante & l'Autorité du Croyant",
            titleMg = "Finoana Velona sy ny Fahefan'ny Mpino",
            descriptionFr = "Apprenez à libérer la foi de Dieu par la méditation de la Parole et les proclamations victorieuses.",
            descriptionMg = "Mianara mamoaka ny finoan'Andriamanitra amin'ny alalan'ny fandinihana ny Teny sy ny fanambarana fandresena.",
            durationDays = 5,
            days = listOf(
                PlanDay(1, "La Nature de la Foi Biblique", "Ny Toetry ny Finoana marina", "Hébreux 11:1-6", "Hébreux", 11, "La ferme assurance des choses qu'on espère", "Fahatokiana mafy ny amin'ny zavatra antenaina"),
                PlanDay(2, "Déplacer les Montagnes par la Parole", "Famindrana ny Tendrombohitra", "Marc 11:22-26", "Jean", 14, "Croire sans douter dans son cœur", "Minoa ao am-po ka aza misalasala"),
                PlanDay(3, "L'Esprit de Foi et la Proclamation", "Ny Fanahin'ny Finoana", "2 Corinthiens 4:13, Romains 10:17", "Romains", 8, "J'ai cru, c'est pourquoi j'ai parlé", "Nino aho, koa izany no nitenenako"),
                PlanDay(4, "Le Bouclier de la Foi face aux traits enflammés", "Ny Ampingam-pinoana", "Éphésiens 6:10-18", "Éphésiens", 5, "Éteindre toutes les flèches du malin", "Famonoana ny zana-tsipìka mirehitra"),
                PlanDay(5, "Plus que Vainqueurs par Son Amour", "Mihoatra noho ny Mpresy", "Romains 8:31-39", "Romains", 8, "Rien ne pourra nous séparer de Dieu", "Tsy misy mahasaraka antsika amin'Andriamanitra")
            )
        ),

        // 4. Plan Thématique : Guérison & Miracles
        ReadingPlan(
            id = "plan_guerison",
            category = PlanCategory.THEMATIQUE,
            titleFr = "Guérison Divine & Miracles de l'Alliance",
            titleMg = "Fanasitranana Masina sy Fahagagana",
            descriptionFr = "S'approprier la santé divine acquise à la croix et prier pour les malades au nom de Jésus.",
            descriptionMg = "Handray ny fahasalamana azo teo amin'ny hazo fijaliana sy hivavaka ho an'ny marary amin'ny anaran'i Jesosy.",
            durationDays = 5,
            days = listOf(
                PlanDay(1, "Par Ses Meurtrissures Nous Sommes Guéris", "Ny Diam-kapony no Nahasitrana antsika", "Ésaïe 53:4-5, 1 Pierre 2:24", "Ésaïe", 53, "Jésus s'est chargé de nos maladies", "Nalainy ny aretintsika"),
                PlanDay(2, "L'Éternel Qui Te Guérit", "Jehovah Mpanasitrana Anao", "Psaume 103:1-5", "Psaumes", 23, "Il guérit toutes tes infirmités", "Manasitrana ny aretinao rehetra Izy"),
                PlanDay(3, "L'Autorité Déléguée aux Disciples", "Ny Fahefana nomena ny Mpianatra", "Matthieu 10:1-8, Marc 16:17-18", "Jean", 14, "Imposez les mains aux malades", "Hametra-tanana amin'ny marary"),
                PlanDay(4, "La Prière de Foi et l'Huile Sainte", "Ny Vavaky ny Finoana", "Jacques 5:13-16", "Actes", 2, "La prière ardente du juste a une grande efficacité", "Manan-kery lehibe ny fivavaky ny marina"),
                PlanDay(5, "L'Onction de Guérison en Action", "Ny Hosotry ny Fanasitranana", "Actes 10:38, Actes 3:1-10", "Actes", 1, "Guérissant tous ceux qui étaient sous l'empire du diable", "Nanasitrana izay rehetra nampahorina")
            )
        ),

        // 5. Plan Nouveau Testament & Psaumes
        ReadingPlan(
            id = "plan_nt_psaumes",
            category = PlanCategory.NOUVEAU_TESTAMENT,
            titleFr = "Nouveau Testament & Psaumes de Gloire",
            titleMg = "Fanekena Vaovao sy Salamon'ny Voninahitra",
            descriptionFr = "Un parcours équilibré combinant la révélation évangélique apostolique et la louange des Psaumes.",
            descriptionMg = "Famakiana mirindra ny fampianaran'ny apostoly sy ny fiderana ao amin'ny bokin'ny Salamo.",
            durationDays = 7,
            days = listOf(
                PlanDay(1, "Le Bon Berger & le Consolateur", "Ilay Mpiandry Tsara sy Mpananatra", "Jean 10, Psaumes 23", "Jean", 14, "L'Éternel est mon Berger", "Jehovah no Mpiandry ahy"),
                PlanDay(2, "Sous l'Abri du Très-Haut", "Ao amin'ny Fialofan'ny Tsitoha", "Psaumes 91, Romains 8", "Psaumes", 91, "La sécurité absolue du croyant", "Fiarovana tsy azo hozongozonina"),
                PlanDay(3, "L'Évangile de la Grâce Vivifiante", "Ny Filazantsaran'ny Fahasoavana", "Galates 1-2, Psaumes 100", "Galates", 5, "Affranchis par le sacrifice de Christ", "Nafahana tamin'ny fanandevozana"),
                PlanDay(4, "Les Richesses de Notre Héritage Céleste", "Ny Harenan'ny Lova any An-danitra", "Éphésiens 1-2, Psaumes 34", "Éphésiens", 1, "Bénis de toutes sortes de bénédictions spirituelles", "Notahina tamin'ny fitahiana ara-panahy rehetra"),
                PlanDay(5, "L'Armure Complète de Dieu", "Ny Fiadian'Andriamanitra", "Éphésiens 6, Psaumes 27", "Éphésiens", 5, "L'Éternel est ma lumière et mon salut", "Jehovah no fahazavako sy famonjena ahy"),
                PlanDay(6, "La Louange Perpétuelle par l'Esprit", "Fiderana lalandava ao amin'ny Fanahy", "Psaumes 150, Actes 16:25", "Actes", 2, "Que tout ce qui respire loue l'Éternel", "Aoka izay rehetra manam-pofon'aina hidera an'i Jehovah"),
                PlanDay(7, "Le Trône de la Grâce", "Ny Sezafiandrianan'ny Fahasoavana", "Hébreux 4, Psaumes 119", "Romains", 8, "Approchons-nous avec une pleine assurance", "Aoka isika hanatona amin'ny fahasahiana")
            )
        ),

        // 6. Plan Annuel
        ReadingPlan(
            id = "plan_annuel",
            category = PlanCategory.ANNUEL,
            titleFr = "Baiboly Manontolo ao anatin'ny 1 Taona (Plan Annuel)",
            titleMg = "Baiboly Manontolo ao anatin'ny 1 Taona",
            descriptionFr = "Lecture quotidienne équilibrée comprenant l'Ancien Testament, le Nouveau Testament, un Psaume et un Proverbe.",
            descriptionMg = "Teti-pamakiana isan'andro ahitana ny Testamenta Taloha, Testamenta Vaovao, Salamo sy Ohabolana.",
            durationDays = 7,
            days = listOf(
                PlanDay(1, "Jour 1 : Genèse & Jean", "Andro 1 : Genesisy & Jaona", "Genèse 1-2, Jean 1, Psaume 1", "Jean", 14, "Au commencement était la Parole", "Tamin'ny voalohany ny Teny"),
                PlanDay(2, "Jour 2 : La Promesse d'Abraham", "Andro 2 : Ny Fampanantenana an'i Abrahama", "Genèse 12, Jean 2, Psaume 2", "Jean", 14, "Toutes les familles de la terre seront bénies", "Hotahina ao aminao ny firenena rehetra"),
                PlanDay(3, "Jour 3 : L'Exode & la Libération", "Andro 3 : Ny Fivoahana tany Egypta", "Exode 1-3, Jean 3, Psaume 3", "Jean", 14, "L'Éternel entend les cris de son peuple", "Nandre ny fitarainana Jehovah"),
                PlanDay(4, "Jour 4 : La Loi & la Grâce", "Andro 4 : Ny Lalàna sy ny Fahasoavana", "Exode 20, Jean 4, Psaume 4", "Jean", 14, "Les vrais adorateurs en esprit et en vérité", "Ny mpivavaka marina mivavaka amin'ny fanahy sy fahamarinana"),
                PlanDay(5, "Jour 5 : Le Sanctuaire & la Présence", "Andro 5 : Ny Toerana Masina", "Lévitique 1, Jean 5, Psaume 5", "Jean", 14, "La sainteté de l'Éternel", "Fahamasinan'i Jehovah"),
                PlanDay(6, "Jour 6 : La Conduite dans le Désert", "Andro 6 : Ny Fitarihana tany an-tany efitra", "Nombres 6, Jean 6, Psaume 6", "Jean", 14, "Le pain vivant descendu du ciel", "Ny mofo velona nidina avy tany an-danitra"),
                PlanDay(7, "Jour 7 : Écoute Israël (Shema)", "Andro 7 : Mihainoa ry Isiraely", "Deutéronome 6, Jean 7, Psaume 7", "Jean", 14, "Des fleuves d'eau vive couleront de son sein", "Hikoriana avy ao anaty reniranon'ny rano velona")
            )
        )
    )
}
