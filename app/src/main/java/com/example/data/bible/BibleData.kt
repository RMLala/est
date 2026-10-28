package com.example.data.bible

enum class BibleVersion(val code: String, val displayName: String, val language: String) {
    MALAGASY("MG", "Baiboly Malagasy", "Malagasy"),
    SEMEUR("BDS", "Bible du Semeur", "Français"),
    AMPLIFIED("AMP", "Amplified Bible", "English")
}

data class BibleVerse(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val textMalagasy: String,
    val textSemeur: String,
    val textAmplified: String
) {
    fun getText(version: BibleVersion): String = when (version) {
        BibleVersion.MALAGASY -> textMalagasy
        BibleVersion.SEMEUR -> textSemeur
        BibleVersion.AMPLIFIED -> textAmplified
    }
}

data class DictionaryEntry(
    val term: String,
    val originalWord: String,
    val language: String, // Grec, Hébreu
    val strongRef: String,
    val transliteration: String,
    val definition: String,
    val theologicalSignificance: String,
    val keyVerses: List<String>
)

data class ConcordanceTopic(
    val topic: String,
    val category: String,
    val description: String,
    val references: List<String>
)

object BibleRepositoryData {

    val booksList = listOf(
        "Jean",
        "Actes",
        "Romains",
        "Galates",
        "Éphésiens",
        "Psaumes",
        "Proverbes",
        "Genèse",
        "Ésaïe",
        "1 Corinthiens",
        "Hébreux",
        "Apocalypse"
    )

    // Curated rich verses covering foundational pneumatological and theological pillars
    val verses: List<BibleVerse> = listOf(
        // Jean 14
        BibleVerse(
            book = "Jean",
            chapter = 14,
            verse = 16,
            textMalagasy = "Ary Izaho hangataka amin'ny Ray, ary Izy hanome anareo Mpananatra hafa mba ho eo aminareo mandrakizay,",
            textSemeur = "Et moi, je prierai le Père, et il vous donnera un autre Défenseur afin qu'il reste éternellement avec vous :",
            textAmplified = "And I will ask the Father, and He will give you another Helper (Comforter, Advocate, Intercessor—Counselor, Strengthener, Standby), to be with you forever—"
        ),
        BibleVerse(
            book = "Jean",
            chapter = 14,
            verse = 17,
            textMalagasy = "dia ny Fanahin'ny fahamarinana, Izay tsy azon'izao tontolo izao horaisina, satria tsy hitany na fantany; fa ianareo no mahalala Azy, satria mitoetra eo aminareo Izy ka ho ao anatinareo.",
            textSemeur = "l'Esprit de la vérité, que le monde ne peut pas recevoir parce qu'il ne le voit pas et ne le connaît pas. Mais vous, vous le connaissez, car il demeure avec vous et il sera en vous.",
            textAmplified = "the Spirit of Truth, whom the world cannot receive (and take to its heart) because it does not see Him or know Him, but you know Him because He (the Holy Spirit) remains with you continually and will be in you."
        ),
        BibleVerse(
            book = "Jean",
            chapter = 14,
            verse = 26,
            textMalagasy = "Fa ny Mpananatra, dia ny Fanahy Masina, Izay hirahin'ny Ray amin'ny anarako, Izy no hampianatra anareo ny zavatra rehetra sy hampiray hevitra anareo ny zavatra rehetra izay nolazaiko taminareo.",
            textSemeur = "Mais le Défenseur, l'Esprit Saint que le Père enverra en mon nom, vous enseignera toutes choses et vous rappellera tout ce que je vous ai dit.",
            textAmplified = "But the Helper (Comforter, Advocate, Intercessor—Counselor, Strengthener, Standby), the Holy Spirit, whom the Father will send in My name [in My place, to represent Me and act on My behalf], He will teach you all things. And He will help you remember everything that I have told you."
        ),
        BibleVerse(
            book = "Jean",
            chapter = 16,
            verse = 13,
            textMalagasy = "Fa raha tonga kosa Izy, dia ny Fanahin'ny fahamarinana, dia hitari-dalana anareo ho amin'ny marina rehetra Izy; fa tsy hiteny ho Azy Izy, fa izay rehetra reny no holazainy; ary ny zavatra ho avy dia hambarany aminareo.",
            textSemeur = "Quand le Défenseur sera venu, l'Esprit de la vérité, il vous conduira dans toute la vérité, car il ne parlera pas de lui-même, mais tout ce qu'il aura entendu, il le dira, et il vous annoncera les choses à venir.",
            textAmplified = "But when He, the Spirit of Truth, comes, He will guide you into all the truth [full and complete truth]—for He will not speak on His own initiative, but He will speak whatever He hears [from the Father—the message regarding the Son], and He will disclose to you what is to come [in the future]."
        ),

        // Actes 1 & 2
        BibleVerse(
            book = "Actes",
            chapter = 1,
            verse = 8,
            textMalagasy = "Fa hahazo hery ianareo amin'ny hilatsahan'ny Fanahy Masina aminareo, ary ho vavolombeloko any Jerosalema sy eran'i Jodia sy Samaria ary hatramin'ny faran'ny tany.",
            textSemeur = "Mais vous recevrez une puissance, le Saint-Esprit survenant sur vous, et vous serez mes témoins à Jérusalem, dans toute la Judée, dans la Samarie, et jusqu'aux extrémités de la terre.",
            textAmplified = "But you will receive power and ability when the Holy Spirit comes upon you; and you will be My witnesses [to tell people about Me] both in Jerusalem and in all Judea, and Samaria, and even to the ends of the earth."
        ),
        BibleVerse(
            book = "Actes",
            chapter = 2,
            verse = 1,
            textMalagasy = "Ary rehefa tonga ny Andro Pentekosta, dia teo amin'ny fitoerana iray izy rehetra.",
            textSemeur = "Le jour de la Pentecôte, ils étaient tous ensemble réunis dans un même lieu.",
            textAmplified = "When the day of Pentecost had come, they were all together in one place."
        ),
        BibleVerse(
            book = "Actes",
            chapter = 2,
            verse = 2,
            textMalagasy = "Ary nisy feo tonga tampoka avy tany an-danitra, toy ny rivotra mahery mifofofofo, ka nanenika ilay trano niipihany rehetra.",
            textSemeur = "Tout à coup il vint du ciel un bruit comme celui d'un vent violent qui souffle, et il remplit toute la maison où ils étaient assis.",
            textAmplified = "Suddenly a sound like a mighty rushing wind came from heaven, and it filled the whole house where they were sitting."
        ),
        BibleVerse(
            book = "Actes",
            chapter = 2,
            verse = 4,
            textMalagasy = "Ary samy feno ny Fanahy Masina izy rehetra, ka dia niteny tamin'ny fiteny maro samihafa, araka ny nampitenenan'ny Fanahy azy.",
            textSemeur = "Et ils furent tous remplis du Saint-Esprit et se mirent à parler en d'autres langues, selon ce que l'Esprit leur donnait d'exprimer.",
            textAmplified = "And they were all filled [that is, diffused throughout their being] with the Holy Spirit and began to speak in other tongues (different languages), as the Spirit was giving them the ability to speak out [clearly and appropriately]."
        ),

        // Romains 8
        BibleVerse(
            book = "Romains",
            chapter = 8,
            verse = 1,
            textMalagasy = "Koa amin'izany dia tsy misy fanamelohana ho an'izay ao amin'i Kristy Jesosy.",
            textSemeur = "Maintenant donc, il n'y a plus de condamnation pour ceux qui sont unis à Jésus-Christ.",
            textAmplified = "Therefore there is now no condemnation [no guilty verdict, no punishment] for those who are in Christ Jesus [who believe in Him as personal Lord and Savior]."
        ),
        BibleVerse(
            book = "Romains",
            chapter = 8,
            verse = 11,
            textMalagasy = "Ary raha ny Fanahin'izay nanangana an'i Jesosy tamin'ny maty no mitoetra ao anatinareo, dia Ilay nanangana an'i Kristy Jesosy tamin'ny maty no hamelona ny tenanareo mety maty koa noho ny Fanahiny Izay mitoetra ao anatinareo.",
            textSemeur = "Et si l'Esprit de celui qui a ressuscité Jésus d'entre les morts habite en vous, celui qui a ressuscité Christ d'entre les morts rendra aussi la vie à vos corps mortels par son Esprit qui habite en vous.",
            textAmplified = "And if the Spirit of Him who raised Jesus from the dead lives in you, He who raised Christ Jesus from the dead will also give life to your mortal bodies through His Spirit, who lives in you."
        ),
        BibleVerse(
            book = "Romains",
            chapter = 8,
            verse = 14,
            textMalagasy = "Fa izay rehetra tarihin'ny Fanahin'Andriamanitra no zanak'Andriamanitra.",
            textSemeur = "Car tous ceux qui sont conduits par l'Esprit de Dieu sont fils de Dieu.",
            textAmplified = "For all who are allowing themselves to be led by the Spirit of God are sons of God."
        ),
        BibleVerse(
            book = "Romains",
            chapter = 8,
            verse = 26,
            textMalagasy = "Ary toy izany koa, ny Fanahy no manampy ny fahalementsika: fa tsy fantatsika izay tokony hangatahintsika araka izay mety; fa ny Fanahy mihitsy no mifona amin'ny fitarainana tsy azo tononina.",
            textSemeur = "De même, l'Esprit vient nous aider dans notre faiblesse. En effet, nous ne savons pas prier comme il faut, mais l'Esprit lui-même intercède pour nous par des soupirs que les mots ne peuvent exprimer.",
            textAmplified = "In the same way the Spirit [comes to us and] helps us in our weakness. We do not know what prayer to offer or how to offer it as we should, but the Spirit Himself [knows our need and at the right time] intercedes on our behalf with sighs and groanings too deep for words."
        ),

        // Galates 5
        BibleVerse(
            book = "Galates",
            chapter = 5,
            verse = 22,
            textMalagasy = "Fa ny vokatry ny Fanahy kosa dia fitiavana, fifaliana, fiadanana, fahari-po, fahamoram-panahy, hatsaram-panahy, fahatokiana,",
            textSemeur = "Mais le fruit de l'Esprit, c'est l'amour, la joie, la paix, la patience, la bonté, la bienveillance, la fidélité,",
            textAmplified = "But the fruit of the Spirit [the result of His presence within us] is love [unselfish concern for others], joy, [inner] peace, patience [not the ability to wait, but how we act while waiting], kindness, goodness, faithfulness,"
        ),
        BibleVerse(
            book = "Galates",
            chapter = 5,
            verse = 23,
            textMalagasy = "fahalemem-panahy, fahononam-po; tsy misy lalàna manohitra izany.",
            textSemeur = "la douceur, la maîtrise de soi. La Loi ne s'oppose pas à de telles choses.",
            textAmplified = "gentleness, self-control. Against such things there is no law."
        ),
        BibleVerse(
            book = "Galates",
            chapter = 5,
            verse = 25,
            textMalagasy = "Raha velona amin'ny Fanahy isika, dia aoka handeha araka ny Fanahy koa.",
            textSemeur = "Si nous vivons par l'Esprit, marchons aussi selon l'Esprit.",
            textAmplified = "If we [claim to] live by the [Holy] Spirit, let us also walk by the Spirit [with personal integrity, godly character, and moral courage—our conduct empowered by the Holy Spirit]."
        ),

        // Éphésiens 1 & 5
        BibleVerse(
            book = "Éphésiens",
            chapter = 1,
            verse = 13,
            textMalagasy = "Tao Aminy koa ianareo, rehefa nandre ny tenin'ny fahamarinana, dia ny filazantsaran'ny famonjena anareo,- eny, tao Aminy koa ianareo, rehefa nino, no nasiana tombo-kase tamin'ny Fanahy Masina nampanantenaina,",
            textSemeur = "En lui vous aussi, après avoir entendu la parole de la vérité, l'Evangile qui vous sauve, en lui vous avez cru et vous avez été scellés du Saint-Esprit qui avait été promis,",
            textAmplified = "In Him, you also, when you heard the word of truth, the good news of your salvation, and [as a result] believed in Him, were stamped with the seal of the promised Holy Spirit,"
        ),
        BibleVerse(
            book = "Éphésiens",
            chapter = 5,
            verse = 18,
            textMalagasy = "Ary aza mamon-divay, fa misy fanaranam-po amin'izany, fa aoka ho feno ny Fanahy,",
            textSemeur = "Ne vous enivrez pas de vin: cela mène à la débauche. Soyez au contraire remplis de l'Esprit :",
            textAmplified = "Do not get drunk with wine, for that is wickedness (corruption, stupidity), but be filled with the [Holy] Spirit and constantly guided by Him."
        ),

        // Psaumes 23 & 91
        BibleVerse(
            book = "Psaumes",
            chapter = 23,
            verse = 1,
            textMalagasy = "Jehovah no Mpiandry ahy; Tsy hanan-java-mahory aho.",
            textSemeur = "L'Eternel est mon berger, je ne manquerai de rien.",
            textAmplified = "The Lord is my Shepherd [to feed, to guide and to shield me], I shall not want."
        ),
        BibleVerse(
            book = "Psaumes",
            chapter = 91,
            verse = 1,
            textMalagasy = "Izay mitoetra ao amin'ny fieren'ny Avo Indrindra Dia handry ao amin'ny fialofan'ny Tsitoha.",
            textSemeur = "Celui qui s'abrite sous la protection du Très-Haut repose à l'ombre du Tout-Puissant.",
            textAmplified = "He who dwells in the shelter of the Most High Will remain secure and rest in the shadow of the Almighty [whose power no enemy can withstand]."
        ),

        // 1 Corinthiens 12 (Les dons du Saint-Esprit)
        BibleVerse(
            book = "1 Corinthiens",
            chapter = 12,
            verse = 7,
            textMalagasy = "Ary samy omena ny fampisehoana ny Fanahy hahasoa ny olona rehetra.",
            textSemeur = "En chacun, l'Esprit se manifeste pour le bien commun.",
            textAmplified = "But to each one is given the manifestation of the Spirit [the spiritual illumination and the enablements of the Holy Spirit] for the common good."
        ),
        BibleVerse(
            book = "1 Corinthiens",
            chapter = 12,
            verse = 8,
            textMalagasy = "Fa ny anankiray omen'ny Fanahy teny fahendrena, ary ny anankiray teny fahalalana, araka izany Fanahy izany ihany,",
            textSemeur = "A l'un est donnée par l'Esprit une parole de sagesse ; à un autre, une parole de connaissance, selon le même Esprit ;",
            textAmplified = "To one is given through the [Holy] Spirit [the power to speak] the message of wisdom, and to another [the power to express] the word of knowledge and understanding according to the same Spirit;"
        )
    )

    // Theological Dictionary
    val dictionaryEntries: List<DictionaryEntry> = listOf(
        DictionaryEntry(
            term = "Paraclet",
            originalWord = "Παράκλητος",
            language = "Grec",
            strongRef = "G3875 (Paraklētos)",
            transliteration = "Paraklētos",
            definition = "Celui qui est appelé aux côtés de quelqu'un pour l'assister. Désigne le Défenseur, Avocat, Consolateur, Conseiller, Fortificateur et Intercesseur.",
            theologicalSignificance = "Jésus présente le Saint-Esprit comme 'un autre Paraclet' (allos parakletos : un autre de la même nature que Lui). Il est la présence constante, personnelle et directrice de Christ dans le croyant.",
            keyVerses = listOf("Jean 14:16", "Jean 14:26", "Jean 15:26", "Jean 16:7", "1 Jean 2:1")
        ),
        DictionaryEntry(
            term = "Ruach",
            originalWord = "רוּחַ",
            language = "Hébreu",
            strongRef = "H7307 (Rûaḥ)",
            transliteration = "Ruach HaKodesh",
            definition = "Souffle, vent puissant, énergie vitale invisible et active, Esprit divin.",
            theologicalSignificance = "Dans l'Ancien Testament, le Ruach planait sur les eaux lors de la création (Genèse 1:2) et animait les prophètes, rois et juges avec une onction souveraine.",
            keyVerses = listOf("Genèse 1:2", "Ézéchiel 37:9", "Zacharie 4:6", "Psaume 104:30")
        ),
        DictionaryEntry(
            term = "Pneuma",
            originalWord = "Πνεῦμα",
            language = "Grec",
            strongRef = "G4151 (Pneuma)",
            transliteration = "Pneuma Hagion",
            definition = "Esprit, souffle saint, principe divin de vie spirituelle immortelle.",
            theologicalSignificance = "Dans le Nouveau Testament, le Pneuma Hagion régénère le cœur de l'homme, accorde la nouvelle naissance et l'adoption filiale (Abba Père).",
            keyVerses = listOf("Jean 3:8", "Romains 8:15", "1 Corinthiens 2:10", "2 Corinthiens 3:17")
        ),
        DictionaryEntry(
            term = "Dunamis",
            originalWord = "Δύναμις",
            language = "Grec",
            strongRef = "G1411 (Dynamis)",
            transliteration = "Dunamis",
            definition = "Puissance intrinsèque, force miraculeuse, capacité d'action surnaturelle (racine du mot 'dynamite').",
            theologicalSignificance = "C'est la puissance que les apôtres reçoivent lors du baptême dans le Saint-Esprit pour vaincre les forces ténèbres et témoigner avec miracles, signes et prodiges.",
            keyVerses = listOf("Actes 1:8", "Luc 24:49", "Romains 1:16", "Éphésiens 3:20")
        ),
        DictionaryEntry(
            term = "Charisma",
            originalWord = "Χάρισμα",
            language = "Grec",
            strongRef = "G5486 (Charisma)",
            transliteration = "Charisma (pl. Charismata)",
            definition = "Don gratuit immérité de grâce accordé par Dieu par pure faveur et bonté.",
            theologicalSignificance = "Désigne spécifiquement les 9 dons de l'Esprit (1 Cor 12) : parole de sagesse, parole de connaissance, foi, dons de guérisons, miracles, prophétie, discernement des esprits, langues, interprétation.",
            keyVerses = listOf("1 Corinthiens 12:4", "Romains 12:6", "1 Pierre 4:10", "1 Timothée 4:14")
        ),
        DictionaryEntry(
            term = "Koinonia",
            originalWord = "Κοινωνία",
            language = "Grec",
            strongRef = "G2842 (Koinōnia)",
            transliteration = "Koinōnia",
            definition = "Communion intime, association étroite, partage fraternel profond, partenariat mutuel.",
            theologicalSignificance = "La communion du Saint-Esprit (2 Cor 13:13) n'est pas seulement une réunion, mais une relation quotidienne d'intimité, d'écoute et de collaboration avec l'Esprit de Dieu.",
            keyVerses = listOf("2 Corinthiens 13:13", "1 Jean 1:3", "Philippiens 2:1", "Actes 2:42")
        ),
        DictionaryEntry(
            term = "Agapē",
            originalWord = "Ἀγάπη",
            language = "Grec",
            strongRef = "G26 (Agapē)",
            transliteration = "Agapē",
            definition = "Amour divin désintéressé, inconditionnel, sacrificiel et éternel.",
            theologicalSignificance = "Premier fruit du Saint-Esprit (Galates 5:22). Cet amour est répandu dans nos cœurs par le Saint-Esprit (Romains 5:5) et constitue l'essence même du disciple.",
            keyVerses = listOf("Romains 5:5", "1 Corinthiens 13:4-8", "Jean 3:16", "Galates 5:22")
        ),
        DictionaryEntry(
            term = "Shalom",
            originalWord = "שָׁלוֹם",
            language = "Hébreu",
            strongRef = "H7965 (Shālôm)",
            transliteration = "Shalôm",
            definition = "Paix totale, bien-être intégral, intégrité, plénitude, santé, réconciliation et prospérité de l'âme.",
            theologicalSignificance = "La paix que Jésus donne n'est pas l'absence de conflit, mais la présence de Dieu et le règne de l'Esprit au milieu de la tempête.",
            keyVerses = listOf("Jean 14:27", "Ésaïe 26:3", "Nombres 6:24-26", "Philippiens 4:7")
        ),
        DictionaryEntry(
            term = "Onction",
            originalWord = "Χρῖσμα",
            language = "Grec",
            strongRef = "G5545 (Chrisma)",
            transliteration = "Chrisma",
            definition = "Consécration par l'huile sainte ; investiture de l'autorité et de la puissance de Dieu.",
            theologicalSignificance = "L'onction brise tout joug de servitude (Ésaïe 10:27). Le croyant possède l'onction du Saint qui lui enseigne toutes choses (1 Jean 2:20, 27).",
            keyVerses = listOf("1 Jean 2:20", "1 Jean 2:27", "Actes 10:38", "Luc 4:18")
        )
    )

    // Concordance Topics
    val concordanceTopics: List<ConcordanceTopic> = listOf(
        ConcordanceTopic(
            topic = "Le Saint-Esprit & Ses Œuvres",
            category = "Pneumatologie",
            description = "L'action, la personne, les dons et le fruit du Saint-Esprit dans l'Église et le croyant.",
            references = listOf(
                "Jean 14:16-17 (Le Consolateur éternel)",
                "Jean 16:13 (Le Guide dans la vérité)",
                "Actes 1:8 (La Puissance pour témoigner)",
                "Actes 2:1-4 (L'Effusion de Pentecôte)",
                "Romains 8:11 (La vie transmise au corps mortel)",
                "Romains 8:14 (Les fils conduits par l'Esprit)",
                "Romains 8:26 (L'Intercession de l'Esprit)",
                "1 Corinthiens 12:7-11 (Les 9 dons spirituels)",
                "Galates 5:22-23 (Le fruit de l'Esprit)",
                "Éphésiens 5:18 (Être constamment rempli de l'Esprit)"
            )
        ),
        ConcordanceTopic(
            topic = "La Prière & L'Intercession",
            category = "Vie Spirituelle",
            description = "Prières efficaces, persévérance, soupirs de l'Esprit et prière de foi.",
            references = listOf(
                "Matthieu 6:9-13 (Le Notre Père)",
                "Marc 11:24 (La prière avec foi)",
                "Éphésiens 6:18 (Prier par l'Esprit en tout temps)",
                "Jacques 5:16 (La prière fervente du juste)",
                "1 Thessaloniciens 5:17 (Priez sans cesse)",
                "Hébreux 4:16 (Le trône de la grâce avec assurance)"
            )
        ),
        ConcordanceTopic(
            topic = "La Foi & Les Déclarations",
            category = "Doctrine",
            description = "Confession de la Parole vivante, autorité spirituelle et victoire sur l'ennemi.",
            references = listOf(
                "Hébreux 11:1 (Définition de la foi victorieuse)",
                "Romains 10:17 (La foi vient de ce qu'on entend)",
                "Proverbes 18:21 (Pouvoir de vie et de mort dans la langue)",
                "2 Corinthiens 4:13 (L'esprit de foi : j'ai cru, c'est pourquoi j'ai parlé)",
                "Marc 11:23 (Ordonner à la montagne de se déplacer)"
            )
        ),
        ConcordanceTopic(
            topic = "La Guérison Divine & La Restauration",
            category = "Miracles",
            description = "Promesses de santé, pardon et restauration divine pour le corps et l'âme.",
            references = listOf(
                "Ésaïe 53:4-5 (Par ses meurtrissures nous sommes guéris)",
                "Matthieu 8:17 (Il a pris nos infirmités)",
                "Jacques 5:14-15 (L'onction d'huile et la prière de foi)",
                "1 Pierre 2:24 (Guéris par ses meurtrissures)",
                "Psaume 103:2-3 (Il guérit toutes tes maladies)"
            )
        )
    )

    // Default Seed Theological Books
    val seedBooks = listOf(
        com.example.data.model.TheologicalBook(
            title = "La Personne et l'Œuvre du Saint-Esprit",
            author = "R.A. Torrey",
            category = "Pneumatologie",
            summary = "Étude biblique systématique sur la divinité, la personnalité, le baptême et la puissance opérante du Saint-Esprit.",
            status = "Lu",
            rating = 5,
            keyQuote = "Le Saint-Esprit est une personne divine, digne de notre adoration, de notre amour et de notre obéissance absolue."
        ),
        com.example.data.model.TheologicalBook(
            title = "Homilétique & Prédication Biblique",
            author = "Haddon Robinson",
            category = "Homilétique",
            summary = "L'art de préparer des sermons bibliques solides, captivants, christocentriques et applicables pour transformer les vies.",
            status = "En cours",
            rating = 5,
            keyQuote = "Une prédication véritable est la vérité de Dieu filtrée à travers la personnalité sanctifiée du prédicateur."
        ),
        com.example.data.model.TheologicalBook(
            title = "L'Onction & La Puissance d'en Haut",
            author = "Charles Finney",
            category = "Histoire des Réveils",
            summary = "Témoignage et doctrine biblique sur l'onction d'en haut nécessaire pour tout ministère fructueux et tout réveil des cœurs.",
            status = "À lire",
            rating = 5,
            keyQuote = "Sans le baptême du Saint-Esprit, tous nos efforts pastoraux ne sont que paille devant le feu."
        ),
        com.example.data.model.TheologicalBook(
            title = "Théologie Systématique",
            author = "Wayne Grudem",
            category = "Théologie Systématique",
            summary = "Introduction doctrinale magistrale et accessible sur les doctrines de Dieu, du salut, de l'Esprit, de l'Église et des fins dernières.",
            status = "En cours",
            rating = 5,
            keyQuote = "La théologie n'est pas faite pour remplir la tête, mais pour embraser le cœur d'adoration envers Dieu."
        )
    )

    // Default Faith Declarations
    val seedDeclarations = listOf(
        com.example.data.model.FaithDeclaration(
            theme = "Identité en Christ",
            title = "Je suis Temple du Saint-Esprit",
            declarationText = "Mon corps est le sanctuaire vivant du Dieu Très-Haut. Le Saint-Esprit habite en moi avec plénitude. Aucune ténèbre, aucune maladie ni aucun découragement n'a de prise sur ma vie, car Celui qui est en moi est plus grand que celui qui est dans le monde.",
            scriptureRef = "1 Corinthiens 6:19, 1 Jean 4:4",
            recitationCount = 14,
            isFavorite = true
        ),
        com.example.data.model.FaithDeclaration(
            theme = "Puissance & Onction",
            title = "Revêtu de Puissance d'en Haut",
            declarationText = "J'ai reçu la puissance (Dunamis) du Saint-Esprit pour être un témoin vivant, efficace et audacieux. L'onction brise tout joug dans ma famille et mon ministère. Les dons spirituels opèrent avec sagesse et amour à travers moi.",
            scriptureRef = "Actes 1:8, Ésaïe 10:27, 1 Corinthiens 12:7",
            recitationCount = 21,
            isFavorite = true
        ),
        com.example.data.model.FaithDeclaration(
            theme = "Victoire & Autorité",
            title = "Plus que Vainqueur",
            declarationText = "En toute circonstance, je suis plus que vainqueur par Jésus-Christ qui m'aime. Aucune arme forgée contre mon appel divin ne prospérera. Je marche par la foi et non par la vue, conduit chaque jour par la paix de Dieu.",
            scriptureRef = "Romains 8:37, Ésaïe 54:17, 2 Corinthiens 5:7",
            recitationCount = 9,
            isFavorite = false
        ),
        com.example.data.model.FaithDeclaration(
            theme = "Direction Divine",
            title = "Conduit par l'Esprit de Vérité",
            declarationText = "L'Esprit de vérité me conduit dans toute la vérité. Mes oreilles spirituelles entendent la voix du Bon Berger, et je ne suivrai point la voix d'un étranger. Dieu ordonne mes pas dans la justice et la gloire.",
            scriptureRef = "Jean 16:13, Psaume 37:23, Jean 10:27",
            recitationCount = 12,
            isFavorite = true
        )
    )

    // Seed Sermon Notes
    val seedSermonNotes = listOf(
        com.example.data.model.SermonNote(
            title = "La Marche selon l'Esprit",
            preacher = "Pasteur Samuel Rakoto",
            churchOrEvent = "Culte du Réveil - Dimanche",
            scriptureRef = "Galates 5:16-26; Romains 8:14",
            content = "1. Marcher selon l'Esprit signifie soumettre chaque décision à Sa voix douce.\n2. Le fruit de l'Esprit n'est pas le produit de nos efforts humains, mais la sève divine qui coule en nous.\n3. Cultiver la communion quotidienne (Koinonia) par la prière et la méditation matinale.",
            keyTakeaways = "Prendre 15 minutes chaque matin pour écouter le Saint-Esprit avant d'ouvrir son téléphone. Pratiquer la douceur face aux provocations.",
            tags = "SaintEsprit, Sanctification, Fruit, Priere",
            dateMillis = System.currentTimeMillis() - 86400000L * 2
        ),
        com.example.data.model.SermonNote(
            title = "L'Efficacité de la Prière Fervente",
            preacher = "Évangéliste David Wilkerson",
            churchOrEvent = "Conférence des Disciples",
            scriptureRef = "Jacques 5:16; Éphésiens 6:18",
            content = "La prière dans l'Esprit pénètre les sphères célestes. Lorsque nous ne savons que demander, le Saint-Esprit intercède avec des soupirs inexprimables.",
            keyTakeaways = "Ne jamais abandonner une requête alignée avec la Parole de Dieu. Intercéder avec foi jusqu'à la paix intérieure.",
            tags = "Priere, Intercession, Foi",
            dateMillis = System.currentTimeMillis() - 86400000L * 5
        )
    )

    // Seed Preaching Preparations
    val seedPreachingPreps = listOf(
        com.example.data.model.PreachingPrep(
            title = "Le Feu du Saint-Esprit dans le Croyant",
            mainPassage = "Actes 2:1-4; Luc 3:16",
            theme = "L'effusion et la purification par le feu divin",
            targetAudience = "Assemblée chrétienne & jeunes disciples",
            introduction = "Illustrer avec la lampe à huile qui a besoin d'huile fraîche pour ne pas s'éteindre. Poser la question : brûlons-nous encore pour Christ ?",
            pointOne = "1. Le Feu Purificateur : Il consume le péché et affine le caractère (Malachie 3:2-3).",
            pointTwo = "2. Le Feu Énergisant : Il donne l'audace et la passion pour les âmes perdues (Jérémie 20:9).",
            pointThree = "3. Le Feu Protecteur : Il établit une muraille de gloire autour de l'Église (Zacharie 2:5).",
            illustrations = "Témoignage du réveil de Pentecôte à Jérusalem et du réveil d'Azusa Street.",
            application = "Appel à renoncer aux compromis du monde et à demander une nouvelle effusion du Saint-Esprit aujourd'hui.",
            altarCall = "Invitation aux personnes qui se sentent tièdes ou découragées à s'avancer pour recevoir une nouvelle onction fraîche.",
            durationMinutes = 40
        )
    )

    // Seed Prayer Requests
    val seedPrayers = listOf(
        com.example.data.model.PrayerItem(
            title = "Réveil spirituel dans notre assemblée",
            category = "Intercession",
            requestDetails = "Prier pour que les cœurs soient embrasés d'amour pour Jésus, repentance sincère et soif de la Parole.",
            isAnswered = false
        ),
        com.example.data.model.PrayerItem(
            title = "Guérison complète de maman",
            category = "Guérison",
            requestDetails = "Demande de restauration totale de ses poumons et de sa force physique.",
            isAnswered = true,
            answeredDateMillis = System.currentTimeMillis() - 86400000L * 4,
            testimony = "Gloire à Dieu ! Les résultats médicaux sont redevenus normaux, le médecin a confirmé une guérison miraculeuse sans séquelles !"
        ),
        com.example.data.model.PrayerItem(
            title = "Sagesse pour la préparation des enseignements",
            category = "Ministère",
            requestDetails = "Recevoir la parole de sagesse et la révélation dans les Écritures pour nourrir les fidèles.",
            isAnswered = true,
            answeredDateMillis = System.currentTimeMillis() - 86400000L * 1,
            testimony = "Le culte de dimanche a touché profondément 8 personnes qui ont donné leur vie à Christ !"
        )
    )
}
